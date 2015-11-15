package pomdp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 観測確率管理クラス
 */
public class ObservationManager {
	// =========================
	// Fields
	// =========================
	private Map<Observation, Double> mObservations;

	// =========================
	// Constructors
	// =========================
	public ObservationManager() {
		mObservations = new HashMap<Observation, Double>();
	}

	// =========================
	// Getters & Setters
	// =========================

	/**
	 * (Observation, Prob)の形で全観測確率を取得
	 */
	public Map<Observation, Double> getObservationsWithProb() {
		return mObservations;
	}

	/**
	 * 全観測値（の組み合わせ）の取得
	 */
	public Set<Observation> getObservations() {
		return mObservations.keySet();
	}

	// ==========================
	// Methods
	// ==========================

	/**
	 * 全観測確率数の取得
	 */
	public int getSize() {
		return mObservations.size();
	}

	/**
	 * 観測確率の追加
	 */
	public void put(Observation pObservation, double pProb) {
		mObservations.put(pObservation, pProb);
		// TODO: 同じ観測がある場合の処理
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mObservations == null) ? 0 : mObservations.hashCode());
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
		ObservationManager other = (ObservationManager) obj;
		if (mObservations == null) {
			if (other.mObservations != null)
				return false;
		} else if (!mObservations.equals(other.mObservations))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Observations(" + getSize() + ") = " + mObservations;
	}
}
