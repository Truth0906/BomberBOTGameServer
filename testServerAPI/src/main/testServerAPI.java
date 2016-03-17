package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;

import Tool.Message;
import Tool.ST;

public class testServerAPI {
	
	private BufferedWriter Writer;
	private BufferedReader Reader;
	private Socket Client;
	private String ServerIP = "127.0.0.1";
	private int PortList[] = {52013, 52014, 53013, 53014};
	
	private String LogName = "TestAPI";
	  
	public testServerAPI(){
	    Writer = null;
	    Reader = null;
	    Client = null;
	}
	
	
	public boolean echo(String inputString){
	    
	    if (!connect()) {
	    	return false;
	    }
	    
	    Message Msg = new Message(), receiveMsg;
	    
	    Msg.setMsg("FunctionName", "echo");
	    Msg.setMsg("Message", inputString);
	    
	    sendMsg(Msg);
	    receiveMsg = receiveMsg();
	    
	    ST.showOnScreen(LogName, receiveMsg.getMsg("Message"));
	    ST.showOnScreen(LogName, receiveMsg.getMsg("ErrorCode"));
	    
	    return true;
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
		
		test.echo("Hello I am testServerAPI");
		
	}
}
