package com.nlteck.service;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.firmware.Channel;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.connector.LogicConnector;
import com.nltecklib.protocol.li.logic.LogicChnSwitchData;
import com.nltecklib.protocol.li.logic.LogicProcedureData;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;

/**
 * 流程管理器 控制流程的核心业务逻辑
 * 
 * @author Administrator
 *
 */
@Deprecated
public class ProcessManager {

	// 配置的流程
	private ProcedureData procedure;
	// 当前步次
	private int stepIndex;
	// 所有管控的通道
	private List<Channel> channels = new ArrayList<Channel>();
	// 用户操作
	private State optState = State.NORMAL;
	// 当前设备或分区状态
	private State deviceState = State.NORMAL;

	private LogicConnector logicController; // 逻辑板控制器
	
	//private MainBoard       mainBoard;  //主控板

	public ProcessManager(ProcedureData pd, List<Channel> list , LogicConnector logicController) {

		this.procedure = pd;
		this.channels = list;
		this.logicController = logicController;
	}

	/**
	 * 初始化逻辑板通道状态;用于每次流程步次开始前逻辑板配置
	 */
	private void initAllLogicBoardChnStates() {

//		for (int n = 0; n < channels.size() / MainBoard.startupCfg.getDriverChnCount(); n++) {
//			// 配置分区通道开关状态
//			LogicChnSwitchData lcsd = new LogicChnSwitchData();
//			lcsd.setUnitIndex(n);
//			// 初始化全部打开
//			lcsd.init(MainBoard.startupCfg.getLogicDriverCount() * MainBoard.startupCfg.getDriverChnCount(), false);
//
//			for (int j = 0; j < MainBoard.startupCfg.getDriverChnCount(); j++) {
//
//				Channel chn = channels.get(n * MainBoard.startupCfg.getDriverChnCount() + j);
//				if (chn.isClosed()) {
//
//					lcsd.setState(n * MainBoard.startupCfg.getDriverChnCount() + j, true);
//				}
//			}
//
//			StringBuffer err = new StringBuffer();
//			
//		}
	}
	
	private void configLogicBoardStep() {
		
		Step step = procedure.getStep(stepIndex);
		LogicProcedureData lpd = new LogicProcedureData();
		
		
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public State getOptState() {
		return optState;
	}

	public void setOptState(State optState) {
		this.optState = optState;
	}

	public State getDeviceState() {
		return deviceState;
	}

	public void setDeviceState(State deviceState) {
		this.deviceState = deviceState;
	}

}
