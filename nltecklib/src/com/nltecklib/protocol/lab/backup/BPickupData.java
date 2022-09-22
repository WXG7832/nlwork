package com.nltecklib.protocol.lab.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.AlertCode;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.ChnState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BPickupData extends Data implements Queryable, Responsable {

	public static class DataPack {

		public int chnIndex;
		public double temperature;
		public ChnState chnState = ChnState.NO_BATTERY;
		public double backupVoltage;
		public double powerVoltage;

		public AlertInfo alertInfo; // 报警信息

		@Override
		public String toString() {
			return "DataPack [chnIndex=" + chnIndex + ", temperature=" + temperature + ", chnState=" + chnState
					+ ", backupVoltage=" + backupVoltage + ", powerVoltage=" + powerVoltage + ", alertInfo=" + alertInfo
					+ "]";
		}

	}

	public static class AlertInfo {

		public AlertCode alertType = AlertCode.NORMAL;
		public double backupVoltage;
		public double powerVoltage;

		public final static int SIZE = 7;
	}

	private List<DataPack> packs = new ArrayList<DataPack>();

	@Override
	public boolean supportChannel() {

		return false;
	}

	@Override
	public void encode() {

		// 数据包个数
		data.add((byte)packs.size());
		//
		for (int n = 0; n < packs.size(); n++) {

			DataPack dp = packs.get(n);
			// 步次序号
			data.add((byte) dp.chnIndex);
			// 温度
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dp.temperature * 10), 2, true)));
			// 通道状态
			data.add((byte) dp.chnState.ordinal());
			// 备份电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.backupVoltage * 1000), 3, true)));
			// 功率电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.powerVoltage * 1000), 3, true)));
			// 报警信息字节
			data.add((byte) (dp.alertInfo == null ? 0 : AlertInfo.SIZE));
			if (dp.alertInfo != null) {

				// 报警码
				data.add((byte) dp.alertInfo.alertType.ordinal());
				// 报警电压
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.alertInfo.backupVoltage * 1000), 3, true)));
				// 报警电流
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dp.alertInfo.powerVoltage * 1000), 3, true)));

			}

		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0 , code = 0;
		// 数据包个数
		int count =data.get(index++);

		for (int n = 0; n < count; n++) {

			DataPack dp = new DataPack();
			// 通道号
			dp.chnIndex =data.get(index++);
			// 通道温度
			dp.temperature = (double)  ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
			index += 2;
			// 通道状态
			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > ChnState.values().length - 1) {

				throw new RuntimeException("error work state code :" + code);
			}
			dp.chnState = ChnState.values()[code];
			// 备份电压
			dp.backupVoltage = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;
			// 功率电压
			dp.powerVoltage = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;
			// 报警信息字节长度
			int alertInfoLen = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (alertInfoLen > 0) {

				dp.alertInfo = new AlertInfo();
				code = ProtocolUtil.getUnsignedByte(data.get(index++));
				if (code > AlertCode.values().length - 1) {

					throw new RuntimeException("error alert type code :" + code);
				}
				dp.alertInfo.alertType = AlertCode.values()[code];
				// 报警电压
				dp.alertInfo.backupVoltage = (double) ProtocolUtil
						.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
				index += 3;
				// 报警电流
				dp.alertInfo.powerVoltage = (double) ProtocolUtil
						.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
				index += 3;

			}

			packs.add(dp);
		}

	}


	@Override
	public Code getCode() {

		return BackupCode.PickupCode;
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
		return "PickupData [packs=" + packs + "]";
	}
	
	

}
