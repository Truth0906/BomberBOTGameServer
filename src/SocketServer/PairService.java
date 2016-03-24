package SocketServer;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import ObjectStructure.*;
import Tool.ST;


public class PairService  extends Notification{
	
	private static Object PlayerPool_Lock;
	private static HashMap<String, Player> PlayerPool;
	private static Timer Timer;
	
	private String LogName = "PairService";
	public PairService(){
		
		PlayerPool_Lock = new Object();
		
		PlayerPool = new HashMap<String, Player>();
		
		Timer = new Timer(1000);
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
			
			int RandomIndex = new Random().nextInt(PlayerPool.size());
			
			int i = 0;
			
			Player PickupA = null;
			Player PickupB = null;
			int ScoreA = 0;
			int ScoreB = 0;
			int Min = Integer.MAX_VALUE;
			
			for(Entry<String, Player> entry : PlayerPool.entrySet()) {
			    
				if(i++ < RandomIndex ) continue;
				
				PickupA = entry.getValue();
				ScoreA = PickupA.getScore();
				
				break;
			}
			
			for(Entry<String, Player> entry : PlayerPool.entrySet()) {
			    
				Player temp = entry.getValue();
				
				ScoreB = temp.getScore();
				
				if(ST.abs(ScoreA - ScoreB) < Min && !(temp.getID().equals(PickupA.getID()))){
					
					Min = ST.abs(ScoreA - ScoreB);
					PickupB = entry.getValue();
					
				}
			}
			
			PlayerPool.remove(PickupA.getID().toLowerCase());
			PlayerPool.remove(PickupB.getID().toLowerCase());
			
			ST.showOnScreen(LogName, PickupA.getID() + " and " + PickupB.getID() + " ready into map");
			
			new Thread(new Map(PickupA, PickupB)).start();
			
		}
	} 
}
