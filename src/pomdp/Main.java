package pomdp;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pomdp.Agent.AgentType;
import pomdp.Agent.AgentType;

public class Main {
	// =========================
	// Enums
	// =========================
	enum Mode {
		POMDP, MDP, SIMULATION
	}

	// =========================
	// Public Methods
	// =========================

	/**
	 * POMDPファイル作成
	 */
	public void execPomdp(String pInputPath) {
		System.out.println("=======================");
		System.out.println("Exec Pomdp Writing");
		System.out.println("=======================");

		// 環境構築
		Environment env = EnvironmentInitializer.generate(pInputPath);
		env.build();

		// 出力
		String outputPath = makePomdpFileName(pInputPath);
		env.writePomdp(outputPath, 0);
	}

	/**
	 * MDPファイル作成（未実装）
	 */
	public void execMdp(String pInputPath) {
		// TODO: 必要であれば実装する
		return;
	}

	/**
	 * シミュレーションの実行
	 */
	public void execSimulation(String pInputPath, String pPolicyPath, String[] pQueuePaths, int pSimulationNum,
			String pAgentType) {
		// 環境構築
		Environment env = EnvironmentInitializer.generate(pInputPath);

		// シミュレーションの回数を指定
		int n;
		boolean createFlag = true;
		if (pQueuePaths == null) {
			n = pSimulationNum;
		} else {
			createFlag = false;
			n = pQueuePaths.length;
		}

		// シミュレーションの実行ループ
		for (int i = 0; i < n; i++) {
			// createFlagがtrue => 待ち行列を作成する
			// createFlagがfalse => ファイルから読込む
			if (createFlag) {
				env.createWorkerQueue(); // 作成
				env.writeWorkerQueue("queue_" + i + ".conf"); // 書込み
			} else {
				env.readWorkerQueue(pQueuePaths[i]); // 読込
			}

			// エージェントの設定
			Agent agent;
			switch (pAgentType) {
			case "equal":
				agent = new EqualAgent(env, AgentType.EQUAL);
				break;
			case "dif":
				agent = new DifAgent(env, AgentType.DIF);
				break;
			case "pomdp":
				agent = new PomdpAgent(env, pPolicyPath, AgentType.POMDP);
				break;
			default:
				agent = null;
			}

			// シミュレーションの実行
			System.out.println("== " + i + " th simulation");
			String outputPath = getPreffix(new File(pInputPath).getName()) + "_sim_" + pAgentType + i + ".result";
			Simulator sim = new Simulator(env, agent);
			sim.run(outputPath);
		}
	}

	// =========================
	// Private Methods
	// =========================

	/**
	 * 拡張子を除いたファイル名を取得（hoge.txt -> hoge）
	 */
	private String getPreffix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(0, point);
		}
		return fileName;
	}

	/**
	 * pomdpファイル名の作成（.input -> .pomdp）
	 */
	private String makePomdpFileName(String pInput) {
		Pattern p = Pattern.compile(".input");
		Matcher m = p.matcher(pInput);
		return m.replaceAll(".pomdp");
	}

	// =========================
	// Entry Point
	// =========================
	/**
	 * メインエントリポイント
	 */
	public static void main(String[] args) {
		// **************************************************************
		// args[0]で"pomdp", "mdp", "simulation"を選択
		//
		// [ pomdp ]
		// + pomdpファイルの作成
		// + args[1]: 環境ファイル(test.input）
		// + (e.g.) java pomdp/Main pomdp env.input
		//
		// [ mdp ]
		// + 同上
		//
		// [ simulation ]
		// + 環境と方策からシミュレーションを行う
		// + args[1]: 環境ファイル(test.input)
		// + args[2]: 方策ファイルのjson（test.json）
		// + args[3]: シミュレーション回数（ワーカセットがある場合，そちらの回数を優先する）
		// + args[4]: agent（"equal", "dif", "pomdp"）
		// + args[5]: ワーカの待ち行列ファイル（queue.conf）← 実際に訪れるワーカセット
		// + (e.g.) java -cp .:../lib/gson-2.3.1.jar pomdp/Main simulation
		// env.input env.policy.json 5 equal
		// **************************************************************

		long start = System.currentTimeMillis();
		Main m = new Main();

		// ==================
		// 引数準備
		// ==================
		String mode = args[0];
		String inputPath = args[1]; // 環境ファイル
		String policyPath = null; // 方策ファイル
		int simulationNum = 1;
		String agentType = "";
		String queuePath = null; // ワーカ設定ファイル

		// ==================
		// モード決定
		// ==================
		switch (mode) {
		case "pomdp": // pomdpファイルの出力
			m.execPomdp(inputPath);
			break;
		case "mdp:": // mdpファイルの出力
			m.execMdp(inputPath);
			break;
		case "simulation": // simulation
			policyPath = args[2];
			simulationNum = Integer.parseInt(args[3]);
			agentType = args[4];

			switch (args.length) {
			case 5: // ワーカ設定がない場合
				queuePath = null;
				break;
			case 6:
				queuePath = args[5];
				break;
			default:
				System.out.println("シミュレーションの引数がおかしい");
				System.exit(0);
				break;
			}

			// シミュレーション実行
			m.execSimulation(inputPath, policyPath, queuePath, simulationNum, agentType);
			break;
		default:
			System.out.println("未定義のモード");
			System.exit(0);
			break;
		}

		// ==================
		// 計測終了
		// ==================
		System.out.println("\n\t\t *END*");
		long end = System.currentTimeMillis();
		double time_second = (end - start) / 1000.0;
		System.out.println("time: " + time_second + "s.\n");
	}
}
