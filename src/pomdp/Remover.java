package pomdp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stringから文字列を削除するだけの糞クラス
 */
public class Remover {

	/**
	 * 文字列中にあるドットを削除する
	 */
	public static String removeDot(String str) {
		String regex = "\\.";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}
}
