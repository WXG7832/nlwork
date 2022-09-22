package com.nltecklib.protocol.plc.pief44;

import com.nltecklib.protocol.plc.PlcData;

/**
 *	 机器控制状态\
 * @author Administrator
 *
 */
public class PIEF44ICControlData extends PlcData {

	public enum PIEFPlcDataCode {

		FixtureState(200), 		//夹具状态
		There_1(201),			//夹具有无料
		Move_1(202),			//移栽1
		Process_Request(203),	//发流程请求
		Move_2(204),			//移栽2
		TestState(205),			//化成状态或容量测试状态
		AutoState(206),			//自动状态
		FixtureShield(207),		//夹具屏蔽
		FixtureTemp(208),		//夹具温度
		FixturePressure(209),	//夹具压力变更信号
		WorkCompletionSignal(210),	//工作完成信号
		NG(211);				//化成全部NG

		private int code;

		private PIEFPlcDataCode(int funCode) {
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
	private PIEFPlcDataCode pIEFPlcDataCode;
	private ControlState controlState;
	
	public byte[] encode(){
		String memory = "";
		if(pIEFPlcDataCode.getCode() == PIEFPlcDataCode.AutoState.getCode()){
			memory = area + "." + pIEFPlcDataCode.getCode() + ".00";
		} else {
			memory = area + "." + pIEFPlcDataCode.getCode() + "." + fixtureIndex;
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
	
	public PIEF44ICControlData() {
		super();
		isBit = true;
		area = DEFAULT_AREA;
	}

	public PIEF44ICControlData(PIEFPlcDataCode pIEFPlcDataCode, int fixtureIndex) {
		super();
		this.pIEFPlcDataCode = pIEFPlcDataCode;
		this.fixtureIndex = fixtureIndex;
		isBit = true;
		area = DEFAULT_AREA;
	}
	
	public PIEF44ICControlData(PIEFPlcDataCode pIEFPlcDataCode, int fixtureIndex, boolean isRead, ControlState controlState) {
		super();
		this.pIEFPlcDataCode = pIEFPlcDataCode;
		this.fixtureIndex = fixtureIndex;
		this.controlState = controlState;
		this.isRead = isRead;
		isBit = true;
		area = DEFAULT_AREA;
	}


	public PIEFPlcDataCode getPlcDataCode() {
		return pIEFPlcDataCode;
	}

	public void setPlcDataCode(PIEFPlcDataCode pIEFPlcDataCode) {
		this.pIEFPlcDataCode = pIEFPlcDataCode;
	}

	public ControlState getControlState() {
		return controlState;
	}

	public void setControlState(ControlState controlState) {
		this.controlState = controlState;
	}
	
	
	
}
