package com.nlteck.service.alert;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.Environment;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.LightAudioData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
 * 三色灯声光警报器
 * @author Administrator
 *
 */
public class DeviceAudioLightAlarmController extends AudioLightAlarmController{
    
	private SerialPort serialPort;
	
	private int     communicationTimeout = 1200;
	
	
	
	
	public DeviceAudioLightAlarmController(int index , SerialPort serialPort) {
		
		this(index,serialPort,1200);
	}
	
	public DeviceAudioLightAlarmController(int index , SerialPort serialPort , int timeout) {
		
		super(index);
		this.serialPort = serialPort;
		this.communicationTimeout = timeout;
	}
	/**
	 * 
	 * @param colorFlag  0x01 green 0x02 yellow  0x04 red
	 * @param lightFlag  
	 * @param audioFlag
	 * @throws AlertException
	 */
	public void configLightAndAudio(byte colorFlag, short lightFlag, short audioFlag ) throws AlertException {
		
		configLightAndAudio(colorFlag, lightFlag, audioFlag ,0);
	}
    
	/**
	 * 
	 * @param colorFlag
	 * @param lightFlag
	 * @param audioFlag
	 * @param audioTimeout  单位s
	 * @throws AlertException
	 */
	public synchronized void configLightAndAudio(byte colorFlag, short lightFlag, short audioFlag , int audioTimeout)
			throws AlertException {
		
		LightAudioData cld = new LightAudioData();
		cld.setDriverIndex(driverIndex);
		cld.setColorFlag(colorFlag);
		cld.setAudioFlag(audioFlag);
		cld.setLightFlag(lightFlag);
		this.colorFlag = colorFlag;
		this.lightFlag = lightFlag;
		this.audioFlag = audioFlag;
		
	 
		try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cld),
					communicationTimeout);
		} catch (IOException e) {
            
			
			CommonUtil.sleep(1000);
			try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cld),
					communicationTimeout);
			} catch (IOException ex) {
				
				ex.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR,"声光报警器通信发生错误:设置三色灯及蜂鸣器失败");
			}
			
		}
		
		if(executor != null) {
			
			executor.shutdown(); //任何灯操作都将先关闭延时操作
			executor = null;
		}
		Environment.infoLogger.info("start beep thread in " + audioTimeout + " s,audioFlag " + audioFlag);
		if(audioTimeout > 0  && audioFlag != 0) {
			
			if(executor == null) {
				
				executor = Executors.newSingleThreadScheduledExecutor();
				executor.schedule(new Runnable() {

					@Override
					public void run() {
						
						Environment.infoLogger.info("close beep!!!");
						try {
							configLightAndAudio(colorFlag,lightFlag,(short)0);
						} catch (AlertException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
				}, audioTimeout, TimeUnit.SECONDS);
			}
			
		}

	}

	public int getDriverIndex() {
		return driverIndex;
	}

	public void setDriverIndex(int driverIndex) {
		this.driverIndex = driverIndex;
	}

	public byte getColorFlag() {
		return colorFlag;
	}

	public void setColorFlag(byte colorFlag) {
		this.colorFlag = colorFlag;
	}

	public short getLightFlag() {
		return lightFlag;
	}

	public void setLightFlag(short lightFlag) {
		this.lightFlag = lightFlag;
	}

	public short getAudioFlag() {
		return audioFlag;
	}

	public void setAudioFlag(short audioFlag) {
		this.audioFlag = audioFlag;
	}

	public int getCommunicationTimeout() {
		return communicationTimeout;
	}

	public void setCommunicationTimeout(int communicationTimeout) {
		this.communicationTimeout = communicationTimeout;
	}
	
	
	
}
