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
	private double mDifficulty; // 難易度
	private int mBaseWage; // ベース賃金

	// ==================
	// Getters & Setters
	// ==================
	public double getDifficulty() {
		return mDifficulty;
	}

	public int getBaseWage() {
		return mBaseWage;
	}

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
