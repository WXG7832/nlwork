package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalMode;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * 主控校准模式基准配置
 * @author Administrator
 *
 */
public class CalChnVoltageCurrentData extends Data implements Configable,  Responsable {

	private byte moduleIndex;
	private CalMode workMode = CalMode.SLEEP;
	private int precisionLevel;

	private long programV;
	private long programI;

	public CalMode getWorkMode() {
		return workMode;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setWorkMode(CalMode workMode) {
		this.workMode = workMode;
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

	public byte getModuleIndex()
	{
		return moduleIndex;
	}

	public void setModuleIndex(byte moduleIndex)
	{
		this.moduleIndex = moduleIndex;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		if (Data.isUseModuleCal()) {
			data.add((byte) moduleIndex);
		}
		// 工作方式
		data.add((byte) workMode.ordinal());

		// 精度
		data.add((byte) precisionLevel);

		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(programV, 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split(programI, 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		if (Data.isUseModuleCal()) {
			moduleIndex = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		}
		
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (flag >= CalMode.values().length) {

			throw new RuntimeException("the workMode value is error:" + flag);
		}
		workMode = CalMode.values()[flag];
		// 双精度

		precisionLevel = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 程控电压
		programV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;
	}

	@Override
	public Code getCode() {
		return MainCode.CalChnVoltageCurrentCode;
	}

	@Override
	public String toString() {
		return "CalChnVoltageCurrentData [workMode=" + workMode + ", programV=" + programV + ", programI=" + programI
				+ ", chn=" + chnIndex + ", moduleIndex=" + moduleIndex + ", precisionLevel=" + precisionLevel + "]";
	}

	public int getPrecisionLevel() {
		return precisionLevel;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.precisionLevel = precisionLevel;
	}

}
