package ObjectStructure;

public class BitFlag {
	
	//Map
	public static int UnknowType = 					0x0000;
	public static int NoPlayer =	 				0x0000;
	public static int Wall_Type = 					0x1000;
	public static int Path_Type =  					0x2000;
	public static int Bomb_Type =  					0x4000;
	
	public static int PlayerA = 			        0x0100;
	public static int PlayerB =  					0x0200;
	
	//Move
	public static int Move_Up = 		  	  		0x0001;
	public static int Move_Down =  		  	  		0x0002;
	public static int Move_Left = 			  		0x0004;
	public static int Move_Right = 			  		0x0008;
	public static int putBombBeforeMove = 	  		0x0010;
	public static int putBombAfterMove =  	  		0x0020;
}
