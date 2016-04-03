package ServerObjectStructure;

public class BitFlag {
	
	//Map
	public static int BlockType_Unknow = 			0x0000;
	public static int BlockType_NoPlayer =			0x0000;
	public static int BlockType_Wall = 				0x1000;
	public static int BlockType_Path =  			0x2000;
	public static int BlockType_Bomb =  			0x4000;
	public static int BlockType_Filter =			0xF000;
	public static int BlockType_BombCDFilter =		0x00FF;
	
	//PlayerMark
	public static int PlayerA = 			        0x0100;
	public static int PlayerB =  					0x0200;
	
	//Move
	public static int Move_Wait = 		  	  		0x0000;
	public static int Move_Up = 		  	  		0x0001;
	public static int Move_Down =  		  	  		0x0002;
	public static int Move_Left = 			  		0x0004;
	public static int Move_Right = 			  		0x0008;
	public static int Move_Filter =					0x000F;
	
	public static int putBombBeforeMove = 	  		0x0010;
	public static int putBombAfterMove =  	  		0x0020;
	
	//version
	public static int Version_Newer =	 	  		0x0010;
	public static int Version_Older =  				0x0020;
	public static int Version_Same =  				0x0040;
	
}
