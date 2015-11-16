package pomdp;

public class Observation {
	// ==================
	// Constants
	// ==================
	public static double NONE = -1.0;

	// ==================
	// Fields
	// ==================
	private State mState;
	private Action mAction;
	private double mEvaluation; // {-1,0.2,0.4,0.6,0.8,1.0}

	// ==================
	// Constructors
	// ==================
	public Observation(Action pAction, State pState, double pEvaluation) {
		mAction = pAction;
		mState = pState;
		mEvaluation = Utility.round(pEvaluation, 2); // 品質は小数2位
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

	public double getObservation() {
		return mEvaluation;
	}

	// ==================
	// Methods
	// ==================

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAction == null) ? 0 : mAction.hashCode());
		long temp;
		temp = Double.doubleToLongBits(mEvaluation);
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
		Observation other = (Observation) obj;
		if (mAction == null) {
			if (other.mAction != null)
				return false;
		} else if (!mAction.equals(other.mAction))
			return false;
		if (Double.doubleToLongBits(mEvaluation) != Double.doubleToLongBits(other.mEvaluation))
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
		return "Z(" + mState.toString() + ", " + mAction.toString() + ", " + mEvaluation + ")";
	}

	public String toName() {
		String name = (mEvaluation > 0.0) ? "o" + Utility.removeDot(Double.toString(mEvaluation)) : "NONE";
		return name;
	}

}
