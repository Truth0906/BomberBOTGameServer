package ObjectStructure;

public class Block {
	
	private int BlockType;
	private Player PlayerTemp;
	private int PlayerType;
	private int BombExplosionTime;
	
	public Block(){
		BlockType = BitFlag.UnknowType;
		PlayerTemp = null;
		PlayerType = BitFlag.NoPlayer;
		BombExplosionTime = 0;
	}
	public Block(int inputType){
		BlockType = inputType;	
	}
	public int getType(){
		return BlockType;
	}
	public void setType(int inputType){
		BlockType = inputType;
	}
	public void setPlayer(Player inputPlayer, int inputPlayerType){
		if(BlockType == BitFlag.Wall_Type) return;
		PlayerTemp = inputPlayer;
		PlayerType = inputPlayerType;
	}
	public Player getPlayer(){
		return PlayerTemp;
	}
	public int getPlayerType(){
		return PlayerType;
	}
	public void setBombExplosionTime(int inputBombExplosionTime){
		if(BlockType == BitFlag.Wall_Type) return;
		if(inputBombExplosionTime <= 0) return;
		BombExplosionTime = inputBombExplosionTime;
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
		if(BlockType == BitFlag.Wall_Type) return;
		if(BombExplosionTime == 0) return;
		
		if(BombExplosionTime > 0) --BombExplosionTime;
		else{
			BlockType = BitFlag.Path_Type;
			if(PlayerType != BitFlag.NoPlayer && PlayerTemp != null){
				PlayerTemp.setLive(false);
			}
		}
	}
}
