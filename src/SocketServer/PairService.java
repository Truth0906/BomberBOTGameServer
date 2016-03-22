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
			
			Random ran = new Random();
	        System.out.println(ran.nextInt(42)+1);
			
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
			    
				ScoreB = entry.getValue().getScore();
				
				if(ST.abs(ScoreA - ScoreB) < Min){
					
					Min = ST.abs(ScoreA - ScoreB);
					PickupB = entry.getValue();
					
				}
			}
			
			//Send Players into map
			
		}
	} 
}
