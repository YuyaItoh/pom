package pomdp;

public class Main {

	/**
	 * メインエントリポイント
	 * 
	 * @param args
	 * @throws MyException
	 */
	public static void main(String[] args) {
		// ==================
		// 計測開始
		// ==================
		long start = System.currentTimeMillis();

		// ==================
		// モデル構築
		// ==================
		String file = "test.input";
		Environment e = EnvironmentInitializer.init(file);
		e.build();
		e.toPomdpSolver("test.pomdp", 0);

		// ==================
		// 計測終了
		// ==================
		System.out.println("\n\t\t *END*");
		long end = System.currentTimeMillis();
		double time_second = (end - start) / 1000.0;
		System.out.println("time: " + time_second + "s.\n");
	}
}
