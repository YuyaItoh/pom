package pomdp;

import pomdp.Action.ActionType;

/**
 * 等分配エージェント．予め繰り返し数を決定しておき，全てのサブタスクに対して同賃金で同じ回数だけ依頼する
 * 
 */
public class EqualAgent extends Agent {
	// ========================
	// Fields
	// ========================
	private int mIterationNum; // 各サブタスクに対する繰り返し数
	private int mCurrentIteration; // 現在の繰り返し回数
	private boolean mIsFirstAction;
	private int[] mWages; // 各ワーカに支払う賃金
	private int mWorkerIdx; // 実行ワーカ

	// ========================
	// Constructors
	// ========================
	public EqualAgent(Environment pEnv, AgentType pAgentType, int pIterationNum) {
		super(pEnv, pAgentType);
		mCurrentIteration = 0;
		mIterationNum = pIterationNum;
		mWorkerIdx = 0;
		mIsFirstAction = true;

		initWages(); // ワーカへの賃金の初期化
	}

	private void initWages() {
		int workersNum = mTaskSet.getSubtaskNum() * mIterationNum;
		int remainingBudget = mBudget;
		mWages = new int[workersNum];

		// 各ワーカに均等に支払う
		for (int i = 0; i < workersNum; i++) {
			mWages[i] = (int) mBudget / workersNum;
			remainingBudget -= mWages[i];
		}

		// 残り予算を順に割当てる
		for (int i = 0; remainingBudget > 0; i++) {
			mWages[i] += 1;
			remainingBudget -= 1;
		}
	}

	// ========================
	// Methods
	// ========================
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
		// EqualAgentでは特に何もしない
		return;
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
