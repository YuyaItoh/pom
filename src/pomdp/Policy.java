package pomdp;

import java.util.List;

/**
 * 方策クラス．信念から行動の写像
 * 
 * @author y-itoh
 *
 */
public class Policy {

	// ============
	// Fields
	// ============
	public String policyType;
	public String numPlanes;
	public List<Plane> planes;

	// ============
	// Constructors
	// ============

	// ============
	// Methods
	// ============
	@Override
	public String toString() {
		return "Policy[policyType=" + policyType + ", numPlanes=" + numPlanes + ", planes=\n" + planes + "\n]\n";
	}
}
