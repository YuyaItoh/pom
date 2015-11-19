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

	/**
	 * サブタスクを実行し，結果の品質を返す
	 */
	public double solve(Subtask pSubTask, int pWage, double pPrevQuality) {
		// TODO: 品質関数の決定．現在は適当のやつだから
		double effort = mAbility * pWage;
		double quality = Math.pow((1 - pSubTask.getDifficulty()), 1.0 / effort) * pPrevQuality;
		return quality;
	}

	/**
	 * サブタスクの品質評価を行い，評価値を返す．評価値は{0.2, 0.4, 0.6, 0.8, 1.0}の5通り
	 */
	public double evaluate(double pQuality) {
		// TODO: 実装する
		return 0.0;
	}

	@Override
	public String toString() {
		return "W(" + mAbility + ")";
	}

}
