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
	private Environment mEnv; // 環境
	private TaskSet mTaskSet; // 対象タスク
	private WorkerSet mWorkerSet; // ワーカ集合
	private Agent mAgent; // エージェント
	private List<Result> mResults; // 結果格納

	private State mCurrentState; // 現在状態
	private State mPrevState; // 前状態
	private double mPrevSubtaskQuality; // 前サブタスクの品質

	private boolean mIsEnd; // シミュレーション終了判定フラグ

	// =======================
	// Constructors
	// =======================

	public Simulator(Environment pEnv, Agent pAgent) {
		// 環境設定変数
		mEnv = pEnv;
		mWorkerSet = mEnv.getWorkerSet();
		mTaskSet = mEnv.getTaskSet();
		mIsEnd = false;

		// エージェントの状態把握用の変数
		mAgent = pAgent;
		mCurrentState = new State(0, 1.0, mAgent.getBudget());
		mPrevState = new State(mCurrentState);
		mPrevSubtaskQuality = mPrevState.getQuality();

		// 結果変数
		mResults = new ArrayList<Result>();
	}

	// =======================
	// Public Methods
	// =======================

	/**
	 * シミュレーションを実行し，ファイルに書き込む
	 */
	public void run(String output) {
		// ****************************************
		// FIXME:
		// nextWorkerあたりで終了条件が上手く言っていない
		// 今は来訪ワーカをタスクご毎に分けているから，未定義のサブタスクインデックスに対して参照しようとしている
		// 対処法としては
		// (1)来訪ワーカをインデックス毎に分離しない
		// (2)上手く予算切れの時に処理を行う
		// ****************************************

		// 予算切れ or 全サブタスク終了までループ
		do {
			Action action = mAgent.selectAction(); // 行動の受信
			double observation = Observation.NONE; // エージェントの観測値
			Worker worker = null; // 来訪ワーカ
			Result res;

			// ワークフロー終了判定（報酬が負，予算が負）
			if (isValidAction(action)) {
				// サブタスク実行
				switch (action.getType()) {
				case CURR:
					worker = mWorkerSet.nextWorker(mCurrentState.getIndex());
					observation = execCurrAction(worker, action.getWage());
					break;
				case NEXT:
					worker = mWorkerSet.nextWorker(mCurrentState.getIndex() + 1);
					observation = execNextAction(worker, action.getWage());
					break;
				case EVAL:
					worker = mWorkerSet.nextWorker(mCurrentState.getIndex());
					observation = execEvalAction(worker, action.getWage());
					break;
				default:
					worker = null;
					observation = Observation.NONE;
					break;
				}
				// 観測値の送信とログの追加
				mAgent.update(observation);
				res = new Result(mPrevState, action, worker, mCurrentState);
			} else {
				// 終了ログを記述
				res = new Result();
				mIsEnd = true;
			}

			mResults.add(res);
		} while (!mIsEnd);

		// 結果の出力
		writeResult(output);
	}

	// =======================
	// Private Methods
	// =======================

	/**
	 * CURRアクションによる処理．観測値としてNONE(-1)を返す
	 */
	private double execCurrAction(Worker pWorker, int pWage) {
		Subtask subtask = mTaskSet.getSubtask(mCurrentState.getIndex());
		double workerQuality = pWorker.solve(subtask, pWage, mPrevSubtaskQuality);
		double quality = (mCurrentState.getQuality() > workerQuality) ? mCurrentState.getQuality() : workerQuality;

		// 状態の更新
		mPrevState = mCurrentState;
		mCurrentState = new State(mCurrentState.getIndex(), quality, mAgent.getRemainingBudget() - pWage);

		return Observation.NONE;
	}

	/**
	 * NEXTアクションによる処理．観測値としてNONE(-1)を返す
	 */
	private double execNextAction(Worker pWorker, int pWage) {
		Subtask subtask = mTaskSet.getSubtask(mCurrentState.getIndex() + 1);
		double quality = pWorker.solve(subtask, pWage, mPrevSubtaskQuality);

		// 状態の更新
		mPrevState = mCurrentState;
		mPrevSubtaskQuality = mPrevState.getQuality();
		mCurrentState = new State(mCurrentState.getIndex() + 1, quality, mAgent.getRemainingBudget() - pWage);

		return Observation.NONE;
	}

	/**
	 * EVALアクションによる処理．ワーカから評価値を受け取る．<br>
	 * ワーカを引数に取るが，今回はワーカの能力は考えない
	 */
	private double execEvalAction(Worker pWorker, int pWage) {
		// ワーカに評価基準を渡して評価値を受け取る
		double evaluation = pWorker.evaluate(mCurrentState.getQuality(), mEnv.getOManager().getEvaluations());

		// 状態の更新
		mPrevState = mCurrentState;
		mCurrentState = new State(mCurrentState.getIndex(), mCurrentState.getQuality(),
				mAgent.getRemainingBudget() - pWage);

		return evaluation;
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
	 * 報酬額を見ることで有効な行動か判定する
	 */
	private boolean isValidAction(Action pAction) {
		// 予算切れの場合や報酬額が負の場合にはfalseを返す
		if (pAction.getWage() <= 0) {
			return false;
		}
		if (mAgent.getRemainingBudget() - pAction.getWage() < 0) {
			return false;
		}
		return true;
	}
}