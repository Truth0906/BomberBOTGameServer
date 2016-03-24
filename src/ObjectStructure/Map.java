package ObjectStructure;

import SocketServer.Option;
import Tool.ERSystem;
import Tool.ErrorCode;
import Tool.ST;

public class Map extends Notification implements Runnable {
	
	private Object checkPlayerLock;
	private Player A, B;
	private int map[][];
	private Timer Timer;
	private int MapTimeUpTimes;
	
	private boolean isContiue;
	private boolean isPlayerALive;
	private boolean isPlayerBLive;
	
	private final int Wall = -1;
	private final int Path =  0;
	
	private final int PlayerA =  101;
	private final int PlayerB =  102;
	
	private int PlayerLocationA[];
	private int PlayerLocationB[];
		
	private String LogName = "Map";
	public Map(Player inputA, Player inputB){
		
		checkPlayerLock = new Object();
		
		A = inputA;
		B = inputB;
		
		A.setState(State.InMap);
		B.setState(State.InMap);
		
		isContiue = true;
		isPlayerALive = true;
		isPlayerBLive = true;

		PlayerLocationA = new int[2];
		PlayerLocationB = new int[2];
		
		MapTimeUpTimes = 0;
		
		initMap();
						
	}
	private void initMap(){
		
		int type = 1;
		
		map = new int[13][15];
		
		for(int y = 0 ; y < 13 ; y++){
			for(int x = 0 ; x < 15 ; x++){
				if(y % 2 == 1 && x % 2 ==1) map[y][x] = Wall;
				else map[y][x] = Path;
			}
		}
		
		map[6][0] = PlayerA;
		map[6][14] = PlayerB;
				
		PlayerLocationA[0] = 6;
		PlayerLocationA[1] = 0;
		PlayerLocationB[0] = 6;
		PlayerLocationB[1] = 14;
	}
	@Override
	public void run() {
		
		ST.showOnScreen(LogName, A.getID() + " and " + B.getID() + " join game");
		
		Message Msg = new Message();
		
		Msg.setMsg("Map", ST.MapToString(map));
		Msg.setMsg("Message", "Game Joined");
		Msg.setMsg("ErrorCode", ErrorCode.Success);
		
		A.sendMsg(Msg);
		B.sendMsg(Msg);
		
		
		Timer = new Timer(Option.TimeInterval);
		Timer.addNotificationList(this);
		new Thread(Timer).start();
		
		do{
			try {
				Thread.sleep(Option.TimeInterval / 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized(checkPlayerLock){
				if(isPlayerALive == false || isPlayerBLive == false){
					break;
				}
			}
		}while(isContiue);
		
		int ScoreTemp = ERSystem.newScore(A.getScore(), B.getScore());
		
		if(isPlayerALive){
			A.setScore(A.getScore() + ScoreTemp);
			B.setScore(B.getScore() - ScoreTemp);
			
			ST.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " " + A.getID() + " Win!");
		}
		else if(isPlayerBLive){
			A.setScore(A.getScore() - ScoreTemp);
			B.setScore(B.getScore() + ScoreTemp);
			
			ST.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " " + B.getID() + " Win!");
		}
		else{
			
			ST.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " " + B.getID() + " in a dead heat!");
			
		}
		
		A.setState(State.InPlayerList);
		B.setState(State.InPlayerList);
	}
	@Override
	public void TimeUp() {
		
		if(MapTimeUpTimes >= Option.GameTimeUp){
			this.isContiue = false;
			return;
		}
		++MapTimeUpTimes;
		
		synchronized(checkPlayerLock){
			Message PlayerMessageA = A.getMessage();
			Message PlayerMessageB = B.getMessage();
			
			if(isObjectNull(PlayerMessageA, PlayerMessageB)) return;
			
			String NextMoveA = PlayerMessageA.getMsg("NextMove");
			String NextMoveB = PlayerMessageA.getMsg("NextMove");
			
			if(isObjectNull(NextMoveA, NextMoveB)) return;
			
		}
	}
	private boolean isObjectNull(Object inputObjectA, Object inputObjectB){
		
		Message Msg = new Message();
		
		Msg.setMsg("Message", "Your input is null");
		Msg.setMsg("ErrorCode", ErrorCode.ParameterError);
		
		if(inputObjectA == null){
			A.sendMsg(Msg);
			isPlayerALive = false;
		}
		if(inputObjectB == null){
			B.sendMsg(Msg);
			isPlayerBLive = false;
		}
		if(isPlayerALive == false || isPlayerBLive == false) return true;
		return false;
	}
}
