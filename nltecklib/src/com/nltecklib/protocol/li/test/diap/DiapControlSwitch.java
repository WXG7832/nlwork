/**
 * 
 */
package com.nltecklib.protocol.li.test.diap;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 模片使能开关配置功能码0x05支持配置 支持查询
* @version: v1.0.0
* @author Admin
* @date: 2021年11月12日 上午9:45:08 
*
*/
public class DiapControlSwitch extends Data implements Configable, Queryable, Responsable {

	private boolean OPEN;// 膜片使能开关
	private long delay;// 指令延迟时间
	
	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) (OPEN ? 0x01 : 0x00));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) delay, 2, true)));
		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		OPEN = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		return DiapTestCode.DiapControlSwitch;
	}

	public boolean isOPEN() {
		return OPEN;
	}

	public void setOPEN(boolean oPEN) {
		OPEN = oPEN;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	
}
