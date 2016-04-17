package BomberBOTGameServer;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ServerTool.EloRating;
import ServerTool.ServerTool;

public class BomberBOTGameServer implements Runnable {
	
	public static String ServerVersion = "1.0.16.0417 beta";
	private String LogName = "BomberBOTGameServer";
	
	public BomberBOTGameServer(){
		new ServerCenter();
		ServerTool.showOnScreen(LogName, "Server center init success");
		ServerTool.showOnScreen(LogName, "Server init success");
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
			ServerTool.showOnScreen(LogName, "Socket server start at " + port + " port");
			isServerStart = true;
			break;
    	}
    	ServerTool.showOnScreen(LogName, "BomberBOTGame Server startup success");
		while(isServerStart){
    		Socket ClientSocket = null;
			try {
				ClientSocket = Server.accept();//
				clientThread=new Thread(new Service(ClientSocket));
				clientThread.start();
			} catch (IOException e) {
				ServerTool.showOnScreen(LogName, "BomberBOTGame Server accept fail");
				return;
			}
    	}
    }
	public static void main(String[] args) {
				
		System.out.println("BomberBOTGame Server v " + BomberBOTGameServer.ServerVersion);
		new Thread(new BomberBOTGameServer()).start();
		
	}
}
 