package ServerObjectStructure;

import java.util.Random;

import BomberGameBOTServer.ServerCenter;
import BomberGameBOTServer.ServerOptions;
import ServerTool.ERSystem;
import ServerTool.ErrorCode;
import ServerTool.ServerTool;

public class Map extends Notification implements Runnable {
	
	private Player A, B;
	private Block MainMap[][];
	private Timer Timer;
	private int MapTimeUpTimes;
	
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
		
		//int type = 1;
		
		MainMap = new Block[13][15];
		
		for(int y = 0 ; y < 13 ; y++){
			for(int x = 0 ; x < 15 ; x++){
				if(y % 2 == 1 && x % 2 ==1) MainMap[y][x] = new Block(BitFlag.BlockType_Wall);
				else MainMap[y][x] = new Block(BitFlag.BlockType_Path);
			}
		}
		
		MainMap[6][ 0].setPlayer(A, BitFlag.PlayerA);
		MainMap[6][14].setPlayer(B, BitFlag.PlayerB);
				
		PlayerLocationA[0] = 6;
		PlayerLocationA[1] = 0;
		PlayerLocationB[0] = 6;
		PlayerLocationB[1] = 14;
	}
	
	@Override
	public void TimeUp() {
				
		if(MapTimeUpTimes >= ServerOptions.GameTimeUp){
			
			if(A.isLive() && B.isLive()){
				A.setLive(false);
				B.setLive(false);
			}
			
			checkAlive("Time up");
			return;
		}
		++MapTimeUpTimes;
		
		Message PlayerMessageA = A.getMessage();
		Message PlayerMessageB = B.getMessage();
		
		if(isObjectNull(PlayerMessageA, PlayerMessageB)) return;
		
		String NextMoveA = PlayerMessageA.getMsg(Message.Move);
		String NextMoveB = PlayerMessageB.getMsg(Message.Move);
		
		if(isObjectNull(NextMoveA, NextMoveB)) return;
		
		int MoveA = Integer.parseInt(NextMoveA);
		int MoveB = Integer.parseInt(NextMoveB);
		
		String TempA = PlayerMessageA.getMsg(Message.BombFlag);
		String TempB = PlayerMessageB.getMsg(Message.BombFlag);
		if(isObjectNull(TempA, TempB)) return;
		
		int BombFlag_A = Integer.parseInt(TempA);
		int BombFlag_B = Integer.parseInt(TempB);
		
		setMove(MoveA, BombFlag_A, PlayerLocationA, BitFlag.PlayerA);
		setMove(MoveB, BombFlag_B, PlayerLocationB, BitFlag.PlayerB);
		
		countMap();
		checkAlive("Game over");
		Message Msg = new Message();
		
		Msg.setMsg(Message.Map, ServerTool.MapToString(MainMap));
		Msg.setMsg(Message.End, false);
		Msg.setMsg(Message.Message, "Next move");
		Msg.setMsg(Message.ErrorCode, ErrorCode.Success);
		
		
		Msg.setMsg(Message.PlayerMark, BitFlag.PlayerA);
		A.sendMsg(Msg);
		
		Msg.setMsg(Message.PlayerMark, BitFlag.PlayerB);
		B.sendMsg(Msg);
		
	}
	@Override
	public void run() {
		
		try {
			Thread.sleep(new Random().nextInt((int)(ServerOptions.TimeInterval * 0.8)) + (int)(ServerOptions.TimeInterval * 0.1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		ServerTool.showOnScreen(LogName, A.getID() + " and " + B.getID() + " join game");
		
		Message Msg = new Message();
		
		Msg.setMsg(Message.Map, ServerTool.MapToString(MainMap));
		Msg.setMsg(Message.Message, "Game Joined");
		Msg.setMsg(Message.ErrorCode, ErrorCode.Success);
		
		Msg.setMsg(Message.PlayerMark, BitFlag.PlayerA);
		A.sendMsg(Msg);
		
		Msg.setMsg(Message.PlayerMark, BitFlag.PlayerB);
		B.sendMsg(Msg);
		
		Timer = new Timer(ServerOptions.TimeInterval);
		Timer.addNotificationList(this);
		new Thread(Timer).start();
	}
	private void checkAlive(String inputMessage){
		if(A.isLive() && B.isLive()) return;
		
		Timer.stop();
		
		String GameResult = "Error result";
		
		int ScoreTemp = ERSystem.newScore(A.getScore(), B.getScore());
		
		if(A.isLive()){
			
			GameResult = "Winner: " + A.getID();
			
			ServerTool.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " " + A.getID() + " Win!");
			ServerTool.showOnScreen(LogName, A.getID() + " " + A.getScore() + " -> " + (A.getScore() + ScoreTemp));
			ServerTool.showOnScreen(LogName, B.getID() + " " + B.getScore() + " -> " + (B.getScore() - ScoreTemp));
			
			
			A.setScore(A.getScore() + ScoreTemp);
			B.setScore(B.getScore() - ScoreTemp);
			
			A.setWins(A.getWins() + 1);
			B.setLosses(B.getLosses() + 1);
			
		}
		else if(B.isLive()){
			
			GameResult = "Winner: " + B.getID();
			
			ServerTool.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " " + B.getID() + " Win!");
			ServerTool.showOnScreen(LogName, A.getID() + " " + A.getScore() + " -> " + (A.getScore() - ScoreTemp));
			ServerTool.showOnScreen(LogName, B.getID() + " " + B.getScore() + " -> " + (B.getScore() + ScoreTemp));
			
			A.setScore(A.getScore() - ScoreTemp);
			B.setScore(B.getScore() + ScoreTemp);
			
			A.setLosses(A.getLosses() + 1);
			B.setWins(B.getWins() + 1);
			
		}
		else{
			
			GameResult = "Peace end";
			
			A.setTie(A.getTie() + 1);
			B.setTie(B.getTie() + 1);
			ServerTool.showOnScreen(LogName, A.getID() + " vs " + B.getID() + " peace end!");
			
		}
				
		ServerCenter.writePlayerData();
		
		Message Msg = new Message();
		if(inputMessage != null) Msg.setMsg(Message.Message, inputMessage);
		
		Msg.setMsg(Message.Map, ServerTool.MapToString(MainMap));
		Msg.setMsg(Message.End, true);
		Msg.setMsg(Message.GameResult, GameResult);
		Msg.setMsg(Message.ErrorCode, ErrorCode.Success);
		
		Msg.setMsg(Message.PlayerMark, BitFlag.PlayerA);
		A.sendMsg(Msg);
		
		Msg.setMsg(Message.PlayerMark, BitFlag.PlayerB);
		B.sendMsg(Msg);
		
		A.setState(State.InPlayerList);
		B.setState(State.InPlayerList);
	}
	private void countMap(){
		for(int y = 0 ; y < MainMap.length ; y++){
			for(int x = 0 ; x < MainMap[0].length ; x++){
				MainMap[y][x].CountBombExplosionTime();
			}
		}
		for(int y = 0 ; y < MainMap.length ; y++){
			for(int x = 0 ; x < MainMap[0].length ; x++){
				if(MainMap[y][x].getBlockType() == BitFlag.BlockType_Bomb){
					setBomb(y, x);
				}
			}
		}
	}
	private void setMove(int InputMove, int inputBombFlag, int [] InputPlayerLocation, int inputPlayerFlag){
		
		InputMove &= BitFlag.Move_Filter;
		
		int Y = InputPlayerLocation[0];
		int X = InputPlayerLocation[1];
		
		Player tempP = MainMap[Y][X].getPlayer(inputPlayerFlag);
		
		if((inputBombFlag & BitFlag.putBombBeforeMove) == BitFlag.putBombBeforeMove) setBomb(Y, X);
		
		if(InputMove == BitFlag.Move_Up){
			if( (Y - 1) >= 0){
				if(MainMap[Y -1][X].getBlockType() == BitFlag.BlockType_Path){
					MainMap[Y][X].removePlayer(inputPlayerFlag);
					Y--;
					InputPlayerLocation[0] = Y;
					MainMap[Y][X].setPlayer(tempP, inputPlayerFlag);
				}
			}
		}
		else if(InputMove == BitFlag.Move_Down){
			if( (Y + 1) < MainMap.length ){
				if(MainMap[Y + 1][X].getBlockType() == BitFlag.BlockType_Path){
					MainMap[Y][X].removePlayer(inputPlayerFlag);
					Y++;
					InputPlayerLocation[0] = Y;
					MainMap[Y][X].setPlayer(tempP, inputPlayerFlag);
				}
			}
		}
		else if(InputMove == BitFlag.Move_Left){
			if( (X - 1) >= 0){
				if(MainMap[Y][X - 1].getBlockType() == BitFlag.BlockType_Path){
					MainMap[Y][X].removePlayer(inputPlayerFlag);
					X--;
					InputPlayerLocation[1] = X;
					MainMap[Y][X].setPlayer(tempP, inputPlayerFlag);
				}
			}
		}
		else if(InputMove == BitFlag.Move_Right){
			if( (X + 1) < MainMap[0].length){
				if(MainMap[Y][X + 1].getBlockType() == BitFlag.BlockType_Path){
					MainMap[Y][X].removePlayer(inputPlayerFlag);
					X++;
					InputPlayerLocation[1] = X;
					MainMap[Y][X].setPlayer(tempP, inputPlayerFlag);
				}
			}
		}
		
		if((inputBombFlag & BitFlag.putBombAfterMove) == BitFlag.putBombAfterMove) setBomb(Y, X);
		
	}
	private void setBomb(int Y, int X){
		
		int BombExplosionTime = MainMap[Y][X].getBombExplosionTime() > 0 ? MainMap[Y][X].getBombExplosionTime() : ServerOptions.BombExplosionTime; 
		
		MainMap[Y][X].setBombExplosionTime(BombExplosionTime);
		MainMap[Y][X].setBlockType(BitFlag.BlockType_Bomb);
		
		for(int i = 0 ; i < ServerOptions.BombExplosionRange ; i++){
			if((Y + i) >= MainMap.length ) break;
			if(MainMap[Y + i][X].getBlockType() == BitFlag.BlockType_Wall) break;
			
			if(MainMap[Y + i][X].getBlockType() == BitFlag.BlockType_Path) MainMap[Y + i][X].setBombExplosionTime(BombExplosionTime);
		}
		
		for(int i = 0 ; i < ServerOptions.BombExplosionRange ; i++){
			if((X + i) >= MainMap[0].length ) break;
			if(MainMap[Y][X + i].getBlockType() == BitFlag.BlockType_Wall) break;
			
			if(MainMap[Y][X + i].getBlockType() == BitFlag.BlockType_Path) MainMap[Y][X + i].setBombExplosionTime(BombExplosionTime);
		}
		
		for(int i = 0 ; i < ServerOptions.BombExplosionRange ; i++){
			if((Y - i) < 0 ) break;
			if(MainMap[Y - i][X].getBlockType() == BitFlag.BlockType_Wall) break;
			
			if(MainMap[Y - i][X].getBlockType() == BitFlag.BlockType_Path) MainMap[Y - i][X].setBombExplosionTime(BombExplosionTime);
		}
		
		for(int i = 0 ; i < ServerOptions.BombExplosionRange ; i++){
			if((X - i) < 0 ) break;
			if(MainMap[Y][X - i].getBlockType() == BitFlag.BlockType_Wall) break;
			
			if(MainMap[Y][X - i].getBlockType() == BitFlag.BlockType_Path) MainMap[Y][X - i].setBombExplosionTime(BombExplosionTime);
		}
	}
		
	private boolean isObjectNull(Object inputObjectA, Object inputObjectB){
		
		if(inputObjectA != null && inputObjectB != null) return false;
		
		if(inputObjectA == null){
			A.setLive(false);
		}
		if(inputObjectB == null){
			B.setLive(false);
		}
		
		checkAlive("Input is null");
		
		return true;
	}
}
