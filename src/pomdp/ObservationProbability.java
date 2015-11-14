package pomdp;

public class ObservationProbability {
	// ================
	// Fields
	// ================
	public State mState;
	public Action mAction;
	public Observation mObservation;
	public double mProb;

	// ================
	// Constructors
	// ================
	public ObservationProbability(Action pAction, State pState, Observation pObservation, double pProb) {
		mAction = pAction;
		mState = pState;
		mObservation = pObservation;
		mProb = pProb;
	}

	// ================
	// Methods
	// ================
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAction == null) ? 0 : mAction.hashCode());
		result = prime * result + ((mObservation == null) ? 0 : mObservation.hashCode());
		long temp;
		temp = Double.doubleToLongBits(mProb);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		ObservationProbability other = (ObservationProbability) obj;
		if (mAction == null) {
			if (other.mAction != null)
				return false;
		} else if (!mAction.equals(other.mAction))
			return false;
		if (mObservation == null) {
			if (other.mObservation != null)
				return false;
		} else if (!mObservation.equals(other.mObservation))
			return false;
		if (Double.doubleToLongBits(mProb) != Double.doubleToLongBits(other.mProb))
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
		return "Z(" + mState.toString() + ", " + mAction.toString() + ", " + mObservation.toString() + ") = " + mProb;
	}

}
