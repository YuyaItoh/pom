package pomdp;

import java.util.Arrays;

/**
 * 超平面クラス．直線（ベクトル）とも言える
 */
public class Plane {
	// ==============
	// Fields
	// ==============
	public int action; // 対応する行動
	public int numEntries; // non-zeroの要素数（使わない）
	public double[] entries; // ベクトル

	// ==============
	// Constructors
	// ==============
	public Plane(int pAction, double[] pEntries) {
		action = pAction;
		entries = pEntries;
	}

	// ===============
	// Methods
	// ===============
	@Override
	public String toString() {
		return "\n\tPlane[action=" + action + ", numEntries=" + numEntries + ", entries=" + Arrays.toString(entries)
				+ "]";
	}

}
