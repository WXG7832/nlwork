package com.nltecklib.protocol.lab.main;

import java.util.Date;

import com.nltecklib.protocol.lab.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;

/**
* @author Administrator
* @version 创建时间：2019年12月5日 下午6:42:40
* 类说明  通道的状态保存数据
*/
public class ChannelStamp {
   
	private ChnState  state; //当前通道状态
	private WorkMode  workMode; //当前工作模式
	private int       stepIndex; //当前通道步次号，从1开始
	private Date      date;   //当前时间
	
	
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
	
	
	
}
