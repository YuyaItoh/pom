package pomdp;

public class Transition {
	// =====================
	// Enumerates
	// =====================
	public enum TransitionType {
		// GOAL: ワークフロー終了による遷移
		// FAIL: ワークフロー失敗の遷移
		// TRANSITION: 途中遷移
		GOAL, FAIL, TRANSITION
	}

	// =====================
	// Fields
	// =====================
	private State mPrevState;
	private Action mAction;
	private State mNextState;
	private TransitionType mType;
	private double mReward; // 報酬は小数点1位まで

	private final double PENALTY = -100.0; // FIXME: 予算切れによるペナルティの妥当性
	private final double THRESHOLD = 0.3; // FIXME: 最低保証品質の妥当性
	private final int REMAINING_MAX_BUDGET = 5; // 予算が5以上だとゴールしても報酬を得られない

	// =====================
	// Constructors
	// =====================
	public Transition(State pPrevState, Action pAction, State pNextState, TransitionType pTransitionType) {
		mPrevState = pPrevState;
		mAction = pAction;
		mNextState = pNextState;
		mType = pTransitionType;
		mReward = Utility.round(calcReward(pTransitionType, pPrevState.getQuality()), 1);
	}

	public Transition(State pPrevState, Action pAction, State pNextState) {
		mPrevState = pPrevState;
		mAction = pAction;
		mNextState = pNextState;
		mReward = 0.0;
	}

	/**
	 * 品質とTransitionTypeから報酬を作成
	 */
	public double calcReward(TransitionType pType, double pQuality) {
		// TODO: より良い品質関数の作成

		// *********************
		// TRANSITIONなら0
		// BUNKRUPTならPENALTY
		// GOALなら報酬関数R(q)
		// *********************
		double reward;
		switch (pType) {
		case TRANSITION:
			reward = 0.0;
			break;
		case FAIL:
			reward = PENALTY;
			break;
		case GOAL:
			// 品質がTHRESHOLD未満の報酬は0
			// 予算が多く残った状態でゴールしても報酬は0
			if (pQuality < THRESHOLD || mPrevState.getBudget() >= REMAINING_MAX_BUDGET) {
				reward = 0.0;
			} else {
				reward = 1000 * ((Math.pow(Math.E, pQuality) - 1) / (Math.E - 1));
			}
			break;
		default:
			reward = 0.0;
		}

		return reward;

	}

	// =====================
	// Getters & Setters
	// =====================
	public State getPrevState() {
		return mPrevState;
	}

	public Action getAction() {
		return mAction;
	}

	public State getNextState() {
		return mNextState;
	}

	public TransitionType getType() {
		return mType;
	}

	public double getReward() {
		return mReward;
	}

	// =====================
	// Methods
	// =====================

	@Override
	public String toString() {
		return "T(" + mPrevState.toString() + ", " + mAction.toString() + ", " + mNextState.toString() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAction == null) ? 0 : mAction.hashCode());
		result = prime * result + ((mNextState == null) ? 0 : mNextState.hashCode());
		result = prime * result + ((mPrevState == null) ? 0 : mPrevState.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transition other = (Transition) obj;
		if (mAction == null) {
			if (other.mAction != null)
				return false;
		} else if (!mAction.equals(other.mAction))
			return false;
		if (mNextState == null) {
			if (other.mNextState != null)
				return false;
		} else if (!mNextState.equals(other.mNextState))
			return false;
		if (mPrevState == null) {
			if (other.mPrevState != null)
				return false;
		} else if (!mPrevState.equals(other.mPrevState))
			return false;
		return true;
	}
}
