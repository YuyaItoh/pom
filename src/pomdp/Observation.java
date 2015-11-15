package pomdp;

public class Observation {
	// ==================
	// Fields
	// ==================
	private State mState;
	private Action mAction;
	private int mObservation;

	// ==================
	// Constructors
	// ==================
	public Observation(Action pAction, State pState, int pObservation) {
		mAction = pAction;
		mState = pState;
		mObservation = pObservation;
	}

	// ==================
	// Getters & Setters
	// ==================
	public State getState() {
		return mState;
	}

	public Action getAction() {
		return mAction;
	}

	public int getObservation() {
		return mObservation;
	}

	// ==================
	// Methods
	// ==================

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAction == null) ? 0 : mAction.hashCode());
		result = prime * result + mObservation;
		result = prime * result + ((mState == null) ? 0 : mState.hashCode());
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
		Observation other = (Observation) obj;
		if (mAction == null) {
			if (other.mAction != null)
				return false;
		} else if (!mAction.equals(other.mAction))
			return false;
		if (mObservation != other.mObservation)
			return false;
		if (mState == null) {
			if (other.mState != null)
				return false;
		} else if (!mState.equals(other.mState))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Z(" + mState.toString() + ", " + mAction.toString() + ", " + mObservation + ")";
	}

	public String toName() {
		return "o" + mObservation;
	}

}
