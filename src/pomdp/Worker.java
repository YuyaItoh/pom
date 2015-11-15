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
	private double mAbility;

	// ====================
	// Constructors
	// ====================
	public Worker(double pAbility) {
		mAbility = pAbility;
	}

	// ====================
	// Getters & Setters
	// ====================
	public double getAbility() {
		return mAbility;
	}

	// ====================
	// Methods
	// ====================
	public double solve(Subtask pSubTask, int pWage, double pPrevQuality) {
		// TODO: 品質関数の決定．現在は適当のやつだから
		double effort = mAbility * pWage;
		double quality = Math.pow((1 - pSubTask.getDifficulty()), 1.0 / effort) * pPrevQuality;
		return quality;
	}

	@Override
	public String toString() {
		return "W(" + mAbility + ")";
	}

}
