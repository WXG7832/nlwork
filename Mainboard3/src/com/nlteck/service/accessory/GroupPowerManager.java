package com.nlteck.service.accessory;

import java.io.IOException;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.PowerErrorInfoData;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData2;
import com.nltecklib.protocol.li.accessory.PowerSupplyData;
import com.nltecklib.protocol.li.accessory.PowerSwitchData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
 * 双组均衡电源管理器
 * 
 * @author Administrator
 *
 */
public class GroupPowerManager extends PowerManager {

	private final static int TIME_OUT = 3000;
	private final static int CHARGE_COUNT = 7; // 恒温箱逆变电源个数
	private final static int GROUP_A_COUNT = 3; // A组逆变电源个数
	private final static int GROUP_B_COUNT = 4; // B组逆变电源个数

	public GroupPowerManager(MainBoard mainBoard) throws AlertException {
		super(mainBoard);

		List<Integer> groupList = MainBoard.startupCfg.getPowerManagerInfo().powerGroups;
		int groupCount = 0;
		for (Integer c : groupList) {

			groupCount += c;
		}

		if (chargePowerInfo.powerCount != groupCount) {

			throw new AlertException(AlertCode.INIT, I18N.getVal(I18N.PowerCountErr));
		}
		// 初始化电源组
		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		int indexInDevice = 0;
		for (int n = 0; n < groupList.size(); n++) {

			PowerGroup pg = new PowerGroup(n);
			for (int i = 0; i < groupList.get(n); i++) {

				InverterPower power = null;

				power = new GDInverterPower(pg, i, indexInDevice, serialPort, chargePowerInfo.communicateTimeout);

				pg.appendPower(power);
				indexInDevice++;
			}
			groups.add(pg);

		}
		serialPort = findSerialPort(0, PowerType.AUXILIARY);
		// 初始化辅助电源
		for (int n = 0; n < auxiliaryPowerInfo.powerCount; n++) {

			auxiliaryPowers.add(new MWAuxiliaryPower(n, serialPort));
		}
	}

	@Override
	public synchronized boolean power(PowerState ps) throws AlertException {

		PowerSwitchData cpd = new PowerSwitchData();
		cpd.setUnitIndex(0xff);
		cpd.setPowerType(PowerType.CHARGE);
		cpd.setState(ps);
		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cpd),
					chargePowerInfo != null ? chargePowerInfo.communicateTimeout : TIME_OUT);
		} catch (IOException ex) {

			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommTimeout));

		}

		waitSeconds = 0;

		return true;

	}

	@Override
	public PowerState getPowerSwitchState() throws AlertException {

		PowerSwitchData cpd = new PowerSwitchData();
		cpd.setUnitIndex(0xff);
		cpd.setPowerType(PowerType.CHARGE);

		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(cpd),
					chargePowerInfo != null ? chargePowerInfo.communicateTimeout : TIME_OUT);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError) + ":" + e.getMessage());
		}

		PowerSwitchData PSD = (PowerSwitchData) response.getDestData();
		return PSD.getState();

	}

	@Override
	public synchronized PowerStateQueryData readPowersState() throws AlertException {

		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		PowerStateQueryData psqd = new PowerStateQueryData();
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(psqd),
					chargePowerInfo == null ? TIME_OUT : chargePowerInfo.communicateTimeout);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError) + ":" + e.getMessage());
		}

		return (PowerStateQueryData) response.getDestData();
	}

	@Override
	public PowerFaultReasonData readPowerFaultInfo(int powerIndex) throws AlertException {

		PowerFaultReasonData fault = new PowerFaultReasonData();
		fault.setDriverIndex(0);
		fault.setChnIndex(powerIndex);
		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort,
					new QueryDecorator(fault), chargePowerInfo != null ? chargePowerInfo.communicateTimeout : TIME_OUT);
			if (response.getResult().getCode() != Result.SUCCESS) {

				throw new AlertException(AlertCode.LOGIC,
						I18N.getVal(I18N.ResponseError, response.getResult().getCode()));
			}
			return (PowerFaultReasonData) response.getDestData();
		} catch (IOException ex) {

			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommTimeout) + ":" + ex.getMessage());

		}

	}

	@Override
	public PowerStateQueryData2 readPowersState2() throws AlertException {
		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		PowerStateQueryData2 psqd = new PowerStateQueryData2();
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(psqd),
					chargePowerInfo == null ? TIME_OUT : chargePowerInfo.communicateTimeout);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError) + ":" + e.getMessage());
		}

		return (PowerStateQueryData2) response.getDestData();
	}

	@Override
	public void writePowerSupplyState(boolean work) throws AlertException {
		
		logger.info("write power supply state :" + (work ? "work" : "wait"));
		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		PowerSupplyData psd = new PowerSupplyData();
		psd.setDriverIndex(0);
		psd.setWork(work);
		try {
			ResponseDecorator response =  SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(psd),
					chargePowerInfo == null ? TIME_OUT : chargePowerInfo.communicateTimeout);
			if(response.getResult().getCode() != Result.SUCCESS) {
				
				throw new AlertException(AlertCode.LOGIC,I18N.getVal(I18N.ResponseError, response.getResult().toString()));
			}
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError) + ":" + e.getMessage());
		}

	}

	@Override
	public PowerErrorInfoData readTBMPowerFaultInfos() throws AlertException {
		
		SerialPort serialPort = findSerialPort(0, PowerType.CHARGE);
		PowerErrorInfoData psqd = new PowerErrorInfoData();
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(psqd),
					chargePowerInfo == null ? TIME_OUT : chargePowerInfo.communicateTimeout);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError) + ":" + e.getMessage());
		}

		return (PowerErrorInfoData) response.getDestData();
	}


}
