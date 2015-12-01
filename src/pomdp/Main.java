package pomdp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pomdp.Agent.AgentType;

public class Main {
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
	 * シミュレーションを1回実行する
	 */
	public void execSimulation(String pEnvironmentPath, String pAgentType, String pQueuePath, String pPomdpPath,
			String pPolicyPath, int pIterationNum) {
		// =====================
		// 環境構築
		// =====================
		Environment environment = EnvironmentInitializer.generate(pEnvironmentPath);
		environment.build();

		// FIXME: ワーカキューファイル，resultファイルを環境ファイルと同ディレクトリに出力
		File pEnvFile = new File(pEnvironmentPath);
		String parent = (pEnvFile.getParent() == null) ? "." : pEnvFile.getParent();

		String currentTimeString = getCurrentTime();

		// =====================
		// ワーカキューの設定
		// =====================
		if (pQueuePath == null) {
			// 待ち行列ファイルの作成（および書込み）
			environment.createWorkerQueue();
			environment.writeWorkerQueue(parent + "/queue_" + currentTimeString + ".conf");
		} else {
			// 待ち行列ファイルの読込み
			environment.readWorkerQueue(pQueuePath); // 読込
		}

		// =====================
		// エージェントの設定
		// =====================
		Agent agent;
		switch (pAgentType) {
		case "equal":
			agent = new EqualAgent(environment, AgentType.EQUAL, pIterationNum);
			break;
		case "dif":
			agent = new DifAgent(environment, AgentType.DIF, pIterationNum);
			break;
		case "pomdp":
			agent = new PomdpAgent(environment, pPolicyPath, pPomdpPath, AgentType.POMDP);
			break;
		default:
			agent = null;
		}

		// =====================
		// シミュレーションの実行
		// =====================
		System.out.println("simulating...");
		String outputPath = parent + "/" + getPreffix(new File(pEnvironmentPath).getName()) + "_" + pAgentType + "_"
				+ currentTimeString + ".result";
		Simulator sim = new Simulator(environment, agent);
		sim.run(outputPath);
		System.out.println("finished");
	}

	// =========================
	// Private Methods
	// =========================

	/**
	 * 現在時刻を"MMdd_HHmmss"の形で取得
	 */
	private String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmmss");
		return sdf.format(date);
	}

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
	 * pomdpファイル名の作成（.environment -> .pomdp）
	 */
	private String makePomdpFileName(String pInput) {
		Pattern p = Pattern.compile(".environment");
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
		// [実行例1] POMDPファイルの作成
		// 環境"test.environment"でpomdpファイルを作成する
		// java pomdp/Main --mode pomdp --environment test.environment
		//
		// [実行例2] シミュレーションの実行
		// 環境"test.environemnt", pomdpファイル"test.pomdp", 方策"test.policy"
		// ワーカキュー"queue.conf", シミュレーション5回, POMDPエージェント
		// java pomdp/Main --mode simulation <options>
		//
		// いずれにしてもライブラリのクラスパスを通すことに注意
		// **************************************************************

		long start = System.currentTimeMillis();
		Main m = new Main();

		// ==================
		// 引数準備
		// ==================
		String mode = null; // 実行モード(pomdp, mdp, simulation)
		String environmentPath = null; // 環境パス
		String policyPath = null; // 方策パス
		String pomdpPath = null; // pomdpパス
		int iterationNum = 1; // サブタスク繰り返し回数
		String agentType = null; // エージェントタイプ
		String queuePath = null; // ワーカキューパス

		// ==================
		// オプション設定
		// ==================
		Options options = new Options();

		// 各モード共通
		options.addOption("m", "mode", true, "execution mode(pomdp, mdp, simulation)");
		options.addOption("e", "environment", true, "envieonment file(***.environment)");
		options.addOption("h", "help", false, "help");
		options.addOption("d", "debug", false, "debug mode");

		// simulationモードのみで利用
		options.addOption("c", "policy", true, "policy file(***.policy.json)");
		options.addOption("p", "pomdp", true, "pomdp file(***.pomdp)");
		options.addOption("a", "agent", true, "agent type(equal, dif, pomdp)");
		options.addOption("q", "queue", true, "worker queue file(queue.conf)");
		options.addOption("i", "iteration", true, "number of iteration");

		// ==================
		// 構文解析
		// ==================
		CommandLineParser parser = new DefaultParser();
		CommandLine cl;
		try {
			cl = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("parse error;");
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("help", options);
			return;
		}

		if (cl.hasOption("mode")) {
			mode = cl.getOptionValue("mode");
		}
		if (cl.hasOption("environment")) {
			environmentPath = cl.getOptionValue("environment");
		}
		if (cl.hasOption("agent")) {
			agentType = cl.getOptionValue("agent");
		}
		if (cl.hasOption("policy")) {
			policyPath = cl.getOptionValue("policy");
		}
		if (cl.hasOption("pomdp")) {
			pomdpPath = cl.getOptionValue("pomdp");
		}
		if (cl.hasOption("queue")) {
			queuePath = cl.getOptionValue("queue");
		}
		if (cl.hasOption("iteration")) {
			iterationNum = Integer.parseInt(cl.getOptionValue("iteration"));
		}

		// ==================
		// help実行
		// ==================
		if (cl.hasOption("h")) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("help", options);

			System.out.println("\nExample");
			System.out.println("cmd -m pomdp -e test.environment ");
			System.out.println(
					"cmd -m simulation --environment test.environment --agent pomdp --pomdp test.pomdp --policy test.policy --queue queue.conf");
			System.out.println(
					"cmd -m simulation --envitonment test.environment --agent equal --iteration 3 --queue queue.conf");
			return;
		}

		// ==================
		// XXX デバッグモード
		// ==================

		// -- 設定項目 -----------------------------------------------
		boolean debug = true;
		boolean simulation = true; // true: simulation, false: pomdp
		// ----------------------------------------------------------

		if (cl.hasOption("debug") || debug) {
			System.out.println("=========================");
			System.out.println("  WARNING: Debug Mode    ");
			System.out.println("=========================");

			if (simulation) {
				// シミュレーションのデバッグ
				// (env, agent, queue, pomdp, policy, iteration)
				m.execSimulation("data/min.environment", "equal", null, "data/min.pomdp", "data/min.policy.json", 5);
			} else {
				// POMDPファイル作成のデバッグ
				m.execPomdp("test.environment");
			}

			System.out.println("\n\t\t *END*");
			long end = System.currentTimeMillis();
			double time_second = (end - start) / 1000.0;
			System.out.println("time: " + time_second + "s.\n");
			return;
		}

		// ==================
		// モード実行
		// ==================
		switch (mode) {
		case "pomdp":
			m.execPomdp(environmentPath);
			break;
		case "mdp:":
			m.execMdp(environmentPath);
			break;
		case "simulation":
			m.execSimulation(environmentPath, agentType, queuePath, pomdpPath, policyPath, iterationNum);
			break;
		default:
			System.out.println("Undefined Execution Mode --" + mode);
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
