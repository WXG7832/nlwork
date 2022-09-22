package com.nltecklib.protocol.li.accessory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class MechanismStateQueryData extends Data implements Queryable, Responsable {

	private ValveState solenoidState = ValveState.OPEN; // 电磁阀
	private ValveState ACRelay = ValveState.OPEN; // 交流继电器
	private ValveState emergencyStopState = ValveState.OPEN; // 急停开关
	private ValveState doorState = ValveState.OPEN; // 门
	private AlertState smogState = AlertState.NORMAL; // 烟雾报警器
	private ValveState trayState = ValveState.OPEN; // 托盘状态
	private double     pressure;  //气压值

	private List<AlertState> tcStates = new ArrayList<AlertState>();
	private List<Double> tcReads = new ArrayList<Double>();
	private AlertState pressureState = AlertState.NORMAL;
	private AlertState connectState = AlertState.NORMAL;
	private List<ValveState> cylinderStates = new ArrayList<ValveState>();
	
	
	public MechanismStateQueryData() {
		
		
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

		data.add((byte) driverIndex);

		data.add((byte) (solenoidState == ValveState.CLOSE ? 0x01 : 0x00));
		data.add((byte) (ACRelay == ValveState.CLOSE ? 0x01 : 0x00));
		data.add((byte) (emergencyStopState == ValveState.CLOSE ? 0x01 : 0x00));
		data.add((byte) (doorState == ValveState.CLOSE ? 0x01 : 0x00));
		data.add((byte) (smogState == AlertState.ALERT ? 0x01 : 0x00));
		// 托盘状态
		data.add((byte) (trayState == ValveState.CLOSE ? 0x01 : 0x00));
		// 气压状态
		data.add((byte) pressureState.ordinal());
		//气压值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(pressure * 100), 2, true)));
		// 气压板连接状态
		data.add((byte) connectState.ordinal());
		// 气缸状态
		data.add((byte) cylinderStates.size()); // 气缸数
		for (int n = 0; n < cylinderStates.size(); n++) {

			data.add((byte) (cylinderStates.get(n) == ValveState.CLOSE ? 0x01 : 0x00));

		}
		// 托盘温度探头个数
		data.add((byte) tcStates.size());
		for (int n = 0; n < tcStates.size(); n++) {

			data.add((byte) (tcStates.get(n).ordinal()));
			data.add((byte) (double) tcReads.get(n));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
        //电磁阀状态
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ValveState.values().length - 1) {

			throw new RuntimeException("error solenoid state code :" + code);
		}
		solenoidState = ValveState.values()[code];
		// 交流继电器
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ValveState.values().length - 1) {

			throw new RuntimeException("error ac relay state code :" + code);
		}
		ACRelay = ValveState.values()[code];
		// 急停开关按钮
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ValveState.values().length - 1) {

			throw new RuntimeException("error emergency stop state code :" + code);
		}
		emergencyStopState = ValveState.values()[code];
		// 门近开关按钮
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ValveState.values().length - 1) {

			throw new RuntimeException("error door state code :" + code);
		}
		doorState = ValveState.values()[code];
		// 烟雾报警器
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > AlertState.values().length - 1) {

			throw new RuntimeException("error smog state code :" + code);
		}
		smogState = AlertState.values()[code];
		// 托盘状态
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ValveState.values().length - 1) {

			throw new RuntimeException("error tray state code :" + code);
		}
		trayState = ValveState.values()[code];
       
		//气压状态
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > AlertState.values().length - 1) {

			throw new RuntimeException("error pressure state code : " + code);
		}
		pressureState = AlertState.values()[code];
		//气压值
		pressure = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 100;
		index += 2;
		// 气压板串口连接状态
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > AlertState.values().length - 1) {

			throw new RuntimeException("error connect state code : " + code);
		}
		connectState = AlertState.values()[code];

		// 气缸个数
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));

		cylinderStates.clear();
		for (int n = 0; n < count; n++) {

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > ValveState.values().length - 1) {

				throw new RuntimeException("error cylinder " + (n + 1) + " state code :" + code);
			}
			cylinderStates.add(ValveState.values()[code]);

		}
		//温度探头个数
		count = ProtocolUtil.getUnsignedByte(data.get(index++));
        for(int n = 0 ; n < count ; n++) {
        	 
        	 code = ProtocolUtil.getUnsignedByte(data.get(index++));
        	 if(code > AlertState.values().length - 1) {
        		 
        		 throw new RuntimeException("error tc state code : " + code);
        	 }
        	 tcStates.add(AlertState.values()[code]);
        	 tcReads.add((double) ProtocolUtil.getUnsignedByte(data.get(index++)));
         }

	}

	@Override
	public Code getCode() {

		return AccessoryCode.MechanismStateQueryCode;
	}

	public ValveState getSolenoidState() {
		return solenoidState;
	}

	public void setSolenoidState(ValveState solenoidState) {
		this.solenoidState = solenoidState;
	}

	public ValveState getACRelay() {
		return ACRelay;
	}

	public void setACRelay(ValveState aCRelay) {
		ACRelay = aCRelay;
	}

	public ValveState getEmergencyStopState() {
		return emergencyStopState;
	}

	public void setEmergencyStopState(ValveState emergencyStopState) {
		this.emergencyStopState = emergencyStopState;
	}

	public ValveState getDoorState() {
		return doorState;
	}

	public void setDoorState(ValveState doorState) {
		this.doorState = doorState;
	}

	public AlertState getSmogState() {
		return smogState;
	}

	public void setSmogState(AlertState smogState) {
		this.smogState = smogState;
	}

	public ValveState getTrayState() {
		return trayState;
	}

	public void setTrayState(ValveState trayState) {
		this.trayState = trayState;
	}

	public List<ValveState> getCylinderStates() {
		return cylinderStates;
	}

	public void setCylinderStates(List<ValveState> cylinderStates) {
		this.cylinderStates = cylinderStates;
	}

	public List<AlertState> getTcStates() {
		return tcStates;
	}

	public void setTcStates(List<AlertState> tcStates) {
		this.tcStates = tcStates;
	}

	public List<Double> getTcReads() {
		return tcReads;
	}

	public void setTcReads(List<Double> tcReads) {
		this.tcReads = tcReads;
	}

	public AlertState getPressureState() {
		return pressureState;
	}

	public void setPressureState(AlertState pressureState) {
		this.pressureState = pressureState;
	}

	public AlertState getConnectState() {
		return connectState;
	}

	public void setConnectState(AlertState connectState) {
		this.connectState = connectState;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}
	
	

}
