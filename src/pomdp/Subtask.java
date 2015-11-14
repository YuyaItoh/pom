package pomdp;

/**
 * サブタスクという名のタスク
 * 
 * @author y-itoh
 *
 */
public class Subtask {
	// ==================
	// Fields
	// ==================
	public double mDifficulty; // 難易度
	public int mBaseWage; // ベース賃金

	// ==================
	// Constructors
	// ==================
	public Subtask(double pDifficulty) {
		mDifficulty = pDifficulty;
	}

	public Subtask(double pDifficulty, int pBaseWage) {
		mDifficulty = pDifficulty;
		mBaseWage = pBaseWage;
	}

	// ==================
	// Methods
	// ==================
	@Override
	public String toString() {
		return "SubTask(w:" + mBaseWage + ", d=" + mDifficulty + ")";
	}

}
