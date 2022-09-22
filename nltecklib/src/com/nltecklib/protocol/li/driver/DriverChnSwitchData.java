package com.nltecklib.protocol.li.driver;

import java.util.Arrays;
import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 通道开关状态
 * @author admin
 */
public class DriverChnSwitchData extends Data implements Configable, Queryable, Responsable{

	private  boolean OPEN; //1：打开  0：关闭
	
	private short chnFlag = 0; //通道选择位
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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
		
		data.add((byte) driverIndex);
		
		data.add((byte) (OPEN ? 0x01 : 0x00));
       
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
		
		driverIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));

		OPEN = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		
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
		return DriverCode.ChnSwitchCode;
	}


	public short getChnFlag() {
		return chnFlag;
	}

	public void setChnFlag(short chnFlag) {
		this.chnFlag = chnFlag;
	}


	public boolean isOPEN() {
		return OPEN;
	}

	public void setOPEN(boolean OPEN) {
		this.OPEN = OPEN;
	}

}
