/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.ArrayList;
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
 * @Description: 信息采集功能码0x07 支持查询配置
 * @version: v1.0.0
 * @author: Admin
 * @date: 2021年11月15日 上午11:33:54
 *
 */
public class InformationCollectionData extends Data implements Configable, Queryable, Responsable {

	private int collectNumber;// 采集数量

	private List<DiapPickupData> pickupDatas = new ArrayList<>();

	public static class DiapPickupData {

		private double mainVolAD;// 主电压AD原始值
		private double mainCurrAD;// 主电流AD原始值
		private double backupVolAD;// 备份电压AD原始值
		private double backupCurrAD;// 备份电流AD原始值

		public DiapPickupData() {
		}

		public double getMainVolAD() {
			return mainVolAD;
		}

		public void setMainVolAD(double mainVolAD) {
			this.mainVolAD = mainVolAD;
		}

		public double getMainCurrAD() {
			return mainCurrAD;
		}

		public void setMainCurrAD(double mainCurrAD) {
			this.mainCurrAD = mainCurrAD;
		}

		public double getBackupVolAD() {
			return backupVolAD;
		}

		public void setBackupVolAD(double backupVolAD) {
			this.backupVolAD = backupVolAD;
		}

		public double getBackupCurrAD() {
			return backupCurrAD;
		}

		public void setBackupCurrAD(double backupCurrAD) {
			this.backupCurrAD = backupCurrAD;
		}

		public DiapPickupData(double mainVolAD, double mainCurrAD, double backupVolAD, double backupCurrAD) {
			this.mainVolAD = mainVolAD;
			this.mainCurrAD = mainCurrAD;
			this.backupVolAD = backupVolAD;
			this.backupCurrAD = backupCurrAD;
		}

	}

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

		data.add((byte) collectNumber);

		for (DiapPickupData pickupData : pickupDatas) {

			data.addAll(Arrays
					.asList(ProtocolUtil.split((long) (pickupData.mainVolAD * Math.pow(10, 5)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.split((long) (pickupData.mainCurrAD * Math.pow(10, 5)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.split((long) (pickupData.backupVolAD * Math.pow(10, 1)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.split((long) (pickupData.backupCurrAD * Math.pow(10, 1)), 2, true)));

		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		collectNumber = ProtocolUtil.getUnsignedByte(data.get(index++));

		pickupDatas.clear();
		for (int i = 0; i < collectNumber; i++) {

			DiapPickupData pickupData = new DiapPickupData();

			//数据以V/A回来,但数据显示以mA/mV显示 所以缩小10的3次幂(10^5与10^1)
			pickupData.setMainVolAD(
					ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
							/ Math.pow(10, 5));
			index += 4;

			pickupData.setMainCurrAD(
					ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
							/ Math.pow(10, 5));
			index += 4;

			pickupData.setBackupVolAD(
					ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
							/ Math.pow(10, 1));
			index += 2;

			pickupData.setBackupCurrAD(
					ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
							/ Math.pow(10, 1));
			index += 2;

			pickupDatas.add(pickupData);

		}

	}

	@Override
	public Code getCode() {
		return DiapTestCode.InformationCollection;
	}

	public int getCollectNumber() {
		return collectNumber;
	}

	public void setCollectNumber(int collectNumber) {
		this.collectNumber = collectNumber;
	}

	public List<DiapPickupData> getPickupDatas() {
		return pickupDatas;
	}

	public void setPickupDatas(List<DiapPickupData> pickupDatas) {
		this.pickupDatas = pickupDatas;
	}

}
