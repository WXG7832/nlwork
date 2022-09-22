package com.nltecklib.protocol.plc.pief44;

import com.nltecklib.protocol.plc.PlcData;
import com.nltecklib.protocol.plc.pief44.PIEF44ICControlData.ControlState;

/**
 * 
* @ClassName: PIEF44FeedingData  
* @Description: TODO(这里用一句话描述这个类的作用)  
* @author zhang_longyong  
* @date 2019年12月16日
 */
public class PIEF44MachineControlData extends PlcData {

	public enum PIEFMachineControlCode {

		START_WRITE(0),//启动,上位机写,plc接收后清零
		RESET_WRITE(1),//复位,上位机写,plc接收后清零
		STOP_WRITE(2),//停止,上位机写,plc接收后清零
		AUTO_READ(3),//自动运行中,上位机读
		STOP_READ(4),//停止中,上位机读
		ALERT_READ(5),//报警中,上位机读
		;
		private int code;

		private PIEFMachineControlCode(int funCode) {
			this.code = funCode;
		}

		public int getCode() {
			return code;
		}
	}
	
	public static final int DEFAULT_ADDRESS = 260;
	private static final String DEFAULT_AREA = "WR";
	private PIEFMachineControlCode pIEFCode;
	private ControlState controlState;
	
	public byte[] encode(){
		String memory = area + "." + address + "." + pIEFCode.getCode();
		byte[] data = null;
		if(!isRead){
			data = new byte[]{(byte) controlState.ordinal()};
		}
		return encode(memory, isBit, isRead, data);
	}
	
	public ControlState controlDecode(byte[] data){
		return ControlState.values()[data[0]];
	}
	
	public PIEF44MachineControlData() {
		super();
		isBit = true;
		area = DEFAULT_AREA;
		address = DEFAULT_ADDRESS;
	}

	public PIEF44MachineControlData(PIEFMachineControlCode pIEFCode, int fixtureIndex) {
		super();
		this.pIEFCode = pIEFCode;
		this.fixtureIndex = fixtureIndex;
		isBit = true;
		area = DEFAULT_AREA;
		address = DEFAULT_ADDRESS;
	}
	
	public PIEF44MachineControlData(PIEFMachineControlCode pIEFCode, int fixtureIndex, boolean isRead, ControlState controlState) {
		super();
		this.pIEFCode = pIEFCode;
		this.fixtureIndex = fixtureIndex;
		this.controlState = controlState;
		this.isRead = isRead;
		isBit = true;
		area = DEFAULT_AREA;
		address = DEFAULT_ADDRESS;
	}

	public ControlState getControlState() {
		return controlState;
	}

	public void setControlState(ControlState controlState) {
		this.controlState = controlState;
	}

	public PIEFMachineControlCode getpIEFCode() {
		return pIEFCode;
	}

	public void setpIEFCode(PIEFMachineControlCode pIEFCode) {
		this.pIEFCode = pIEFCode;
	}
	
}
