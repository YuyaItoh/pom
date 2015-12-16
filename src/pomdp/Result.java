package pomdp;

/**
 * 各時間ステップの遷移結果を保持
 */
public class Result {

	// =======================
	// Fields
	// =======================
	private State mPrevState;
	private Action mAction;
	private Worker mWorker;
	private State mNextState;
	private boolean mIsEnd;

	// =======================
	// Constructors
	// =======================

	/**
	 * 終了時のログ
	 */
	public Result() {
		mIsEnd = true;
	}

	public Result(State pPrevState, Action pAction, Worker pWorker, State pNextState) {
		mPrevState = pPrevState;
		mAction = pAction;
		mNextState = pNextState;
		mWorker = pWorker;
		mIsEnd = false;
	}

	// =======================
	// Methods
	// =======================

	@Override
	public String toString() {
		String str = "";

		if (!mIsEnd) {
			str += "PrevState = " + mPrevState.toString() + "\n";
			str += "Action = " + mAction.toString() + "\n";
			str += "Worker = " + mWorker.toString() + "\n";
			str += "NextState = " + mNextState.toString() + "\n";
		}
		return str;
	}

	/**
	 * CSV形式で出力する出力形式はindex, worker_ability, wage, quality, budgetの順
	 */
	public String toCsv() {
		String str = "";
		if (!mIsEnd) {
			str += String.format("%d, %.1f, %d, %.2f, %d", mNextState.getIndex(), mWorker.getAbility(),
					mAction.getWage(), mNextState.getQuality(), mNextState.getBudget());
		}
		return str;
	}
}
