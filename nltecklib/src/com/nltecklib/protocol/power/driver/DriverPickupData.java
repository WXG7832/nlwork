package com.nltecklib.protocol.power.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.AlertCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月15日 下午4:11:27 驱动板采集协议
 */
public class DriverPickupData extends Data implements Queryable, Responsable {

	private boolean driverStateOk;
	private DriverMode  driverMode; //驱动板工作模式
	private double temperature1;
	private double temperature2;
	private boolean checkInverterSwtState;// 回检板逆变开关状态
	private byte driverFanState;// 驱动板风机状态

	private List<ChnDataPack> packs = new ArrayList<>();

	/**
	 * 通道数据包
	 * 
	 * @author wavy_zheng 2021年12月15日
	 *
	 */
	public static class ChnDataPack implements Cloneable{

		private int chnIndex; // 逻辑板通道序号
		private double voltage;
		private double backupVoltage;
		private double powerVoltage;
		private double current;
		private double capacity;// 通道
		private long stepElapsedTime;// 步次流逝时间ms
		private int loopIndex;// 步次循环号，第几遍循环
		private int stepIndex;// 步次序号，从1开始
		private AlertCode alertCode;
		private ChnState state = ChnState.UDT;
		private WorkMode workMode;

		private boolean important; // 关键点数据？

		private double alertVolt;
		private double alertCurrent;
		private long    alertTime;
		private double  alertCapacity;
		

		public int getChnIndex() {
			return chnIndex;
		}

		public void setChnIndex(int chnIndex) {
			this.chnIndex = chnIndex;
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

		public double getCapacity() {
			return capacity;
		}

		public void setCapacity(double capacity) {
			this.capacity = capacity;
		}

		public long getStepElapsedTime() {
			return stepElapsedTime;
		}

		public void setStepElapsedTime(long stepElapsedTime) {
			this.stepElapsedTime = stepElapsedTime;
		}

		public int getLoopIndex() {
			return loopIndex;
		}

		public void setLoopIndex(int loopIndex) {
			this.loopIndex = loopIndex;
		}

		public int getStepIndex() {
			return stepIndex;
		}

		public void setStepIndex(int stepIndex) {
			this.stepIndex = stepIndex;
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

		public WorkMode getWorkMode() {
			return workMode;
		}

		public void setWorkMode(WorkMode workMode) {
			this.workMode = workMode;
		}

		public boolean isImportant() {
			return important;
		}

		public void setImportant(boolean important) {
			this.important = important;
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

		public long getAlertTime() {
			return alertTime;
		}

		public void setAlertTime(long alertTime) {
			this.alertTime = alertTime;
		}

		public double getBackupVoltage() {
			return backupVoltage;
		}

		public void setBackupVoltage(double backupVoltage) {
			this.backupVoltage = backupVoltage;
		}

		public double getPowerVoltage() {
			return powerVoltage;
		}

		public void setPowerVoltage(double powerVoltage) {
			this.powerVoltage = powerVoltage;
		}
		
		
		

		public double getAlertCapacity() {
			return alertCapacity;
		}

		public void setAlertCapacity(double alertCapacity) {
			this.alertCapacity = alertCapacity;
		}

		@Override
		public String toString() {
			return "ChnDataPack [chnIndex=" + chnIndex + ", voltage=" + voltage + ", backupVoltage=" + backupVoltage
					+ ", powerVoltage=" + powerVoltage + ", current=" + current + ", capacity=" + capacity
					+ ", stepElapsedTime=" + stepElapsedTime + ", loopIndex=" + loopIndex + ", stepIndex=" + stepIndex
					+ ", alertCode=" + alertCode + ", state=" + state + ", workMode=" + workMode + ", important="
					+ important + ", alertVolt=" + alertVolt + ", alertCurrent=" + alertCurrent + ", alertTime="
					+ alertTime + "]";
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

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

	@Override
	public void encode() {

		data.add((byte) (driverStateOk ? 0x00 : 0x01));
		data.add((byte) driverMode.ordinal());
		data.add((byte) temperature1); // 温度1
		data.add((byte) temperature2); // 温度2
		data.add((byte) (checkInverterSwtState ? 0x01 : 0x00));
		data.add(driverFanState);
		data.add((byte) packs.size());

		for (int n = 0; n < packs.size(); n++) {

			ChnDataPack chnData = packs.get(n);
			int chnIndexInDriver = chnData.getChnIndex();
			if (isReverseDriverChnIndex()) {

				chnIndexInDriver = Data.getDriverChnCount() - 1 - n;
			}
			// 通道序号
			data.add((byte) chnIndexInDriver);
			// 通道状态
			data.add((byte) chnData.getState().getCode());
			data.add((byte) chnData.getWorkMode().ordinal()); // 工作模式

			// 步次循环号
			data.add((byte) chnData.getLoopIndex());
			// 步次序号
			data.add((byte) chnData.getStepIndex());

			// 主电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getVoltage() * Math.pow(10, 1)), 3, true)));
			// 备份电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getBackupVoltage() * Math.pow(10, 1)), 2, true)));
			// 功率电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getPowerVoltage() * Math.pow(10, 1)), 2, true)));
			// 电流
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getCurrent() * Math.pow(10, 1)), 3, true)));
			//步次容量
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getCapacity() * Math.pow(10, 1)), 4, true)));
			
			// 步次流逝时间
			data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getStepElapsedTime(), 4, true)));

			// 报警码
			data.add((byte) chnData.getAlertCode().ordinal());
			// 报警电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getAlertVolt() * Math.pow(10, 1)), 3, true)));
			// 报警电流
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getAlertCurrent() * Math.pow(10, 1)), 3, true)));

			// 步次报警流逝时间
			data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getAlertTime(), 4, true)));
			
			//步次容量
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getAlertCapacity() * Math.pow(10, 1)), 4, true)));


		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverStateOk = data.get(index++) == 0x00;
		
		int code = data.get(index++);
		if(code > DriverMode.values().length - 1) {
			
			throw new RuntimeException("error driver mode code:" + code);
		}
		driverMode = DriverMode.values()[code];
		
		// 板载温度1
		temperature1 = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 板载温度2
		temperature2 = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		checkInverterSwtState = data.get(index++) == 0x01;
		
		driverFanState = data.get(index++);

		int chnCount = ProtocolUtil.getUnsignedByte(data.get(index++)); // 通道包数
		packs.clear();

		for (int n = 0; n < chnCount; n++) {

			ChnDataPack chnData = new ChnDataPack();

			int chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			// 通道序号
			chnData.setChnIndex(Data.isReverseDriverChnIndex() ? Data.getDriverChnCount() - 1 - chnIndex : chnIndex);
			// 通道状态
			int stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
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

			// 步次循环号
			chnData.setLoopIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
			// 步次序号
			chnData.setStepIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));

			// 通道电压
			chnData.setVoltage(
					(double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10);
			index += 3;
			// 备份电压
			chnData.setBackupVoltage(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
			// 功率电压
			chnData.setPowerVoltage(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;

			// 通道电流
			chnData.setCurrent((double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 10);
			index += 3;
			
			// 通道容量
			chnData.setCapacity((double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 10);
			index += 4;
			
			// 步次流逝时间
			chnData.setStepElapsedTime(ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true));
			index += 4;

			// 报警码
			int alertIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (alertIndex > AlertCode.values().length - 1) {

				throw new RuntimeException("error channel(" + (n + 1) + ") alert code :" + alertIndex);
			}
			chnData.setAlertCode(AlertCode.values()[alertIndex]);
			// 报警电压
			chnData.setAlertVolt(
					(double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10);
			index += 3;
			// 报警电流
			chnData.setAlertCurrent(
					(double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10);
			index += 3;
			
			// 报警流逝时间
			chnData.setAlertTime(ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true));
			index += 4;
			
			// 通道报警容量
			chnData.setAlertCapacity((double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 10);
			index += 4;
			
			
			packs.add(chnData);

		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.PickupCode;
	}

	public boolean isDriverStateOk() {
		return driverStateOk;
	}

	public void setDriverStateOk(boolean driverStateOk) {
		this.driverStateOk = driverStateOk;
	}

	public double getTemperature1() {
		return temperature1;
	}

	public void setTemperature1(double temperature1) {
		this.temperature1 = temperature1;
	}

	public double getTemperature2() {
		return temperature2;
	}

	public void setTemperature2(double temperature2) {
		this.temperature2 = temperature2;
	}

	public List<ChnDataPack> getPacks() {
		return packs;
	}
	
	

	public DriverMode getDriverMode() {
		return driverMode;
	}

	public void setDriverMode(DriverMode driverMode) {
		this.driverMode = driverMode;
	}

	public boolean isCheckInverterSwtState() {
		return checkInverterSwtState;
	}

	public void setCheckInverterSwtState(boolean checkInverterSwtState) {
		this.checkInverterSwtState = checkInverterSwtState;
	}

	public byte getDriverFanState() {
		return driverFanState;
	}

	public void setDriverFanState(byte driverFanState) {
		this.driverFanState = driverFanState;
	}

	@Override
	public String toString() {
		return "DriverPickupData [driverStateOk=" + driverStateOk + ", temperature1=" + temperature1 + ", temperature2="
				+ temperature2 + ", packs=" + packs + "]";
	}
	
	

}
