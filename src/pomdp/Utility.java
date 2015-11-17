package pomdp;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 複数クラスで利用するユーティリティ関数クラス
 */
public class Utility {

	/**
	 * 文字列中にあるドットを削除する
	 */
	public static String removeDot(String str) {
		String regex = "\\.";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	/**
	 * 小数点をn桁に丸める（四捨五入）
	 */
	public static double round(double val, int n) {
		BigDecimal bd = new BigDecimal(val);
		return bd.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 小数点をn桁に丸める（切り捨て）
	 */
	public static double roundDown(double val, int n) {
		BigDecimal bd = new BigDecimal(val);
		return bd.setScale(n, BigDecimal.ROUND_DOWN).doubleValue();
	}

	/**
	 * 正規分布に従い確率密度f(x)を求める
	 */
	public static double dnorm(double x, double mean, double var) {
		if (var == 0.0) {
			return mean == x ? 1.0 : 0.0;
		}
		return Math.exp(-0.5 * Math.pow(x - mean, 2.0) / var) / Math.sqrt(2.0 * Math.PI * var);
	}
}
