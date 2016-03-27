package ObjectStructure;

import java.util.ArrayList;
import java.util.Iterator;


public class Notifier implements Runnable{
	
	private Object List_Lock;
	private ArrayList<Notification> NotificationList;
	public Notifier(){
		List_Lock = new Object();
		NotificationList = new ArrayList<Notification>();
		
	}
	public boolean remove(Notification x){
		
		synchronized(List_Lock){
			
			return NotificationList.remove(x);
		
		}
	}
	public boolean append(Notification x){
		
		synchronized(List_Lock){
			return NotificationList.add(x);
		}
		
	}
	@Override
	public void run() {
		synchronized(List_Lock){
			
			Iterator<Notification> ListIterator = NotificationList.iterator();
			
			while(ListIterator.hasNext()){
				ListIterator.next().TimeUp();
			}
		}
	}
}
