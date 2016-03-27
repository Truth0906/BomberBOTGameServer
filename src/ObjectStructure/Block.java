package ObjectStructure;

public class Block {
	
	public static final int UnknowType = 				0x0000;
	public static final int NoPlayer =	 				0x0000;
	public static final int Wall_Type = 				0x1000;
	public static final int Path_Type =  				0x2000;
	public static final int Bomb_Type =  				0x3000;
	
	private int BlockType;
	private Player PlayerTemp;
	private int PlayerType;
	private int BombExplosionTime;
	
	public Block(){
		BlockType = UnknowType;
		PlayerTemp = null;
		PlayerType = NoPlayer;
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
		if(BlockType == Wall_Type) return;
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
		if(BlockType == Wall_Type) return;
		if(inputBombExplosionTime <= 0) return;
		BombExplosionTime = inputBombExplosionTime;
	}
	public int getBombExplosionTime(){
		return BombExplosionTime;
	}
	@Override
	public String toString() {
		
		if(PlayerTemp != null) return PlayerType + "";
		if(BombExplosionTime > 0) return BombExplosionTime + "";
		return BlockType + "";
		
//		String result = "";
//		int temp = 0;
//		
//		temp = temp & PlayerType;
//		temp = temp & BombExplosionTime;
//		temp = temp & BlockType;
//		
//		result = temp + "";
//		
//		return result;
	}
	
	public void CountBombExplosionTime() {
		if(BlockType == Wall_Type) return;
		if(BombExplosionTime == 0) return;
		
		if(BombExplosionTime > 0) --BombExplosionTime;
		else{
			BlockType = Path_Type;
			if(PlayerType != NoPlayer && PlayerTemp != null){
				PlayerTemp.setLive(false);
			}
		}
	}
}
