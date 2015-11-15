package pomdp;

import java.util.HashMap;
import java.util.Map;

public class TransitionManager {
	// ==================
	// Fields
	// ==================
	public Map<Transition, Double> mTransitions;

	// ==================
	// Constructors
	// ==================
	public TransitionManager() {
		mTransitions = new HashMap<Transition, Double>();
	}

	// ==================
	// Methods
	// ==================

	/**
	 * 遷移情報の追加
	 */
	public void put(Transition pTransition, double pProb) {
		// 既に同じ遷移がある場合には，遷移確率を上げる
		if (mTransitions.containsKey(pTransition)) {
			double beforeProb = mTransitions.get(pTransition);
			mTransitions.put(pTransition, beforeProb + pProb);
		} else {
			mTransitions.put(pTransition, pProb);
		}
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
