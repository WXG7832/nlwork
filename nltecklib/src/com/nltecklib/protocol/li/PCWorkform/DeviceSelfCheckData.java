package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CheckResult;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Fan;
import com.nltecklib.protocol.power.driver.DriverEnvironment.FanType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年3月7日 上午9:41:11 三代设备自检信息查询
 */
public class DeviceSelfCheckData extends Data implements Queryable, Responsable {

	/**
	 * 单驱动板自检信息
	 * 
	 * @author wavy_zheng 2022年3月7日
	 *
	 */
	public static class DriverCheckInfoData {

		public int driverIndex;

		public CheckResult driverSram;
		public CheckResult driverFlash;
		public CheckResult calParam;
		public CheckResult adPick;
		public CheckResult tempPick;
		public CheckResult checkboard;
		public boolean powerOk;
		public List<Fan> fans = new ArrayList<>();
	}

	private List<DriverCheckInfoData> driverCheckInfoDataList = new ArrayList<>();

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public void encode() {

		// 驱动板数量
		data.add((byte) driverCheckInfoDataList.size());

		for (int n = 0; n < driverCheckInfoDataList.size(); n++) {
			
			DriverCheckInfoData dcid = driverCheckInfoDataList.get(n);
			
			data.add((byte) dcid.driverSram.ordinal());
			data.add((byte) dcid.driverFlash.ordinal());
			data.add((byte) dcid.calParam.ordinal());
			data.add((byte) dcid.adPick.ordinal());
			data.add((byte) dcid.tempPick.ordinal());
			data.add((byte) dcid.checkboard.ordinal());
			data.add((byte) (dcid.powerOk ? 0x00 : 0x01));
			data.add((byte) dcid.fans.size());
			for (int i = 0; i < dcid.fans.size(); i++) {

				data.add((byte) dcid.fans.get(n).fanType.ordinal());
				data.add((byte) (dcid.fans.get(n).stateOk ? 0x00 : 0x01));
			}

		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
       
		data = encodeData;
		int index = 0;
		
		int driverCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < driverCount ; n++) {
			
			DriverCheckInfoData dcid = new DriverCheckInfoData();
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CheckResult.values().length - 1) {

				throw new RuntimeException("error sram code : " + code);
			}
			dcid.driverSram = CheckResult.values()[code];
			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CheckResult.values().length - 1) {

				throw new RuntimeException("error flash code : " + code);
			}
			dcid.driverFlash = CheckResult.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CheckResult.values().length - 1) {

				throw new RuntimeException("error cal param code : " + code);
			}
			dcid.calParam = CheckResult.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CheckResult.values().length - 1) {

				throw new RuntimeException("error ad pick code : " + code);
			}
			dcid.adPick = CheckResult.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CheckResult.values().length - 1) {

				throw new RuntimeException("error temp pick code : " + code);
			}
			dcid.tempPick = CheckResult.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CheckResult.values().length - 1) {

				throw new RuntimeException("error checkboard code : " + code);
			}
			dcid.checkboard = CheckResult.values()[code];

			dcid.powerOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;

			int fanCount = ProtocolUtil.getUnsignedByte(data.get(index++));

			for (int i = 0; i < fanCount; i++) {

				Fan fan = new Fan();
				code = ProtocolUtil.getUnsignedByte(data.get(index++));
				if (code > FanType.values().length - 1) {

					throw new RuntimeException("error fan type code : " + code);
				}
				fan.fanType = FanType.values()[code];

				fan.stateOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
				
				dcid.fans.add(fan);
			}
			
			
			driverCheckInfoDataList.add(dcid);
		}
		
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.DeviceSelfCheckCode;
	}

	public List<DriverCheckInfoData> getDriverCheckInfoDataList() {
		return driverCheckInfoDataList;
	}

	public void setDriverCheckInfoDataList(List<DriverCheckInfoData> driverCheckInfoDataList) {
		this.driverCheckInfoDataList = driverCheckInfoDataList;
	}

}
