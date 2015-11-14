package pomdp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ワーカ集合.ワーカとその出現頻度を指定
 * 
 * @author y-itoh
 *
 */
public class WorkerSet {
	// ================
	// Fields
	// ================
	Map<Worker, Double> mWorkers;

	// ================
	// Constructors
	// ================
	public WorkerSet() {
		mWorkers = new HashMap<Worker, Double>();
	}

	// ================
	// Methods
	// ================

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
	 * 全てのワーカを取得
	 */
	public Set<Worker> getWorkers() {
		Set<Worker> workers = new HashSet<Worker>();
		for (Worker w : mWorkers.keySet()) {
			workers.add(w);
		}
		return workers;
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
