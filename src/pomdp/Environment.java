package pomdp;

import java.util.Map;

import pomdp.SeqAction.ActionType;
import pomdp.SeqTransition.TransitionType;

/**
 * 環境クラス．神の視点<br>
 * 
 * @author y-itoh
 *
 */
public class Environment {
	// =====================
	// Fields
	// =====================
	public WorkerSet mWorkerSet; // ワーカ集合
	public TaskSet mTaskSet; // タスク集合
	public ActionSet mActionSet; // 行動集合
	public int mBudget; // 予算

	// Manager
	public StateManager mSManager;
	public TransitionManager mTManager;
	public ObservationManager mOManager;

	private int count = 0; // プログレスバー

	// =====================
	// Constructors
	// =====================

	/**
	 * 設定の読込み
	 */
	public Environment(WorkerSet pWorkers, TaskSet pTasks, ActionSet pActions, int pBudget) {
		mWorkerSet = pWorkers;
		mTaskSet = pTasks;
		mBudget = pBudget;
		mActionSet = pActions;
		initManager();
	}

	/**
	 * Manager変数の初期化
	 */
	private void initManager() {
		State root = new SeqState(0, 1.0, mBudget);
		mSManager = new StateManager((SeqState) root);
		mTManager = new TransitionManager();
		mOManager = new ObservationManager();
	}

	// =====================
	// Public Methods
	// =====================

	/**
	 * POMDP環境を構築する
	 */
	public void build() {
		// 状態遷移モデルの構築
		// 根ノードではNEXTのみを展開する
		for (SeqAction a : mActionSet.getNextActions()) {
			transit(mSManager.mRootState, a, 1.0);
		}

		// 観測確率の計算
	}

	// =====================
	// Private Methods
	// =====================

	/**
	 * 状態sの展開
	 * 
	 * @param prevQuality:前サブタスクの品質．CURRENTで利用
	 */
	private void expand(SeqState s, double prevQuality) {
		count++;
		if (count % 100000 == 0) {
			System.out.println(count);
		}
		for (SeqAction a : mActionSet.mActions) {
			transit(s, a, prevQuality);
		}
	}

	/**
	 * 遷移
	 * 
	 * @param prevQuality:前サブタスクの品質．CURRENTで利用
	 */
	private void transit(SeqState s, SeqAction a, double prevQuality) {
		// ********************************************
		// [最終タスク]
		// ・NEXT, 予算切れ: 報酬を与えて初期状態に戻る
		// ・EVAL, CURRENT: 予算を減らして状態遷移
		// ・
		//
		// [非最終タスク]
		// ・予算切れ: 負の報酬を与え初期状態に遷移
		// ・NEXT, EVAL, CURRENT: 予算を減らして状態遷移
		// ********************************************

		if (isGoal((SeqState) s, (SeqAction) a)) {
			goal(s, a);
			return;
		}

		if (isBunkrupt((SeqState) s, (SeqAction) a)) {
			bunkrupt(s, a);
			return;
		}

		switch (a.mType) {
		case CURRENT:
			actCurrent(s, a, prevQuality);
			break;
		case NEXT:
			actNext(s, a, prevQuality);
			break;
		case EVAL:
			actEval(s, a, prevQuality);
			break;
		default:
			break;
		}
	}

	/**
	 * CURRENTアクション
	 */
	private void actCurrent(SeqState s, SeqAction a, double prevQuality) {
		for (Map.Entry<Worker, Double> e : mWorkerSet.mWorkers.entrySet()) {
			// 状態作成
			double quality = e.getKey().solve(mTaskSet.mTasks.get(s.mIndex), a.mWage, prevQuality);
			// 品質が高い方を保持
			// FIXME: ここで高い方の品質を選ぶことによって状態遷移確率の和が1にならなくなる
			quality = (s.mQuality > quality) ? s.mQuality : quality;
			SeqState nextState = new SeqState(s.mIndex, quality, s.mBudget - a.mWage);

			// 状態，状態遷移の追加
			mSManager.add(nextState);
			mTManager.add(new SeqTransition(s, a, nextState, e.getValue(), TransitionType.TRANSITION));

			// 展開
			expand(nextState, prevQuality);
		}
	}

	/**
	 * NEXTアクション
	 */
	private void actNext(SeqState s, SeqAction a, double prevQuality) {
		for (Map.Entry<Worker, Double> e : mWorkerSet.mWorkers.entrySet()) {
			// 状態作成
			double quality = e.getKey().solve(mTaskSet.mTasks.get(s.mIndex + 1), a.mWage, s.mQuality);
			SeqState nextState = new SeqState(s.mIndex + 1, quality, s.mBudget - a.mWage);

			// 状態，状態遷移の追加
			mSManager.add(nextState);
			mTManager.add(new SeqTransition(s, a, nextState, e.getValue(), TransitionType.TRANSITION));

			// 展開
			expand(nextState, s.mQuality);
		}
	}

	/**
	 * EVALアクション
	 */
	private void actEval(SeqState s, SeqAction a, double prevQuality) {
		SeqState nextState = new SeqState(s);
		nextState.mBudget -= a.mWage;

		// 状態，状態遷移の追加
		mSManager.add(nextState);
		mTManager.add(new SeqTransition(s, a, nextState, 1.0, TransitionType.TRANSITION));

		// 展開
		expand(nextState, prevQuality);
	}

	/**
	 * GOAL判定
	 */
	private boolean isGoal(SeqState s, SeqAction a) {
		// 最終状態でNEXT or 最終状態で予算切れの場合にTRUE
		if (s.mIndex == mTaskSet.mDivNum) {
			if (a.mType == ActionType.NEXT || s.mBudget - a.mWage < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * BUNKRUPT判定
	 */
	private boolean isBunkrupt(SeqState s, SeqAction a) {
		return (s.mBudget - a.mWage) < 0;
	}

	/**
	 * GOAL情報を付加して初期状態に戻る
	 */
	private void goal(State s, Action a) {
		mTManager.add(new SeqTransition(s, a, mSManager.mRootState, 1.0, TransitionType.GOAL));
	}

	/**
	 * BUNKRUPT情報を付加して初期状態に戻る
	 */
	private void bunkrupt(State s, Action a) {
		mTManager.add(new SeqTransition(s, a, mSManager.mRootState, 1.0, TransitionType.BUNKRUPT));
	}

	@Override
	public String toString() {
		String ans = "";
		ans += String.format("# ******************************************\n# \n");

		// ワーカのスキルレベルと出現頻度
		ans += String.format("# + Worker : %d\n", mWorkerSet.mWorkers.size());
		for (Map.Entry<Worker, Double> e : mWorkerSet.mWorkers.entrySet()) {
			ans += String.format("# \t* (ability, freq) = (%.2f, %.2f)\n", e.getKey().mAbility, e.getValue());
		}

		// サブタスク数，難易度，ベース賃金
		ans += String.format("# + Task : %d\n", mTaskSet.mDivNum);
		for (Map.Entry<Integer, Subtask> e : mTaskSet.mTasks.entrySet()) {
			ans += String.format("# \t* (index, difficulty, base_wage) = (%d, %.2f, %d)\n", e.getKey(),
					e.getValue().mDifficulty, e.getValue().mBaseWage);
		}

		// 予算
		ans += String.format("# + Budget : %d\n", mBudget);

		// アクションとコスト
		ans += String.format("# + Action : %d\n", mActionSet.mActions.size());

		int actionIndex = 0;
		for (SeqAction a : mActionSet.mActions) {
			ans += String.format("# \t* %d (type, wage) = (%s, %d)\n", actionIndex, a.mType.toString(), a.mWage);
			actionIndex++;
		}

		// 総状態数
		ans += String.format("# + State : %d\n", mSManager.getSize());
		ans += String.format("# \n# ******************************************\n\n");
		return ans;
	}

}
