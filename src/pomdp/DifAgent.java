package pomdp;

import pomdp.Action.ActionType;

/**
 * タスク難易度に応じた報酬額を決定するエージェント
 *
 */
public class DifAgent extends Agent {
	// =========================
	// Fields
	// =========================
	private int mIterationNum; // 各サブタスクに対する繰り返し数
	private int mCurrentIteration; // 現在の繰り返し数
	private boolean mIsFirstAction;
	private int[] mWages; // 各ワーカに支払う賃金
	private int mWorkerIdx; // 実行ワーカ

	// =========================
	// Constructors
	// =========================
	public DifAgent(Environment pEnv, AgentType pAgentType, int pIterationNum) {
		super(pEnv, pAgentType);
		mIterationNum = pIterationNum;
		mCurrentIteration = 0;
		mWorkerIdx = 0;
		mIsFirstAction = true;
		initWages();
	}

	/**
	 * 各サブタスクに支払う賃金を決定する
	 */
	private void initWages() {
		int workersNum = mTaskSet.getSubtaskNum() * mIterationNum;
		int remainingBudget = mBudget;
		mWages = new int[workersNum];

		// 正規化用の難易度合計の計算
		double difSum = 0.0;
		for (int i = 1; i <= mTaskSet.getSubtaskNum(); i++) {
			difSum += mTaskSet.getSubtask(i).getDifficulty();
		}

		// 難易度に応じて割り当てる
		for (int i = 1; i <= mTaskSet.getSubtaskNum(); i++) {
			double difficulty = mTaskSet.getSubtask(i).getDifficulty() / difSum;
			int wage = (int) (mBudget * difficulty) / mIterationNum;

			for (int j = 0; j < mIterationNum; j++) {
				int idx = (i - 1) * mIterationNum + j;
				mWages[idx] = wage;
				remainingBudget -= mWages[idx];

			}
		}

		// 残り予算を順に割り当てる
		for (int i = 0; remainingBudget > 0; i++) {
			mWages[i] += 1;
			remainingBudget -= 1;
		}
	}

	// =========================
	// Methods
	// =========================

	@Override
	public Action selectAction() {
		Action action;
		// 現在の状況によって異なる行動を取る
		if (mIsFirstAction || (mCurrentIteration >= mIterationNum)) {
			action = selectNextAction();
			mIsFirstAction = false;
		} else {
			action = selectCurrAction();
		}
		// 予算の更新
		mRemainingBudget -= action.getWage();
		// ワーカidxの更新
		mWorkerIdx++;

		return action;
	}

	@Override
	public void update(Object pObject) {
		// DifAgentでは特に何もしない
	}

	// =========================
	// Private Methods
	// =========================

	/**
	 * CURRアクションを選択する
	 */
	private Action selectCurrAction() {
		// 反復回数のインクリメント
		mCurrentIteration++;
		return new Action(ActionType.CURR, mWages[mWorkerIdx]);
	}

	/**
	 * NEXTアクションを選択する
	 */
	private Action selectNextAction() {
		// サブタスクindexをインクリメント
		mCurrentTaskIndex++;

		// 反復回数を1にリセット
		mCurrentIteration = 1;

		// 全サブタスクが終了した場合は-1の報酬額を払うことで終了合図
		Action action;
		if (mCurrentTaskIndex > mTaskSet.getSubtaskNum()) {
			action = new Action(ActionType.NEXT, -1);
		} else {
			action = new Action(ActionType.NEXT, mWages[mWorkerIdx]);
		}

		return action;
	}
}
