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
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.PolarityMode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.PrecisionMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 工作模式配置0x02支持配置 支持查询
 * @version: v1.0.0
 * @author Admin
 * @date: 2021年11月12日 上午9:43:11
 *
 */
public class WorkPatternConfig extends Data implements Configable, Queryable, Responsable {

	private int workPattern;
	private long partDelay;// 部分延迟时间
	private long nextDelay;// 下一指令延迟时间

	// private ChargeMode chargeMode;// 模片充放电
	// private PolarityMode polarityMode;// 膜片极性
	// private PrecisionMode precisionMode;// 膜片精度

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

		data.add((byte) workPattern);
		// data.add((byte) chargeMode.ordinal());
		// data.add((byte) polarityMode.ordinal());
		// data.add((byte) precisionMode.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) partDelay, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) nextDelay, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		workPattern = ProtocolUtil.getUnsignedByte(data.get(index++));

		partDelay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		nextDelay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		// int code_precision = ProtocolUtil.getUnsignedByte(data.get(index++));
		// if (code_precision > PrecisionMode.values().length - 1) {
		// throw new RuntimeException("error precision mode code : " + code_precision);
		// }
		// precisionMode = PrecisionMode.values()[code_precision];
		// delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index +
		// 2).toArray(new Byte[0]), true);
		// index += 2;

	}

	@Override
	public Code getCode() {
		return DiapTestCode.WorkPatternConfig;
	}

	public int getWorkPattern() {
		return workPattern;
	}

	public void setWorkPattern(int workPattern) {
		this.workPattern = workPattern;
	}

	public long getPartDelay() {
		return partDelay;
	}

	public void setPartDelay(long partDelay) {
		this.partDelay = partDelay;
	}

	public long getNextDelay() {
		return nextDelay;
	}

	public void setNextDelay(long nextDelay) {
		this.nextDelay = nextDelay;
	}

}
