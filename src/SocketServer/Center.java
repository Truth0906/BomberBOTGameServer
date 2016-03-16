package SocketServer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import ObjectStructure.Player;
import Tool.ST;


public class Center {
	
	private static HashMap<String, Player> Players;
	
	private String LogName = "Center";
	public Center(){
		Players = new HashMap<String, Player>();
		readPlayerData();
	}
	public void newPlayer(String inputID, String inputPassword){
		Player NewPlayer = new Player();
		NewPlayer.setID(inputID);
		NewPlayer.setPassWord(inputPassword);
		
		Players.put(inputID.toLowerCase(), NewPlayer);
	}
	public boolean isPlayerExist(String inputID){
		if(inputID == null) return false;
		
		Player temp = Players.get(inputID.toLowerCase());
		
		return temp != null;
	}
	public boolean verifyPassword(String inputID, String inputPassword){
		if(inputID == null || inputPassword == null) return false;
		
		Player temp = Players.get(inputID.toLowerCase());
		if(temp == null) return false;
		
		return temp.getPassWord().equals(inputPassword);
	}
	private void readPlayerData(){
		try(BufferedReader br = new BufferedReader(new FileReader(Option.PlayerFileName))) {
			
		    String line = null;

		    while ((line = br.readLine()) != null) {
		    	Player temp = ST.StringToPlayer(line);
		    	Players.put(temp.getID().toLowerCase(), temp);
		    }
		    
		    br.close();
		    
		} catch (IOException e) {
			File f = new File(Option.PlayerFileName);

			try {
				f.createNewFile();
			} catch (IOException e1) {e1.printStackTrace();}
			
			ST.showOnScreen(LogName, "read player file fail, create new one");
		}
	}
}
