package BomberBOTGameServer;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ServerObjectStructure.Map;
import ServerObjectStructure.Notification;
import ServerObjectStructure.Player;
import ServerObjectStructure.Timer;
import ServerTool.ServerTool;

public class PairService extends Notification{
	
	private static Object PlayerPool_Lock;
	private static HashMap<String, Player> PlayerPool;
	private static Timer Timer;
	
	private String LogName = "PairService";
	public PairService(){
		
		PlayerPool_Lock = new Object();
		
		PlayerPool = new HashMap<String, Player>();
		
		Timer = new Timer(ServerOptions.PairTimeInterval);
		Timer.addNotificationList(this);
		new Thread(Timer).start();
	}
	
	public void add(Player inputPlayer){
		
		synchronized(PlayerPool_Lock){
			PlayerPool.put(inputPlayer.getID().toLowerCase(), inputPlayer);
		}
		
	}
	
	@Override
	public void TimeUp() {
		
		synchronized(PlayerPool_Lock){

			if(PlayerPool.size() < 2) return;
						
			Player PickupA = null;
			Player PickupB = null;
			int ScoreA = 0;
			int ScoreB = 0;
			int Min = Integer.MAX_VALUE;
			
			for(Entry<String, Player> entry : PlayerPool.entrySet()) {
			    
				Player temp = entry.getValue();
				
				if(temp.isServerAI()) continue;
				
				ScoreA = temp.getScore();
				PickupA = temp;
				break;
			}
			
			if(PickupA == null) return;
			//Find a player that is not Server Test AI
			boolean isTestAI = PickupA.isTestAI();
			
			ArrayList<Player> PlayerBList = new ArrayList<Player>();
			
			for(Entry<String, Player> entry : PlayerPool.entrySet()) {
			    
				Player temp = entry.getValue();
				
				ScoreB = temp.getScore();
				
				if(isTestAI && (!temp.isServerAI()) && (!temp.isTestAI())) continue;
				if((!isTestAI) && (temp.isServerAI() || temp.isTestAI())) continue;
				
				if(ServerTool.abs(ScoreA - ScoreB) < Min && !(temp.getID().equals(PickupA.getID()))){
					
					Min = ServerTool.abs(ScoreA - ScoreB);
					PlayerBList = new ArrayList<Player>();
					PlayerBList.add(temp);
					
				}
				else if(ServerTool.abs(ScoreA - ScoreB) == Min){
					PlayerBList.add(temp);
				}
			}
			
			if(PlayerBList.size() == 0){
				for(Entry<String, Player> entry : PlayerPool.entrySet()) {
				    
					Player temp = entry.getValue();
					
					ScoreB = temp.getScore();
					
					if(ServerTool.abs(ScoreA - ScoreB) < Min && !(temp.getID().equals(PickupA.getID()))){
						
						Min = ServerTool.abs(ScoreA - ScoreB);
						PlayerBList = new ArrayList<Player>();
						PlayerBList.add(temp);
						
					}
					else if(ServerTool.abs(ScoreA - ScoreB) == Min){
						PlayerBList.add(temp);
					}
				}
				if(PlayerBList.size() == 0) return;
			}
			
			PickupB = PlayerBList.get(new SecureRandom().nextInt(PlayerBList.size()));
			
			PlayerPool.remove(PickupA.getID().toLowerCase());
			PlayerPool.remove(PickupB.getID().toLowerCase());
			
			ServerTool.showOnScreen(LogName, PickupA.getID() + " and " + PickupB.getID() + " ready into map");
			
			new Thread(new Map(PickupA, PickupB)).start();
			
		}
	}
}
