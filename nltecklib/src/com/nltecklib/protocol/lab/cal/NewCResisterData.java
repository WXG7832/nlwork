/**
 * 
 */
package com.nltecklib.protocol.lab.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 新电阻系数 0x0D
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年8月9日 上午11:52:07
 *
 */
public class NewCResisterData extends Data implements Configable, Queryable, Responsable {

	private CalMode workMode = CalMode.SLEEP;// 工作模式
	private int precisionLevel;// 精度档位
	private double resisterFactor;// 电阻系数

	@Override
	public boolean supportMain() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) workMode.ordinal());
		data.add((byte) (precisionLevel));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resisterFactor * Math.pow(10, 6)), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		// 工作模式
		int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (mode > CalMode.values().length - 1) {

			throw new RuntimeException("error work mode index : " + mode);
		}
		workMode = CalMode.values()[mode];

		precisionLevel = ProtocolUtil.getUnsignedByte(data.get(index++));

		resisterFactor = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 6);
		index += 4;

	}

	@Override
	public Code getCode() {
		return CalCode.RESISTER2;
	}

	public CalMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalMode workMode) {
		this.workMode = workMode;
	}

	public int getPrecisionLevel() {
		return precisionLevel;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.precisionLevel = precisionLevel;
	}

	public double getResisterFactor() {
		return resisterFactor;
	}

	public void setResisterFactor(double resisterFactor) {
		this.resisterFactor = resisterFactor;
	}

	@Override
	public String toString() {
		return "NewCResisterData [workMode=" + workMode + ", precisionLevel=" + precisionLevel + ", resisterFactor="
				+ resisterFactor + "]";
	}

}
