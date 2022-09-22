package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月29日 下午5:02:22
* 类说明
*/
public class DelayData extends Data implements Configable, Queryable, Responsable {
    
	private int   moduleOpenDelay;
	private int   moduleCloseDelay;
	private int   modeSwitchDelay;
	private int   programSetDelay;
	private int   low2hightDelay;
	private int   high2lowDelay;
	private int   readMeterDelay;
	private int   switchMeterDelay;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(moduleOpenDelay), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(moduleCloseDelay), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(modeSwitchDelay), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(programSetDelay), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(high2lowDelay), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(low2hightDelay), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(readMeterDelay), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(switchMeterDelay), 2,true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		moduleOpenDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		moduleCloseDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		modeSwitchDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programSetDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		high2lowDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		low2hightDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		readMeterDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		switchMeterDelay = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.DelayCode;
	}

	public int getModuleOpenDelay() {
		return moduleOpenDelay;
	}

	public void setModuleOpenDelay(int moduleOpenDelay) {
		this.moduleOpenDelay = moduleOpenDelay;
	}

	public int getModuleCloseDelay() {
		return moduleCloseDelay;
	}

	public void setModuleCloseDelay(int moduleCloseDelay) {
		this.moduleCloseDelay = moduleCloseDelay;
	}

	public int getModeSwitchDelay() {
		return modeSwitchDelay;
	}

	public void setModeSwitchDelay(int modeSwitchDelay) {
		this.modeSwitchDelay = modeSwitchDelay;
	}

	public int getProgramSetDelay() {
		return programSetDelay;
	}

	public void setProgramSetDelay(int programSetDelay) {
		this.programSetDelay = programSetDelay;
	}

	public int getLow2hightDelay() {
		return low2hightDelay;
	}

	public void setLow2hightDelay(int low2hightDelay) {
		this.low2hightDelay = low2hightDelay;
	}

	public int getHigh2lowDelay() {
		return high2lowDelay;
	}

	public void setHigh2lowDelay(int high2lowDelay) {
		this.high2lowDelay = high2lowDelay;
	}

	public int getReadMeterDelay() {
		return readMeterDelay;
	}

	public void setReadMeterDelay(int readMeterDelay) {
		this.readMeterDelay = readMeterDelay;
	}

	public int getSwitchMeterDelay() {
		return switchMeterDelay;
	}

	public void setSwitchMeterDelay(int switchMeterDelay) {
		this.switchMeterDelay = switchMeterDelay;
	}
	
	

}
