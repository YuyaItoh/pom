package pomdp;

/**
 * ワーカクラス．各ワーカは異なる能力を持つ
 * 
 * @author y-itoh
 *
 */
public class Worker {
	// ====================
	// Fields
	// ====================
	public double mAbility;

	// ====================
	// Constructors
	// ====================
	public Worker(double pAbility) {
		mAbility = pAbility;
	}

	// ================
	// Methods
	// ================
	public double solve(Subtask pSubTask, int pWage, double pPrevQuality) {
		// TODO: 品質関数の決定．現在は適当のやつだから
		double effort = mAbility * pWage;
		double quality = Math.pow((1 - pSubTask.mDifficulty), 1.0 / effort) * pPrevQuality;
		return quality;
	}

	@Override
	public String toString() {
		return "W(" + mAbility + ")";
	}

}
