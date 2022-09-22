/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.Alarm;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.check.CheckEnvironment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 回检电压采集 功能码0x04 (支持查询)
 * @version: v1.0.0
 * @author: Admin
 * @date: 2021年12月29日 上午10:11:38
 *
 */
public class VolPickData extends Data implements Queryable, Responsable {

	private int pickCount;
	private SwitchState switchState;

	private List<PickData> pickDatas = new ArrayList<>();

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) pickCount);
		data.add((byte) switchState.ordinal());

		for (int n = 0; n < pickCount; n++) {
			data.addAll(Arrays
					.asList(ProtocolUtil.split((long) (pickDatas.get(n).getChnnVol() * Math.pow(10, 1)), 2, true)));
			data.add((byte) pickDatas.get(n).getAlarm().ordinal());
			data.add((byte) pickDatas.get(n).getAlarmTime());
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		pickCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > SwitchState.values().length - 1) {

			throw new RuntimeException("error SwitchState code : " + code);
		}
		switchState = SwitchState.values()[code];

		pickDatas.clear();

		for (int n = 0; n < pickCount; n++) {

			PickData pickData = new PickData();

			pickData.setChnnVol(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;

			int alarmIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (alarmIndex > Alarm.values().length - 1) {

				throw new RuntimeException("error Alarm mode index :" + alarmIndex);
			}
			pickData.setAlarm(Alarm.values()[alarmIndex]);

			if(alarmIndex == 1) {
				pickData.setAlarmTime(ProtocolUtil.getUnsignedByte(data.get(index++)));
			}

			pickDatas.add(pickData);
		}
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.VolPickCode;
	}

	public int getPickCount() {
		return pickCount;
	}

	public void setPickCount(int pickCount) {
		this.pickCount = pickCount;
	}

	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

	public List<PickData> getPickDatas() {
		return pickDatas;
	}

	public void setPickDatas(List<PickData> pickDatas) {
		this.pickDatas = pickDatas;
	}

	public static class PickData {

		private double chnnVol;
		private Alarm alarm;
		private int alarmTime;

		public double getChnnVol() {
			return chnnVol;
		}

		public void setChnnVol(double chnnVol) {
			this.chnnVol = chnnVol;
		}

		public Alarm getAlarm() {
			return alarm;
		}

		public void setAlarm(Alarm alarm) {
			this.alarm = alarm;
		}

		public int getAlarmTime() {
			return alarmTime;
		}

		public void setAlarmTime(int alarmTime) {
			this.alarmTime = alarmTime;
		}

		public PickData() {

		}

		public PickData(double chnnVol, Alarm alarm, int alarmTime) {
			this.chnnVol = chnnVol;
			this.alarm = alarm;
			this.alarmTime = alarmTime;
		}

	}
}
