package com.nltecklib.protocol.lab.main;

import java.util.Date;

import com.nltecklib.protocol.lab.main.MainEnvironment.AlertCode;

/**
 * 报警数据结构
 * @author Administrator
 *
 */
public class AlertPack {
    
	private int   chnIndex;  //通道地址
	private Date date = new Date(); //报警时间
	private AlertCode alertCode = AlertCode.NORMAL;
	private String alertInfo = "";  //异常信息
	private int infoBytes; //信息长度
	
	
	public AlertPack() {
		
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public AlertCode getAlertCode() {
		return alertCode;
	}
	public void setAlertCode(AlertCode alertCode) {
		this.alertCode = alertCode;
	}
	public String getAlertInfo() {
		return alertInfo;
	}
	public void setAlertInfo(String alertInfo) {
		this.alertInfo = alertInfo;
	}

	public int getInfoBytes() {
		return infoBytes;
	}

	public void setInfoBytes(int infoBytes) {
		this.infoBytes = infoBytes;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	@Override
	public String toString() {
		return "AlertPack [chnIndex=" + chnIndex + ", date=" + date + ", alertCode=" + alertCode + ", alertInfo="
				+ alertInfo + ", infoBytes=" + infoBytes + "]";
	}
	
	
	
}
