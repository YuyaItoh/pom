package pomdp;

/**
 * ワーカクラス．各ワーカは異なる能力を持つ
 * 
 * @author y-itoh
 *
 */
public abstract class Worker {
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

	// ====================
	// Methods
	// ====================

	// サブタスクを解く
	public abstract double solve(Subtask pSubTask, int mWage, double pPrevQuality);

	@Override
	public String toString() {
		return "W(" + mAbility + ")";
	}
	
	
}
