package BomberGameBOTServer;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ServerTool.ServerTool;

public class Server implements Runnable {
	//private ServerCenter Center;
	private String LogName = "SocketServer";
	
	public Server(){
		new ServerCenter();
		ServerTool.showOnScreen(LogName, "Socket server init complete");
	}
	@Override
    public void run(){
    	Thread clientThread=null;
    	ServerSocket Server = null;
    	
    	boolean isServerStart = false;
    	
    	int [] PortList = ServerOptions.PortList;
    	
    	for(int i = 0 ; i< PortList.length ; i++){
			try {
				Server = new ServerSocket(PortList[i]);
			} catch (IOException e) {
				ServerTool.showOnScreen(LogName, "Socket server try to start at " + PortList[i] + " port fail");
				continue;
			}
			ServerTool.showOnScreen(LogName, "Socket server start at " + PortList[i] + " port success");
			isServerStart = true;
			break;
    	}
		while(isServerStart){
    		Socket ClientSocket = null;
			try {
				ClientSocket = Server.accept();//
				clientThread=new Thread(new Service(ClientSocket));
				clientThread.start();
			} catch (IOException e) {
				return;
				
			}
    	}
    }
	public static void main(String[] args) {
				
		System.out.print("BomberGameBOT Server v1.0.16.0401 beta\n");
		Server S = new Server();
		new Thread(S).start();
				
	}
}
 