package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.Context;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class OfflinePickupData extends Data implements Alertable {

	private List<ChannelData> chnDataList = new ArrayList<ChannelData>();
	

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	public List<ChannelData> getChnDataList() {
		return chnDataList;
	}

	public void setChnDataList(List<ChannelData> chnDataList) {
		this.chnDataList = chnDataList;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex); // 查询，回复都要分区序号
		Calendar cal = Calendar.getInstance();

		// 离线数据包个数
		data.addAll(Arrays.asList(ProtocolUtil.split((long) chnDataList.size(), 2, true)));

		for (int n = 0; n < chnDataList.size(); n++) {

			ChannelData chnData = chnDataList.get(n);
			data.add((byte) chnData.getChannelIndex());
			data.add((byte) chnData.getState().ordinal());
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getVoltage() * Math.pow(10, Data.getVoltageResolution())), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getCurrent() * Math.pow(10, Data.getCurrentResolution())), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getCapacity() * Math.pow(10, Data.getCapacityResolution())), 4,
					true)));
			// 能量
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getEnergy() * Math.pow(10, Data.getEnergyResolution())), 4, true)));
			// 累计容量
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getAccumulateCapacity() * Math.pow(10, Data.getCapacityResolution())),
					4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getAccumulateEnergy() * Math.pow(10, Data.getEnergyResolution())), 4,
					true)));
			// 备份电池电压
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getDeviceVoltage() * Math.pow(10, Data.getVoltageResolution())), 2,
					true)));
			// 备份功率电压
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getPowerVoltage() * Math.pow(10, Data.getVoltageResolution())), 2,
					true)));
			// 流程流逝时间
			data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getTimeTotalSpend(), 4, true)));
			// 步次流逝时间
			data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getTimeStepSpend(), 3, true)));
			// 实时时间
			cal.setTime(chnData.getDate() == null ? new Date() : chnData.getDate());
			int year = cal.get(Calendar.YEAR) - 2000;
			int month = cal.get(Calendar.MONTH) + 1;
			int date = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);

			data.add((byte) year);
			data.add((byte) month);
			data.add((byte) date);
			data.add((byte) hour);
			data.add((byte) min);
			data.add((byte) sec);

			// 循环次号
			data.add((byte) chnData.getLoopIndex());
			// 步次序号
			data.add((byte) chnData.getStepIndex());
			// 工作模式
			data.add(chnData.getWorkMode() == null ? (byte) 0xff : (byte) chnData.getWorkMode().ordinal());
			// 报警代码
			data.add((byte) chnData.getAlertCode().ordinal());
			// 报警电压
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getAlertVoltage() * Math.pow(10,Data.getVoltageResolution())), 2,
					true)));
			// 报警电流
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (chnData.getAlertCurrent() * Math.pow(10, Data.getCurrentResolution())), 3,
					true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		// chnData = new ChannelData();

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 解析时间
		Calendar cal = Calendar.getInstance();

		int packCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		for (int n = 0; n < packCount; n++) {

			ChannelData chnData = new ChannelData();
			int chnIndexInLogic = ProtocolUtil.getUnsignedByte(data.get(index++));
			chnData.setChannelIndex(chnIndexInLogic);
			
			if (data.get(index) > ChnState.values().length - 1) {

				throw new RuntimeException("error chn state code :" + data.get(index));
			}
			chnData.setState(ChnState.values()[data.get(index++)]);
			// 通道电压
			long val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			chnData.setVoltage((double) val / Math.pow(10, Data.getVoltageResolution()));
			// 通道电流
			val = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
			index += 3;
			chnData.setCurrent((double) val / Math.pow(10, Data.getCurrentResolution()));
			// 通道容量
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			chnData.setCapacity((double) val / Math.pow(10, Data.getCapacityResolution()));
			// 通道能量
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			chnData.setEnergy((double) val / Math.pow(10, Data.getEnergyResolution()));
			// 累计通道容量
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			chnData.setAccumulateCapacity((double) val / Math.pow(10, Data.getCapacityResolution()));
			// 累计通道能量
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			chnData.setAccumulateEnergy((double) val / Math.pow(10, Data.getEnergyResolution()));
			// 备份电池电压
			val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			chnData.setDeviceVoltage((double) val / Math.pow(10, Data.getVoltageResolution()));
			// 备份功率电压
			val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			chnData.setPowerVoltage((double) val / Math.pow(10, Data.getVoltageResolution()));
			// 流程流逝时间
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			chnData.setTimeTotalSpend(val);
			// 步次流逝时间
			val = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
			index += 3;
			chnData.setTimeStepSpend((int) val);

			// 实时时间
			int year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
			int month = ProtocolUtil.getUnsignedByte(data.get(index++)) - 1;
			int day = ProtocolUtil.getUnsignedByte(data.get(index++));
			int hour = ProtocolUtil.getUnsignedByte(data.get(index++));
			int min = ProtocolUtil.getUnsignedByte(data.get(index++));
			int sec = ProtocolUtil.getUnsignedByte(data.get(index++));

			if (month > 11) {

				throw new RuntimeException("invalid month format:" + month);
			}
			if (day > 31) {

				throw new RuntimeException("invalid day format:" + day);
			}
			if (hour > 24) {

				throw new RuntimeException("invalid hour format:" + hour);
			}
			if (min > 59) {

				throw new RuntimeException("invalid minute format:" + min);
			}
			if (sec > 59) {

				throw new RuntimeException("invalid second format:" + sec);
			}

			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, sec);
			cal.set(Calendar.MILLISECOND, 0);

			chnData.setDate(cal.getTime());
			// 循环次号
			chnData.setLoopIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
			// 步次序号
			val = ProtocolUtil.getUnsignedByte(data.get(index++));
			chnData.setStepIndex((int) val); // 步次序号
			// 工作模式
			int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (mode < WorkMode.values().length) {
				chnData.setWorkMode(WorkMode.values()[mode]);
			} else if (chnData.getState() == ChnState.RUN) {

				throw new RuntimeException("error chn work mode :" + mode);

			}
			// 报警码
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code < AlertCode.values().length) {

				chnData.setAlertCode(AlertCode.values()[code]);
			} else {

				throw new RuntimeException("error alert code :" + code);
			}
			// 报警电压
			val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			chnData.setAlertVoltage((double) val / Math.pow(10, Data.getVoltageResolution()));
			// 报警电流
			val = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
			index += 3;
			chnData.setAlertCurrent((double) val / Math.pow(10, Data.getCurrentResolution()));
			//添加到列表
			chnDataList.add(chnData);
		}

	}

	@Override
	public Code getCode() {

		return MainCode.OfflineUploadCode;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

}
