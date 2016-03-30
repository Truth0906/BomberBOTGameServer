package ObjectStructure;

import Tool.ServerTool;

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
			++cycle;
			
			if(cycle >= Long.MAX_VALUE){
				cycle = 1;
				start = temp;
			}
			
			do{
				
				end = System.currentTimeMillis();
				if((insidetemp = (temp - end)) > 1) insidetemp = (temp - end)>>1;
				
				try {
					if(end < temp) Thread.sleep(insidetemp);
				} catch (InterruptedException e) {e.printStackTrace();}
				
				if(!isContinue) break;
				
			}while(end < temp);
			
			if(isContinue) new Thread(Notifier).run();
			
		}while(isContinue);
		
	}
}
