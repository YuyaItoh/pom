package pomdp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 観測確率管理クラス
 */
public class ObservationManager {
	// =========================
	// Fields
	// =========================
	public static final double VAR = 0.01; // TODO: 妥当な分散の決定
	private Map<Observation, Double> mObservations;
	private List<Double> mEvaluations; // ワーカの評価値

	// =========================
	// Constructors
	// =========================
	public ObservationManager() {
		mObservations = new LinkedHashMap<Observation, Double>();

		// 観測値の決定
		mEvaluations = new ArrayList<Double>();
		mEvaluations.add(Observation.NONE);
		mEvaluations.add(0.2);
		mEvaluations.add(0.4);
		mEvaluations.add(0.6);
		mEvaluations.add(0.8);
		mEvaluations.add(1.0);
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

	/**
	 * 全評価値（観測値）の取得
	 */
	public List<Double> getEvaluations() {
		return mEvaluations;
	}

	// ==========================
	// Public Methods
	// ==========================
	/**
	 * 観測確率の計算
	 */
	public void calcObservations(StateManager pStateManager, ActionSet pActionSet) {
		for (Action action : pActionSet.getActions()) {
			for (State state : pStateManager.getStates()) {
				switch (action.getType()) {
				case CURR: // 観測値は常にNONE
					put(new Observation(action, state, Observation.NONE), 1.0);
					break;
				case NEXT: // 観測値は常にNONE
					put(new Observation(action, state, Observation.NONE), 1.0);
					break;
				case EVAL: // 観測値を取得
					calcEval(action, state);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * 観測値の追加
	 */
	public void put(Observation pObservation, double pProb) {
		mObservations.put(pObservation, pProb);
	}

	/**
	 * 全観測確率数の取得
	 */
	public int getSize() {
		return mObservations.size();
	}

	/**
	 * 指定した観測組み合わせの確率を取得
	 */
	public double getObservationProbability(Action pAction, State pState, double pEvaluation) {
		Observation o = new Observation(pAction, pState, pEvaluation);
		if (mObservations.containsKey(o)) {
			return mObservations.get(o);
		} else {
			// ないものは全て0を返す
			System.err.println("There is no such a Observation --" + o.toString());
			return 0.0;
		}
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

	/**
	 * 観測値（評価値）をStringとしてpomdp書式にする
	 */
	public String toName() {
		String str = "";
		for (double eval : mEvaluations) {
			String name = (eval > 0.0) ? "o" + Utility.removeDot(Double.toString(eval)) : "NONE";
			str += (name + " ");
		}
		return str;
	}

	// ==========================
	// Private Methods
	// ==========================
	/**
	 * EVALアクションによる観測確率の計算
	 */
	private void calcEval(Action pAction, State pState) {
		// ********************************************************
		// EVALアクションでは，真状態の品質を平均として正規分布に従う
		// 真状態の品質が0.6なら，評価値が0.6になる確率が一番高い
		// ********************************************************

		// 密度総和の取得
		double dSum = 0.0;
		for (double eval : mEvaluations) {
			// NONEは無視する
			if (eval == Observation.NONE) {
				continue;
			}
			dSum += Utility.dnorm(eval, pState.getQuality(), VAR);
		}

		// 観測確率の計算
		double probSum = 1.0;

		Iterator<Double> it = mEvaluations.iterator();
		while (it.hasNext()) {
			double eval = (double) it.next();

			// NONEは無視する
			if (eval == Observation.NONE) {
				continue;
			}

			Observation o = new Observation(pAction, pState, eval);

			// *******************************************************
			// + 確率というpieを取り合っていくことで確率和を1に決定する
			// + 最後の要素は残りのpieを全て取る
			// + 途中でpieが無くならないように，四捨五入ではなく切り捨て
			// *******************************************************
			double d = Utility.dnorm(eval, pState.getQuality(), VAR);
			double prob = it.hasNext() ? Utility.roundDown(d / dSum, 3) : probSum;
			put(o, prob);

			// 確率和の減算
			probSum -= prob;
		}
	}
}
