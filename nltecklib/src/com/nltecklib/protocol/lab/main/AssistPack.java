package com.nltecklib.protocol.lab.main;

/**
 * 辅助数据
 * @author Administrator
 *
 */
public class AssistPack {
	
	private int    chnIndex; //通道序号
	private double powerVoltage; //功率电压
	private double backupVoltage; //备份电压
	private double chnTemp; //通道温度
	
	public AssistPack() {

	}
	
	public double getPowerVoltage() {
		return powerVoltage;
	}
	public void setPowerVoltage(double powerVoltage) {
		this.powerVoltage = powerVoltage;
	}
	public double getBackupVoltage() {
		return backupVoltage;
	}
	public void setBackupVoltage(double backupVoltage) {
		this.backupVoltage = backupVoltage;
	}
	public double getChnTemp() {
		return chnTemp;
	}
	public void setChnTemp(double chnTemp) {
		this.chnTemp = chnTemp;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	@Override
	public String toString() {
		return "AssistPack [chnIndex=" + chnIndex + ", powerVoltage=" + powerVoltage + ", backupVoltage="
				+ backupVoltage + ", chnTemp=" + chnTemp + "]";
	}
	
	

}
