package pomdp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ワーカ集合.ワーカとその出現頻度を指定
 * 
 * @author y-itoh
 *
 */
public class WorkerSet {
	// ==================
	// Fields
	// ==================
	private Map<Worker, Double> mWorkers;

	// ==================
	// Constructors
	// ==================
	public WorkerSet() {
		mWorkers = new HashMap<Worker, Double>();
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
	// Methods
	// ==================

	/**
	 * ワーカを追加
	 * 
	 * @param pWorker
	 * @param pFrequency
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
	 * ワーカをランダムに取り出す
	 * 
	 * @return
	 */
	public Worker random() {
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

	@Override
	public String toString() {
		return "WorkerSet(" + mWorkers + ")";
	}

}
