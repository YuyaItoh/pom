package pomdp;

import java.math.BigDecimal;

/**
 * 状態は(index, quality, remaining budget)で定義
 * 
 * @author y-itoh
 *
 */
public class SeqState extends State {
	// ======================
	// Fields
	// ======================
	public int mIndex;
	public double mQuality;
	public int mBudget;

	// ======================
	// Constructors
	// ======================
	public SeqState(int pIndex, double pQuality, int pBudget) {
		super();
		mIndex = pIndex;
		mQuality = pQuality;
		mBudget = pBudget;

		// 2桁で品質を保持する
		BigDecimal bd = new BigDecimal(mQuality);
		mQuality = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	// コピーの作成
	public SeqState(SeqState pState) {
		mIndex = pState.mIndex;
		mQuality = pState.mQuality;
		mBudget = pState.mBudget;
	}

	// =====================
	// Methods
	// =====================
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeqState other = (SeqState) obj;
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

}
