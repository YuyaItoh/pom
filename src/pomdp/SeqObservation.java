package pomdp;

/**
 * 観測値はリカード尺度
 * 
 * @author y-itoh
 *
 */
public class SeqObservation extends Observation {
	// ==================
	// Fields
	// ==================
	public int mObservation;

	// ==================
	// Constructors
	// ==================

	/**
	 * Observation(double)を小数点2桁に整理してから格納
	 * 
	 * @param pObservation
	 */
	public SeqObservation(Object pObservation) {
		super(pObservation);
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
		SeqObservation other = (SeqObservation) obj;
		if (mObservation != other.mObservation)
			return false;
		return true;
	}
}