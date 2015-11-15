package pomdp;

import java.util.HashSet;
import java.util.Set;

import pomdp.Action.ActionType;

/**
 * 行動集合クラス
 */
public class ActionSet {
	// =================
	// Fields
	// =================
	public Set<Action> mActions;

	// =================
	// Constructors
	// =================
	public ActionSet() {
		mActions = new HashSet<Action>();
	}

	// =================
	// Methods
	// =================
	public void add(Action pAction) {
		mActions.add(pAction);
	}

	/**
	 * NEXTアクション集合を取得
	 */
	public Set<Action> getNextActions() {
		Set<Action> actions = new HashSet<Action>();
		for (Action a : mActions) {
			if (a.mType == ActionType.NEXT) {
				actions.add(a);
			}
		}
		return actions;
	}

	/**
	 * CURRENTアクション集合を取得
	 */
	public Set<Action> getCurrentActions() {
		Set<Action> actions = new HashSet<Action>();
		for (Action a : mActions) {
			if (a.mType == ActionType.CURRENT) {
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
