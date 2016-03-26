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
		long temp = 0;
		long insidetemp = 0;
		long cycle = 1;
		start = System.currentTimeMillis();
		
		do{
			temp = start + timeInterval * cycle;
			do{
				
				end = System.currentTimeMillis();
				insidetemp = (temp - end)>>1;
				
				try {
					if(insidetemp > 0) Thread.sleep(insidetemp);
				} catch (InterruptedException e) {e.printStackTrace();}
				
				if(!isContinue) break;
				
			}while(end < temp);
			
			if(isContinue) new Thread(Notifier).run();
			
		}while(isContinue);
		
	}
}
