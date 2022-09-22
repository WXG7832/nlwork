package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.AlertType;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.WorkEnv;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.WorkState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PPickupData extends Data implements Queryable, Responsable {

	public static class DataPack {

		public int stepIndex;
		public int loopIndex;
		public WorkState workState;
		public WorkMode workMode;
		public double voltage;
		public double current;
		public double capacity;// 新增容量值
		public long miliseconds;
		public AlertInfo alertInfo; // 报警信息

		@Override
		public String toString() {
			return "DataPack [stepIndex=" + stepIndex + ", loopIndex=" + loopIndex + ", workState=" + workState
					+ ", workMode=" + workMode + ", voltage=" + voltage + ", current=" + current + ", capacity="
					+ capacity + ", miliseconds=" + miliseconds + ", alertInfo=" + alertInfo + "]";
		}

	}

	public static class AlertInfo {

		public AlertType alertType = AlertType.NORMAL;
		public double alertVoltage;
		public double alertCurrent;
		public double alertCapacity;// 新增报警容量

		public final static int SIZE = 13;

		@Override
		public String toString() {
			return "AlertInfo [alertType=" + alertType + ", alertVoltage=" + alertVoltage + ", alertCurrent="
					+ alertCurrent + ", alertCapacity=" + alertCapacity + "]";
		}

	}

	// 运行环境
	private WorkEnv runEnv = WorkEnv.WORK; // 默认工作环境
	private List<DataPack> packs = new ArrayList<DataPack>();

	@Override
	public boolean supportChannel() {

		return true;
	}

	@Override
	public void encode() {

		// 运行环境
		data.add((byte) runEnv.ordinal());
		// 数据包个数
		data.addAll(Arrays.asList(ProtocolUtil.split(packs.size(), 2, true)));
		//
		for (int n = 0; n < packs.size(); n++) {

			DataPack dp = packs.get(n);
			// 步次序号
			data.addAll(Arrays.asList(ProtocolUtil.split(dp.stepIndex, 2, true)));
			// 循环序号
			data.addAll(Arrays.asList(ProtocolUtil.split(dp.loopIndex, 2, true)));
			// 通道状态
			data.add((byte) dp.workState.ordinal());
			// 工作模式
			data.add((byte) dp.workMode.ordinal());
			// 电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.voltage * 1000), 3, true)));
			// 电流
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.current * 1000), 3, true)));

			if (Data.isUsePickupCapacity()) {
				// 容量
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.capacity * Math.pow(10, 1)), 4, true)));
			}
			// 步次时间戳
			data.addAll(Arrays.asList(ProtocolUtil.split(dp.miliseconds, 6, true)));
			// 报警信息字节
			data.add((byte) (dp.alertInfo == null ? 0 : AlertInfo.SIZE));
			if (dp.alertInfo != null) {

				// 报警码
				data.add((byte) dp.alertInfo.alertType.ordinal());
				// 报警电压
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.alertInfo.alertVoltage * 1000), 3, true)));
				// 报警电流
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.alertInfo.alertCurrent * 1000), 3, true)));

				if (Data.isUsePickupCapacity()) {
					// 报警容量
					data.addAll(Arrays.asList(
							ProtocolUtil.split((long) (dp.alertInfo.alertCapacity * Math.pow(10, 1)), 4, true)));
				}

			}

		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		// 运行环境
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > WorkEnv.values().length - 1) {

			throw new RuntimeException("error run env code :" + code);
		}
		runEnv = WorkEnv.values()[code];
		// 数据包个数
		int count = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		for (int n = 0; n < count; n++) {

			DataPack dp = new DataPack();
			// 步次序号
			dp.stepIndex = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			// 循环序号
			dp.loopIndex = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			// 通道状态
			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > WorkState.values().length - 1) {

				throw new RuntimeException("error work state code :" + code);
			}
			dp.workState = WorkState.values()[code];
			// 工作模式
			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > WorkMode.values().length - 1) {

				throw new RuntimeException("error work mode code :" + code);
			}
			dp.workMode = WorkMode.values()[code];
			// 电压
			dp.voltage = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;
			// 电流
			dp.current = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;

			if (Data.isUsePickupCapacity()) {
				// 容量
				dp.capacity = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ 10;
				index += 4;

			}
			// 时间戳
			dp.miliseconds = ProtocolUtil.compose(data.subList(index, index + 6).toArray(new Byte[0]), true);
			index += 6;
			// 报警信息字节长度
			int alertInfoLen = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (alertInfoLen > 0) {

				dp.alertInfo = new AlertInfo();
				code = ProtocolUtil.getUnsignedByte(data.get(index++));
				if (code > AlertType.values().length - 1) {

					dp.alertInfo.alertType = AlertType.OTHER;

				} else {
					dp.alertInfo.alertType = AlertType.values()[code];
				}
				// 报警电压
				dp.alertInfo.alertVoltage = (double) ProtocolUtil
						.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
				index += 3;
				// 报警电流
				dp.alertInfo.alertCurrent = (double) ProtocolUtil
						.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
				index += 3;

				if (Data.isUsePickupCapacity()) {
					// 报警容量
					dp.alertInfo.alertCapacity = (double) ProtocolUtil
							.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 10;
					index += 4;
				}

			}

			packs.add(dp);
		}

	}

	@Override
	public Code getCode() {

		return ChipPickupCode.PickupCode;
	}

	public WorkEnv getRunEnv() {
		return runEnv;
	}

	public void setRunEnv(WorkEnv runEnv) {
		this.runEnv = runEnv;
	}

	public List<DataPack> getPacks() {
		return packs;
	}

	public void setPacks(List<DataPack> packs) {
		this.packs = packs;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "PickupData [runEnv=" + runEnv + ", packs=" + packs + "]";
	}

}
