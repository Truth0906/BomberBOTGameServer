package ServerTool;

public class ERSystem {
	private static int win = 1;
//	private static int loss = 0;
//	private static float noWiner = 0.5f;
	private static int K = 16;
	public ERSystem(){
	}
	private static double getExpectedScore (int ScoreA, int ScoreB) {
        return 1.0 / (1.0 + Math.pow(10.0, ((ScoreB - ScoreA) / 400.0)));
	}
	public static int newScore(int ScoreA,int ScoreB){
		
		if(ScoreA < ScoreB){
			int temp = ScoreA;
			ScoreA = ScoreB;
			ScoreB = temp;
		}
		
		double temp = K * (win - getExpectedScore(ScoreA, ScoreB));
		if(abs(temp - (int)temp) >= 0.5){
			if(temp > 0) temp +=0.5;
			else temp -= 0.5;
		}
		return (int)temp;
	}
	private static double abs(double x){
		if(x < 0 ) return -1 * x;
		return x;
	}
}
