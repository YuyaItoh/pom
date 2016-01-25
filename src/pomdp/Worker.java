package pomdp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ワーカクラス．各ワーカは異なる能力を持つ
 */
public class Worker {
	// ====================
	// Fields
	// ====================
	private double mAbility;
	private final double EFFORT_WEIGHT = 1.5; // 努力値の重み

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
		double effort = mAbility * pWage * EFFORT_WEIGHT;
		double quality = Math.pow((1 - pSubTask.getDifficulty()), 1.0 / effort) * pPrevQuality;
		return quality;
	}

	/**
	 * サブタスクの品質評価を行う．mEvaluationsに評価基準のリカード尺度を指定する
	 */
	public double evaluate(double pQuality, List<Double> pEvaluations) {
		Map<Double, Double> evalProbs = new LinkedHashMap<Double, Double>();
		double dSum = calcDensitySum(pQuality, pEvaluations);

		// 確率の計算
		double probSum = 1.0;
		Iterator<Double> it = pEvaluations.iterator();
		while (it.hasNext()) {
			double eval = (double) it.next();

			// NONEは無視
			if (eval == Observation.NONE) {
				continue;
			}

			double d = Utility.dnorm(eval, pQuality, ObservationManager.VAR);
			double prob = it.hasNext() ? Utility.roundDown(d / dSum, 3) : probSum;

			evalProbs.put(eval, prob); // 確率の計算
			probSum -= prob; // 確率和の減算
		}

		// 乱数を発生させて，評価値を取得する
		double rand = Math.random();
		for (Map.Entry<Double, Double> evalProb : evalProbs.entrySet()) {
			// randが0以下になった時の評価値を返す
			rand -= evalProb.getValue();

			if (rand <= 0) {
				return evalProb.getKey();
			}
		}

		return Observation.NONE;
	}

	@Override
	public String toString() {
		return "W(" + mAbility + ")";
	}

	// ==========================
	// Private Methods
	// ==========================

	/**
	 * 正規化を行うための確率密度和を計算する
	 */
	private double calcDensitySum(double pQuality, List<Double> pEvaluations) {
		// 密度総和の取得
		double dSum = 0.0;
		for (double eval : pEvaluations) {
			// NONEは無視する
			if (eval == Observation.NONE) {
				continue;
			}
			dSum += Utility.dnorm(eval, pQuality, ObservationManager.VAR);
		}
		return dSum;
	}

}
