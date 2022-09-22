package com.nltecklib.protocol.plc.doubleACT;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 机器控制状态
 * @author Administrator
 *
 */
public class DoubleACTControlData extends PlcData {

	public enum PlcDataCode {

		FixtureState(200), 		//夹具状态
		There_1(201),			//夹具有无料1
		Move_1_1(202),			//移栽1-1
		Process_Request(203),	//发流程请求
		Move_2_1(204),			//移栽2-1
		TestState(205),			//测试状态
		AutoState(206),			//自动状态
		FixtureShield(207),		//夹具屏蔽
		FixtureTemp(208),		//夹具温度
		FixturePressure(209),	//夹具压力变更信号
		WorkCompletionSignal(210),	//工作完成信号
		There_2(211),			//夹具有无料2
		Move_1_2(212),			//移栽1-2
		FormationCapacity(213),	//夹具化成/容量
		Move_2_2(214),			//移栽2-2
		NG(215),				//化成全部NG
		State(216),
		PrecompletionSignal(217);	//容量测试预完成信号

		private int code;

		private PlcDataCode(int funCode) {
			this.code = funCode;
		}

		public int getCode() {
			return code;
		}
	}
	
	/**
	 *	机器控制状态
	 */
	public static enum ControlState {
		
		OFF, ON;
	}
	
	private static final String DEFAULT_AREA = "WR";
	private PlcDataCode plcDataCode;
	private ControlState controlState;
	
	public byte[] encode(){
		String memory = "";
		if(plcDataCode.getCode() == PlcDataCode.AutoState.getCode()){
			memory = area + "." + plcDataCode.getCode() + ".00";
		} else {
			memory = area + "." + plcDataCode.getCode() + "." + fixtureIndex;
		}
		byte[] data = null;
		if(!isRead){
			data = new byte[]{(byte) controlState.ordinal()};
		}
		return encode(memory, isBit, isRead, data);
	}
	
	public ControlState controlDecode(byte[] data){
		return ControlState.values()[data[0]];
	}
	
	public DoubleACTControlData() {
		super();
		isBit = true;
		area = DEFAULT_AREA;
	}

	public DoubleACTControlData(PlcDataCode plcDataCode, int fixtureIndex) {
		super();
		this.plcDataCode = plcDataCode;
		this.fixtureIndex = fixtureIndex;
		isBit = true;
		area = DEFAULT_AREA;
	}
	
	public DoubleACTControlData(PlcDataCode plcDataCode, int fixtureIndex, boolean isRead, ControlState controlState) {
		super();
		this.plcDataCode = plcDataCode;
		this.fixtureIndex = fixtureIndex;
		this.controlState = controlState;
		this.isRead = isRead;
		isBit = true;
		area = DEFAULT_AREA;
	}


	public PlcDataCode getPlcDataCode() {
		return plcDataCode;
	}

	public void setPlcDataCode(PlcDataCode plcDataCode) {
		this.plcDataCode = plcDataCode;
	}

	public ControlState getControlState() {
		return controlState;
	}

	public void setControlState(ControlState controlState) {
		this.controlState = controlState;
	}
	
	
	
}
