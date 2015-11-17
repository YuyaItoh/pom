package pomdp;

import java.util.Map;

import pomdp.Action.ActionType;
import pomdp.Transition.TransitionType;

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
	private WorkerSet mWorkerSet; // ワーカ集合
	private TaskSet mTaskSet; // タスク集合
	private ActionSet mActionSet; // 行動集合

	private int mBudget; // 予算

	// Manager
	private StateManager mSManager;
	private TransitionManager mTManager;
	private ObservationManager mOManager;

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
		State root = new State(0, 1.0, mBudget);
		mSManager = new StateManager((State) root);
		mTManager = new TransitionManager();
		mOManager = new ObservationManager();
	}

	// =====================
	// Getters & Setters
	// =====================
	public WorkerSet getWorkerSet() {
		return mWorkerSet;
	}

	public TaskSet getTaskSet() {
		return mTaskSet;
	}

	public ActionSet getActionSet() {
		return mActionSet;
	}

	public int getBudget() {
		return mBudget;
	}

	public StateManager getSManager() {
		return mSManager;
	}

	public TransitionManager getTManager() {
		return mTManager;
	}

	public ObservationManager getOManager() {
		return mOManager;
	}

	// =====================
	// Public Methods
	// =====================
	/**
	 * POMDP環境を構築する
	 */
	public void build() {
		// ---------------------------
		// 状態遷移モデルの作成
		// ---------------------------
		System.out.print("building...");
		for (Action action : mActionSet.getActions()) {
			// 根ノードではNEXT以外はFAILにする
			if (action.getType() == ActionType.NEXT) {
				transit(mSManager.getRootState(), action, 1.0);
			} else {
				fail(mSManager.getRootState(), action);
			}
		}
		System.out.println("finish.");

		// ---------------------------
		// 観測モデルの計算
		// ---------------------------
		System.out.print("calculating...");
		mOManager.calcObservations(mSManager, mActionSet);
		System.out.println("finish.");
	}

	/**
	 * pomdp形式で出力する．mode:0でpomdp, 1でmdp
	 */
	public void toPomdpSolver(String pPath, int mode) {
		System.out.print("output...");
		PomdpSolveWriter.getInstance(this).write(pPath, mode);
		System.out.print("finish.");
	}

	// =====================
	// Private Methods
	// =====================

	/**
	 * 状態sの展開
	 * 
	 * @param prevQuality:前サブタスクの品質．CURRENTで利用
	 */
	private void expand(State pState, double pPrevQuality) {
		if (!mSManager.contains(pState)) {
			count++;
			if (count % 1000 == 0) {
				System.out.print(".");
			}
			for (Action action : mActionSet.getActions()) {
				transit(pState, action, pPrevQuality);
			}
		}
	}

	/**
	 * 遷移
	 * 
	 * @param prevQuality:前サブタスクの品質．CURRENTで利用
	 */
	private void transit(State pState, Action pAction, double pPrevQuality) {
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

		if (isGoal((State) pState, (Action) pAction)) {
			goal(pState, pAction);
			return;
		}

		if (isBunkrupt((State) pState, (Action) pAction)) {
			fail(pState, pAction);
			return;
		}

		switch (pAction.getType()) {
		case CURR:
			actCurrent(pState, pAction, pPrevQuality);
			break;
		case NEXT:
			actNext(pState, pAction, pPrevQuality);
			break;
		case EVAL:
			actEval(pState, pAction, pPrevQuality);
			break;
		default:
			break;
		}
	}

	/**
	 * CURRENTアクション
	 */
	private void actCurrent(State pState, Action pAction, double pPrevQuality) {
		for (Map.Entry<Worker, Double> workerFreq : mWorkerSet.getWorkersWithFreq().entrySet()) {
			// 状態作成
			double quality = workerFreq.getKey().solve(mTaskSet.mTasks.get(pState.getIndex()), pAction.getWage(),
					pPrevQuality);
			// 品質が高い方を保持
			quality = (pState.getQuality() > quality) ? pState.getQuality() : quality;
			State nextState = new State(pState.getIndex(), quality, pState.getBudget() - pAction.getWage());

			// 展開
			expand(nextState, pPrevQuality);

			// 状態，状態遷移の追加
			mSManager.add(nextState);
			mTManager.put(new Transition(pState, pAction, nextState, TransitionType.TRANSITION), workerFreq.getValue());

		}
	}

	/**
	 * NEXTアクション
	 */
	private void actNext(State pState, Action pAction, double pPrevQuality) {
		for (Map.Entry<Worker, Double> workerFreq : mWorkerSet.getWorkersWithFreq().entrySet()) {
			// 状態作成
			double quality = workerFreq.getKey().solve(mTaskSet.mTasks.get(pState.getIndex() + 1), pAction.getWage(),
					pState.getQuality());
			State nextState = new State(pState.getIndex() + 1, quality, pState.getBudget() - pAction.getWage());

			// 展開
			expand(nextState, pState.getQuality());

			// 状態，状態遷移の追加
			mSManager.add(nextState);
			mTManager.put(new Transition(pState, pAction, nextState, TransitionType.TRANSITION), workerFreq.getValue());

		}
	}

	/**
	 * EVALアクション
	 */
	private void actEval(State pState, Action pAction, double pPrevQuality) {
		State nextState = new State(pState);
		nextState.decreaseBudget(pAction.getWage());

		// 展開
		expand(nextState, pPrevQuality);

		// 状態，状態遷移の追加
		mSManager.add(nextState);
		mTManager.put(new Transition(pState, pAction, nextState, TransitionType.TRANSITION), 1.0);
	}

	/**
	 * GOAL判定
	 */
	private boolean isGoal(State pState, Action pAction) {
		// 最終状態でNEXT or 最終状態で予算切れの場合にTRUE
		if (pState.getIndex() == mTaskSet.getDivNum()) {
			if (pAction.getType() == ActionType.NEXT || pState.getBudget() - pAction.getWage() < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * BUNKRUPT判定
	 */
	private boolean isBunkrupt(State pState, Action pAction) {
		return (pState.getBudget() - pAction.getWage()) < 0;
	}

	/**
	 * GOAL情報を付加して初期状態に戻る
	 */
	private void goal(State pState, Action pAction) {
		mTManager.put(new Transition(pState, pAction, mSManager.getRootState(), TransitionType.GOAL), 1.0);
	}

	/**
	 * BUNKRUPT情報を付加して初期状態に戻る
	 */
	private void fail(State pState, Action pAction) {
		mTManager.put(new Transition(pState, pAction, mSManager.getRootState(), TransitionType.FAIL), 1.0);
	}

	@Override
	public String toString() {
		String ans = "";
		ans += String.format("# ******************************************\n# \n");

		// ワーカのスキルレベルと出現頻度
		ans += String.format("# + Worker : %d\n", mWorkerSet.getSize());
		for (Map.Entry<Worker, Double> e : mWorkerSet.getWorkersWithFreq().entrySet()) {
			ans += String.format("# \t* (ability, freq) = (%.2f, %.2f)\n", e.getKey().getAbility(), e.getValue());
		}

		// サブタスク数，難易度，ベース賃金
		ans += String.format("# + Task : %d\n", mTaskSet.getDivNum());
		for (Map.Entry<Integer, Subtask> e : mTaskSet.mTasks.entrySet()) {
			ans += String.format("# \t* (index, difficulty, base_wage) = (%d, %.2f, %d)\n", e.getKey(),
					e.getValue().getDifficulty(), e.getValue().getBaseWage());
		}

		// 予算
		ans += String.format("# + Budget : %d\n", mBudget);

		// アクションとコスト
		ans += String.format("# + Action : %d\n", mActionSet.getActions().size());

		int actionIndex = 0;
		for (Action a : mActionSet.getActions()) {
			ans += String.format("# \t* %d (type, wage) = (%s, %d)\n", actionIndex, a.getType().toString(),
					a.getWage());
			actionIndex++;
		}

		ans += String.format("# + State : %d\n", mSManager.getSize()); // 状態数
		ans += String.format("# + Transition : %d\n", mTManager.getSize()); // 状態遷移数
		ans += String.format("# + Observation : %d\n", mOManager.getSize()); // 観測確率数

		ans += String.format("# \n# ******************************************\n\n");
		return ans;
	}

}
