/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.ChargeMode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.MultiRange;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 6.万用表读通道选择配置功能码0x06支持配置 支持查询
 * @version: v1.0.0
 * @author Admin
 * @date: 2021年11月12日 上午9:47:20
 *
 */
public class MultiReadchannelSelect extends Data implements Configable, Queryable, Responsable {

	private MultiRange multiRange;// 万用表档位
	private long delay;// 指令延迟时间
	
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

		data.add((byte) multiRange.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) delay, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		int code_multiRange = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_multiRange > MultiRange.values().length - 1) {

			throw new RuntimeException("error multiRange mode code : " + code_multiRange);
		}
		multiRange = MultiRange.values()[code_multiRange];

		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		return DiapTestCode.MultiReadchannelSelect;
	}

	public MultiRange getMultiRange() {
		return multiRange;
	}

	public void setMultiRange(MultiRange multiRange) {
		this.multiRange = multiRange;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	

}
