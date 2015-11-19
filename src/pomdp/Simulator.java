package pomdp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 環境を読み込んでシミュレーションを行うクラス
 *
 */
public class Simulator {
	// =======================
	// Fields
	// =======================
	Environment mEnv; // 環境
	TaskSet mTaskSet; // 対象タスク
	WorkerSet mWorkerSet; // ワーカ集合
	Agent mAgent; // エージェント
	List<Result> mResults; // 結果格納

	State mCurrentState; // 現在状態
	State mPrevState; // 前状態
	double mPrevSubtaskQuality; // 前サブタスクの品質

	// =======================
	// Constructors
	// =======================

	public Simulator(Environment pEnv, TaskSet pTaskSet, WorkerSet pWorkerSet, Agent pAgent) {
		mEnv = pEnv;
		mWorkerSet = pWorkerSet;
		mAgent = pAgent;
		mResults = new ArrayList<Result>();

		mCurrentState = new State(0, 1.0, mAgent.getBudget());
		mPrevState = new State(mCurrentState);
		mPrevSubtaskQuality = mPrevState.getQuality();
	}

	// =======================
	// Public Methods
	// =======================

	/**
	 * シミュレーションを実行し，ファイルに書き込む
	 */
	public void run(String output) {
		// 予算切れ or 全サブタスク終了までループ
		do {
			Action action = mAgent.selectAction(); // 行動の受信
			Worker worker = mWorkerSet.nextWorker(mCurrentState.getIndex()); // ワーカの決定
			double observation; // エージェントの観測値

			// サブタスク実行
			switch (action.getType()) {
			case CURR:
				observation = execCurrAction(worker, action.getWage());
				break;
			case NEXT:
				observation = execNextAction(worker, action.getWage());
				break;
			case EVAL:
				observation = execNextAction(worker, action.getWage());
				break;
			default:
				observation = Observation.NONE;
				break;
			}

			// 観測値の送信
			mAgent.update(observation);

			// ログの追加
			mResults.add(new Result(mPrevState, action, worker, mCurrentState));
		} while (!isEnd());

		// 結果の出力
		writeResult(output);
	}

	// =======================
	// Private Methods
	// =======================

	/**
	 * CURRアクションによる処理
	 * 
	 * @return 観測値としてNONE(-1)を返す
	 */
	private double execCurrAction(Worker pWorker, int pWage) {
		Subtask subtask = mTaskSet.getSubtask(mCurrentState.getIndex());
		double workerQuality = pWorker.solve(subtask, pWage, mPrevSubtaskQuality);
		double quality = (mCurrentState.getQuality() > workerQuality) ? mCurrentState.getQuality() : workerQuality;

		// 状態の更新
		mPrevState = mCurrentState;
		mCurrentState = new State(mCurrentState.getIndex(), quality, mAgent.getRemainedBudget() - pWage);

		return Observation.NONE;
	}

	/**
	 * NEXTアクションによる処理．
	 * 
	 * @return 観測値としてNONE(-1)を返す
	 */
	private double execNextAction(Worker pWorker, int pWage) {
		Subtask subtask = mTaskSet.getSubtask(mCurrentState.getIndex() + 1);
		double quality = pWorker.solve(subtask, pWage, mPrevSubtaskQuality);

		// 状態の更新
		mPrevState = mCurrentState;
		mPrevSubtaskQuality = mPrevState.getQuality();
		mCurrentState = new State(mCurrentState.getIndex() + 1, quality, mAgent.getRemainedBudget() - pWage);

		return Observation.NONE;
	}

	/**
	 * EVALアクションによる処理<br>
	 * ワーカを引数に取るが，今回はワーカの能力は考えない
	 */
	private double execEvalAction(Worker pWorker, int pWage) {
		// TODO: Evalアクションの処理の追加

		// 評価値をワーカから受け取るんだよ
		// ワーカの能力は考慮しないんだよ
		// 真の評価値を平均とした正規分布に従って，あるひとつの評価値を出すんだよ．
		
		
		
		
		return -1.0;
	}

	/**
	 * シミュレーション結果の出力
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
	 * 終了判定
	 */
	private boolean isEnd() {
		return (mCurrentState.getIndex() > mTaskSet.getSubtaskNum() || mAgent.getRemainedBudget() <= 0);
	}
}