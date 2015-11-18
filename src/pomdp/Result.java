package pomdp;

/**
 * 各時間ステップ毎の結果（アクション，状態遷移 品質，残り予算）を返す
 * 
 * @author y-itoh
 *
 */
public class Result {
	public double mRemainedBudget;
	public String mAction;
	public String mStartState;
	public String mEndState;
	public String mWorker;
	public double mReward;
	public int mTaskIdx;

	public Result(double budget, String action, String worker, String startState, String endState, double reward,
			int taskIdx) {
		mRemainedBudget = budget;
		mAction = action;
		mStartState = startState;
		mWorker = worker;
		mEndState = endState;
		mReward = reward;
		mTaskIdx = taskIdx;
	}

	public Result(double budget, String action, String worker, double reward, int taskIdx) {
		mRemainedBudget = budget;
		mAction = action;
		mWorker = worker;
		mReward = reward;
		mTaskIdx = taskIdx;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "";
		str += "TaskIdx=" + mTaskIdx + "\n";
		str += "Action=" + mAction + "\n";
		str += "Worker=" + mWorker + "\n";
		// str += "StartState=" + mStartState + "\n";
		// str += "EndState=" + mEndState + "\n";
		str += "Reward=" + mReward + "\n";
		str += "Remained Budget=" + mRemainedBudget + "\n";

		return str;
	}

}
