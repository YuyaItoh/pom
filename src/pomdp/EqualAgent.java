package pomdp;

import pomdp.Action.ActionType;

/**
 * 等分配エージェント．予め繰り返し数を決定しておき，全てのサブタスクに対して同賃金で同じ回数だけ依頼する
 * 
 */
public class EqualAgent extends Agent {
	private int mIterationNum; // 各サブタスクに対する繰り返し数
	private int mCurrentIteration; // 現在の繰り返し回数
	private int mWage; // 各ワーカに支払う賃金
	private boolean mIsFirstAction;

	public EqualAgent(Environment pEnv, AgentType pAgentType, int pIterationNum) {
		super(pEnv, pAgentType);
		mCurrentIteration = 0;
		mIterationNum = pIterationNum;
		mIsFirstAction = true;

		// ワーカへの賃金は予算を（繰り返し数 * サブタスク数）で割った値
		// FIXME: 切り捨てになってるから予算残るけど．．．どうしよう？
		mWage = (int) mBudget / (mTaskSet.getSubtaskNum() * mIterationNum);
	}

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
		return new Action(ActionType.CURR, mWage);
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
			action = new Action(ActionType.NEXT, mWage);
		}

		return action;
	}
}
