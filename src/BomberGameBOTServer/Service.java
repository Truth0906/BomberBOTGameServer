package BomberGameBOTServer;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ServerObjectStructure.Message;
import ServerObjectStructure.State;
import ServerTool.ErrorCode;
import ServerTool.ServerTool;
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
			ServerTool.showOnScreen(LogName, ClientSocket.getInetAddress()+" FunctionName not found");
			Msg.setMsg("Message", "Empty function name");
			Msg.setMsg("ErrorCode", ErrorCode.ParameterError);
			sendMsg(Msg);
			return;
		}
		
		if(FunctionName.equals("echo")){
			
			String m = ClientMsg.getMsg("Message");
			m = (m == null ? "" : m);
			ServerTool.showOnScreen(LogName, ClientSocket.getInetAddress()+" echo " + m);
			
			Msg.setMsg("Message", m + " echo back at " + ServerTool.getTime());
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
			if(ServerCenter.isPlayerExist(ID)){ // registered player
				if(!ServerCenter.verifyPassword(ID, Password)){
					Msg.setMsg("Message", "Wrong password");
					Msg.setMsg("ErrorCode", ErrorCode.IDandPWError);
					sendMsg(Msg);
					return;
				}
				ServerTool.showOnScreen(LogName, ID + " verify password success");
			}
			else{ //new player
				ServerCenter.newPlayer(ID, Password);
				ServerTool.showOnScreen(LogName, ID + " create player success");
			}
			if(!ServerCenter.checkPlayerState(ID, State.InPlayerList)){
				Msg.setMsg("Message", "Player state is pairing or playing");
				Msg.setMsg("ErrorCode", ErrorCode.PlayerStateNotCorret);
				sendMsg(Msg);
				return;
			}
			
			ServerCenter.addPairPlayer(ID, Writer);
			
		}
		else if(FunctionName.equals("move")){
			
			String ID = ClientMsg.getMsg("ID");
			String Password = ClientMsg.getMsg("Password");
			if(ID == null || Password == null){
				Msg.setMsg("Message", "null ID or Password");
				Msg.setMsg("ErrorCode", ErrorCode.ParameterError);
				sendMsg(Msg);
				return;
			}
			if(!ServerCenter.verifyPassword(ID, Password)){
				Msg.setMsg("Message", "Wrong password");
				Msg.setMsg("ErrorCode", ErrorCode.IDandPWError);
				sendMsg(Msg);
				return;
			}
			if(!ServerCenter.checkPlayerState(ID, State.InMap)){
				Msg.setMsg("Message", "Player state is not playing");
				Msg.setMsg("ErrorCode", ErrorCode.PlayerStateNotCorret);
				sendMsg(Msg);
				return;
			}
			//ST.showOnScreen(LogName, ID + " verify password success");
			ServerCenter.setPlayerMove(ID, ClientMsg, Writer);
			return;
		}
		else{
			
			ServerTool.showOnScreen(LogName, ClientSocket.getInetAddress()+" Unknow function " + FunctionName);
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
			
			ServerTool.showOnScreen(LogName, ClientSocket.getInetAddress()+" Create IO Buffer fail");
			ServerTool.showOnScreen(LogName, ClientSocket.getInetAddress()+" lose connection");
			
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
		
		String Msg = ServerTool.MessageToString(inputMsg);
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
			resultMsg = ServerTool.StringToMessage(receivedString);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultMsg;
	}
}
