package com.nlteck.fireware;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.model.Channel;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月7日 下午12:55:32
* 设备驱动板模型
*/
public class DeviceDriverboard {
   
	
	private int driverIndex;
	
	private List<Channel> channels = new ArrayList<>();
	
	
	public DeviceDriverboard(int driverIndex) {
		
		this.driverIndex = driverIndex;
	}
	
	/**
	 * 用于同步控制，进入校准测试
	 * @author  wavy_zheng
	 * 2022年3月7日
	 */
	public synchronized  void enterCal() {
		
		
	}
	
	/**
	 * 用于同步控制,退出校准测试
	 * @author  wavy_zheng
	 * 2022年3月7日
	 */
	public synchronized void exitCal() {
		
		
	}
	
	
	public void addChannel(Channel chn) {
		
		channels.add(chn);
		chn.setDriverboard(this);
	}
	
}
