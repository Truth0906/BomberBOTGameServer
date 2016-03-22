package ObjectStructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import Tool.ST;

public class Map {
	
	private Player A, B;
	private int map[][];
	private Timer Timer;
	private boolean isContiue;
	private boolean isPlayerALive;
	private boolean isPlayerBLive;
	
	private final int Wall = -1;
	private final int Path =  0;
	
	private String LogName = "Map";
	public Map(Player inputA, Player inputB){
		A = inputA;
		B = inputB;
		
		A.setPlayingMap(this);
		B.setPlayingMap(this);
		
		isContiue = true;
		isPlayerALive = true;
		isPlayerBLive = true;
		
		initMap();
		new Thread(A).start();
		new Thread(B).start();
		
		ST.showOnScreen(LogName, A.getID() + " and " + B.getID() + " join game");
		
		Message Msg = new Message();
		
		Msg.setMsg("Map", "QvQ");
		Msg.setMsg("Message", "Join game");
		Msg.setMsg("ErrorCode", 0);
		
		sendMsg(A, Msg);
		sendMsg(B, Msg);
	}
	private void initMap(){
		map = new int[13][15];
		
		for(int y = 0 ; y < 13 ; y++){
			for(int x = 0 ; x < 15 ; x++){
				if(y % 2 == 1 && x % 2 ==1) map[y][x] = Wall;
				else map[y][x] = Path;
			}
		}
		
	}
	private boolean sendMsg(Player inputPlayer, Message inputMsg){
		
		BufferedWriter Writer = inputPlayer.getWriter();
		BufferedReader Reader = inputPlayer.getReader();
		
		String Msg = ST.MessageToString(inputMsg);
		try {
			Writer.write(Msg + "\r\n");
			Writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isContiue() {
		return isContiue;
	}
	
}
