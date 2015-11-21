package pomdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PomdpParser {

	/**
	 * POMDPファイルから状態集合を取得する
	 */
	public static State[] getStates(String pFilePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader((new File(pFilePath))));
			State[] states = null;

			// ================
			// 行単位で読込み
			// ================
			String str;
			while ((str = br.readLine()) != null) {
				// コメントアウトは処理しない
				if (str.length() == 0 || str.charAt(0) == '#') {
					continue;
				}

				// 先頭単語を空白区切りで取得し，"states:"が出たら処理を行う
				String words[] = str.split(" ");
				if (!words[0].equals("states"))
					continue;

				// 単語からStateを作成する
				states = new State[words.length - 1];
				for (int i = 0; i < states.length; i++) {
					String stateStr = words[i + 1];
					states[i] = toState(stateStr);
				}
				return states;
			}
			return null;
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
	 * Stringから状態を作成する．"s1_072_5" -> （index:1, quality:0.72, budget:5）
	 */
	private static State toState(String pStr) {
		String[] data = pStr.substring(1, pStr.length()).split("_");
		int index = Integer.parseInt(data[0]);
		String qualityString = data[1].substring(0, 1) + "." + data[1].substring(1, data[1].length());
		double quality = Double.parseDouble(qualityString);
		int budget = Integer.parseInt(data[2]);

		return new State(index, quality, budget);
	}
}
