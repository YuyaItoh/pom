package pomdp;

/**
 * 観測値クラス
 * 
 * @author y-itoh
 *
 */
public class Observation {
	// ==================
	// Fields
	// ==================
	public int mObservation;

	// ==================
	// Constructors
	// ==================
	public Observation(int pObservation) {
		mObservation = pObservation;
	}

	// ==================
	// Methods
	// ==================

	@Override
	public String toString() {
		return "O(" + mObservation + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mObservation;
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
		if (mObservation != other.mObservation)
			return false;
		return true;
	}
}
