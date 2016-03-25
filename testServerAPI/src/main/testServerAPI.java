package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;

import Tool.ErrorCode;
import Tool.Message;
import Tool.ST;

public class testServerAPI {
	
	private BufferedWriter Writer;
	private BufferedReader Reader;
	private Socket Client;
	private String ServerIP = "127.0.0.1";
	private int PortList[] = {52013, 52014, 53013, 53014};
	private int Errorcode;
	private String ErrorMessage;
	
	private String LogName = "TestAPI";
	  
	public testServerAPI(){
	    Writer = null;
	    Reader = null;
	    Client = null;
	}
	
	
public int echo(String inputString){
	    
	    if (!connect()) {
	    	Errorcode = ErrorCode.ConnectError;
	    	ErrorMessage = "Connect Error";
	    	return Errorcode;
	    }
	    
	    Message Msg = new Message(), receiveMsg;
	    
	    Msg.setMsg("FunctionName", "echo");
	    Msg.setMsg("Message", inputString);
	    
	    sendMsg(Msg);
	    receiveMsg = receiveMsg();
	    
	    ST.showOnScreen(LogName, receiveMsg.getMsg("Message"));
	    ST.showOnScreen(LogName, receiveMsg.getMsg("ErrorCode"));
	    
	    Errorcode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
	    
	    return Errorcode;
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
	    
	    ST.showOnScreen(LogName, receiveMsg.getMsg("Message"));
	    ST.showOnScreen(LogName, receiveMsg.getMsg("ErrorCode"));
	    //ST.showOnScreen(LogName, receiveMsg.getMsg("Map"));
	    
	    int ErrorCode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
	    
	    if(ErrorCode != 0 ) return ErrorCode;
	    
	    String tempMap[] = receiveMsg.getMsg("Map").split(" ");
	    int map[][] = new int[ Integer.parseInt(tempMap[0]) ][Integer.parseInt(tempMap[1])];
	    
	    int indexTemp = 2;
	    
	    for(int y = 0 ; y < map.length ; y++){
			for(int x = 0 ; x < map[0].length ; x++){
				
				map[y][x] = Integer.parseInt(tempMap[indexTemp++]);
				
			}
		}
	    
	    for(int y = 0 ; y < map.length ; y++){
			for(int x = 0 ; x < map[0].length ; x++){
				
				System.out.print(map[y][x] + " ");
//				if(map[y][x] == 0)			System.out.print(" ");
//				else if(map[y][x] == -1) 	System.out.print("+");
//				else 						System.out.print("?");
			}
			System.out.print(System.lineSeparator());
		}
	    
	    Errorcode = Integer.parseInt(receiveMsg.getMsg("ErrorCode"));
	    
	    return Errorcode;
	}
	private boolean connect(){
		
		for(int i = 0 ; i < PortList.length ; i++){
			
			ST.showOnScreen(LogName, "Connect to port " + PortList[i]);
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
	    ST.showOnScreen(LogName, "Connect success");
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
		testServerAPI test = new testServerAPI();
		
		if(args.length != 2) return;
		
		test.echo("QvQ");
		test.match(args[0], args[1]);
		
		
	}
}
