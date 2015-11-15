package pomdp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class PomdpSolveWriter {

	// ===============
	// Fields
	// ===============
	private static PomdpSolveWriter mInstance;
	private Environment mEnv;

	// ===============
	// Constructors
	// ===============
	private PomdpSolveWriter(Environment pEnvironment) {
		mEnv = pEnvironment;
	}

	// ===============
	// Methods
	// ===============

	/**
	 * シングルトン
	 */
	public static PomdpSolveWriter getInstance(Environment pEnvironment) {
		if (mInstance == null) {
			mInstance = new PomdpSolveWriter(pEnvironment);
		}
		return mInstance;
	}

	/**
	 * 現在の環境をpomdp-solveの書式として出力する<br>
	 * modeが0ならpomdp，1ならmdp
	 */
	public void write(String pPath, int mode) {
		switch (mode) {
		case 0:
			writePomdp(pPath);
			break;
		case 1:
			writeMdp(pPath);
			break;
		default:
			System.out.println("Unknown Number -- PomdpSolveWriter.write");
		}
	}

	/**
	 * POMDPモードで記述
	 */
	private void writePomdp(String pPath) {
		try {
			File file = new File(pPath);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			writeEnvironment(pw); // 環境情報

			// discount, values
			pw.printf("discount: %f\n", 0.9);
			pw.printf("values: reward\n\n");

			// 定義
			writeStates(pw); // 状態集合
			writeActions(pw); // 行動集合
			writeObservation(pw); // 観測集合

			// 確率
			writeTransitionProb(pw); // 遷移確率
			writeObservationProb(pw); // 観測確率
			writeRewardPomdp(pw); // 報酬

			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * mdpモードで記述
	 * 
	 * @param pPath
	 */
	private void writeMdp(String pPath) {
		try {
			File file = new File(pPath);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			writeEnvironment(pw); // 環境情報

			// discount, values
			pw.printf("discount: %f\n", 0.9);
			pw.printf("values: reward\n\n");

			// 定義
			writeStates(pw); // 状態集合
			writeActions(pw); // 行動集合

			// 確率
			writeTransitionProb(pw); // 遷移確率
			writeRewardMdp(pw); // 報酬

			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * 環境情報の記述
	 */
	private void writeEnvironment(PrintWriter pw) {
		pw.printf("%s", mEnv.toString());
	}

	/**
	 * 状態集合の記述
	 */
	private void writeStates(PrintWriter pw) {
		pw.printf("# == states\n");
		pw.printf("states: ");

		// RootStateだけは先に記述しないとシミュレーション時に困る
		pw.printf("%s ", mEnv.getSManager().getRootState().toName());

		for (State s : mEnv.getSManager().getStates()) {
			if (!mEnv.getSManager().isRootState(s)) {
				pw.printf("%s ", s.toName());
			}
		}
		pw.printf("\n");
	}

	/**
	 * 行動集合の記述
	 */
	private void writeActions(PrintWriter pw) {
		pw.printf("\n# == actions\n");

		pw.printf("actions: ");
		for (Action a : mEnv.getActionSet().getActions()) {
			pw.printf("%s ", a.toName());
		}
		pw.printf("\n");
	}

	/**
	 * 観測集合の記述
	 */
	private void writeObservation(PrintWriter pw) {
		pw.printf("\n# == observations\n");

		pw.printf("observations: ");
		for (Observation o : mEnv.getOManager().getObservations()) {
			pw.printf("%s ", o.toName());
		}
		pw.printf("\n");
	}

	/**
	 * 状態遷移確率の記述
	 */
	private void writeTransitionProb(PrintWriter pw) {
		// ********************************************
		// T: <action> : <start-state> : <end-state> %f
		// ********************************************
		pw.printf("\n# == transition probability\n");
		pw.printf("# == T: <action> : <start-state> : <end-state> f\n\n");

		for (Map.Entry<Transition, Double> transitionProb : mEnv.getTManager().getTransitionsWithProb().entrySet()) {
			Transition t = transitionProb.getKey();
			double prob = transitionProb.getValue();
			pw.printf("T: %s : %s : %s %.3f\n", t.getAction().toName(), t.getPrevState().toName(),
					t.getNextState().toName(), prob);
		}

		pw.printf("\n");
	}

	/**
	 * 観測確率の記述
	 */
	private void writeObservationProb(PrintWriter pw) {
		// ********************************************
		// O: <action> : <end-state> : <observation> %f
		// ********************************************
		pw.printf("\n# == observation probability\n");
		pw.printf("# == O: <action> : <end-state> : <observation> f\n\n");

		for (Map.Entry<Observation, Double> obsProb : mEnv.getOManager().getObservationsWithProb().entrySet()) {
			Observation o = obsProb.getKey();
			double prob = obsProb.getValue();
			pw.printf("O: %s : %s : %s %.3f\n", o.getAction().toName(), o.getState().toName(), o.toName(), prob);
		}

		pw.printf("\n");
	}

	/**
	 * 報酬の記述(POMDP)
	 */
	private void writeRewardPomdp(PrintWriter pw) {
		// ********************************************
		// R: <action> : <start-state> : <end-state> : <observation> %f
		//
		// 報酬は以下(上から優先順位が高い)
		// 1. 最終状態から初期状態への遷移 : 0
		// 2. 非最終状態から予算切れによる初期状態への遷移: -100
		// 3. LISTENアクションによる遷移: 0
		// 4. 上記以外: 遷移後の作業品質
		//
		// observationは報酬に影響を与えない
		// ********************************************

		pw.printf("\n# == rewards\n");
		pw.printf("# == R: <action> : <start-state> : <end-state> : <observation> f\n\n");

		for (Transition t : mEnv.getTManager().getTransitions()) {
			pw.printf("R: %s : %s : %s : * %.3f\n", t.getAction().toName(), t.getPrevState().toName(),
					t.getNextState().toName(), t.getReward());
		}

		pw.printf("\n");
	}

	/**
	 * 報酬の記述(MDP)
	 */
	private void writeRewardMdp(PrintWriter pw) {
		// ********************************************
		// R: <action> : <start-state> : <end-state> %f
		//
		// 報酬は以下(上から優先順位が高い)
		// 1. 最終状態から初期状態への遷移 : 0
		// 2. 非最終状態から予算切れによる初期状態への遷移: -100
		// 3. LISTENアクションによる遷移: 0
		// 4. 上記以外: 遷移後の作業品質
		// ********************************************

		pw.printf("\n# == rewards\n");
		pw.printf("# == R: <action> : <start-state> : <end-state> f\n\n");

		for (Transition t : mEnv.getTManager().getTransitions()) {
			pw.printf("R: %s : %s : %s %.3f\n", t.getAction().toName(), t.getPrevState().toName(),
					t.getNextState().toName(), t.getReward());
		}

		pw.printf("\n");
	}
}