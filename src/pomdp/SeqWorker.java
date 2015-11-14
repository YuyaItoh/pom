package pomdp;

public class SeqWorker extends Worker {
	// ================
	// Constructors
	// ================
	public SeqWorker(double pAbility) {
		super(pAbility);
	}

	// ================
	// Methods
	// ================
	@Override
	public double solve(Subtask pSubTask, int pWage, double pPrevQuality) {
		// TODO: 品質関数の決定．現在は適当のやつだから
		double effort = mAbility * pWage;
		double quality = Math.pow((1 - pSubTask.mDifficulty), 1.0 / effort) * pPrevQuality;
		return quality;
	}
}
