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
 * @Description: 新校准模式 0x0E
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年8月9日 下午5:17:47
 *
 */
public class CCalibrateExData extends Data implements Configable, Queryable, Responsable {

	private boolean enable;
	private CalMode workMode = CalMode.SLEEP;
	private int precisionLevel;
	private boolean positive;
	private long programV;// 程控电压
	private long programI;// 程控电流

	@Override
	public boolean supportMain() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) (enable ? 0x01 : 0x00));
		data.add((byte) workMode.ordinal());
		data.add((byte) (precisionLevel));
		data.add((byte) (positive ? 0x01 : 0x00));
		data.addAll(Arrays.asList(ProtocolUtil.split(programV, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(programI, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		enable = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

		// 工作模式
		int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (mode > CalMode.values().length - 1) {

			throw new RuntimeException("error work mode index : " + mode);
		}
		workMode = CalMode.values()[mode];

		precisionLevel = ProtocolUtil.getUnsignedByte(data.get(index++));
		positive = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		programV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		return CalCode.CAL3;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
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

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public long getProgramV() {
		return programV;
	}

	public void setProgramV(long programV) {
		this.programV = programV;
	}

	public long getProgramI() {
		return programI;
	}

	public void setProgramI(long programI) {
		this.programI = programI;
	}

	@Override
	public String toString() {
		return "CCalibrateExData [enable=" + enable + ", workMode=" + workMode + ", precisionLevel=" + precisionLevel
				+ ", positive=" + positive + ", programV=" + programV + ", programI=" + programI + "]";
	}

}
