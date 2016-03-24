package SocketServer;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ObjectStructure.Message;
import ObjectStructure.State;
import Tool.ErrorCode;
import Tool.ST;
/*
 * This class service one client
 * */
public class Service implements Runnable {
	private Socket ClientSocket;
	private BufferedWriter Writer;
	private BufferedReader Reader;
	
	private String LogName = "ClientService";
	public Service(Socket inputClient){
		ClientSocket = inputClient;
    	Reader = null;
    	Writer = null;
	}
	@Override
	public void run(){//////
		if(!initIO()) return;
		
		Message ClientMsg, Msg = new Message();
		
		ClientMsg = receiveMsg();
		
		String FunctionName = ClientMsg.getMsg("FunctionName");
		
		if(FunctionName == null){
			ST.showOnScreen(LogName, ClientSocket.getInetAddress()+" FunctionName not found");
			Msg.setMsg("Message", "Empty function name");
			Msg.setMsg("ErrorCode", ErrorCode.ParameterError);
			sendMsg(Msg);
			return;
		}
		
		if(FunctionName.equals("echo")){
			String m = ClientMsg.getMsg("Message");
			m = (m == null ? "" : m);
			ST.showOnScreen(LogName, ClientSocket.getInetAddress()+" echo " + m);
			
			Msg.setMsg("Message", m + " echo back at " + ST.getTime());
			Msg.setMsg("ErrorCode", ErrorCode.Success);
			sendMsg(Msg);
			return;
		}
		else if(FunctionName.equals("match")){
			String ID = ClientMsg.getMsg("ID");
			String Password = ClientMsg.getMsg("Password");
			if(ID == null || Password == null){
				Msg.setMsg("Message", "null ID or Password");
				Msg.setMsg("ErrorCode", ErrorCode.ParameterError);
				sendMsg(Msg);
				return;
			}
			if(Center.isPlayerExist(ID)){ // registered player
				if(!Center.verifyPassword(ID, Password)){
					Msg.setMsg("Message", "Wrong password");
					Msg.setMsg("ErrorCode", ErrorCode.IDandPWError);
					sendMsg(Msg);
					return;
				}
				ST.showOnScreen(LogName, ID + " verify password success");
			}
			else{ //new player
				Center.newPlayer(ID, Password);
				ST.showOnScreen(LogName, ID + " create player success");
			}
			if(!Center.checkPlayerState(ID, State.InPlayerList)){
				Msg.setMsg("Message", "Player state is pairing or playing");
				Msg.setMsg("ErrorCode", ErrorCode.PlayerStateNotCorret);
				sendMsg(Msg);
				return;
			}
			
			Center.addPairPlayer(ID, Writer);
			
		}
		else{
			
			ST.showOnScreen(LogName, ClientSocket.getInetAddress()+" Unknow function " + FunctionName);
			Msg.setMsg("ErrorCode", ErrorCode.ParameterError);
			sendMsg(Msg);
			
		}
		
	    return;
	}
	private boolean initIO(){
		
		try {
    		Reader= new BufferedReader(new InputStreamReader(ClientSocket.getInputStream(),"UTF-8"));
    		Writer= new BufferedWriter(new OutputStreamWriter(ClientSocket.getOutputStream(),"UTF-8"));
		} catch (java.io.IOException e) {
			
			ST.showOnScreen(LogName, ClientSocket.getInetAddress()+" Create IO Buffer fail");
			ST.showOnScreen(LogName, ClientSocket.getInetAddress()+" lose connection");
			
			closeConnection();
			
			return false;
        }
		return true;
	}
	private void closeConnection(){
		
		try {
			ClientSocket.close();
			ClientSocket = null;
		} catch (IOException e) {e.printStackTrace();}
	}
	private boolean sendMsg(Message inputMsg){
		
		String Msg = ST.MessageToString(inputMsg);
		try {
			Writer.write(Msg + "\r\n");
			Writer.flush();
			Writer = null;
			return true;
		} catch (IOException e) {
			Writer = null;
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
}
