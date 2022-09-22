package com.nlteck.service.alert;

import java.util.concurrent.ScheduledExecutorService;

import com.nlteck.AlertException;
import com.nlteck.Environment;

/**
 * 三色灯控制器基类
 * @author Administrator
 *
 */
public abstract class AudioLightAlarmController {
     
	protected int       driverIndex;
	
	protected byte    colorFlag;
	protected short   lightFlag;
	protected short   audioFlag;
	
	protected ScheduledExecutorService  executor = null;
	
	public final static short FLICKER = (short)0xf0f0;
	public final static short ON = (short)0xffff;
	public final static short OFF = (short)0x00;
	
	public enum LightColor {
		
		GREEN(0x01),YELLOW(0X02),RED(0x04);
		private int code ;
		private LightColor(int code) {
			
			this.code = code;
		}
		
		public int getCode() {
			
			return code;
		}
		
	}
	
	protected  class CloseBeep implements Runnable {

		@Override
		public void run() {
			
			try {
				
				configLightAndAudio(colorFlag,lightFlag,(short)0);
				executor = null; //关闭线程
				Environment.infoLogger.info("trigger beep thread");
			} catch (AlertException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		
		
	}
	
	protected AudioLightAlarmController( int driverIndex) {
		
		
		this.driverIndex = driverIndex;
	}
	/**
	 * 控制三色灯及蜂鸣器
	 * @param colorFlag
	 * @param lightFlag
	 * @param audioFlag
	 * @throws AlertException
	 */
	public abstract void configLightAndAudio(byte colorFlag, short lightFlag, short audioFlag ) throws AlertException ;
	
	/**
	 * 控制三色灯及蜂鸣器在一段时间内蜂鸣后关闭
	 *@param audioTimeout  蜂蜜器鸣叫时间 单位s
	 */
	public abstract void configLightAndAudio(byte colorFlag, short lightFlag, short audioFlag , int audioTimeout)
			throws AlertException;
}
