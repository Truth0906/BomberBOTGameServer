package ObjectStructure;

public class Timer implements Runnable{
	
	private long timeInterval;
	private Notifier Notifier;
	private boolean isContinue;
	private String LogName = "Timer";
	
	public Timer(long inputTimeInterval){
		
		timeInterval = inputTimeInterval;
		isContinue = true;
		Notifier = new Notifier();
	}
	public void addNotificationList(Notification inputNewNotification){
		Notifier.append(inputNewNotification);
	}
	public void stop(){
		isContinue = false;
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
			
			if(!isContinue) break;
			
			new Thread(Notifier).run();
			
		}while(true);
		
	}
}
