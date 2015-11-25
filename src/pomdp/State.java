package pomdp;

/**
 * 状態クラス
 */
public class State {
	// ======================
	// Fields
	// ======================
	private int mIndex;
	private double mQuality;
	private int mBudget;
	private double mPrevStateQuality; // 前サブタスクの品質

	// ======================
	// Constructors
	// ======================
	public State(int pIndex, double pQuality, int pBudget, double pPrevStateQuality) {
		mIndex = pIndex;
		mBudget = pBudget;
		mQuality = Utility.round(pQuality, 2); // 品質は小数点2位
		mPrevStateQuality = Utility.round(pPrevStateQuality, 2);
	}

	// コピーの作成
	public State(State pState) {
		mIndex = pState.mIndex;
		mQuality = pState.mQuality;
		mBudget = pState.mBudget;
		mPrevStateQuality = pState.mPrevStateQuality;
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

	public double getPrevStateQuality() {
		return mPrevStateQuality;
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
		if (Double.doubleToLongBits(mPrevStateQuality) != Double.doubleToLongBits(other.mPrevStateQuality))
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
		temp = Double.doubleToLongBits(mPrevStateQuality);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(mQuality);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "S(i:" + mIndex + ", q:" + mQuality + ", b:" + mBudget + ", pq:" + mPrevStateQuality + ")";
	}

	public String toName() {
		return Utility.removeDot(String.format("s%d_%.2f_%d_%.2f", mIndex, mQuality, mBudget, mPrevStateQuality));
	}
}
