package ServerObjectStructure;

import ServerTool.ServerTool;

public class Block {
	
	private int BlockType;
	private Player PlayerTemp[];
	private int PlayerType;
	private int BombExplosionTime;
	
	public Block(int inputType){
		PlayerTemp = new Player[2];
		BlockType = inputType;
		PlayerType = BitFlag.BlockType_NoPlayer;
		BombExplosionTime = 0;
	}
	public int getBlockType(){
		return BlockType;
	}
	public void setBlockType(int inputType){
		BlockType = inputType;
	}
	
	public void removePlayer(int inputPlayerType){
		if(BlockType == BitFlag.BlockType_Wall) return;
		if(inputPlayerType == BitFlag.PlayerA) PlayerTemp[0] = null;
		if(inputPlayerType == BitFlag.PlayerB) PlayerTemp[1] = null;
		PlayerType &= ~(inputPlayerType);
	}
	
	public void setPlayer(Player inputPlayer, int inputPlayerType){
		if(BlockType == BitFlag.BlockType_Wall) return;
		if(inputPlayerType == BitFlag.PlayerA) PlayerTemp[0] = inputPlayer;
		if(inputPlayerType == BitFlag.PlayerB) PlayerTemp[1] = inputPlayer;
		PlayerType |= inputPlayerType;
	}
	public Player getPlayer(int inputPlayerType){
		if(inputPlayerType == BitFlag.PlayerA) return PlayerTemp[0];
		if(inputPlayerType == BitFlag.PlayerB) return PlayerTemp[1];
		return null;
	}
	public int getPlayerType(){
		return PlayerType;
	}
	public void setBombExplosionTime(int inputBombExplosionTime){
		if(BlockType == BitFlag.BlockType_Wall) return;
		if(inputBombExplosionTime <= 0) return;
		if(BombExplosionTime > inputBombExplosionTime || BombExplosionTime == 0) BombExplosionTime = inputBombExplosionTime;
	}
	public int getBombExplosionTime(){
		return BombExplosionTime;
	}
	@Override
	public String toString() {
		
//		if(PlayerTemp != null) return PlayerType + "";
//		if(BombExplosionTime > 0) return BombExplosionTime + "";
//		return BlockType + "";
		
		String result = "";
		int temp = 0;
		
		temp |= PlayerType;
		temp |= BombExplosionTime;
		temp |= BlockType;
		
		result = temp + "";
		
		return result;
	}
	
	public void CountBombExplosionTime() {
		if(BlockType == BitFlag.BlockType_Wall) return;
		if(BombExplosionTime == 0) return;
		
		if(BombExplosionTime > 0) BombExplosionTime--;
		
		if(BombExplosionTime == 0){
			BlockType = BitFlag.BlockType_Path;
			if(PlayerType != BitFlag.BlockType_NoPlayer){
				if(ServerTool.CompareBitFlag(PlayerType, BitFlag.PlayerA))PlayerTemp[0].setLive(false);
				if(ServerTool.CompareBitFlag(PlayerType, BitFlag.PlayerB))PlayerTemp[1].setLive(false);
			}
		}
	}
}
