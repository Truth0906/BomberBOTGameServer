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
    	
    	for(int port : PortList){
			try {
				Server = new ServerSocket(port);
			} catch (IOException e) {
				ServerTool.showOnScreen(LogName, "Socket server try to start at " + port + " port fail");
				continue;
			}
			ServerTool.showOnScreen(LogName, "Socket server start at " + port + " port success");
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
				
		System.out.print("BomberGameBOT Server v 1.0.16.0404 beta\n");
		new Thread(new Server()).start();
		
	}
}
 