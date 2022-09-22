package com.nltecklib.protocol.li.check;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.AlertCode;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.check.CheckEnvironment.ChnState;
import com.nltecklib.protocol.li.check.CheckEnvironment.PowerState;
import com.nltecklib.protocol.li.check.CheckEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class CheckPickupData extends Data implements Queryable, Responsable {

	private PowerState powerState = PowerState.POWEROFF;
	private WorkMode workMode = WorkMode.CHECK;
	private List<ChnData> chnDatas = new LinkedList<ChnData>();

	public class ChnData {
		
		private int unitIndex;
		private int driverIndex;
		private PowerState powerState = PowerState.POWEROFF;
		private WorkMode workMode = WorkMode.CHECK;
		private int index;

		private ChnState chnState = ChnState.NONE;
		private double deviceVoltage;
		private double powerVoltage;
		private AlertCode alertCode = AlertCode.NORMAL;
		private double alertVoltage;
		
		
		

		public int getUnitIndex() {
			return unitIndex;
		}


		public int getDriverIndex() {
			return driverIndex;
		}

		public PowerState getPowerState() {
			return powerState;
		}


		public WorkMode getWorkMode() {
			return workMode;
		}

		public int getIndex() {
			return index;
		}

		public ChnState getChnState() {
			return chnState;
		}

		public void setChnState(ChnState chnState) {
			this.chnState = chnState;
		}

		public double getDeviceVoltage() {
			return deviceVoltage;
		}

		public void setDeviceVoltage(double deviceVoltage) {
			this.deviceVoltage = deviceVoltage;
		}

		public double getPowerVoltage() {
			return powerVoltage;
		}

		public void setPowerVoltage(double powerVoltage) {
			this.powerVoltage = powerVoltage;
		}

		public AlertCode getAlertCode() {
			return alertCode;
		}

		public void setAlertCode(AlertCode alertCode) {
			this.alertCode = alertCode;
		}

		public double getAlertVoltage() {
			return alertVoltage;
		}

		public void setAlertVoltage(double alertVoltage) {
			this.alertVoltage = alertVoltage;
		}

		@Override
		public String toString() {
			return "ChnData [chnState=" + chnState + ", deviceVoltage=" + deviceVoltage + ", powerVoltage=" + powerVoltage
					+ ", alertCode=" + alertCode + ", alertVoltage=" + alertVoltage + "]";
		}

	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public List<ChnData> getChnDatas() {
		return chnDatas;
	}

	public void setChnDatas(List<ChnData> chnDatas) {
		this.chnDatas = chnDatas;
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
		data.add((byte) powerState.ordinal());
		data.add((byte) workMode.ordinal());
		data.add((byte) (chnDatas.size() - 1));
		for (int i = 0; i < chnDatas.size(); i++) {
            
			int chnIndex = i;
			if(isReverseDriverChnIndex()) {
				
				chnIndex = chnDatas.size() - 1 - i;
			}
			ChnData chnData = chnDatas.get(chnIndex);
			if (chnData == null) {

				throw new RuntimeException("can not pickup channel data");
			}
			data.add((byte) chnData.getChnState().ordinal());
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getPowerVoltage() * 10), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getDeviceVoltage() * 10), 2, true)));
			
			data.add((byte) chnData.getAlertCode().ordinal());
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getAlertVoltage() * 10), 2, true)));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		// 分区状态
		int stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (stateIndex > PowerState.values().length - 1) {

			throw new RuntimeException("error unit state index :" + stateIndex);
		}
		powerState = PowerState.values()[stateIndex];
		// 分区模式
		int modeIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(modeIndex > WorkMode.values().length - 1) {
			
			throw new RuntimeException("error workmode index :" + modeIndex);
		}
		workMode = WorkMode.values()[modeIndex];

		int chnCount = ProtocolUtil.getUnsignedByte(data.get(index++)) + 1;

		chnDatas.clear();
		for (int n = 0; n < chnCount; n++) {
             
			ChnData chnData = new ChnData();
			
			chnData.unitIndex=unitIndex;
			chnData.driverIndex=driverIndex;
			chnData.index=n;
			chnData.powerState=powerState;
			chnData.workMode=workMode;
			
			
			
			stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(stateIndex > ChnState.values().length - 1) {
				
				throw new RuntimeException("error channel(" + (n + 1)+") state index :" + stateIndex);
			}
			chnData.setChnState( ChnState.values()[stateIndex]);
			
			//通道电池电压
			chnData.setPowerVoltage((double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
			//通道设备电压
			chnData.setDeviceVoltage((double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
			
			//报警码
			int alertIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(alertIndex > AlertCode.values().length - 1) {
				
				throw new RuntimeException("error channel(" + (n + 1)+") alert code :" + alertIndex);
			}
			chnData.setAlertCode( AlertCode.values()[alertIndex]);
			//报警电压
			chnData.setAlertVoltage((double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
			
			if (isReverseDriverChnIndex()) {

				// 反向通道序号
                chnDatas.add(0,chnData);
			} else {
				chnDatas.add(chnData);
			}
			
			
			
		}
	}

	@Override
	public Code getCode() {

		return CheckCode.PickupCode;
	}

	public PowerState getPowerState() {
		return powerState;
	}

	public void setPowerState(PowerState powerState) {
		this.powerState = powerState;
	}
	
	

}
