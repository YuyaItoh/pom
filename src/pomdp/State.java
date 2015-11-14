package pomdp;

/**
 * 状態クラス
 * 
 * @author y-itoh
 *
 */
public abstract class State {
	// ==================
	// Constructors
	// ==================
	public State() {

	}

	// ==================
	// abstract Methods
	// ==================
	public abstract boolean equals(Object obj);

	public abstract int hashCode();

}
