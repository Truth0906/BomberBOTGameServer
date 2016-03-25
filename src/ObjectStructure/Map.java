package ObjectStructure;

import SocketServer.Option;
import Tool.ERSystem;
import Tool.ErrorCode;
import Tool.ST;

public class Map extends Notification implements Runnable {
	
	private Object checkPlayerLock;
	private Player A, B;
	private int MainMap[][];
	private Timer Timer;
	private int MapTimeUpTimes;
	
	private boolean isContiue;
	private boolean isPlayerALive;
	private boolean isPlayerBLive;
	
	private final int PlayerA = 			0x0100;
	private final int PlayerB =  			0x0200;
	
	private final int putBombBeforeMove = 	0x10;
	private final int putBombAfterMove =  	0x20;
	
	private final int Move_Up = 		  	0x01;
	private final int Move_Down =  		  	0x02;
	private final int Move_Left = 			0x03;
	private final int Move_Right = 			0x04;
	
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
		
		MainMap = new int[13][15];
		
		for(int y = 0 ; y < 13 ; y++){
			for(int x = 0 ; x < 15 ; x++){
				if(y % 2 == 1 && x % 2 ==1) MainMap[y][x] = Wall;
				else MainMap[y][x] = Path;
			}
		}
		
		MainMap[6][0] = PlayerA;
		MainMap[6][14] = PlayerB;
				
		PlayerLocationA[0] = 6;
		PlayerLocationA[1] = 0;
		PlayerLocationB[0] = 6;
		PlayerLocationB[1] = 14;
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
			
			int MoveA = Integer.parseInt(NextMoveA);
			int MoveB = Integer.parseInt(NextMoveB);
			
			setMove(MoveA, PlayerLocationA);
			
		}
	}
	
	private void setMove(int InputMove, int [] InputPlayerLocation){
		
		int Y = InputPlayerLocation[0];
		int X = InputPlayerLocation[1];
		
		if( (InputMove & putBombBeforeMove) == 1){
			
			MainMap[Y][X] = Option.BombExplosionTime;
			
			for(int i = 0 ; i < Option.BombExplosionRange ; i++){
				
				if((Y + i) >= MainMap.length ) break;
				if((MainMap[Y + i][X] & Wall) == 1) continue;
				
				if(MainMap[Y + i][X] == Path) MainMap[Y + i][X] = Option.BombExplosionTime;
				
			}
			
		}
		
	}
	
	@Override
	public void run() {
		
		ST.showOnScreen(LogName, A.getID() + " and " + B.getID() + " join game");
		
		Message Msg = new Message();
		
		Msg.setMsg("Map", ST.MapToString(MainMap));
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
			
			ST.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " peace end!");
			
		}
		
		A.setState(State.InPlayerList);
		B.setState(State.InPlayerList);
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
