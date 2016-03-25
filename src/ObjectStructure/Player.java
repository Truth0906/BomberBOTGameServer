package ObjectStructure;

import java.io.BufferedWriter;
import java.io.IOException;

import SocketServer.Option;
import Tool.ST;

public class Player{
	private String ID;
	private int Score;
	private String Password;
	private int Wins;
	private int Losses;
	private int Tie;
	private transient int MatchRound;
	private transient BufferedWriter Writer;
	private transient Message Message;
	private transient State Status;
	private boolean isLive;
	
	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public Player(){
		ID = null;
		Score = Option.InitScore;
		Password = null;
		Wins = 0;
		Losses = 0;
		Tie = 0;
		Message = null;
		Status = new State();
	}
	
	public int getTie() {
		return Tie;
	}

	public void setTie(int inputTie) {
		Tie = inputTie;
	}

	public String getState(){
		return Status.toString();
	}
	public void setState(String inputState){
		Status.setStatus(inputState);
	}
	
	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public int getMatchRound() {
		return MatchRound;
	}

	public void setMatchRound(int matchRound) {
		MatchRound = matchRound;
	}

	public void setWriter(BufferedWriter writer) {
		Writer = writer;
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
	public boolean sendMsg(Message inputMsg){
		
		String Msg = ST.MessageToString(inputMsg);
		try {
			Writer.write(Msg + "\r\n");
			Writer.flush();
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
		
	}
//	private Message receiveMsg(){
//		Message resultMsg = null;
//		
//		String receivedString = null;
//		try {
//			
//			receivedString = Reader.readLine();
//			resultMsg = ST.StringToMessage(receivedString);
//			
//		} catch (IOException e) {e.printStackTrace();}
//		
//		return resultMsg;
//	}

	public Message getMessage() {
		return Message;
	}
}
