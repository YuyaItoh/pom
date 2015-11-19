package pomdp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import pomdp.Action.ActionType;

public class PomdpAgent extends Agent {
	// ====================
	// Fields
	// ====================
	public Policy mPolicy; // 方策オブジェクト
	public double[] mBelief; // 信念ベクトル
	public ActionSet mActionSet;
	public String mPrevAction; // 直近のアクション

	// ====================
	// Constructors
	// ====================
	public PomdpAgent(Environment env, String policyPath, AgentType type) {
		super(env, type);
		mPolicy = null;
		mActionSet = env.getActionSet();

		// JSONの読込み
		Gson gson = new Gson();
		try {
			JsonReader reader = new JsonReader(new BufferedReader(new FileReader(policyPath)));
			mPolicy = gson.fromJson(reader, Policy.class);

			// 信念ベクトルの初期化
			initBelief();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
	// Methods
	// ====================

	@Override
	public void update(Object o) {
		// 信念状態の更新
		updateBelief(mPrevAction, (String) o);

	}

	@Override
	public Action selectAction() {
		// *********************************
		// 最終タスクなら残り予算を全額払う
		// そうでないならば，最適方策で支払う
		// *********************************
		if (isLastTask()) {
			double payoff = mRemainingBudget;

			// 予算の更新
			mRemainingBudget -= payoff;
			mCurrentTaskIndex++;
			return payoff;
		}

		// 価値関数が最大になるようなPlaneを取得
		Plane maxPlane = null;
		double maxVal = 0.0;
		for (Plane p : mPolicy.planes) {
			if (maxPlane == null) {
				maxPlane = p;
				maxVal = calcVal(p);
			} else {
				// 内積計算するんやで！
				double val = calcVal(p);
				if (maxVal <= val) {
					maxPlane = p;
					maxVal = val;
				}
			}
		}

		// maxPlaneに最適方策が入っているから，このアクションをとってくるんやで！
		Action a = mActionSet.getActions().get(maxPlane.action);
		mPrevAction = a.mName;

		// 予算とタスクidxの更新
		mRemainingBudget -= a.mPay;
		mCurrentTaskIndex = (a.getType() == ActionType.EVAL) ? mCurrentTaskIndex : mCurrentTaskIndex + 1;

		return a.mPay;
	}

	/**
	 * 超平面と信念状態から行動価値を計算する
	 * 
	 * @param plane
	 * @return
	 */
	private double calcVal(Plane plane) {
		double val = 0.0;
		for (int i = 0; i < mBelief.length; i++) {
			val += mBelief[i] * plane.entries[i];
		}
		return val;
	}

	/**
	 * 行動と観測値から，自身の信念状態を更新する
	 * 
	 * @param state
	 * @param a
	 * @param o
	 */
	public void updateBelief(String action, String o) {
		System.out.print("update...");
		// **********************************************************
		// 信念状態の更新式は以下のように表される
		// （s: 前の状態, s': 新しい状態）
		//
		// b'(s') = k * O(a, s', o) * Σ_s T(s,a,s')b(s)
		// k = 1 / ( Σ_s' O(a, s', o) Σ_s T(s,a,s')b(s) )
		//
		// よって，以下の条件を満たすレコードを取得
		// Transitionから「行動=a, 現状態s'を満たすレコード集合」
		// Observationから「行動=a, 現状態=s', 観測値=oを満たすレコード」
		// Observationから「行動=a, 観測値=oを満たすレコード集合」
		// **********************************************************

		// 新しい信念状態ベクトルを作る
		double updatedBelief[] = new double[mBelief.length];
		// 正規化用の和
		double probSum = 0.0;

		// 信念ベクトルの各要素を計算
		for (int i = 0; i < mBelief.length; i++) {

			// Σs Prob(状態sにいる) * Prob(sからs'に到達する)
			Map<Integer, Double> ss = mDb.selectTransitionWithEnd(action, i);
			double reachProb = 0.0;
			for (Map.Entry<Integer, Double> s : ss.entrySet()) {
				reachProb += mBelief[s.getKey()] * s.getValue();
			}

			// Prob(s'でoを観測する)
			double obsProb = mDb.getObservationProbWith(action, i, o);

			// 状態iの新しい信念
			updatedBelief[i] = obsProb * reachProb;
			probSum += updatedBelief[i];
		}

		// 正規化する
		for (int i = 0; i < updatedBelief.length; i++) {
			updatedBelief[i] = updatedBelief[i] / probSum;
		}

		// 最後に信念状態を置き換える
		mBelief = updatedBelief;

		System.out.println("done");
		return;
	}
}
