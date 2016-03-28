package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.net.SocketFactory;

import ObjectStructure.ErrorCode;
import ObjectStructure.Message;
import Tool.ST;

public class testServerAPI {
	
	private BufferedWriter Writer;
	private BufferedReader Reader;
	private Socket Client;
	private String ServerIP = "127.0.0.1";
	private int PortList[] = {52013, 52014, 53013, 53014};
	private int Errorcode;
	private String ErrorMessage;
	private int PlayerMark;
	private int map[][];
	
	private String LogName = "TestAPI";
	  
	public testServerAPI(){
	    Writer = null;
	    Reader = null;
	    Client = null;
	}
	
	
	public String echo(String inputString){
	    
	    if (!connect()) {
	    	Errorcode = ErrorCode.ConnectError;
	    	ErrorMessage = "Connect Error";
	    	return null;
	    }
	    
	    Message Msg = new Message(), receiveMsg;
	    
	    Msg.setMsg("FunctionName", "echo");
	    Msg.setMsg("Message", inputString);
	    
	    sendMsg(Msg);
	    receiveMsg = receiveMsg();
//	    
//	    ST.showOnScreen(LogName, receiveMsg.getMsg("Message"));
//	    ST.showOnScreen(LogName, receiveMsg.getMsg("ErrorCode"));
//	    
	    Errorcode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
	    
	    return receiveMsg.getMsg("Message");
	}
	public int match(String inputID, String inputPW){
	    
	    if (!connect()) {
	    	Errorcode = ErrorCode.ConnectError;
	    	return Errorcode;
	    }
	    
	    Message Msg = new Message(), receiveMsg;
	    
	    Msg.setMsg("FunctionName", "match");
	    Msg.setMsg("ID", inputID);
	    Msg.setMsg("Password", inputPW);
	    
	    sendMsg(Msg);
	    receiveMsg = receiveMsg();
	    
//	    ST.showOnScreen(LogName, receiveMsg.getMsg("Message"));
//	    ST.showOnScreen(LogName, receiveMsg.getMsg("ErrorCode"));
	    //ST.showOnScreen(LogName, receiveMsg.getMsg("Map"));
	    
	    ErrorMessage = receiveMsg.getMsg("Message");
	    Errorcode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
	    
	    if(Errorcode != 0 ) return Errorcode;
	    
	    String tempMap[] = receiveMsg.getMsg("Map").split(" ");
	    map = new int[ Integer.parseInt(tempMap[0]) ][Integer.parseInt(tempMap[1])];
	    
	    int indexTemp = 2;
	    
	    for(int y = 0 ; y < map.length ; y++){
			for(int x = 0 ; x < map[0].length ; x++){
				
				map[y][x] = Integer.parseInt(tempMap[indexTemp++]);
				
			}
		}
	    
	    PlayerMark = Integer.parseInt(receiveMsg.getMsg("PlayerMark"));
	    
	    Errorcode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
	    
	    return Errorcode;
	}
	public int move(String inputID, String inputPW, String inputMove, int putBombFlag){
		
		final int Move_Up = 		  	  0x01;
		final int Move_Down =  		  	  0x02;
		final int Move_Left = 			  0x03;
		final int Move_Right = 			  0x04;
		
		if (!connect()) {
	    	Errorcode = ErrorCode.ConnectError;
	    	return Errorcode;
	    }
	    
	    Message Msg = new Message(), receiveMsg;
	    
	    Msg.setMsg("FunctionName", "move");
	    Msg.setMsg("ID", inputID);
	    Msg.setMsg("Password", inputPW);
	    Msg.setMsg("BombFlag", putBombFlag + "");
	    if(inputMove.toLowerCase().equals("up")) 	Msg.setMsg("Move", Move_Up);
	    if(inputMove.toLowerCase().equals("down")) 	Msg.setMsg("Move", Move_Down);
	    if(inputMove.toLowerCase().equals("left")) 	Msg.setMsg("Move", Move_Left);
	    if(inputMove.toLowerCase().equals("right"))	Msg.setMsg("Move", Move_Right);
	    
	    sendMsg(Msg);
	    receiveMsg = receiveMsg();
	    
	    ErrorMessage = receiveMsg.getMsg("Message");
	    Errorcode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
	    
	    if(Errorcode != 0 ) return Errorcode;
	    
	    String tempMap[] = receiveMsg.getMsg("Map").split(" ");
	    map = new int[ Integer.parseInt(tempMap[0]) ][Integer.parseInt(tempMap[1])];
	    
	    int indexTemp = 2;
	    
	    for(int y = 0 ; y < map.length ; y++){
			for(int x = 0 ; x < map[0].length ; x++){
				
				map[y][x] = Integer.parseInt(tempMap[indexTemp++]);
				
			}
		}
	    
	    Errorcode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
		
		return Errorcode;
	}
	public void showMap(){
		final int Wall_Type = 				0x1000;
		final int Path_Type =  				0x2000;
		final int Bomb_Type =  				0x3000;
		final int PlayerA = 				0x0100;
		final int PlayerB =  				0x0200;		
		
		String Wall = null;
		String Path = null;
		String Bomb = null;
		
		String PA = null;
		String PB = null;//★☆
		try {
			Wall = new String("▉".getBytes("UTF-8"), Charset.forName("UTF-8"));
			Path = new String("　".getBytes("UTF-8"), Charset.forName("UTF-8"));
			Bomb = new String("◎".getBytes("UTF-8"), Charset.forName("UTF-8"));
			
			PA = new String("★".getBytes("UTF-8"), Charset.forName("UTF-8"));
			PB = new String("☆".getBytes("UTF-8"), Charset.forName("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    for(int y = 0 ; y < map.length ; y++){
			for(int x = 0 ; x < map[0].length ; x++){
				
				if(map[y][x] == Bomb_Type) 		System.out.print(Bomb);
				else if(map[y][x] == Path_Type) 	System.out.print(Path);
				else if(map[y][x] == Wall_Type) 	System.out.print(Wall);
				else if(map[y][x] == PlayerA) 	System.out.print(PA);
				else if(map[y][x] == PlayerB) 	System.out.print(PB);
				//System.out.print(map[y][x] + " ");
				
			}
			System.out.print(System.lineSeparator());
		}
	    
	}
	public int getPlayerMark(){
		return PlayerMark;
	}
	public String getErrorMessage(){
		return ErrorMessage;
	}
	public int getErrorCode(){
		return Errorcode;
	}
	public int[][] getMap(){
		return map;
	}
	private boolean connect(){
		
		for(int i = 0 ; i < PortList.length ; i++){
			
//			ST.showOnScreen(LogName, "Connect to port " + PortList[i]);
		    try{
		    	Client = SocketFactory.getDefault().createSocket();
		    	InetSocketAddress remoteaddr = new InetSocketAddress(ServerIP, PortList[i]);
		    	Client.connect(remoteaddr, 2000);
		    	Reader = new BufferedReader(new InputStreamReader(Client.getInputStream(), "UTF-8"));
		    	Writer = new BufferedWriter(new OutputStreamWriter(Client.getOutputStream(), "UTF-8"));
		    }
		    catch (IOException e){
		    	ST.showOnScreen(LogName, "Connect prot " + PortList[i] + " time out");
		    	continue;
		    }
		    break;
		}
//	    ST.showOnScreen(LogName, "Connect success");
	    return true;
	}
	private boolean sendMsg(Message inputMsg){
		
		String Msg = ST.MessageToString(inputMsg);
		try {
			Writer.write(Msg + "\r\n");
			Writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	private Message receiveMsg(){
		Message resultMsg = null;
		
		String receivedString = null;
		try {
			
			receivedString = Reader.readLine();
			resultMsg = ST.StringToMessage(receivedString);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultMsg;
	}
	public static void main(String[] args) {
		
		if(args.length != 2) return;
		
		String inputID = args[0];
		String inputPW = args[1];
		
		testServerAPI test = new testServerAPI();

		int rtn = test.match(inputID, inputPW);
		if(rtn != 0){
			ST.showOnScreen("AI", test.getErrorCode());
			ST.showOnScreen("AI", test.getErrorMessage());
			return;
		}
		
		SecureRandom rand = new SecureRandom();
		while(true){
			int move = rand.nextInt();
			
			if(move < 0) move = move * -1;
			move = move % 4;
			
			if(move == 0){
				test.move(inputID, inputPW, "up", 0);
				ST.showOnScreen("AI", "up");
			}
			else if(move == 1){
				test.move(inputID, inputPW, "down", 0);
				ST.showOnScreen("AI", "down");
			}
			else if(move == 2){
				test.move(inputID, inputPW, "right", 0);
				ST.showOnScreen("AI", "right");
			}
			else if(move == 3){
				test.move(inputID, inputPW, "left", 0);
				ST.showOnScreen("AI", "left");
			}
			else{
				ST.showOnScreen("AI", "move = " + move);
			}
			test.showMap();
		}
		//int [][] map = test.getMap();
		
	}
}
