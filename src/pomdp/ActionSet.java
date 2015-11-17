package pomdp;

import java.util.ArrayList;
import java.util.List;

import pomdp.Action.ActionType;

/**
 * 行動集合クラス
 */
public class ActionSet {
	// =================
	// Fields
	// =================
	private List<Action> mActions;

	// =================
	// Constructors
	// =================
	public ActionSet() {
		mActions = new ArrayList<Action>();
	}

	// =================
	// Getters & Setters
	// =================
	public List<Action> getActions() {
		return mActions;
	}

	// =================
	// Methods
	// =================
	public void add(Action pAction) {
		// 重複は許さない
		if (!mActions.contains(pAction)) {
			mActions.add(pAction);
		}
	}

	/**
	 * NEXTアクション集合を取得
	 */
	public List<Action> getNextActions() {
		List<Action> actions = new ArrayList<Action>();
		for (Action a : mActions) {
			if (a.getType() == ActionType.NEXT) {
				actions.add(a);
			}
		}
		return actions;
	}

	/**
	 * CURRENTアクション集合を取得
	 */
	public List<Action> getCurrentActions() {
		List<Action> actions = new ArrayList<Action>();
		for (Action a : mActions) {
			if (a.getType() == ActionType.CURR) {
				actions.add(a);
			}
		}
		return actions;
	}

	@Override
	public String toString() {
		return "ActionSet (" + mActions + ")";
	}

}
