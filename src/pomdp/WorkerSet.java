package pomdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ワーカ集合.ワーカとその出現頻度を指定<br>
 * 
 * @author y-itoh
 *
 */
public class WorkerSet {
	// ==================
	// Fields
	// ==================
	private final int QUEUE_MAX = 100; // ワーカの待受最大数
	private Map<Worker, Double> mWorkers; // ワーカタイプ一覧
	private Map<Integer, List<Worker>> mQueue; // タスクに対する待受ワーカ行列

	// ==================
	// Constructors
	// ==================
	public WorkerSet() {
		mWorkers = new HashMap<Worker, Double>();
		mQueue = new HashMap<Integer, List<Worker>>();
	}

	// ==================
	// Getters & Setters
	// ==================

	/**
	 * (Worker, Freq)の組を全て取得
	 */
	public Map<Worker, Double> getWorkersWithFreq() {
		return mWorkers;
	}

	/**
	 * 全てのワーカを取得
	 */
	public Set<Worker> getWorkers() {
		return mWorkers.keySet();
	}

	// ==================
	// Public Methods
	// ==================

	/**
	 * ワーカを追加
	 */
	public void add(Worker pWorker, double pFrequency) {
		mWorkers.put(pWorker, pFrequency);
	}

	/**
	 * ワーカ数を取得
	 */
	public int getSize() {
		return mWorkers.size();
	}

	/**
	 * サブタスク数とワーカ数を指定して，各サブタスクに対する待受ワーカ列を作成する
	 */
	public void createWorkerQueue(int pTaskNum) {
		for (int i = 1; i <= pTaskNum; i++) {
			mQueue.put(i, random(QUEUE_MAX));
		}
	}

	/**
	 * ファイルから待受ワーカ列を読み込む
	 */
	public void readWorkerQueue(String pPath) {
		// TODO 読込みパーザの作成
	}

	/**
	 * 待受ワーカ列をファイルに書き込む
	 */
	public void writeWorkerQueue(String pPath) {
		// TODO 書込みの作成
	}

	@Override
	public String toString() {
		return "WorkerSet(" + mWorkers + ")";
	}

	// ==================
	// Private Methods
	// ==================

	/**
	 * ワーカをn人ランダムに取り出す
	 */
	private List<Worker> random(int pNumber) {
		List<Worker> workers = new ArrayList<Worker>();
		for (int i = 0; i < pNumber; i++) {
			workers.add(random());
		}
		return workers;
	}

	/**
	 * ワーカをランダムに取り出す
	 */
	private Worker random() {
		double rand = Math.random();
		for (Map.Entry<Worker, Double> e : mWorkers.entrySet()) {
			// randが0以下になった時のワーカを返す
			rand -= e.getValue();
			if (rand < 0) {
				return e.getKey();
			}
		}
		return null;
	}
}
