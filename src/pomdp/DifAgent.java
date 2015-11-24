package pomdp;

import java.util.LinkedHashMap;
import java.util.Map;

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
	private Map<Integer, Integer> mWages; // 各サブタスクに支払う賃金
	private int mCurrentIteration; // 現在の繰り返し数
	private boolean mIsFirstAction;

	// =========================
	// Constructors
	// =========================
	public DifAgent(Environment pEnv, AgentType pAgentType, int pIterationNum) {
		super(pEnv, pAgentType);
		mIterationNum = pIterationNum;
		mCurrentIteration = 0;
		mWages = new LinkedHashMap<Integer, Integer>();
		mIsFirstAction = true;
		calcWages();
	}

	/**
	 * 各サブタスクに支払う賃金を決定する
	 */
	private void calcWages() {
		// FIXME: intによる予算の余りの調整

		// 正規化用の難易度合計の計算
		double difSum = 0.0;
		for (int i = 1; i <= mTaskSet.getSubtaskNum(); i++) {
			difSum += mTaskSet.getSubtask(i).getDifficulty();
		}

		// 賃金の設定
		for (int i = 1; i <= mTaskSet.getSubtaskNum(); i++) {
			double difficulty = mTaskSet.getSubtask(i).getDifficulty() / difSum;
			int wage = (int) (mBudget * difficulty) / mIterationNum;
			mWages.put(i, wage);
		}
	}

	// =========================
	// Methods
	// =========================

	@Override
	public Action selectAction() {
		Action action;
		// FIXME: シミュレーションを実行するとぬるぽが発生するので修正すること！！
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
		return new Action(ActionType.CURR, mWages.get(mCurrentTaskIndex));
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
		if (mWages.containsKey(mCurrentTaskIndex)) {
			action = new Action(ActionType.NEXT, mWages.get(mCurrentTaskIndex));
		} else {
			action = new Action(ActionType.NEXT, -1);
		}

		return action;
	}
}
