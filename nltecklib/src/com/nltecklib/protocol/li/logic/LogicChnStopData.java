package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 用于单板单个或多个通道的测试停止命令；用于设备通道暂停
 * @author Administrator
 *
 */
public class LogicChnStopData extends Data implements Configable,Responsable{
    
	private short  chnFlag = 0; //通道序号选中情况，1表示选中，0表示未选中
	
	
	public LogicChnStopData() {
		
	}
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte)unitIndex); 
		//板号
		data.add((byte)driverIndex);
		
		short flag = chnFlag;
		if(Data.getDriverChnCount() > 16) {
			
			throw new RuntimeException("not surpport driver channel count : " + Data.getDriverChnCount());
		}
		if(isReverseDriverChnIndex()) {
			
			if(Data.getDriverChnCount() <=  8 ) {
			   flag = ProtocolUtil.reverseByteBit((byte)flag,Data.getDriverChnCount());
			}else {
				
			   flag = ProtocolUtil.reverseShortBit(flag, Data.getDriverChnCount());
			}
		}
		
		
		//通道选中情况
		data.addAll(Arrays.asList(ProtocolUtil.split((long)flag, 2, true)));
		
		
		
		
	}
	

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
	    unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
	    driverIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
	    short flag =  (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	    index += 2;
        if(isReverseDriverChnIndex()) {
			
        	if(Data.getDriverChnCount() <=  8 ) {
 			   chnFlag = ProtocolUtil.reverseByteBit((byte)flag,Data.getDriverChnCount());
 			}else {
 				
 			   chnFlag = ProtocolUtil.reverseShortBit(flag, Data.getDriverChnCount());
 			}
		}else {
			
			chnFlag = flag;
		}
	   
	}

	@Override
	public Code getCode() {
		
		return LogicCode.ChnStopCode;
	}

	public short getSelectChns() {
		return chnFlag;
	}

	public void setSelectChns(short selectChns) {
		this.chnFlag = selectChns;
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
	
	

}
