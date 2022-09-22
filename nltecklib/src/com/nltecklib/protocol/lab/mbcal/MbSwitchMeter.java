package com.nltecklib.protocol.lab.mbcal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年5月7日 下午2:12:38
* 校准板切表命令
*/
public class MbSwitchMeter extends Data implements Configable, Queryable, Responsable {
    

	private boolean connect;  //连接
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
    
	/**
	 * 
	 */
	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) (connect ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		connect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01 ;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MbCalCode.SWITCH_METER;
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}
	
	

}
