package pomdp;

/**
 * 各時間ステップの遷移結果を保持
 */
public class Result {

	// =======================
	// Fields
	// =======================
	public State mPrevState;
	public Action mAction;
	public Worker mWorker;
	public State mNextState;

	// =======================
	// Constructors
	// =======================
	public Result(State pPrevState, Action pAction, Worker pWorker, State pNextState) {
		mPrevState = pPrevState;
		mAction = pAction;
		mWorker = pWorker;
		mNextState = pNextState;
	}

	// =======================
	// Methods
	// =======================

	@Override
	public String toString() {
		String str = "";
		str += "PrevState = " + mPrevState.toString() + "\n";
		str += "Action = " + mAction.toString() + "\n";
		str += "Worker = " + mWorker.toString() + "\n";
		str += "NextState = " + mNextState.toString() + "\n";
		return str;
	}

}
