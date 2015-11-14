package pomdp;

public abstract class Transition {
	// =====================
	// Fields
	// =====================
	State mPrevState;
	Action mAction;
	State mNextState;
	double mProb;
	double mReward;

	// =====================
	// Constructors
	// =====================
	public Transition(State pPrevState, Action pAction, State pNextState, double pProb) {
		mPrevState = pPrevState;
		mAction = pAction;
		mNextState = pNextState;
		mProb = pProb;
		calcReward();
	}

	// =====================
	// Abstract Methods
	// =====================
	protected abstract void calcReward();

	// =====================
	// Methods
	// =====================
	@Override
	public String toString() {
		return "T(" + mPrevState.toString() + ", " + mAction.toString() + ", " + mNextState.toString() + ") = " + mProb;
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
