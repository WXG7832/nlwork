package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
* @author  wavy_zheng
* @version 创建时间：2020年4月24日 下午2:43:17
* 类说明
*/
public class LogicHKOperationData extends Data implements Configable, Queryable, Responsable {
    
	public enum SwitchState {
		
		OFF , ON;
		
	}
	
	
	private short  selectFlag; //选中标志位
	private SwitchState  switchState = SwitchState.OFF;
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) switchState.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long)selectFlag, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > SwitchState.values().length - 1) {
			
			throw new RuntimeException("error switch code " + code);
		}
		switchState = SwitchState.values()[code];
		selectFlag = (short) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);

	}

	@Override
	public Code getCode() {
		
		return LogicCode.HKOperateCode;
	}

	public short getSelectFlag() {
		return selectFlag;
	}

	public void setSelectFlag(short selectFlag) {
		this.selectFlag = selectFlag;
	}

	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}
	
	

}
