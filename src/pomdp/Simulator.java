package pomdp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import pomdp.Action.ActionType;

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
		mCurrentState = new State(0, 1.0, mAgent.getBudget(), 1.0);
		mPrevState = new State(mCurrentState);

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
		System.out.println("**************************");
		System.out.println("*    Simulation Start    *");
		System.out.println("**************************");

		int round = 1;
		// 予算切れ or 全サブタスク終了までループ
		do {
			System.out.println("[START: round" + round + "]");
			Action action = mAgent.selectAction(); // 行動の受信
			double observation = Observation.NONE; // エージェントの観測値
			Worker worker = null; // 来訪ワーカ
			Result res;

			System.out.println("action: " + action.toString());

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

			System.out.println("[END: round" + round + "]");
			round++;
		} while (!mIsEnd);

		// 結果の出力をplain textとcsvで出力する
		writeResultPlain(output);
		writeResultCsv(output);
	}

	// =======================
	// Private Methods
	// =======================

	/**
	 * CURRアクションによる処理．観測値としてNONE(-1)を返す
	 */
	private double execCurrAction(Worker pWorker, int pWage) {
		Subtask subtask = mTaskSet.getSubtask(mCurrentState.getIndex());
		double workerQuality = pWorker.solve(subtask, pWage, mCurrentState.getPrevStateQuality());
		double quality = (mCurrentState.getQuality() > workerQuality) ? mCurrentState.getQuality() : workerQuality;

		// 状態の更新
		mPrevState = mCurrentState;
		mCurrentState = new State(mCurrentState.getIndex(), quality, mAgent.getRemainingBudget(),
				mPrevState.getPrevStateQuality());

		return Observation.NONE;
	}

	/**
	 * NEXTアクションによる処理．観測値としてNONE(-1)を返す
	 */
	private double execNextAction(Worker pWorker, int pWage) {
		Subtask subtask = mTaskSet.getSubtask(mCurrentState.getIndex() + 1);
		double quality = pWorker.solve(subtask, pWage, mCurrentState.getQuality());

		// 状態の更新
		mPrevState = mCurrentState;
		mCurrentState = new State(mCurrentState.getIndex() + 1, quality, mAgent.getRemainingBudget(),
				mPrevState.getQuality());

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
		mCurrentState = new State(mCurrentState.getIndex(), mCurrentState.getQuality(), mAgent.getRemainingBudget(),
				mCurrentState.getPrevStateQuality());

		return evaluation;
	}

	/**
	 * シミュレーション結果をPlainTextで出力
	 */
	private void writeResultPlain(String pOutput) {
		try {
			String output = pOutput + ".txt";
			File file = new File(output);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			// 環境情報を出力
			pw.println(mEnv.toString());

			// シミュレーション結果を出力
			pw.println("# Agent: " + mAgent.getClass().toString() + "\n");
			for (Result res : mResults) {
				pw.println(res.toString());
			}
			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * シミュレーション結果をCSV形式で出力
	 */
	private void writeResultCsv(String pOutput) {
		try {
			String output = pOutput + ".csv";
			File file = new File(output);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			// フォーマットは<index>, <worker>, <wage>, <quality>, <budget>
			pw.println("<index>, <worker>, <wage>, <quality>, <budget>");
			for (Result res : mResults) {
				pw.println(res.toCsv());
			}
			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * 有効な行動か判定
	 */
	private boolean isValidAction(Action pAction) {
		// -----------------------------
		// 無効な行動とは以下の場合
		// 1. 賃金が負の行動
		// 2. アクションによる残り予算が負
		// 3. 最終タスクでNEXTアクション
		// -----------------------------
		if (pAction.getWage() <= 0) {
			return false;
		}
		if (mAgent.getRemainingBudget() < 0) {
			return false;
		}
		if (mCurrentState.getIndex() == mTaskSet.getSubtaskNum() && pAction.getType() == ActionType.NEXT) {
			return false;
		}
		return true;
	}
}