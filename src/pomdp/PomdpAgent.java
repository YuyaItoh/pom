package pomdp;

import java.io.BufferedReader;
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
	private Policy mPolicy; // 方策
	private double[] mBelief; // 信念ベクトル
	private ActionSet mActionSet;// 行動集合
	private Action mPrevAction; // 直近のアクション

	// ====================
	// Constructors
	// ====================
	public PomdpAgent(Environment pEnv, String pPolicyPath, String pPomdpPath, AgentType pAgentType) {
		super(pEnv, pAgentType);
		mEnv = pEnv;
		mPolicy = null;
		mStates = PomdpParser.getStates(pPomdpPath);
		mActionSet = mEnv.getActionSet();

		// JSONの読込み
		Gson gson = new Gson();
		try {
			JsonReader reader = new JsonReader(new BufferedReader(new FileReader(pPolicyPath)));
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
	// Public Methods
	// ====================

	@Override
	public void update(Object o) {
		// 信念状態の更新
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
		// TODO: インデックスからアクション取ってるけど，順序関係大丈夫かなあ？
		Action action = mActionSet.getActions().get(maxPlane.action);
		mPrevAction = action;

		// 予算とタスクインデックスの更新
		mRemainingBudget -= action.getWage();
		mCurrentTaskIndex = (action.getType() == ActionType.NEXT) ? mCurrentTaskIndex + 1 : mCurrentTaskIndex;

		return action;
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
		// TODO: 実装
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
		for (int i = 0; i < mBelief.length; i++) {
			double reachProb = 0.0; // 状態s'への到達可能性，Σ_s T(s, a, s')b(s)
			double observeProb = mEnv.getOManager().getObservationProbability(mPrevAction, mStates[i], pObservation); // 観測oの取得可能性，P(o'|s',a)

			// 到達確率Σ_s P(s'|s,a)b(s)の計算
			for (int j = 0; j < mBelief.length; j++) {
				reachProb += mEnv.getTManager().getProbability(mStates[j], mPrevAction, mStates[i]) * mBelief[i];
			}

			// 状態iの信念
			updatedBelief[i] = observeProb * reachProb;
			probSum += updatedBelief[i];
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
