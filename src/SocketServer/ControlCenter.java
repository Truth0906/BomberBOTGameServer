package SocketServer;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class ControlCenter {
	
	private int PortList[] = {52013, 52014, 53013, 53014};
	
	public ControlCenter(){
	
	}
	
	public int[] getPortList(){
		return PortList;
	}
}
