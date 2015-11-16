package pomdp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 状態管理クラス
 */
public class StateManager {
	// =================
	// Fields
	// =================
	private State mRootState;
	private Map<Integer, Set<State>> mStates; // index毎に分類

	// =================
	// Constructors
	// =================
	public StateManager(State pRootState) {
		mRootState = pRootState;
		mStates = new HashMap<Integer, Set<State>>();
		add(mRootState);
	}

	// =================
	// Getters & Setters
	// =================
	public State getRootState() {
		return mRootState;
	}

	public Map<Integer, Set<State>> getStatesWithIndex() {
		return mStates;
	}

	// =================
	// Methods
	// =================

	/**
	 * 状態を追加する
	 */
	public void add(State pState) {
		int index = pState.getIndex();

		// インデックスに対して初めての追加の場合は新規作成
		if (mStates.get(index) == null) {
			mStates.put(index, new HashSet<State>());
		}

		mStates.get(index).add(pState);
	}

	/**
	 * 存在判定
	 */
	public boolean contains(State pState) {
		if (mStates.containsKey(pState.getIndex())) {
			return mStates.get(pState.getIndex()).contains(pState);
		} else {
			return false;
		}
	}

	/**
	 * インデックスを指定して状態を取得
	 */
	public Set<State> getStates(int pIndex) {
		return mStates.get(pIndex);
	}

	/**
	 * 全状態を取得
	 */
	public Set<State> getStates() {
		Set<State> all = new HashSet<State>();
		for (Set<State> states : mStates.values()) {
			all.addAll(states);
		}
		return all;
	}

	/**
	 * インデックスと予算を指定して状態を取得
	 */
	public Set<State> getStates(int pIndex, int pBudget) {
		Set<State> states = new HashSet<State>();
		for (State s : getStates(pIndex)) {
			if (s.getBudget() == pBudget) {
				states.add(s);
			}
		}
		return states;
	}

	/**
	 * インデックスを指定して状態数を取得
	 */
	public int getSize(int pIndex) {
		return mStates.get(pIndex).size();
	}

	/**
	 * 全状態数を取得
	 */
	public int getSize() {
		int size = 0;
		for (Set<State> states : mStates.values()) {
			size += states.size();
		}
		return size;
	}

	@Override
	public String toString() {
		return "States(" + getSize() + ") = " + mStates;
	}

	/**
	 * 根ノードか判定する
	 */
	public boolean isRootState(State s) {
		return s.equals(mRootState);
	}

}
