package pomdp;

import java.util.HashSet;
import java.util.Set;

import pomdp.SeqAction.ActionType;

/**
 * 行動集合クラス
 */
public class ActionSet {
	// =================
	// Fields
	// =================
	public Set<SeqAction> mActions;

	// =================
	// Constructors
	// =================
	public ActionSet() {
		mActions = new HashSet<SeqAction>();
	}

	// =================
	// Methods
	// =================
	public void add(SeqAction pAction) {
		mActions.add(pAction);
	}

	/**
	 * NEXTアクション集合を取得
	 */
	public Set<SeqAction> getNextActions() {
		Set<SeqAction> actions = new HashSet<SeqAction>();
		for (SeqAction a : mActions) {
			if (a.mType == ActionType.NEXT) {
				actions.add(a);
			}
		}
		return actions;
	}

	/**
	 * CURRENTアクション集合を取得
	 */
	public Set<SeqAction> getCurrentActions() {
		Set<SeqAction> actions = new HashSet<SeqAction>();
		for (SeqAction a : mActions) {
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
