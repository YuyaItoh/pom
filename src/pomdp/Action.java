package pomdp;

/**
 * 行動クラス
 * 
 * @author y-itoh
 *
 */
public abstract class Action {
	// =====================
	// Fields
	// =====================

	// =====================
	// Constructors
	// =====================
	public Action() {
	}

	// =====================
	// Methods
	// =====================
	public abstract boolean equals(Object obj);

	public abstract int hashCode();
}