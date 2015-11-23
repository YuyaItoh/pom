package pomdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import pomdp.Action.ActionType;

public class PomdpParser {
	// =========================
	// Fields
	// =========================
	private String mFilePath;
	private State[] mStates;
	private Action[] mActions;

	// ========================
	// Constructors
	// ========================
	public PomdpParser(String pFilePath) {
		mFilePath = pFilePath;
		mStates = null;
		mActions = null;
	}

	// ========================
	// Getters
	// ========================
	public State[] getStates() {
		return mStates;
	}

	public Action[] getActions() {
		return mActions;
	}

	// ========================
	// Public Methods
	// ========================

	/**
	 * POMDPファイルから状態集合を取得する
	 */
	public void parse() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader((new File(mFilePath))));

			// ================
			// 行単位で読込み
			// ================
			String str;
			while ((str = br.readLine()) != null) {
				// コメントアウト
				if (str.length() == 0 || str.charAt(0) == '#') {
					continue;
				}

				// 空白区切りで要素を取得
				String words[] = str.split(" ");
				String item = words[0];

				// 先頭単語による分岐処理
				switch (item) {
				case "states:":
					// 単語からStateを作成する
					mStates = new State[words.length - 1];
					for (int i = 0; i < mStates.length; i++) {
						String stateStr = words[i + 1];
						mStates[i] = toState(stateStr);
					}
					break;
				case "actions:":
					mActions = new Action[words.length - 1];
					for (int i = 0; i < mStates.length; i++) {
						String actionStr = words[i + 1];
						mActions[i] = toAction(actionStr);
					}
					break;
				default:
					break;
				}

				// StateとActionを定義し終えたら終了
				if (mStates != null && mActions != null) {
					return;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("引数なし");
			System.out.println(e);
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

	// ========================
	// Private Methods
	// ========================
	/**
	 * Stringから状態を作成する．"s1_072_5" -> （index:1, quality:0.72, budget:5）
	 */
	private State toState(String pStr) {
		String[] data = pStr.substring(1, pStr.length()).split("_");
		int index = Integer.parseInt(data[0]);
		String qualityString = data[1].substring(0, 1) + "." + data[1].substring(1, data[1].length());
		double quality = Double.parseDouble(qualityString);
		int budget = Integer.parseInt(data[2]);

		return new State(index, quality, budget);
	}

	/**
	 * Stringからアクションを作成する．"EVAL_1" -> (type: EVAL, wage: 1)
	 */
	private Action toAction(String pStr) {
		String[] data = pStr.split("_");
		ActionType type = Action.parseType(data[0]);
		int wage = Integer.parseInt(data[1]);

		return new Action(type, wage);
	}
}
