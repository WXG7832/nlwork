package com.nltecklib.protocol.lab.backup;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.LedState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年10月25日 上午10:05:22
* 备份板LED控制
*/
public class BLedData extends Data implements Configable, Responsable {
    
	private  short   chnFlag; //亮灯通道标志位
	private  LedState  ledState = LedState.OFF;
	
	@Override
	public boolean supportMain() {
		
		return false;
	}

	@Override
	public boolean supportChannel() {
		
		return false;
	}

	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)chnFlag, 2, true)));
		data.add((byte) ledState.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int   index = 0;
		chnFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > LedState.values().length - 1) {
			
			throw new RuntimeException("error led state code :" + code);
		}

	}

	@Override
	public Code getCode() {
		
		return BackupCode.LedCode;
	}
	
	public void  clearFlag() {
		
		chnFlag = 0;
	}
	
	
	public void appendFlag(int chnIndexInBoard) {
		
		this.chnFlag  =  (short) (this.chnFlag  |  0x01 << chnIndexInBoard);
		
	}

	@Override
	public String toString() {
		return "BLedData [chnFlag=" + chnFlag + ", ledState=" + ledState + "]";
	}
	
	

}
