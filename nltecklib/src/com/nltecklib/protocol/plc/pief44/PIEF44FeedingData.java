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
public class PIEF44FeedingData extends PlcData {

	public enum PIEFFeedingCode {

		BARCODE(0), 		                //扫码
		DATABASE(1),			            //调用数据库
		UNLOAD_SIGN(5),			            //下料信号
		UNLOAD_CONFIRM(6),	                //下料确认
		UNLOAD_CONVEYOR_SIGN(7),	        //电芯下料传送带信号
		UNLOAD_CONVAYOR_CONFIRM(8),         //电芯下料传送带确认
		UNLOAD_WITH_CLIP_SIGN(9),	        //电芯入弹夹信号
		UNLOAD_WITH_CLIP_CONFIRM(10),       //电芯入弹夹确认信号
		UNLOAD_CLIP_BARCODE_SIGN(11),		//下料弹夹扫码触发
		UNLOAD_CLIP_BARCODE_OK(12),			//下料弹夹扫码OK信号
		UNLOAD_CLIP_BARCODE_NG(13),			//下料弹夹扫码NG信号
		UNLOAD_CLIP_BINDING(14),			//下料弹夹绑定结束
		UNLOAD_CLIP_BINDING_FINISH(15);		//下料弹夹绑定结束确认

		private int code;

		private PIEFFeedingCode(int funCode) {
			this.code = funCode;
		}

		public int getCode() {
			return code;
		}
	}
	
	public static final int DEFAULT_ADDRESS = 212;
	private static final String DEFAULT_AREA = "WR";
	private PIEFFeedingCode pIEFFeedingCode;
	private ControlState controlState;
	
	public byte[] encode(){
		String memory = area + "." + address + "." + pIEFFeedingCode.getCode();
		byte[] data = null;
		if(!isRead){
			data = new byte[]{(byte) controlState.ordinal()};
		}
		return encode(memory, isBit, isRead, data);
	}
	
	public ControlState controlDecode(byte[] data){
		return ControlState.values()[data[0]];
	}
	
	public PIEF44FeedingData() {
		super();
		isBit = true;
		area = DEFAULT_AREA;
		address = DEFAULT_ADDRESS;
	}

	public PIEF44FeedingData(PIEFFeedingCode pIEFFeedingCode, int fixtureIndex) {
		super();
		this.pIEFFeedingCode = pIEFFeedingCode;
		this.fixtureIndex = fixtureIndex;
		isBit = true;
		area = DEFAULT_AREA;
		address = DEFAULT_ADDRESS;
	}
	
	public PIEF44FeedingData(PIEFFeedingCode pIEFFeedingCode, int fixtureIndex, boolean isRead, ControlState controlState) {
		super();
		this.pIEFFeedingCode = pIEFFeedingCode;
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

	public PIEFFeedingCode getpIEFFeedingCode() {
		return pIEFFeedingCode;
	}

	public void setpIEFFeedingCode(PIEFFeedingCode pIEFFeedingCode) {
		this.pIEFFeedingCode = pIEFFeedingCode;
	}	
	
	
}
