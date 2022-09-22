package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.AlertCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.ChnState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.PickupState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 对分区单板数据采样通信协议
 * 
 * @author Administrator
 *
 */
public class LogicPickupData extends Data implements Queryable, Responsable {

	private int temperature1;
	private int temperature2;
	private PickupState logicPickupState; // 逻辑板采集状态
	private PickupState driverPickupState; // 驱动板采集状态
	private WorkMode workMode; // 工作模式
	private List<ChnData> chnDatas = new LinkedList<ChnData>(); // 通道集合

	public LogicPickupData() {

	}

	public void setChnDatas(List<ChnData> chnDatas) {
		this.chnDatas = chnDatas;
	}

	public int getTemperature1() {
		return temperature1;
	}

	public void setTemperature1(int temperature) {
		this.temperature1 = temperature;
	}

	public int getTemperature2() {
		return temperature2;
	}

	public void setTemperature2(int temperature2) {
		this.temperature2 = temperature2;
	}

	public List<ChnData> getChnDatas() {
		return chnDatas;
	}

	/**
	 * 单通道采样数据
	 * 
	 * @author Administrator
	 *
	 */
	public class ChnData {

		private LogicPickupData parentData;

		private double voltage;
		private double current;
		private double powerVoltage;
		private AlertCode alertCode;
		private ChnState state = ChnState.UDT;
		private WorkMode workMode;

		private Date date;

		private double alertVolt;
		private double alertCurrent;

		public LogicPickupData getParentData() {
			return parentData;
		}

		public void setParentData(LogicPickupData parentData) {
			this.parentData = parentData;
		}

		public double getVoltage() {
			return voltage;
		}

		public void setVoltage(double voltage) {
			this.voltage = voltage;
		}

		public double getCurrent() {
			return current;
		}

		public void setCurrent(double current) {
			this.current = current;
		}

		public AlertCode getAlertCode() {
			return alertCode;
		}

		public void setAlertCode(AlertCode alertCode) {
			this.alertCode = alertCode;
		}

		public ChnState getState() {
			return state;
		}

		public void setState(ChnState state) {
			this.state = state;
		}

		public double getAlertVolt() {
			return alertVolt;
		}

		public void setAlertVolt(double alertVolt) {
			this.alertVolt = alertVolt;
		}

		public double getAlertCurrent() {
			return alertCurrent;
		}

		public void setAlertCurrent(double alertCurrent) {
			this.alertCurrent = alertCurrent;
		}

		public int getTemperature() {
			return temperature1;
		}

		public void setTemperature(int temp) {
			temperature1 = temp;
		}

		public PickupState getPickupState() {
			return logicPickupState;
		}

		public void setPickupState(PickupState ps) {
			logicPickupState = ps;
		}

		public WorkMode getWorkMode() {
			return workMode;
		}

		public void setWorkMode(WorkMode wm) {
			workMode = wm;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public double getPowerVoltage() {
			return powerVoltage;
		}

		public void setPowerVoltage(double powerVoltage) {
			this.powerVoltage = powerVoltage;
		}

		@Override
		public String toString() {
			return "ChnData [parentData=" + parentData + ", voltage=" + voltage + ", current=" + current
					+ ", powerVoltage=" + powerVoltage + ", alertCode=" + alertCode + ", state=" + state + ", workMode="
					+ workMode + ", date=" + date + ", alertVolt=" + alertVolt + ", alertCurrent=" + alertCurrent + "]";
		}

	}

	@Override
	public boolean supportUnit() {

		return true;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex); // 查询，回复都要分区序号
		data.add((byte) driverIndex); // 板号
		data.add((byte) temperature1); // 温度1
		data.add((byte) temperature2); // 温度2
		data.add((byte) logicPickupState.ordinal()); // 逻辑板状态
		data.add((byte) driverPickupState.ordinal()); // 驱动板状态
		data.add((byte) ((byte) chnDatas.size() - 1));

		for (int n = 0; n < chnDatas.size(); n++) {

			int chnIndex = n;
			if (isReverseDriverChnIndex()) {

				chnIndex = chnDatas.size() - 1 - n;
			}

			ChnData chnData = chnDatas.get(chnIndex);
			if (chnData == null) {

				throw new RuntimeException("can not pickup channel data");
			}
			// 通道状态
			data.add((byte) chnData.getState().getCode());
			data.add((byte) chnData.getWorkMode().ordinal()); // 工作模式
			// 电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getVoltage() * 10), 2, true)));
			// 电流
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getCurrent() * Math.pow(10, Data.getCurrentResolution())), 3, true)));

			// 功率电压
			if (Data.isUseLogicPowerVoltage()) {

				int vxp = Data.getVoltageResolution();
				data.addAll(Arrays
						.asList(ProtocolUtil.split((long) (chnData.getPowerVoltage() * Math.pow(10, vxp)), 2, true)));
			}
			// 报警码
			data.add((byte) chnData.getAlertCode().ordinal());
			// 报警电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getAlertVolt() * 10), 2, true)));
			// 报警电流
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getAlertCurrent() * Math.pow(10, Data.getCurrentResolution())), 3, true)));

		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 板载温度1
		temperature1 = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 板载温度2
		temperature2 = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 分区状态
		int stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (stateIndex > PickupState.values().length - 1) {

			throw new RuntimeException("error logic pickup state index :" + stateIndex);
		}
		logicPickupState = PickupState.values()[stateIndex];

		stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (stateIndex > PickupState.values().length - 1) {

			throw new RuntimeException("error driver pickup state index :" + stateIndex);
		}
		driverPickupState = PickupState.values()[stateIndex];

		int chnCount = ProtocolUtil.getUnsignedByte(data.get(index++)) + 1; // 通道数0表示1

		chnDatas.clear();

		for (int n = 0; n < chnCount; n++) {

			ChnData chnData = new ChnData();
			chnData.setParentData(this);
			stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (ChnState.valueOf(stateIndex) == null) {

				throw new RuntimeException("error channel(" + (n + 1) + ") state index :" + stateIndex);
			}
			chnData.setState(ChnState.valueOf(stateIndex));
			// 通道工作模式
			int modeIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (modeIndex > WorkMode.values().length - 1) {

				throw new RuntimeException("error work mode index :" + modeIndex);
			}
			chnData.setWorkMode(WorkMode.values()[modeIndex]);

			// 通道电压
			chnData.setVoltage(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
			// 通道电流

			chnData.setCurrent((double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, Data.getCurrentResolution()));
			index += 3;

			// 功率电压
			if (Data.isUseLogicPowerVoltage()) {
				int vxp = Data.getVoltageResolution();
				chnData.setPowerVoltage(
						(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
								/ Math.pow(10, vxp));
				index += 2;
			}
			// 报警码
			int alertIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (alertIndex > AlertCode.values().length - 1) {

				throw new RuntimeException("error channel(" + (n + 1) + ") alert code :" + alertIndex);
			}
			chnData.setAlertCode(AlertCode.values()[alertIndex]);
			// 报警电压
			chnData.setAlertVolt(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
			// 报警电流
			chnData.setAlertCurrent(
					(double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
							/ Math.pow(10, Data.getCurrentResolution()));
			index += 3;

			if (isReverseDriverChnIndex()) {

				// 反向通道序号
				chnDatas.add(0, chnData);
			} else {
				chnDatas.add(chnData);
			}

		}

	}

	@Override
	public Code getCode() {

		return LogicCode.PickupCode;
	}

	@Override
	public String toString() {
		return "LogicPickupData [temperature=" + temperature1 + ", pickupState=" + logicPickupState + ", workMode="
				+ workMode + ", chnDatas=" + chnDatas + "]";
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	public PickupState getLogicPickupState() {
		return logicPickupState;
	}

	public void setLogicPickupState(PickupState logicPickupState) {
		this.logicPickupState = logicPickupState;
	}

	public PickupState getDriverPickupState() {
		return driverPickupState;
	}

	public void setDriverPickupState(PickupState driverPickupState) {
		this.driverPickupState = driverPickupState;
	}

}
