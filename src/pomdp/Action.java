package pomdp;

/**
 * 行動クラス
 * 
 * @author y-itoh
 *
 */
public class Action {
	// =====================
	// ENUMERATES
	// =====================
	public enum ActionType {
		// eval: 現タスクの品質確認
		// next: 次タスクの依頼
		// current: 現タスクの再依頼
		EVAL, NEXT, CURR
	}

	// ===================
	// Fields
	// ===================
	private ActionType mType; // アクションタイプ
	private int mWage; // 支払い賃金

	// ===================
	// Constructors
	// ===================
	public Action(ActionType pType, int pWage) {
		mType = pType;
		mWage = pWage;
	}

	// ===================
	// Getters & Setters
	// ===================
	public ActionType getType() {
		return mType;
	}

	public int getWage() {
		return mWage;
	}

	// ===================
	// Methods
	// ===================

	@Override
	public String toString() {
		return "A(" + mType + ", w:" + mWage + ")";
	}

	public String toName() {
		return Utility.removeDot(String.format("%s_%d", mType.toString(), mWage));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mType == null) ? 0 : mType.hashCode());
		result = prime * result + mWage;
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
		Action other = (Action) obj;
		if (mType != other.mType)
			return false;
		if (mWage != other.mWage)
			return false;
		return true;
	}
}