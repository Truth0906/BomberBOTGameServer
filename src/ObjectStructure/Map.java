package ObjectStructure;

import SocketServer.Option;
import Tool.ERSystem;
import Tool.ErrorCode;
import Tool.ST;

public class Map extends Notification implements Runnable {
	
	private Object checkPlayerLock;
	private Player A, B;
	private Block MainMap[][];
	private Timer Timer;
	private int MapTimeUpTimes;
	
	private boolean isContiue;
		
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
			setMove(MoveB, PlayerLocationB);
			
			countMap();
			
			Message Msg = new Message();
			
			Msg.setMsg("Map", ST.MapToString(MainMap));
			Msg.setMsg("ErrorCode", ErrorCode.Success);
			
			Msg.setMsg("Live", A.isLive() + "");
			A.sendMsg(Msg);
			
			Msg.setMsg("Live", B.isLive() + "");
			B.sendMsg(Msg);
		}
	}
	@Override
	public void run() {
		
		ST.showOnScreen(LogName, A.getID() + " and " + B.getID() + " join game");
		
		Message Msg = new Message();
		
		Msg.setMsg("Map", ST.MapToString(MainMap));
		Msg.setMsg("Message", "Game Joined");
		Msg.setMsg("ErrorCode", ErrorCode.Success);
		
		Msg.setMsg("Mark", PlayerA);
		A.sendMsg(Msg);
		
		Msg.setMsg("Mark", PlayerB);
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
				if(A.isLive() == false || B.isLive() == false){
					break;
				}
			}
		}while(isContiue);
		
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
	private void setMove(int InputMove, int [] InputPlayerLocation){
		
		int Y = InputPlayerLocation[0];
		int X = InputPlayerLocation[1];
		
		Player tempP = MainMap[Y][X].getPlayer();
		int tempPT = MainMap[Y][X].getPlayerType();
		
		if( (InputMove & putBombBeforeMove) == 1) setBomb(Y, X);
		
		if( (InputMove & Move_Up) == 1){
			if( (Y - 1) >= 0){
				if(MainMap[Y -1][X].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					Y--;
					InputPlayerLocation[0] = Y;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		else if((InputMove & Move_Down) == 1){
			if( (Y + 1) >= MainMap.length ){
				if(MainMap[Y + 1][X].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					Y++;
					InputPlayerLocation[0] = Y;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		else if( (InputMove & Move_Left) == 1){
			if( (X - 1) >= 0){
				if(MainMap[Y][X - 1].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					X--;
					InputPlayerLocation[1] = X;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		else if((InputMove & Move_Right) == 1){
			if( (X + 1) >= MainMap[0].length){
				if(MainMap[Y][X + 1].getType() == Block.Path_Type){
					MainMap[Y][X].setPlayer(null, Block.NoPlayer);
					X++;
					InputPlayerLocation[1] = X;
					MainMap[Y][X].setPlayer(tempP, tempPT);
				}
			}
		}
		
		if( (InputMove & putBombAfterMove) == 1) setBomb(Y, X);
		
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
