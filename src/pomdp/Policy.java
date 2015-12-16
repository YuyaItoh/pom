package pomdp;

import java.util.ArrayList;
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
	public Policy() {
		planes = new ArrayList<Plane>();
	}

	// ============
	// Methods
	// ============
	@Override
	public String toString() {
		return "Policy[policyType=" + policyType + ", numPlanes=" + numPlanes + ", planes=\n" + planes + "\n]\n";
	}

	/**
	 * 平面の追加
	 */
	public void add(Plane pPlane) {
		this.planes.add(pPlane);
	}
}
