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
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 1.模片电源配置功能码0x01支持配置 支持查询
 * @version: v1.0.0
 * @author Admin
 * @date: 2021年11月12日 上午9:41:18
 *
 */
public class DiapPowerConfig extends Data implements Configable, Responsable, Queryable {

	private long da1;
	private long da2;
	private long delay;// 延迟时间
	private boolean OPEN;// 使能开关
	private long nextDelay;// 下一个延迟时间

	
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

		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) da1, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) da2, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) delay, 2, true)));
		data.add((byte) (OPEN ? 0x01 : 0x00));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) nextDelay, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		da1 = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		da2 = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		OPEN = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		nextDelay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		return DiapTestCode.DiapPowerConfig;
	}

	public long getDa1() {
		return da1;
	}

	public void setDa1(long da1) {
		this.da1 = da1;
	}

	public long getDa2() {
		return da2;
	}

	public void setDa2(long da2) {
		this.da2 = da2;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public boolean isOPEN() {
		return OPEN;
	}

	public void setOPEN(boolean oPEN) {
		OPEN = oPEN;
	}

	public long getNextDelay() {
		return nextDelay;
	}

	public void setNextDelay(long nextDelay) {
		this.nextDelay = nextDelay;
	}

	

}
