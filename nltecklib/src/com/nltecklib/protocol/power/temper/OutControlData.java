/**
 * 
 */
package com.nltecklib.protocol.power.temper;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 温度板输出控制 功能码0x02 支持查询,支持配置
* @version: v1.0.0
* @date: 2021年12月29日 下午7:56:29 
*
*/
public class OutControlData extends Data implements Configable, Queryable, Responsable {

	private int outCtrChnn;
	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) outCtrChnn);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		outCtrChnn = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		return TemperCode.OutControlCode;
	}

	public int getOutCtrChnn() {
		return outCtrChnn;
	}

	public void setOutCtrChnn(int outCtrChnn) {
		this.outCtrChnn = outCtrChnn;
	}

}
