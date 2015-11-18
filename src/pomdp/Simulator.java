package pomdp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pomdp.Agent.AgentType;

/**
 * 環境を読み込んでシミュレーションを行うクラス
 * 
 * @author y-itoh
 *
 */
public class Simulator {
	// =======================
	// Fields
	// =======================
	Environment mEnv;
	Agent mAgent;
	String mCurrentState; // エージェントの位置
	double mQuality; // 作業品質
	List<Result> mResults; // 結果格納

	// =======================
	// Constructors
	// =======================

	public Simulator(Env pEnv, Agent pAgent, Database pDb, Worker[] pWorkers) {
		mEnv = pEnv;
		mAgent = pAgent;
		mDb = pDb;
		mWorkers = pWorkers;
		mQuality = new BigDecimal("1.0");
		mResults = new ArrayList<Result>();

		// 初期状態を設定
		mCurrentState = mEnv.mSManager.mRootState.mName;
	}

	// =======================
	// Methods
	// =======================

	/**
	 * シミュレーションを実行する
	 * 
	 * @param output
	 */
	public void run(String output) {
		// エージェントタイプによってシミュレーションの方法を変更する
		if (mAgent.mAgentType == AgentType.POMDP) {
			runPomdp();
		} else {
			runNormal();
		}

		// 結果を出力
		writeResult(output);
	}

	/**
	 * POMDP, MDP以外のシミュレーション
	 * 
	 * @param output
	 */
	public void runNormal() {
		// TODO: 予算に応じた全ての報酬分配法のシミュレーションを行えるように（Agentクラスに実装する話だが）

		for (int i = 0; i < mEnv.mTaskNum; i++) {
			// エージェントが設定報酬額を取得
			double payoff = mAgent.selectAction();
			String action = removeDot(String.format("NEXT_%.1f", payoff));

			// ワーカがタスクを完了する
			BigDecimal rewardBd = mWorkers[i].solve(mQuality, mEnv.mDif[i], payoff);
			mQuality = rewardBd;

			// Resultオブジェクトの追加
			mResults.add(new Result(mAgent.mRemainedBudget, action, mWorkers[i].toString(), rewardBd.doubleValue(), i));
		}
	}

	/**
	 * シミュレーションを行う．結果はoutputに出力
	 * 
	 * @param output
	 */
	public void runPomdp() {
		// *************************************************
		// シミュレーションの流れ
		// 1. 初期状態にエージェントを立たせる
		// 2. エージェントからアクションを受け取る
		// 3. エージェントの環境内の位置を動かす
		// 4. エージェントに観測値oを与え，信念の更新を行わせる
		// 5. 2に戻って繰り返し
		// *************************************************

		System.out.print("\n simulation start \n");

		// FIXME: POMDPモードの修正

		// 1回目は実行して欲しいからdo..whileにしてみた
		do {
			// アクションの選択
			double payoff = mAgent.selectAction();
			String action = removeDot(String.format("NEXT_%.1f", payoff));

			// ワーカに解答してもらい，作業品質を取得
			// FIXME: rewardBdを小数2桁に修正しないとDBから呼び出せない
			int taskIdx = getTaskIndex(mCurrentState);
			BigDecimal rewardBd = mWorkers[taskIdx].solve(mQuality, mEnv.mDif[taskIdx], payoff);

			// エージェントが知覚する観測値を生成
			// FIXME: ワーカが指定された時のDBとの関係やらcurrentTaskやら
			// FIXME: 作業品質の計算はこっちでやって，それに該当する観測値をDBから引っ張ってくる
			String nextState = "";
			String observation = observe(action, nextState);
			mAgent.update(observation); // 信念の更新

			// Resultオブジェクトの追加
			mResults.add(new Result(mAgent.mRemainedBudget, action, mWorkers[getTaskIndex(mCurrentState)].toString(),
					mCurrentState, nextState, rewardBd.doubleValue(), getTaskIndex(mCurrentState)));

			// 現在値の変更
			mCurrentState = nextState;
		} while (!isGoal());

		System.out.println("completed!\n");
	}

	/**
	 * 行動actionによって状態endStateに到達した場合の観測値を確率的に決定する
	 * 
	 * @param action
	 * @param endState
	 * @return
	 */
	private String observe(String action, String endState) {
		// actionによってend-stateに到達した場合に得られる観測集合を取得する
		Map<String, Double> possibleObservation = mDb.selectObservationStates(action, endState);

		// 各状態の観測確率に応じて確率的に観測値を決定する
		// [0..1]で乱数rを発生させて，rから観測確率を減算していく．rが0以下になった時点の観測値を返す
		double r = Math.random();
		String observation = null;
		for (Map.Entry<String, Double> m : possibleObservation.entrySet()) {
			r -= m.getValue();
			if (r <= 0) {
				observation = m.getKey();
				break;
			}
		}
		return observation;
	}

	/**
	 * エージェントが受理状態に達したか判定
	 * 
	 * @param s
	 * @return
	 */
	private boolean isGoal() {
		// ***********************************
		// 受理状態とは以下のいずれか
		// 1. タスクidxがタスク分割数と同じ
		// 2. 初期状態に戻った場合（予算切れ）
		// ***********************************
		int taskIdx = getTaskIndex(mCurrentState);
		return (taskIdx == mEnv.mTaskNum || taskIdx == 0);
	}

	/**
	 * 文字列からタスクidxを取得する
	 * 
	 * @param state
	 * @return
	 */
	private int getTaskIndex(String state) {
		// 状態は"s1_1333_200"な感じ．先頭から2文字目を取得すれば良い
		char idx = state.charAt(1);
		// アスキーコードの0は48なので，48引くと良い
		return idx - 48;
	}

	/**
	 * シミュレーションの結果を出力する
	 */
	private void writeResult(String output) {
		try {
			File file = new File(output);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			// 環境情報を出力
			pw.println(mEnv.toString());

			// シミュレーション結果を出力
			pw.println("= = = = = = = = = = = = = = = = ");
			pw.println("Agent: " + mAgent.getClass().toString() + "\n");
			for (Result res : mResults) {
				pw.println(res.toString());
			}
			pw.println("= = = = = = = = = = = = = = = = ");
			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	/**
	 * 文字列中にあるドットを削除する
	 * 
	 * @param s
	 * @return
	 */
	protected String removeDot(String s) {
		String regex = "\\.";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(s);
		return m.replaceAll("");
	}
}
