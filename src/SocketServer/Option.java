package SocketServer;

public class Option {
	public static int[]PortList = new int[] {52013, 52014, 53013, 53014};
	public static long TimeInterval = 5000;
	public static int InitScore = 1200;
	public static String PlayerFileName = "Player_Data";
	public static long savePlayerDataTime = (long) 10;//mins
	public static long GameTimeUp = 240;//ticks
	public static int BombExplosionTime = 10;
	public static int BombExplosionRange = 8;
	public static boolean Log = false;
}
