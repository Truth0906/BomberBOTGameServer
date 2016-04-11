package ServerTool;

public class EloRating {
	private static int win = 1;
	private static int K = 16; // 400
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
		return (int)temp;
	}
}
