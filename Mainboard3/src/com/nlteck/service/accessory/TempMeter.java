package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatLine;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatMeterCurrent;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatMode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.OverTempFlag;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.TempQueryData;

/**
 * 温控表基类
 * @author Administrator
 *
 */
public abstract class TempMeter {
     
	protected int index; //表序号
	
	protected double  temperature = 20; //当前温度
	
	protected double  tempUpper = 70; //超温报警值
	
	protected double  tempLower; //低温报警值
	
	protected PowerState  ps = PowerState.OFF; //温度开关
	
	protected WorkState  ws  = WorkState.NORMAL; //工作状态
	
	protected OverTempFlag  overFlag = OverTempFlag.NORMAL;
	
	protected PowerState procedureStartup = PowerState.OFF; //流程启动时必须要处于恒温？
	
	protected boolean  use; //表禁用?
	
	protected long     runMiliseconds;
	
	
	//具体故障信息
	protected HeatLine            heatLine         = HeatLine.NORMAL;  //加热丝断？
	protected HeatMeterCurrent    heatMeterCurrent = HeatMeterCurrent.NORMAL;  //表过流
	protected HeatMode            heatMode         = HeatMode.ST;  //加热模式
	
	
	protected TempMeter(int index) {
		
		this.index = index;
	}
	
	/**
	 * 开启或关闭温控表
	 * @param ps
	 */
	public abstract void  power(PowerState  ps) throws AlertException;
	
	/**
	 * 读取当前温度
	 * @return
	 */
	public abstract TempQueryData  readTemperatureData() throws AlertException;
	
	/**
	 * 写入恒定温度,设定当前恒温温度
	 * @param temp
	 */
	public abstract void    writeConstTemperature(double temp) throws AlertException;
	
	/**
	 * 读取配置的恒定温度
	 */
	public abstract double  readConstTemperature() throws AlertException;
	
	/**
	 * 设定超温限值
	 * @param temp
	 */
	public abstract void    writeTempUpper(double temp) throws AlertException;
	
	/**
	 * 读取超温限值
	 * @return
	 */
	public abstract double  readTempUpper() throws AlertException;
	
	

	public PowerState getPs() {
		return ps;
	}

	public void setPs(PowerState ps) {
		
//		StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
//		for(StackTraceElement stack : stacks) {
//			
//			System.out.println(stack.toString());
//		}
		this.ps = ps;
	}

	public WorkState getWs() {
		return ws;
	}

	public void setWs(WorkState ws) {
		this.ws = ws;
	}

	public int getIndex() {
		return index;
	}

	public double getTemperature() {
		return temperature;
	}
	
	public void setTemperature(double temperature) {
		
		this.temperature = temperature;
	}

	public HeatLine getHeatLine() {
		return heatLine;
	}

	public HeatMeterCurrent getHeatMeterCurrent() {
		return heatMeterCurrent;
	}

	public HeatMode getHeatMode() {
		return heatMode;
	}

	public double getTempUpper() {
		return tempUpper;
	}

	public double getTempLower() {
		return tempLower;
	}

	public void setTempUpper(double tempUpper) {
		this.tempUpper = tempUpper;
	}

	public void setTempLower(double tempLower) {
		this.tempLower = tempLower;
	}

	public PowerState getProcedureStartup() {
		return procedureStartup;
	}

	public void setProcedureStartup(PowerState procedureStartup) {
		this.procedureStartup = procedureStartup;
	}

	public OverTempFlag getOverFlag() {
		return overFlag;
	}

	public void setOverFlag(OverTempFlag overFlag) {
		this.overFlag = overFlag;
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	

	
	
	
	
}
