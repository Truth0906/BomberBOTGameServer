package ObjectStructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import SocketServer.Option;
import Tool.ST;

public class Player implements Runnable{
	private String ID;
	private int Score;
	private String Password;
	private int Wins;
	private int Losses;
	
	private transient int MatchRound;
	private transient BufferedWriter Writer;
	private transient BufferedReader Reader;
	private transient Message Message;
	private transient Map PlayingMap;
	
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

	public BufferedWriter getWriter() {
		return Writer;
	}

	public void setWriter(BufferedWriter writer) {
		Writer = writer;
	}

	public BufferedReader getReader() {
		return Reader;
	}

	public void setReader(BufferedReader reader) {
		Reader = reader;
	}

	public Player(){
		ID = null;
		Score = Option.InitScore;
		Password = null;
		Wins = 0;
		Losses = 0;
		Message = null;
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
	
	@Override
	public void run() {
		Message temp;
		while(PlayingMap.isContiue()){
			
			temp = receiveMsg();
			if(temp != null) Message = temp;
			
		}
	}
	
	private Message receiveMsg(){
		Message resultMsg = null;
		
		String receivedString = null;
		try {
			
			receivedString = Reader.readLine();
			resultMsg = ST.StringToMessage(receivedString);
			
		} catch (IOException e) {e.printStackTrace();}
		
		return resultMsg;
	}

	public Message getMessage() {
		return Message;
	}

	public void setPlayingMap(Map playingMap) {
		PlayingMap = playingMap;
	}
}
