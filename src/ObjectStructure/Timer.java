package ObjectStructure;

import Tool.ST;

public class Timer implements Runnable{
	
	private long timeInterval;
	
	
	private String LogName = "Timer";
	
	public Timer(long inputTimeInterval){
		
		timeInterval = inputTimeInterval;
	}
	
	@Override
	public void run() {
		
		long start = 0;
		long end = 0;
		
		ST.showOnScreen(LogName, "Timer Start");
		//do{
			start = System.currentTimeMillis();
			//new Thread(this.notifier).run();
			do{
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {e.printStackTrace();}
				end = System.currentTimeMillis();
			}while((end - start) < this.timeInterval);
		//}while(true);
		
		ST.showOnScreen(LogName, "Timer Stop");
	}

}
