package BomberBOTGameServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import ServerObjectStructure.Message;
import ServerObjectStructure.Notification;
import ServerObjectStructure.Player;
import ServerObjectStructure.State;
import ServerObjectStructure.Timer;
import ServerTool.ServerTool;


public class ServerCenter extends Notification{
	
	private static String PlayerDataFile = "Players";
	private static String OptionDataFile = "Options";
	
	private static Object Players_Lock;
	private static HashMap<String, Player> Players;
	
	private static PairService PairService;
	
	private static Timer Timer;
	
	private static String LogName = "Center";
	public ServerCenter(){
		Players_Lock = new Object();

		readOptions();
				
		Players = new HashMap<String, Player>();
		readPlayerData();
		
		PairService = new PairService();
		
//		Timer = new Timer(Options.savePlayerDataTime * 60 * 1000);
//		//Timer = new Timer(6000);
//		Timer.addNotificationList(this);
//		new Thread(Timer).start();
	}
	public static boolean checkPlayerState(String inputID, String inputState){
		Player P = null;
		
		synchronized(Players_Lock){
			P = Players.get(inputID.toLowerCase());
		}
		
		return P == null ? false : P.getState().equals(inputState);
	}
	public static void addPairPlayer(String inputID, BufferedWriter inputWriter){
		Player P = null;
		
		synchronized(Players_Lock){
			P = Players.get(inputID.toLowerCase());
			P.setState(State.InPairPool);
			P.setWriter(inputWriter);
		}
		
		PairService.add(P);
	}
	public static void setPlayerMove(String inputID, Message inputMessage, BufferedWriter inputWriter){
		Player P = null;
		
		synchronized(Players_Lock){
			P = Players.get(inputID.toLowerCase());
			P.setWriter(inputWriter);
		}

		P.setMessage(inputMessage);
	}
	public static void newPlayer(String inputID, String inputPassword){
		
		Player NewPlayer = new Player();
		NewPlayer.setID(inputID);
		NewPlayer.setPassWord(ServerTool.SHA256(inputPassword));
		
		synchronized(Players_Lock){
			Players.put(inputID.toLowerCase(), NewPlayer);
			writePlayerData();
		}
	}
	public static boolean isPlayerExist(String inputID){
		if(inputID == null) return false;
		
		Player temp = null;
		synchronized(Players_Lock){
			temp = Players.get(inputID.toLowerCase());
		}
		return temp != null;
	}
	public static boolean verifyPassword(String inputID, String inputPassword){
		if(inputID == null || inputPassword == null) return false;
		
		Player temp = null;
		synchronized(Players_Lock){
			temp = Players.get(inputID.toLowerCase());
		}
		if(temp == null) return false;
		
		return temp.getPassWord().equals(ServerTool.SHA256(inputPassword));
	}
	private void writeOptions(){
		
		File OptionFile = null;
		
		try {
			OptionFile = new File(OptionDataFile);
			if(! OptionFile.exists()){
				ServerTool.showOnScreen(LogName, "Find Option file fail, create new one");
				OptionFile.createNewFile();
			}
		} catch (IOException e) {
			ServerTool.showOnScreen(LogName, "Error! create new Option file fails");
			return;
		}
		
		try(BufferedWriter Writer = new BufferedWriter(new FileWriter(OptionFile))) {
			
			ServerOptions temp = new ServerOptions();
			
			String OptionString = ServerTool.OptionToString(temp);
			
			OptionString = OptionString.replaceAll("\\{", "\\{" + System.lineSeparator() + "\t");
			OptionString = OptionString.replaceAll(",\"", "," + System.lineSeparator() + "\t\"");
			OptionString = OptionString.replaceAll("}", System.lineSeparator() + "}");
			
			Writer.write(OptionString);

			Writer.close();
		    
		} catch (IOException e) {
			ServerTool.showOnScreen(LogName, "Error! write Option file fails");
			return;
		}
	}
	public void readOptions(){
		
		String OptionString = "";
		
		try(BufferedReader br = new BufferedReader(new FileReader(OptionDataFile))) {
			
		    String line = null;
		    
			while ((line = br.readLine()) != null) {
				OptionString += line + System.lineSeparator();
			}
			br.close();
			
		} catch (IOException e) {
			writeOptions();
		}
				
		ServerTool.StringToOption(OptionString);
		writeOptions();
		ServerTool.showOnScreen(LogName, "Read Option file success");
	}
	private void readPlayerData(){
		try(BufferedReader br = new BufferedReader(new FileReader(PlayerDataFile))) {
			
		    String line = null;

		    while ((line = br.readLine()) != null) {
		    	Player temp = ServerTool.StringToPlayer(line);
		    	
		    	
		    	
		    	Players.put(temp.getID().toLowerCase(), temp);
		    }
		    
		    br.close();
		    
		} catch (IOException e) {
			File f = new File(PlayerDataFile);

			try {
				f.createNewFile();
			} catch (IOException e1) {e1.printStackTrace();}
			
			ServerTool.showOnScreen(LogName, "Find player file fail, create new one");
		}
		ServerTool.showOnScreen(LogName, "Read Player file success");
	}
	public static void writePlayerData(){
		synchronized(Players_Lock){
			
			File PlayerFile = null;
			
			try {
				PlayerFile = new File(PlayerDataFile);
				if(! PlayerFile.exists()){
					PlayerFile.createNewFile();
				}
			} catch (IOException e) {
				ServerTool.showOnScreen(LogName, "Error! create new player file fails");
				return;
			}
			
			try(BufferedWriter Writer = new BufferedWriter(new FileWriter(PlayerFile))) {
				
				for(Entry<String, Player> entry : Players.entrySet()) {
				    
					Player TempPlayer = entry.getValue();
					if(TempPlayer.getID().startsWith("HelloAI") && TempPlayer.getID().length() == "HelloAI164B5301C4960F72AC00A4E5670569718FD81F421DE45F5B857195AD1AB54E43".length()) continue;
					String line = ServerTool.PlayerToString(TempPlayer);
					Writer.write(line);
					Writer.newLine();
					
				}

				Writer.close();
			    
			} catch (IOException e) {
				ServerTool.showOnScreen(LogName, "Error! write player file fails");
				return;
			}
		}
	}
	public static String getPlayerInformation(){
		
		HashMap<String, Integer> ScoreMap = new HashMap<String, Integer>();
		
		synchronized(Players_Lock){
			for(Entry<String, Player> entry : Players.entrySet()) {
			    
				Player TempPlayer = entry.getValue();
				ScoreMap.put(TempPlayer.getID(), TempPlayer.getScore());
			}
		}
		
		return ServerTool.ScoreMapToString(ScoreMap);
	}
	@Override
	public void TimeUp() {
//		System.gc();
//		ST.showOnScreen(LogName, "Free system resource");
	}
}
