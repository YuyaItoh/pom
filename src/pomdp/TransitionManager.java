package pomdp;

import java.util.HashSet;
import java.util.Set;

public class TransitionManager {
	// ==================
	// Fields
	// ==================
	public Set<Transition> mTransitions;

	// ==================
	// Constructors
	// ==================
	public TransitionManager() {
		mTransitions = new HashSet<Transition>();
	}

	// ==================
	// Methods
	// ==================

	/**
	 * 遷移情報の追加
	 */
	public void add(Transition t) {
		mTransitions.add(t);
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
