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
	public SeqState mRootState;
	public Map<Integer, Set<State>> mStates; // index毎に分類

	// =================
	// Constructors
	// =================
	public StateManager(SeqState pRootState) {
		mRootState = pRootState;
		mStates = new HashMap<Integer, Set<State>>();
		add(mRootState);
	}

	// =================
	// Methods
	// =================

	/**
	 * 状態を追加する
	 */
	public void add(SeqState pState) {
		int index = pState.mIndex;

		// インデックスに対して初めての追加の場合は新規作成
		if (mStates.get(index) == null) {
			mStates.put(index, new HashSet<State>());
		}

		mStates.get(index).add(pState);
	}

	/**
	 * 存在判定
	 */
	public boolean contains(SeqState pState) {
		return mStates.get(pState.mIndex).contains(pState);
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

}
