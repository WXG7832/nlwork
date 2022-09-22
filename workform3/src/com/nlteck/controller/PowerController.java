package com.nlteck.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * 总电源控制器
 * 
 * @author Administrator
 *
 */
public abstract class PowerController {

	protected List<PowerListener> listeners = new ArrayList<PowerListener>();
    
	protected boolean poweroff; //主控进入掉电模式？
	public interface PowerListener {

		void powerOff(); // 设备断电通知
	}

	public void addPowerListener(PowerListener listener) {

		listeners.add(listener);
	}

	public void removePowerListener(PowerListener listener) {

		listeners.remove(listener);
	}

	public void triggerPowerOffEvent() {

		for (PowerListener listener : listeners) {

			listener.powerOff();
		}
	}
	/**
	 * 是否发生断电
	 * @return
	 */
	public boolean isPoweroff() {
		return poweroff;
	}
    
	/**
	 * 彻底断电
	 */
	public  abstract void powerOff();
	
	/**
	 * 主控供电是否启动
	 */
	public abstract void powerOn();

}
