package pomdp;

/**
 * 観測値クラス
 * 
 * @author y-itoh
 *
 */
public abstract class Observation {
	Object mObservation;

	public Observation(Object pObservation) {
		mObservation = pObservation;
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
