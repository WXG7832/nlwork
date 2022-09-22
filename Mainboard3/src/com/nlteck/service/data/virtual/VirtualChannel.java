package com.nlteck.service.data.virtual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;
import com.nltecklib.protocol.power.driver.DriverResumeData.ResumeUnit;
import com.nltecklib.protocol.power.driver.DriverStepData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月17日 下午1:37:49 虚拟通道
 */
public class VirtualChannel {

	public final double INIT_VOLT = 3800; // 初始锂电池电压
    
	private int tickCount;
	
	private int index;
	private WorkMode workMode = WorkMode.SLEEP;
	
	private int stepIndex;
	private int loopIndex;
	private long miliSeconds;
	private double capacity;
	
	
	
	private double  startVoltage; //步次的第1个电压值

	private double voltage = INIT_VOLT; // 当前电芯电压
	private double current; // 当前电芯电流
	
	private int    overVoltageCount; //连续超压次数
	private int    watchIndex; //轮询计数
	
	private DriverEnvironment.ChnState   state = DriverEnvironment.ChnState.UDT; //回检通道状态
	private DriverEnvironment.AlertCode  alertCode = DriverEnvironment.AlertCode.NORMAL;
    private double     lastVoltage; //上一次触发保护的电压值
	private boolean    monitorOverVoltage; //正在监视超压


	private long lastProductTime; // 上1次产生数据的系统时间

	private List<ChnDataPack> datas = Collections.synchronizedList(new ArrayList<>()); // 测试缓存数据

	private VirtualDriver driver;
	
	private Logger logger;

	public VirtualChannel(VirtualDriver driver , int chnIndexInDriver) {

		this.driver = driver;
		this.index = chnIndexInDriver;
		
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setLogger(Logger logger) {
		
		this.logger = logger;
	}
	

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public ChnState getState() {
		return state;
	}

	public void setState(ChnState state) {
		this.state = state;
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public int getLoopIndex() {
		return loopIndex;
	}

	public void setLoopIndex(int loopIndex) {
		this.loopIndex = loopIndex;
	}

	public long getMiliSeconds() {
		return miliSeconds;
	}

	public void setMiliSeconds(long miliSeconds) {
		this.miliSeconds = miliSeconds;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public double getCurrent() {
		return current;
	}

	public void setCurrent(double current) {
		this.current = current;
	}

	

	public void setDriver(VirtualDriver driver) {
		this.driver = driver;
	}

	public long getLastProductTime() {
		return lastProductTime;
	}

	public void setLastProductTime(long lastProductTime) {
		this.lastProductTime = lastProductTime;
	}
	
	public synchronized void clearData() {
		
		datas.clear();
	}

	public synchronized void pushData(ChnDataPack chnData) {
        
		
		if(chnData.getState() == ChnState.UDT) {
			
			datas.clear();
		} else {
			
			if(datas.size() >= 10) {
				
				//datas.subList(0, 1).clear(); //删除头
				if(state == ChnState.RUNNING) {
					close(false); //和主控失联状态，关闭通道
					state = ChnState.EXCEPT;
					
				}
			}
			
		}
		datas.add(chnData);
	}

	public List<ChnDataPack> getDatas() {
		return datas;
	}

	public void clearDatas() {

		datas.clear();
	}

	public boolean open()  {
       
		if(driver.getMode() != DriverMode.WORK) {
			
			return false;
		}
		
		if(driver.getProcedure() == null) {
			
			return false;
		}
		
		setState(ChnState.RUNNING);
		if (getStepIndex() == 0) {

			setStepIndex(1);
		}
		if (getLoopIndex() == 0) {

			setLoopIndex(1);
		}
		//logger.info("open channel stepIndex " + stepIndex + ",loopIndex " + loopIndex );
		
		WorkMode mode = WorkMode.values()[getProcedure().getSteps().get(getStepIndex() - 1).workMode.ordinal()];
		/*由主控告知*/
		
		setWorkMode(mode);
		setLastProductTime(new Date().getTime());
		setState(ChnState.RUNNING);
		
		if(driver.getDriverIndex() == 0 && index == 0) {
			
			//System.out.println("step miliseconds:" + getMiliSeconds());
		}
		
		return true;

	}

	public void close(boolean stop) {
        
		//logger.info("close channel is stop " + stop );
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		if(traces != null) {
			
			for(StackTraceElement trace : traces) {
				
				//logger.info(trace.getClassName() + " " + trace.getFileName() + " " + trace.getLineNumber() + " " + trace.getMethodName());
			}
			
		}
		
		setState(stop ? ChnState.STOP : ChnState.COMPLETE);
		setStepIndex(0);
		setLoopIndex(0);
		setCapacity(0);
		setMiliSeconds(0);
		setWorkMode(null);
	}
	
	
	

	public double getStartVoltage() {
		return startVoltage;
	}

	public void setStartVoltage(double startVoltage) {
		this.startVoltage = startVoltage;
	}
	
	
	
	

	public int getOverVoltageCount() {
		return overVoltageCount;
	}

	public void setOverVoltageCount(int overVoltageCount) {
		this.overVoltageCount = overVoltageCount;
	}


	public boolean isMonitorOverVoltage() {
		return monitorOverVoltage;
	}

	public void setMonitorOverVoltage(boolean monitorOverVoltage) {
		this.monitorOverVoltage = monitorOverVoltage;
	}


	public int getWatchIndex() {
		return watchIndex;
	}

	public void setWatchIndex(int watchIndex) {
		this.watchIndex = watchIndex;
	}
	
	
	

	public double getLastVoltage() {
		return lastVoltage;
	}

	public void setLastVoltage(double lastVoltage) {
		this.lastVoltage = lastVoltage;
	}
	
	
	

	public int getTickCount() {
		return tickCount;
	}

	public void setTickCount(int tickCount) {
		this.tickCount = tickCount;
	}
	
	
	
	
	/**
	 * 根据实际情况获取通道的执行流程
	 * @author  wavy_zheng
	 * 2021年7月8日
	 * @return
	 */
	public DriverStepData getProcedure() {
		
		
		if(driver.getProcedure() != null) {
			
			return driver.getProcedure();
		} 
		
		return null;
		
	}
	
	public VirtualDriver  getDriver() {

		return  driver;
	}
	
	
	

	public DriverEnvironment.AlertCode getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(DriverEnvironment.AlertCode alertCode) {
		this.alertCode = alertCode;
	}

	@Override
	public String toString() {
		return "VirtualChannel [index=" + index + ", workMode=" + workMode + ", state=" + state + ", stepIndex="
				+ stepIndex + ", loopIndex=" + loopIndex + ", miliSeconds=" + miliSeconds + ", capacity=" + capacity
				+ ", startVoltage=" + startVoltage + ", voltage=" + voltage + ", current=" + current
				+ ", lastProductTime=" + lastProductTime +  "]";
	}
	
	public void setResumeUnit(ResumeUnit unit) {
		
		this.capacity = unit.capacity;
		this.stepIndex = unit.stepIndex;
		this.loopIndex = unit.loopIndex;
		this.miliSeconds = unit.miliseconds;
		
	}
	
	

}
