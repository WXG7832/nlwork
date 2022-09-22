/**
 * 
 */
package com.nltecklib.protocol.li.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 电子负载超温报警(0x12) 可读
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年6月30日 上午9:26:47
 *
 */
public class ELoadOverTempAlarmData extends Data implements Queryable, Responsable {

	/** 电子负载温度 */
	private double eloadTemper;

	/** 温控状态 */
	private boolean temperOk;

	/** 运行状态 */
	private boolean runOk;

	/** 风机状态 */
	private boolean fanOk;

	@Override
	public boolean supportUnit() {
		return false;
	}

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

		data.add((byte) driverIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (eloadTemper * 10), 2, true)));
		data.add((byte) (temperOk ? 0 : 1));
		data.add((byte) (runOk ? 0 : 1));
		data.add((byte) (fanOk ? 0 : 1));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		eloadTemper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		temperOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		runOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		fanOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;

	}

	@Override
	public Code getCode() {
		return CalEnvironment.CalCode.ELoadOverTempAlarmCode;
	}

	public double getEloadTemper() {
		return eloadTemper;
	}

	public void setEloadTemper(double eloadTemper) {
		this.eloadTemper = eloadTemper;
	}

	public boolean isTemperOk() {
		return temperOk;
	}

	public void setTemperOk(boolean temperOk) {
		this.temperOk = temperOk;
	}

	public boolean isRunOk() {
		return runOk;
	}

	public void setRunOk(boolean runOk) {
		this.runOk = runOk;
	}

	public boolean isFanOk() {
		return fanOk;
	}

	public void setFanOk(boolean fanOk) {
		this.fanOk = fanOk;
	}

	@Override
	public String toString() {
		return "ELoadOverTempAlarmData [eloadTemper=" + eloadTemper + ", temperOk=" + temperOk + ", runOk=" + runOk
				+ ", fanOk=" + fanOk + "]";
	}

}
