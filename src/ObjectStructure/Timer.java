package ObjectStructure;

public class Timer implements Runnable{
	
	private long timeInterval;
	private Notifier Notifier;
	
	private String LogName = "Timer";
	
	public Timer(long inputTimeInterval){
		
		timeInterval = inputTimeInterval;
		
		Notifier = new Notifier();
	}
	public void addNotificationList(Notification inputNewNotification){
		Notifier.append(inputNewNotification);
	}
	@Override
	public void run() {
		
		long start = 0;
		long end = 0;
		
		do{
			start = System.currentTimeMillis();
			
			do{
				try {
					Thread.sleep((long) (timeInterval / 10.0));
				} catch (InterruptedException e) {e.printStackTrace();}
				end = System.currentTimeMillis();
			}while((end - start) < this.timeInterval);
			
			new Thread(Notifier).run();
			
		}while(true);
		
	}
}
