package ObjectStructure;

import SocketServer.Center;
import SocketServer.Option;
import Tool.ERSystem;
import Tool.ErrorCode;
import Tool.ST;

public class Map extends Notification implements Runnable {
	
	private Player A, B;
	private Block MainMap[][];
	private Timer Timer;
	private int MapTimeUpTimes;
	
	private final int PlayerA = 			0x0100;
	private final int PlayerB =  			0x0200;
		
	private final int Move_Up = 		  	  0x01;
	private final int Move_Down =  		  	  0x02;
	private final int Move_Left = 			  0x03;
	private final int Move_Right = 			  0x04;
	
	private int PlayerLocationA[];
	private int PlayerLocationB[];
	
	private String LogName = "Map";
	public Map(Player inputA, Player inputB){
		
		A = inputA;
		B = inputB;
		
		A.setState(State.InMap);
		B.setState(State.InMap);
		
		A.setLive(true);
		B.setLive(true);

		PlayerLocationA = new int[2];
		PlayerLocationB = new int[2];
		
		MapTimeUpTimes = 0;
		
		initMap();
						
	}
	private void initMap(){
		
		int type = 1;
		
		MainMap = new Block[13][15];
		
		for(int y = 0 ; y < 13 ; y++){
			for(int x = 0 ; x < 15 ; x++){
				if(y % 2 == 1 && x % 2 ==1) MainMap[y][x] = new Block(Block.Wall_Type);
				else MainMap[y][x] = new Block(Block.Path_Type);
			}
		}
		
		MainMap[6][0].setPlayer(A, PlayerA);
		MainMap[6][14].setPlayer(B, PlayerB);
				
		PlayerLocationA[0] = 6;
		PlayerLocationA[1] = 0;
		PlayerLocationB[0] = 6;
		PlayerLocationB[1] = 14;
	}
	
	@Override
	public void TimeUp() {
		ST.showOnScreen(LogName, "A " + A.isLive());
		ST.showOnScreen(LogName, "B " + B.isLive());
		
		if(MapTimeUpTimes >= Option.GameTimeUp){
			checkAlive();
			return;
		}
		++MapTimeUpTimes;
		
		Message PlayerMessageA = A.getMessage();
		Message PlayerMessageB = B.getMessage();
		
		if(isObjectNull(PlayerMessageA, PlayerMessageB)){
			checkAlive();
			return;
		}
		
		String NextMoveA = PlayerMessageA.getMsg("Move");
		String NextMoveB = PlayerMessageA.getMsg("Move");
		
		if(isObjectNull(NextMoveA, NextMoveB)){
			checkAlive();
			return;
		}
		
		int MoveA = Integer.parseInt(NextMoveA);
		int MoveB = Integer.parseInt(NextMoveB);
		
		String TempA = PlayerMessageA.getMsg("putBombBeforeMove");
		String TempB = PlayerMessageB.getMsg("putBombBeforeMove");
		if(isObjectNull(TempA, TempB)){
			checkAlive();
			return;
		}
		boolean PBBM_A = Boolean.parseBoolean(TempA);
		boolean PBBM_B = Boolean.parseBoolean(TempB);
		
		TempA = PlayerMessageA.getMsg("putBombAfterMove");
		TempB = PlayerMessageB.getMsg("putBombAfterMove");
		if(isObjectNull(TempA, TempB)){
			checkAlive();
			return;
		}
		boolean PBAM_A = Boolean.parseBoolean(TempA);
		boolean PBAM_B = Boolean.parseBoolean(TempB);
		
		setMove(PBBM_A, MoveA, PBAM_A, PlayerLocationA);
		setMove(PBBM_B, MoveB, PBAM_B, PlayerLocationB);
		
		countMap();
		checkAlive();
		
		Message Msg = new Message();
		
		Msg.setMsg("Map", ST.MapToString(MainMap));
		Msg.setMsg("ErrorCode", ErrorCode.Success);
		
		Msg.setMsg("Live", A.isLive() + "");
		A.sendMsg(Msg);
		
		Msg.setMsg("Live", B.isLive() + "");
		B.sendMsg(Msg);
		
	}
	@Override
	public void run() {
		
		ST.showOnScreen(LogName, A.getID() + " and " + B.getID() + " join game");
		
		Message Msg = new Message();
		
		Msg.setMsg("Map", ST.MapToString(MainMap));
		Msg.setMsg("Message", "Game Joined");
		Msg.setMsg("ErrorCode", ErrorCode.Success);
		
		Msg.setMsg("PlayerMark", PlayerA);
		A.sendMsg(Msg);
		
		Msg.setMsg("PlayerMark", PlayerB);
		B.sendMsg(Msg);
		
		
		Timer = new Timer(Option.TimeInterval);
		Timer.addNotificationList(this);
		new Thread(Timer).start();
	}
	private void checkAlive(){
		if(A.isLive() && B.isLive()) return;
		
		Timer.stop();
		
		int ScoreTemp = ERSystem.newScore(A.getScore(), B.getScore());
		
		if(A.isLive()){
			A.setScore(A.getScore() + ScoreTemp);
			B.setScore(B.getScore() - ScoreTemp);
			
			A.setWins(A.getWins() + 1);
			B.setLosses(B.getLosses() + 1);
			ST.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " " + A.getID() + " Win!");
		}
		else if(B.isLive()){
			A.setScore(A.getScore() - ScoreTemp);
			B.setScore(B.getScore() + ScoreTemp);
			
			A.setLosses(A.getLosses() + 1);
			B.setWins(B.getWins() + 1);
			ST.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " " + B.getID() + " Win!");
		}
		else{
			
			A.setTie(A.getTie() + 1);
			B.setTie(B.getTie() + 1);
			ST.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " peace end!");
			
		}
		
		Center.writePlayerData();
		
		A.setState(State.InPlayerList);
		B.setState(State.InPlayerList);
	}
	private void countMap(){
		for(int y = 0 ; y < MainMap.length ; y++){
			for(int x = 0 ; x < MainMap[0].length ; x++){
				MainMap[y][x].CountBombExplosionTime();
			}
		}
	}
	//boolean putBombBeforeMove, String inputMove, boolean putBombAfterMove
	private void setMove(boolean putBombBeforeMove, int InputMove,  boolean putBombAfterMove, int [] InputPlayerLocation){
		
		int Y = InputPlayerLocation[0];
		int X = InputPlayerLocation[1];
		
		Player tempP = MainMap[Y][X].getPlayer();
		int tempPT = MainMap[Y][X].getPlayerType();
		
		if( putBombBeforeMove) setBomb(Y, X);
		
		if(InputMove == Move_Up){
			if( (Y - 1) >= 0){
				if(MainMap[Y -1][X].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					Y--;
					InputPlayerLocation[0] = Y;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		else if(InputMove == Move_Down){
			if( (Y + 1) >= MainMap.length ){
				if(MainMap[Y + 1][X].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					Y++;
					InputPlayerLocation[0] = Y;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		else if(InputMove == Move_Left){
			if( (X - 1) >= 0){
				if(MainMap[Y][X - 1].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					X--;
					InputPlayerLocation[1] = X;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		else if(InputMove == Move_Right){
			if( (X + 1) >= MainMap[0].length){
				if(MainMap[Y][X + 1].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					X++;
					InputPlayerLocation[1] = X;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		
		if(putBombAfterMove) setBomb(Y, X);
		
	}
	private void setBomb(int Y, int X){
		int BombExplosionTime = MainMap[Y][X].getBombExplosionTime() > 0 ? MainMap[Y][X].getBombExplosionTime() : Option.BombExplosionTime; 
		
		MainMap[Y][X].setBombExplosionTime(BombExplosionTime);
		MainMap[Y][X].setType(Block.Bomb_Type);
		
		for(int i = 0 ; i < Option.BombExplosionRange ; i++){
			if((Y + i) >= MainMap.length ) break;
			if(MainMap[Y + i][X].getType() == Block.Wall_Type) continue;
			
			if(MainMap[Y + i][X].getType() == Block.Path_Type) MainMap[Y + i][X].setBombExplosionTime(BombExplosionTime);
		}
		
		for(int i = 0 ; i < Option.BombExplosionRange ; i++){
			if((X + i) >= MainMap[0].length ) break;
			if(MainMap[Y][X + i].getType() == Block.Wall_Type) continue;
			
			if(MainMap[Y][X + i].getType() == Block.Path_Type) MainMap[Y][X + i].setBombExplosionTime(BombExplosionTime);
		}
		
		for(int i = 0 ; i < Option.BombExplosionRange ; i++){
			if((Y - i) < 0 ) break;
			if(MainMap[Y - i][X].getType() == Block.Wall_Type) continue;
			
			if(MainMap[Y - i][X].getType() == Block.Path_Type) MainMap[Y - i][X].setBombExplosionTime(BombExplosionTime);
		}
		
		for(int i = 0 ; i < Option.BombExplosionRange ; i++){
			if((X - i) < 0 ) break;
			if(MainMap[Y][X - i].getType() == Block.Wall_Type) continue;
			
			if(MainMap[Y][X - i].getType() == Block.Path_Type) MainMap[Y][X - i].setBombExplosionTime(BombExplosionTime);
		}
	}
		
	private boolean isObjectNull(Object inputObjectA, Object inputObjectB){
		
		Message Msg = new Message();
		
		Msg.setMsg("Message", "Your input is null");
		Msg.setMsg("ErrorCode", ErrorCode.ParameterError);
		
		if(inputObjectA == null){
			A.sendMsg(Msg);
			A.setLive(false);
		}
		if(inputObjectB == null){
			B.sendMsg(Msg);
			B.setLive(false);
		}
		if(A.isLive() == false || B.isLive() == false) return true;
		return false;
	}
}
