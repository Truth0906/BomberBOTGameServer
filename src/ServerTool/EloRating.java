package ServerTool;

public class EloRating {
	private static int win = 1;
	private static int K = 11; // 400
	private static double getExpectedScore (int ScoreA, int ScoreB) {
        return 1.0 / (1.0 + Math.pow(10.0, ((ScoreB - ScoreA) / 400.0)));
	}
	//The winner's score put first
	public static int newScore(int ScoreA,int ScoreB){
		double temp = K * (win - getExpectedScore(ScoreA, ScoreB));
		return (int)temp;
	}
}
