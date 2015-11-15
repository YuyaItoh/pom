package pomdp;

public class Transition {
	// =====================
	// Enumerates
	// =====================
	public enum TransitionType {
		// GOAL: ワークフロー終了による遷移
		// BUNKRUPT: 予算切れによる遷移
		// TRANSITION: 途中遷移
		GOAL, BUNKRUPT, TRANSITION
	}

	// =====================
	// Fields
	// =====================
	State mPrevState;
	Action mAction;
	State mNextState;
	TransitionType mType;
	double mReward;

	// =====================
	// Constructors
	// =====================
	public Transition(State pPrevState, Action pAction, State pNextState, TransitionType pType) {
		mPrevState = pPrevState;
		mAction = pAction;
		mNextState = pNextState;
		mType = pType;
		calcReward();
	}

	// =====================
	// Methods
	// =====================
	public double calcReward() {
		// TODO: 報酬関数を作る事
		return 1.0;
	}

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
