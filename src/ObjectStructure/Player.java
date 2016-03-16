package ObjectStructure;

import SocketServer.Option;

public class Player {
	private String ID;
	private int Score;
	private String Password;
	private int Wins;
	private int Losses;
	
	public Player(){
		ID = null;
		Score = Option.InitScore;
		Password = null;
		Wins = 0;
		Losses = 0;
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public int getScore() {
		return Score;
	}
	public void setScore(int score) {
		Score = score;
	}
	public String getPassWord() {
		return Password;
	}
	public void setPassWord(String passWord) {
		Password = passWord;
	}
	public int getWins() {
		return Wins;
	}
	public void setWins(int wins) {
		Wins = wins;
	}
	public int getLosses() {
		return Losses;
	}
	public void setLosses(int losses) {
		Losses = losses;
	}
}
