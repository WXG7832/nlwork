/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 模片温度检测 0x0B
 * @author: Administrator
 * @date: 2022年1月24日 上午11:54:25
 *
 */
public class DiapTemperCheckData extends Data implements Configable, Queryable, Responsable {

	private int temper;// 温度

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
		data.add((byte) temper);
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		temper = ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		return DiapTestCode.DiapTemperCheck;
	}

	public int getTemper() {
		return temper;
	}

	public void setTemper(int temper) {
		this.temper = temper;
	}

}
