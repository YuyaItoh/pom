package pomdp;

import java.math.BigDecimal;

/**
 * 状態クラス
 * 
 * @author y-itoh
 *
 */
public class State {
	// ======================
	// Fields
	// ======================
	private int mIndex;
	private double mQuality;
	private int mBudget;

	// ======================
	// Constructors
	// ======================
	public State(int pIndex, double pQuality, int pBudget) {
		mIndex = pIndex;
		mQuality = pQuality;
		mBudget = pBudget;

		// 2桁で品質を保持する
		BigDecimal bd = new BigDecimal(mQuality);
		mQuality = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	// コピーの作成
	public State(State pState) {
		mIndex = pState.mIndex;
		mQuality = pState.mQuality;
		mBudget = pState.mBudget;
	}

	// ======================
	// Getters & Setters
	// ======================
	public int getIndex() {
		return mIndex;
	}

	public double getQuality() {
		return mQuality;
	}

	public int getBudget() {
		return mBudget;
	}

	// ======================
	// Methods
	// ======================

	/**
	 * 予算を減らす
	 */
	public void decreaseBudget(int pVal) {
		mBudget -= pVal;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (mBudget != other.mBudget)
			return false;
		if (mIndex != other.mIndex)
			return false;
		if (Double.doubleToLongBits(mQuality) != Double.doubleToLongBits(other.mQuality))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mBudget;
		result = prime * result + mIndex;
		long temp;
		temp = Double.doubleToLongBits(mQuality);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "S(" + mIndex + ", " + mQuality + ", " + mBudget + ")";
	}

	public String toName() {
		return Remover.removeDot(String.format("s%d_%.2f_%d", mIndex, mQuality, mBudget));
	}
}
