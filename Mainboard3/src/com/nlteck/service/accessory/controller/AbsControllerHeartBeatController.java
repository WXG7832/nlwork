package com.nlteck.service.accessory.controller;
/**
* @author  wavy_zheng
* @version 创建时间：2020年12月24日 下午5:22:27
*  控制板心跳链接
*/

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract  class AbsControllerHeartBeatController {
   
	private int   seconds; //检测间隔
	private int   maxCount; //最大链接不回复次数
	private BeatConnectListener  listener; //心跳事件
	private ScheduledExecutorService executor ;
	private int count;
	
	
	public interface BeatConnectListener {
		
		/**
		 * 心跳事件
		 * @author  wavy_zheng
		 * 2020年12月24日
		 * @param seconds
		 * @param count
		 */
		public void beatConnect(int seconds , int count);
		
	}
	
	
	public void startWork() {
		
		if(executor == null) {
			
			
			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					
					if(!checkHeartbeat()) {
						
						count++;
						if(count > maxCount) {
							
							if(listener != null) {
								
								listener.beatConnect(seconds, count); //通知心跳链接超时
							}
							
						}
					} else {
						
						count = 0;
					}
					
				}
				
				
			}, 1, seconds, TimeUnit.SECONDS);
			
		}
		
	}
	

	 public AbsControllerHeartBeatController(int seconds, int maxCount) {
		super();
		this.seconds = seconds;
		this.maxCount = maxCount;
		
		
		
	}
   



	/**
	  * 心跳检测
	  * @author  wavy_zheng
	  * 2020年12月24日
	  */
	 public abstract boolean checkHeartbeat();




	public int getSeconds() {
		return seconds;
	}




	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}


	public BeatConnectListener getListener() {
		return listener;
	}




	public void setListener(BeatConnectListener listener) {
		this.listener = listener;
	}




	public int getMaxCount() {
		return maxCount;
	}




	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	 
	 
	 
	
	
}
