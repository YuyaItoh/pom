package pomdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import pomdp.Action.ActionType;

public class EnvironmentInitializer {
	/**
	 * Environmentの作成
	 */
	public static Environment init(String pFilePath) {
		// ***********************************
		// 準備物
		// + 予算
		// + ワーカ集合(ability, freq)
		// + タスク集合(difficulty, base_wage)
		// + 行動集合(action_type, wage)
		// + Evalの賃金
		// + ベース賃金からの探索範囲
		// ***********************************
		int budget = 0;
		int evalWage = 0;
		int searchRange = 0;
		TaskSet tasks = new TaskSet();
		WorkerSet workers = new WorkerSet();
		ActionSet actions = new ActionSet();

		try {
			File file = new File(pFilePath);
			BufferedReader br = new BufferedReader(new FileReader(file));

			// ================
			// 行単位で読込み
			// ================
			String str;
			while ((str = br.readLine()) != null) {
				// コメントアウトは処理しない
				if (str.length() == 0 || str.charAt(0) == '#') {
					continue;
				}

				char c = str.charAt(0); // 記号(B, T, W, E)
				String data[] = str.substring(1, str.length()).trim().split(" "); // データ部分(空白区切り)

				// 先頭文字により分岐処理
				switch (c) {
				case 'B': // 予算
					budget = Integer.parseInt(data[0]);
					break;
				case 'T': // タスク<diff, wage>
					tasks.setDivNum(data.length);
					for (int i = 0; i < data.length; i++) {
						double difficulty = Double.parseDouble(data[i].split(",")[0]);
						int baseWage = Integer.parseInt(data[i].split(",")[1]);
						Subtask subtask = new Subtask(difficulty, baseWage);
						tasks.putSubtask(i + 1, subtask);
					}
					break;
				case 'W': // ワーカ(ability, freq)
					for (int i = 0; i < data.length; i++) {
						double ability = Double.parseDouble(data[i].split(",")[0]);
						double freq = Double.parseDouble(data[i].split(",")[1]);
						workers.add(new Worker(ability), freq);
					}
					break;
				case 'E': // Eval
					evalWage = Integer.parseInt(data[0]);
					break;
				case 'N': // ベース金額の近傍範囲
					searchRange = Integer.parseInt(data[0]);
					break;
				default: // 該当無し
					System.out.println("Undefined Keyword");
					break;
				}
			}

			// =======================
			// 行動集合の作成
			// =======================

			// Eオプションがある or Eが0でない場合，EVALアクションを追加
			if (evalWage != 0) {
				actions.add(new Action(ActionType.EVAL, evalWage));
			}

			// NEXT, CURRアクションの追加
			for (Subtask st : tasks.getSubtasks()) {
				// サブタスクのベース賃金±1でアクションを作成する
				for (int i = -1 * searchRange; i <= searchRange; i++) {
					Action ac = new Action(ActionType.CURR, st.getBaseWage() + i);
					Action an = new Action(ActionType.NEXT, st.getBaseWage() + i);
					actions.add((Action) ac);
					actions.add((Action) an);
				}
			}

			// =======================
			// 環境クラスの作成
			// =======================
			Environment env = new Environment(workers, tasks, actions, budget);
			br.close();
			return env;
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("引数なし");
			System.out.println(e);
			return null;
		}
	}
}
