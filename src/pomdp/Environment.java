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
		// 状態遷移モデルの構築
		// 根ノードではNEXTのみを展開する
		for (Action a : mActionSet.getNextActions()) {
			transit(mSManager.getRootState(), a, 1.0);
		}

		// TODO: 観測確率の計算
	}

	/**
	 * pomdp形式で出力する．mode:0でpomdp, 1でmdp
	 */
	public void toPomdpSolver(String pPath, int mode) {
		PomdpSolveWriter.getInstance(this).write(pPath, mode);
	}

	// =====================
	// Private Methods
	// =====================

	/**
	 * 状態sの展開
	 * 
	 * @param prevQuality:前サブタスクの品質．CURRENTで利用
	 */
	private void expand(State s, double prevQuality) {
		if (!mSManager.contains(s)) {
			count++;
			if (count % 100 == 0) {
				System.out.println(count);
			}
			for (Action a : mActionSet.getActions()) {
				transit(s, a, prevQuality);
			}
		}
	}

	/**
	 * 遷移
	 * 
	 * @param prevQuality:前サブタスクの品質．CURRENTで利用
	 */
	private void transit(State s, Action a, double prevQuality) {
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

		if (isGoal((State) s, (Action) a)) {
			goal(s, a);
			return;
		}

		if (isBunkrupt((State) s, (Action) a)) {
			bunkrupt(s, a);
			return;
		}

		switch (a.getType()) {
		case CURR:
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
	private void actCurrent(State s, Action a, double prevQuality) {
		for (Map.Entry<Worker, Double> workerFreq : mWorkerSet.getWorkersWithFreq().entrySet()) {
			// 状態作成
			double quality = workerFreq.getKey().solve(mTaskSet.mTasks.get(s.getIndex()), a.getWage(), prevQuality);
			// 品質が高い方を保持
			quality = (s.getQuality() > quality) ? s.getQuality() : quality;
			State nextState = new State(s.getIndex(), quality, s.getBudget() - a.getWage());

			// 展開
			expand(nextState, prevQuality);

			// 状態，状態遷移の追加
			mSManager.add(nextState);
			mTManager.put(new Transition(s, a, nextState, TransitionType.TRANSITION), workerFreq.getValue());

		}
	}

	/**
	 * NEXTアクション
	 */
	private void actNext(State s, Action a, double prevQuality) {
		for (Map.Entry<Worker, Double> workerFreq : mWorkerSet.getWorkersWithFreq().entrySet()) {
			// 状態作成
			double quality = workerFreq.getKey().solve(mTaskSet.mTasks.get(s.getIndex() + 1), a.getWage(),
					s.getQuality());
			State nextState = new State(s.getIndex() + 1, quality, s.getBudget() - a.getWage());

			// 展開
			expand(nextState, s.getQuality());

			// 状態，状態遷移の追加
			mSManager.add(nextState);
			mTManager.put(new Transition(s, a, nextState, TransitionType.TRANSITION), workerFreq.getValue());

		}
	}

	/**
	 * EVALアクション
	 */
	private void actEval(State s, Action a, double prevQuality) {
		State nextState = new State(s);
		nextState.decreaseBudget(a.getWage());

		// 展開
		expand(nextState, prevQuality);

		// 状態，状態遷移の追加
		mSManager.add(nextState);
		mTManager.put(new Transition(s, a, nextState, TransitionType.TRANSITION), 1.0);
	}

	/**
	 * GOAL判定
	 */
	private boolean isGoal(State s, Action a) {
		// 最終状態でNEXT or 最終状態で予算切れの場合にTRUE
		if (s.getIndex() == mTaskSet.getDivNum()) {
			if (a.getType() == ActionType.NEXT || s.getBudget() - a.getWage() < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * BUNKRUPT判定
	 */
	private boolean isBunkrupt(State s, Action a) {
		return (s.getBudget() - a.getWage()) < 0;
	}

	/**
	 * GOAL情報を付加して初期状態に戻る
	 */
	private void goal(State s, Action a) {
		mTManager.put(new Transition(s, a, mSManager.getRootState(), TransitionType.GOAL), 1.0);
	}

	/**
	 * BUNKRUPT情報を付加して初期状態に戻る
	 */
	private void bunkrupt(State s, Action a) {
		mTManager.put(new Transition(s, a, mSManager.getRootState(), TransitionType.BUNKRUPT), 1.0);
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

		// 総状態数
		ans += String.format("# + State : %d\n", mSManager.getSize());
		ans += String.format("# \n# ******************************************\n\n");
		return ans;
	}

}
