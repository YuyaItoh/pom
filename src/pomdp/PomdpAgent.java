package pomdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import pomdp.Action.ActionType;

public class PomdpAgent extends Agent {
	// ====================
	// Fields
	// ====================
	private Environment mEnv; // 環境
	private State[] mStates; // 状態配列
	private Action[] mActions; // 行動配列
	private Policy mPolicy; // 方策
	private double[] mBelief; // 信念ベクトル
	private Action mPrevAction; // 直近のアクション

	// ====================
	// Constructors
	// ====================
	public PomdpAgent(Environment pEnv, String pPolicyPath, String pPomdpPath, AgentType pAgentType) {
		super(pEnv, pAgentType);
		mEnv = pEnv;
		mPolicy = null;

		initPomdp(pPomdpPath);
		initPolicy(pPolicyPath);
		initBelief();
	}

	/**
	 * Policyファイルを読込み方策を取得する．フォーマットはTreyのタイプ(xxx.policy)とCassandraのタイプ(xxx.
	 * alpha) の2種類
	 */
	private void initPolicy(String pPolicyPath) {
		if (pPolicyPath.contains(".policy")) {
			initTreyPolicy(pPolicyPath);
		} else if (pPolicyPath.contains(".alpha")) {
			initCassandraPolicy(pPolicyPath);
		} else {
			System.out.println("unknown extension of policy file --" + pPolicyPath);
		}
	}

	/**
	 * Treyのpolicyフォーマット(xxx.policy)を読み込み初期化
	 */
	private void initTreyPolicy(String pPolicyPath) {
		Gson gson = new Gson();
		try {
			JsonReader reader = new JsonReader(new BufferedReader(new FileReader(pPolicyPath)));
			mPolicy = gson.fromJson(reader, Policy.class);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Cassandraのpolicyフォーマット(xxx.alpha)を読み込み初期化
	 */
	private void initCassandraPolicy(String pPolicyPath) {
		// CassandraのファイルをPolicyクラスに当てはめる
		mPolicy = new Policy();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader((new File(pPolicyPath))));
			String str;

			while ((str = br.readLine()) != null) {
				// コメントアウト，空行の無視
				if (str.length() == 0 || str.charAt(0) == '#') {
					continue;
				}

				// 1行目: アクション
				int action = Integer.parseInt(str);
				str = br.readLine();

				// 2行目: ベクトル（空白区切り）
				// 空白区切りで要素を取得し，doubleに変換
				String entriesStr[] = str.split(" ");
				double entries[] = new double[entriesStr.length];
				for (int i = 0; i < entriesStr.length; i++) {
					entries[i] = Double.parseDouble(entriesStr[i]);
				}

				// Planeを作成し，Policyに追加
				mPolicy.add(new Plane(action, entries));
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} catch (ArrayIndexOutOfBoundsException e) {
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
	 * POMDPファイルを読込み，状態と行動を取得する
	 */
	private void initPomdp(String pFilePath) {
		PomdpParser pp = new PomdpParser(pFilePath);
		pp.parse();
		mStates = pp.getStates();
		mActions = pp.getActions();
	}

	/**
	 * 信念ベクトルの初期化
	 */
	private void initBelief() {
		// 配列長を定義して，mBelief[0]の確率を1.0にする
		mBelief = new double[mPolicy.planes.get(0).entries.length];
		mBelief[0] = 1.0;
	}

	// ====================
	// Public Methods
	// ====================

	@Override
	public void update(Object o) {
		updateBelief(mPrevAction, (double) o);
	}

	@Override
	public Action selectAction() {
		// 価値関数が最大になるようなPlaneを取得
		Plane maxPlane = null; // 最適平面
		double maxVal = 0.0; // 最大価値

		// ループ
		for (Plane p : mPolicy.planes) {
			if (maxPlane == null) {
				maxPlane = p;
				maxVal = calcValue(p);
			} else {
				double val = calcValue(p);
				if (maxVal <= val) {
					maxPlane = p;
					maxVal = val;
				}
			}
		}

		// 最適方策
		Action action = mActions[maxPlane.action];
		mPrevAction = action;

		// 予算とタスクインデックスの更新
		mRemainingBudget -= action.getWage();
		mCurrentTaskIndex = (action.getType() == ActionType.NEXT) ? mCurrentTaskIndex + 1 : mCurrentTaskIndex;

		return action;
	}

	/**
	 * 信念ベクトルの表示
	 */
	public void printBelief() {
		System.out.print("[ ");
		for (int i = 0; i < mBelief.length; i++) {
			System.out.printf("%.3f ", mBelief[i]);
		}
		System.out.println("]");
	}

	// ====================
	// Private Methods
	// ====================

	/**
	 * 超平面と信念状態から価値関数を計算する
	 */
	private double calcValue(Plane plane) {
		// 内積の計算
		double val = 0.0;
		for (int i = 0; i < mBelief.length; i++) {
			val += mBelief[i] * plane.entries[i];
		}
		return val;
	}

	/**
	 * 行動と観測値をもとに信念状態を更新する
	 */
	private void updateBelief(Action pAction, double pObservation) {
		System.out.print("updating...");
		// **********************************************************
		// 信念状態の更新式は以下のように表される
		// （s: 前の状態, s': 新しい状態）
		//
		// b'(s') = k * P(o|s',a) * Σ_s P(s'|s,a)b(s)
		// k = 1 / ( Σ_s' P(o'|s',a) * Σ_s P(s'|s,a) b(s) )
		//
		// 言葉で表せば，「s'でoを観測する確率 × s'にいる確率」
		// **********************************************************

		// 新しい信念ベクトル
		double updatedBelief[] = new double[mBelief.length];
		// 正規化用の確率和(k)
		double probSum = 0.0;

		// 信念b(i)を計算
		// FIXME: 信念ベクトルの大きさ（＝状態数）の2乗のオーダーになるのでlet's 枝刈り
		for (int after = 0; after < mBelief.length; after++) {
			double reachProb = 0.0; // 状態s'への到達確率，Σ_s P(s'|s,a)b(s)
			double observeProb = mEnv.getOManager().getObservationProbability(mPrevAction, mStates[after],
					pObservation); // oの観測可能性，P(o'|s',a)

			// 観測確率が0の場合は積が0になるので到達確率の計算不要
			if (observeProb != 0) {
				// 到達確率Σ_s P(s'|s,a)b(s)の計算
				for (int before = 0; before < mBelief.length; before++) {
					reachProb += mEnv.getTManager().getProbability(mStates[before], mPrevAction, mStates[after])
							* mBelief[before];
				}
			}
			// 状態iの信念
			updatedBelief[after] = observeProb * reachProb;
			probSum += updatedBelief[after];
		}

		// 正規化する
		for (int i = 0; i < updatedBelief.length; i++) {
			updatedBelief[i] = updatedBelief[i] / probSum;
		}

		// 信念状態の更新
		mBelief = updatedBelief;

		System.out.println("finished");
		return;
	}
}
