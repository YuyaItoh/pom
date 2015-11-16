package pomdp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransitionManager {
	// ==================
	// Fields
	// ==================
	private Map<Transition, Double> mTransitions;

	// ==================
	// Constructors
	// ==================
	public TransitionManager() {
		mTransitions = new HashMap<Transition, Double>();
	}

	// ==================
	// Getters & Setters
	// ==================

	/**
	 * (状態遷移, 確率)のmapを取得
	 */
	public Map<Transition, Double> getTransitionsWithProb() {
		return mTransitions;
	}

	/**
	 * 全状態遷移の取得
	 */
	public Set<Transition> getTransitions() {
		return mTransitions.keySet();
	}

	// ==================
	// Methods
	// ==================
	/**
	 * 遷移情報の追加
	 */
	public void put(Transition pTransition, double pProb) {
		// 既に同じ遷移がある場合には，遷移確率を上げる
		double prob = mTransitions.containsKey(pTransition) ? mTransitions.get(pTransition) + pProb : pProb;

		// 確率は小数点3位まで
		prob = Utility.round(prob, 3);

		mTransitions.put(pTransition, prob);
	}

	/**
	 * 全遷移数を取得
	 */
	public int getSize() {
		return mTransitions.size();
	}

	@Override
	public String toString() {
		return "Transitions(" + getSize() + ") = " + mTransitions;
	}

}
