package pomdp;

/**
 * 指定したサブタスクに対する行動
 * 
 * @author y-itoh
 *
 */
public class SeqAction extends Action {
	// =====================
	// ENUMERATES
	// =====================
	public enum ActionType {
		// eval: 現タスクの品質確認
		// next: 次タスクの依頼
		// current: 現タスクの再依頼
		EVAL, NEXT, CURRENT
	}

	// ===================
	// Fields
	// ===================
	public ActionType mType; // アクションタイプ
	public int mWage; // 支払い賃金

	// ===================
	// Constructors
	// ===================
	public SeqAction(ActionType pType, int pWage) {
		super();
		mType = pType;
		mWage = pWage;
	}

	// ===================
	// Methods
	// ===================

	@Override
	public String toString() {
		return "A(" + mType + ", w:" + mWage + ")";
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
		SeqAction other = (SeqAction) obj;
		if (mType != other.mType)
			return false;
		if (mWage != other.mWage)
			return false;
		return true;
	}

}
