package com.nlteck.junit;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeTestor {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				
				System.out.println(new Date());
				
			}
			
			
		},100, 500, TimeUnit.MILLISECONDS);
		
	    Thread.sleep(2000);
	    
	    System.out.println("shut down nown");
	    executor.shutdownNow();
	    System.out.println("delay 5000 ms");
	    Thread.sleep(5000);
	    
	    System.out.println("process end");
	    
		

	}

}
