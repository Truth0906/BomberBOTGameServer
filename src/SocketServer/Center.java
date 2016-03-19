package SocketServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import ObjectStructure.*;

import Tool.ST;


public class Center extends Notification{
	
	private static Object Players_Lock;
	private static HashMap<String, Player> Players;
	
	private static Timer Timer;
	
	private String LogName = "Center";
	public Center(){
		Players_Lock = new Object();

		readOptions();
				
		Players = new HashMap<String, Player>();
		readPlayerData();
		
		Timer = new Timer(Option.savePlayerDataTime * 60 * 1000);
		Timer.addNotificationList(this);
		new Thread(Timer).start();
	}
	public void newPlayer(String inputID, String inputPassword){
		
		
		Player NewPlayer = new Player();
		NewPlayer.setID(inputID);
		NewPlayer.setPassWord(inputPassword);
		
		synchronized(Players_Lock){
			Players.put(inputID.toLowerCase(), NewPlayer);
		}
	}
	public boolean isPlayerExist(String inputID){
		if(inputID == null) return false;
		
		Player temp = null;
		synchronized(Players_Lock){
			temp = Players.get(inputID.toLowerCase());
		}
		return temp != null;
	}
	public boolean verifyPassword(String inputID, String inputPassword){
		if(inputID == null || inputPassword == null) return false;
		
		Player temp = null;
		synchronized(Players_Lock){
			temp = Players.get(inputID.toLowerCase());
		}
		if(temp == null) return false;
		
		return temp.getPassWord().equals(inputPassword);
	}
	private void writeOptions(){
		
		File OptionFile = null;
		
		try {
			OptionFile = new File("Option");
			if(! OptionFile.exists()){
				ST.showOnScreen(LogName, "Find Option file fail, create new one");
				OptionFile.createNewFile();
			}
		} catch (IOException e) {
			ST.showOnScreen(LogName, "Error! create new Option file fails");
			return;
		}
		
		try(BufferedWriter Writer = new BufferedWriter(new FileWriter(OptionFile))) {
			
			Option temp = new Option();
			
			String OptionString = ST.OptionToString(temp);
			
			OptionString = OptionString.replaceAll("\\{", "\\{" + System.lineSeparator() + "\t");
			OptionString = OptionString.replaceAll(",\"", "," + System.lineSeparator() + "\t\"");
			OptionString = OptionString.replaceAll("}", System.lineSeparator() + "}");
			
			Writer.write(OptionString);

			Writer.close();
		    
		} catch (IOException e) {
			ST.showOnScreen(LogName, "Error! write Option file fails");
			return;
		}
		ST.showOnScreen(LogName, "Write Option file success");
	}
	private void readOptions(){
		
		String OptionString = "";
		
		try(BufferedReader br = new BufferedReader(new FileReader("Option"))) {
			
		    String line = null;
		    
			while ((line = br.readLine()) != null) {
				OptionString += line + System.lineSeparator();
			}
			br.close();
			
		} catch (IOException e) {
			writeOptions();
		}
				
		ST.StringToOption(OptionString);
		writeOptions();
		
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
	private void writePlayerData(){
		synchronized(Players_Lock){
			
			File PlayerFile = null;
			
			try {
				PlayerFile = new File(Option.PlayerFileName);
				if(! PlayerFile.exists()){
					PlayerFile.createNewFile();
				}
			} catch (IOException e) {
				ST.showOnScreen(LogName, "Error! create new player file fails");
				return;
			}
			
			
			try(BufferedWriter Writer = new BufferedWriter(new FileWriter(PlayerFile))) {
				
				HashMap<String, Player> selects = new HashMap<String, Player>();

				for(Entry<String, Player> entry : selects.entrySet()) {
				    
					
					Player TempPlayer = entry.getValue();
					String line = ST.PlayerToString(TempPlayer);
					Writer.write(line);
					Writer.newLine();
					
				}

				Writer.close();
			    
			} catch (IOException e) {
				ST.showOnScreen(LogName, "Error! write player file fails");
				return;
			}
		}
	}
	@Override
	public void TimeUp() {
		writePlayerData();
		ST.showOnScreen(LogName, "Save player data success");
	}
}
