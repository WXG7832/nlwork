package com.nlteck.model;

import java.util.Date;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月27日 下午2:33:18
* 稳定度测试数据
*/
public class StableDataDO {
    
	private int id;
	private String mode;
	private String pole;
	private double calculateDot;
	private double meter;
	private double adc;
	private Date   date;
	private int    chnId;
	private String result;
	private int    index;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getPole() {
		return pole;
	}
	public void setPole(String pole) {
		this.pole = pole;
	}
	public double getCalculateDot() {
		return calculateDot;
	}
	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}
	public double getMeter() {
		return meter;
	}
	public void setMeter(double meter) {
		this.meter = meter;
	}
	public double getAdc() {
		return adc;
	}
	public void setAdc(double adc) {
		this.adc = adc;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getChnId() {
		return chnId;
	}
	public void setChnId(int chnId) {
		this.chnId = chnId;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public String toString() {
		return "StableDataDO [id=" + id + ", mode=" + mode + ", pole=" + pole + ", calculateDot=" + calculateDot
				+ ", meter=" + meter + ", adc=" + adc + ", date=" + date + ", chnId=" + chnId + ", result=" + result
				+ ", index=" + index + "]";
	}
	
	
	
	
}
