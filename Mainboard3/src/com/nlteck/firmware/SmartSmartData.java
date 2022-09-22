package com.nlteck.firmware;

import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年5月4日 下午2:34:21
* 进行智能保护的样本数据
*/
public class SmartSmartData {
   
	private double voltage;
	private double backupVoltage; //备份电压
	private double powerVoltage; //功率电压
	private double current;
	private long   seconds; //当前步次流逝的时间s
	private WorkMode  workMode; //当前工作模式
	private double resister; //当前接触电阻
	
	
	public SmartSmartData(double voltage, double backupVolt , double powerVolt , 
			double current , long secs , WorkMode workMode) {
		
		this.voltage = voltage;
		this.backupVoltage = backupVolt;
		this.powerVoltage  = powerVolt;
		this.current       = current;
		this.seconds       = secs;
		this.workMode      = workMode;
		
	}
	
	
	
	public double getVoltage() {
		return voltage;
	}
	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}
	public double getBackupVoltage() {
		return backupVoltage;
	}
	public void setBackupVoltage(double backupVoltage) {
		this.backupVoltage = backupVoltage;
	}
	public double getPowerVoltage() {
		return powerVoltage;
	}
	public void setPowerVoltage(double powerVoltage) {
		this.powerVoltage = powerVoltage;
	}
	public double getCurrent() {
		return current;
	}
	public void setCurrent(double current) {
		this.current = current;
	}
	public long getSeconds() {
		return seconds;
	}
	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}
	public WorkMode getWorkMode() {
		return workMode;
	}
	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}
	public double getResister() {
		return resister;
	}
	public void setResister(double resister) {
		this.resister = resister;
	}
	
	
	
	
	
}
