package com.nltecklib.protocol.lab.main;

import com.nltecklib.protocol.lab.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.lab.main.MainEnvironment.SaveFlag;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年7月31日 上午10:29:29
* 用于离线推送的数据包结构
* 与普通推送数据包相比 增加了备份电压和功率电压
*/
public class ChannelOfflineData {
    
	private ChnState state = ChnState.NONE;
	private WorkMode workMode; // 当前工作模式
	private int stepIndex; // 流程步次号
	private int loopIndex; // 流程循环号
	private int executeIndex; //执行序号
	private SaveFlag saveFlag = SaveFlag.COMMON; //关键点数据标记
	private double voltage; // 当前通道的电池电压 mV
	private double current; // 当前通道的电流mA
	private double capacity; // 当前通道电池容量mAh
	private double accumulateCapacity; // 当前通道充放电累计容量
	private long pickupTime; // 采样时间ms
	private long runTime;   //步次累计运行时间ms
	private long totalTime;  //流程累计运行时间ms

	private int mainIndex; // 主控板序号
	private int chnIndexInMain; // 主控板内通道序号
	
	private double energy; // 能量mWh 
	private double accumulateEnergy; // 累计能量
	
	private double powerVoltage; //功率电压
	private double backupVoltage; //备份电压
	private double temperature; //通道温度
	
	
	public ChnState getState() {
		return state;
	}
	public void setState(ChnState state) {
		this.state = state;
	}
	public WorkMode getWorkMode() {
		return workMode;
	}
	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
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
	public int getExecuteIndex() {
		return executeIndex;
	}
	public void setExecuteIndex(int executeIndex) {
		this.executeIndex = executeIndex;
	}
	public SaveFlag getSaveFlag() {
		return saveFlag;
	}
	public void setSaveFlag(SaveFlag saveFlag) {
		this.saveFlag = saveFlag;
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
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
	public double getAccumulateCapacity() {
		return accumulateCapacity;
	}
	public void setAccumulateCapacity(double accumulateCapacity) {
		this.accumulateCapacity = accumulateCapacity;
	}
	public long getPickupTime() {
		return pickupTime;
	}
	public void setPickupTime(long pickupTime) {
		this.pickupTime = pickupTime;
	}
	public long getRunTime() {
		return runTime;
	}
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}
	public int getMainIndex() {
		return mainIndex;
	}
	public void setMainIndex(int mainIndex) {
		this.mainIndex = mainIndex;
	}
	public int getChnIndexInMain() {
		return chnIndexInMain;
	}
	public void setChnIndexInMain(int chnIndexInMain) {
		this.chnIndexInMain = chnIndexInMain;
	}
	public double getEnergy() {
		return energy;
	}
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	public double getAccumulateEnergy() {
		return accumulateEnergy;
	}
	public void setAccumulateEnergy(double accumulateEnergy) {
		this.accumulateEnergy = accumulateEnergy;
	}
	public double getPowerVoltage() {
		return powerVoltage;
	}
	public void setPowerVoltage(double powerVoltage) {
		this.powerVoltage = powerVoltage;
	}
	
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getBackupVoltage() {
		return backupVoltage;
	}
	public void setBackupVoltage(double backupVoltage) {
		this.backupVoltage = backupVoltage;
	}

	public long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}
	@Override
	public String toString() {
		return "ChannelOfflineData [state=" + state + ", workMode=" + workMode + ", stepIndex=" + stepIndex
				+ ", loopIndex=" + loopIndex + ", executeIndex=" + executeIndex + ", saveFlag=" + saveFlag
				+ ", voltage=" + voltage + ", current=" + current + ", capacity=" + capacity + ", accumulateCapacity="
				+ accumulateCapacity + ", pickupTime=" + pickupTime + ", runTime=" + runTime + ", totalTime="
				+ totalTime + ", mainIndex=" + mainIndex + ", chnIndexInMain=" + chnIndexInMain + ", energy=" + energy
				+ ", accumulateEnergy=" + accumulateEnergy + ", powerVoltage=" + powerVoltage + ", backupVoltage="
				+ backupVoltage + ", temperature=" + temperature + "]";
	}
	
	
	
	
	
	
	
}
