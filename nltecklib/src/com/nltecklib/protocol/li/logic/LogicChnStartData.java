package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 单通道启动指令
 * 用于单板单个或多个通道的测试启动恢复命令
 * @author Administrator
 *
 */
public class LogicChnStartData extends Data implements Configable, Responsable {

	private short chnFlag = 0; // 启动通道标志
	private WorkMode workMode = WorkMode.UDT;
	private double programVoltage;
	private double programCurrent;
	private double endThreshold; // 结束电压电流值
	

	public LogicChnStartData() {

		
	}

	@Override
	public boolean supportUnit() {

		return true;
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

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
       
        if(Data.getDriverChnCount() > 16) {
			
			throw new RuntimeException("not surpport driver channel count : " + Data.getDriverChnCount());
		}
		if (isReverseDriverChnIndex()) {

			if (Data.getDriverChnCount() <= 8) {

				chnFlag = ProtocolUtil.reverseByteBit((byte) chnFlag, Data.getDriverChnCount());
			} else if (Data.getDriverChnCount() <= 16) {
				chnFlag = ProtocolUtil.reverseShortBit(chnFlag, Data.getDriverChnCount());
			}
		}

		// 启动通道
		data.addAll(Arrays.asList(ProtocolUtil.split((long) chnFlag, 2, true)));
		// 工作模式
		data.add((byte) workMode.ordinal());
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programVoltage * 10), 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programCurrent * 10), 3, true)));
		// 结束条件
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (endThreshold * 10), 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
        if(Data.getDriverChnCount() > 16) {
			
			throw new RuntimeException("not surpport driver channel count : " + Data.getDriverChnCount());
		}
		// 启动通道
		short flag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		if (isReverseDriverChnIndex()) {
            
			if (Data.getDriverChnCount() <= 8) {
                
				chnFlag = ProtocolUtil.reverseByteBit((byte) flag, Data.getDriverChnCount());
			} else if (Data.getDriverChnCount() <= 16) {
				chnFlag = ProtocolUtil.reverseShortBit(flag, Data.getDriverChnCount());
			} 
		}
		int val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		if (val > WorkMode.values().length - 1) {

			throw new RuntimeException("error workmode index:" + val + "in procedure step configuration");
		}
		workMode = WorkMode.values()[val];
		// 程控电压
		programVoltage = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		// 程控电流
		programCurrent = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
		index += 3;
		// 结束条件
		endThreshold = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
		index += 3;
	}

	@Override
	public Code getCode() {
		return LogicEnvironment.LogicCode.ChnStartCode;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public double getProgramVoltage() {
		return programVoltage;
	}

	public void setProgramVoltage(double programVoltage) {
		this.programVoltage = programVoltage;
	}

	public double getProgramCurrent() {
		return programCurrent;
	}

	public void setProgramCurrent(double programCurrent) {
		this.programCurrent = programCurrent;
	}

	public double getEndThreshold() {
		return endThreshold;
	}

	public void setEndThreshold(double endThreshold) {
		this.endThreshold = endThreshold;
	}

	public void setChnFlag(short chnFlag) {
		this.chnFlag = chnFlag;
	}

	public short getChnFlag() {
		return chnFlag;
	}

}
