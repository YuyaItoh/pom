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

	private final double PENALTY = -1000.0;

	// =====================
	// Constructors
	// =====================
	public Transition(State pPrevState, Action pAction, State pNextState, TransitionType pType) {
		mPrevState = pPrevState;
		mAction = pAction;
		mNextState = pNextState;
		mType = pType;
		mReward = Utility.round(calcReward(pType, pPrevState.getQuality()), 1);
	}

	/**
	 * 品質とTransitionTypeから報酬を作成
	 */
	public double calcReward(TransitionType pType, double pQuality) {
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
			reward = 1000 * ((Math.pow(Math.E, pQuality) - 1) / (Math.E - 1));
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
		long temp;
		temp = Double.doubleToLongBits(mReward);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((mType == null) ? 0 : mType.hashCode());
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
		if (Double.doubleToLongBits(mReward) != Double.doubleToLongBits(other.mReward))
			return false;
		if (mType != other.mType)
			return false;
		return true;
	}
}
