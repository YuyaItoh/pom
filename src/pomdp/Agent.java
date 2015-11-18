package pomdp;

/**
 * エージェントの匿名クラス
 */
abstract class Agent {
	// ====================
	// Enums
	// ====================
	public enum AgentType {
		EQUAL, DIF, POMDP
	}

	// ====================
	// Fields
	// ====================
	protected AgentType mAgentType; // エージェントタイプ
	protected TaskSet mTaskSet; // タスク集合
	protected double mBudget; // 初期予算
	protected double mRemainedBudget; // 残り予算
	protected ActionSet mActionSet; // 行動集合
	protected int mCurrentTaskIndex; // 現在のタスクインデックス

	// ====================
	// Getters & Setters
	// ====================
	public AgentType getAgentType() {
		return mAgentType;
	}

	public TaskSet getTaskSet() {
		return mTaskSet;
	}

	public double getBudget() {
		return mBudget;
	}

	public double getRemainedBudget() {
		return mRemainedBudget;
	}

	public ActionSet getActionSet() {
		return mActionSet;
	}

	public int getCurrurentTaskIndex() {
		return mCurrentTaskIndex;
	}

	// ====================
	// Constructors
	// ====================

	/**
	 * 環境とエージェントタイプからエージェントを初期化する
	 */
	public Agent(Environment pEnv, AgentType pAgentType) {
		mAgentType = pAgentType;
		mBudget = pEnv.getBudget();
		mRemainedBudget = mBudget;
		mTaskSet = pEnv.getTaskSet();
		mActionSet = pEnv.getActionSet();
		mCurrentTaskIndex = 0;
	}

	// ====================
	// Methods
	// ====================

	/**
	 * 行動(アクションタイプ, 賃金)を決定する
	 */
	public abstract Action selectAction();

	/**
	 * エージェントに関する情報の更新
	 */
	public abstract void update(Object pObject);

	/**
	 * 最終タスクか判定する
	 */
	protected boolean isLastTask() {
		// FIXME: あってる？？
		return (mCurrentTaskIndex == mTaskSet.getSubtaskNum());
	}
}
