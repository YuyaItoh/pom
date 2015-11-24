package pomdp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ワーカ集合.ワーカとその出現頻度を指定<br>
 *
 */
public class WorkerSet {
	// ==================
	// Fields
	// ==================
	private final int QUEUE_MAX = 100; // ワーカの待受最大数
	private Map<Worker, Double> mWorkers; // ワーカタイプ一覧
	private List<Worker> mQueue; // タスクに対する待受ワーカ行列
	private int mCurrentWorkerPtr; // ワーカのポインタ

	// ==================
	// Constructors
	// ==================
	public WorkerSet() {
		mWorkers = new HashMap<Worker, Double>();
		mQueue = new ArrayList<Worker>();
		mCurrentWorkerPtr = 0;
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
		mQueue.addAll(random(QUEUE_MAX));
	}

	/**
	 * ファイルから待受ワーカ列を読み込む
	 */
	public void readWorkerQueue(String pFilePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader((new File(pFilePath))));

			// 行単位で読込み
			String str;
			while ((str = br.readLine()) != null) {
				// コメントアウトは処理しない
				if (str.length() == 0 || str.charAt(0) == '#') {
					continue;
				}

				// 空白区切り
				String words[] = str.split(" ");
				List<Worker> workers = new ArrayList<Worker>(); // 待受ワーカ
				for (int i = 0; i < words.length; i++) {
					double ability = Double.parseDouble(words[i]);
					workers.add(new Worker(ability));
				}
				mQueue.addAll(workers);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 待受ワーカ列をファイルに書き込む
	 */
	public void writeWorkerQueue(String pFilePath) {
		File file;
		PrintWriter pw = null;
		try {
			file = new File(pFilePath);
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			for (Worker worker : mQueue) {
				pw.printf("%.2f ", worker.getAbility());
			}
		} catch (

		IOException e)

		{
			System.out.println(e);
		} finally

		{
			pw.close();
		}

	}

	/**
	 * 指定したサブタスクにワーカが訪れる
	 */
	public Worker nextWorker(int pIndex) {
		Worker worker = mQueue.get(mCurrentWorkerPtr);
		// ポインタが待ち行列の末尾まできたら0に戻る
		mCurrentWorkerPtr = (mCurrentWorkerPtr == QUEUE_MAX - 1) ? 0 : mCurrentWorkerPtr + 1;
		return worker;
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
			if (rand <= 0) {
				return e.getKey();
			}
		}
		return null;
	}
}
