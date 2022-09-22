package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.PowerErrorInfoData;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData.CommState;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData.FanState;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData2;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 虚拟逆变电源管理器
 * 
 * @author Administrator
 *
 */
public class VirtualGroupPowerManager extends PowerManager {

	private boolean powerSwitch; // 总电源开关

	private final static int CHARGE_COUNT = 7; // 恒温箱逆变电源个数
	private final static int GROUP_A_COUNT = 3; // A组逆变电源个数
	private final static int GROUP_B_COUNT = 4; // B组逆变电源个数

	/**
	 * 虚拟恒温箱电源管理器
	 * 
	 * @param mainBoard
	 * @throws AlertException
	 */
	public VirtualGroupPowerManager(MainBoard mainBoard) throws AlertException {
		super(mainBoard);

		List<Integer> groupList = MainBoard.startupCfg.getPowerManagerInfo().powerGroups;
		int groupCount = 0;
		for (Integer c : groupList) {

			groupCount += c;
		}

		if (chargePowerInfo.powerCount != groupCount) {

			throw new AlertException(AlertCode.INIT, "错误的逆变电源配置个数与分组个数不匹配:" + chargePowerInfo.powerCount);
		}
		int indexInDevice = 0;
		for (int n = 0; n < groupList.size(); n++) {

			PowerGroup pg = new PowerGroup(n);
			for (int i = 0; i < groupList.get(n); i++) {

				InverterPower power = null;

				power = new VirtualInverterPower(pg, i, indexInDevice);

				pg.appendPower(power);
				
				/*if(i == 0) {
					
					power.setWs(WorkState.FAULT);
				}*/
			}
			groups.add(pg);
			indexInDevice++;

		}
		// 初始化辅助电源
		for (int n = 0; n < auxiliaryPowerInfo.powerCount; n++) {

			auxiliaryPowers.add(new VirtualAuxiliaryPower(n));
		}

	}

	@Override
	public boolean power(PowerState ps) throws AlertException {

		/**
		 * 辅助电源不受影响
		 */

		System.out.println("powerManager power + " + ps);

		for (PowerGroup pg : groups) {

			pg.power(ps);

		}

		powerSwitch = ps == PowerState.ON;
		waitSeconds = 0;
		return true;
	}

	@Override
	public PowerState getPowerSwitchState() throws AlertException {

		return powerSwitch ? PowerState.ON : PowerState.OFF;

	}

	@Override
	public PowerStateQueryData readPowersState() throws AlertException {

		PowerStateQueryData psqd = new PowerStateQueryData(getChargePowerCount(), getAuxiliaryPowerCount());

		List<Byte> list = new ArrayList<>();
		byte workState = 0, powerSwitch = 0;
		for (int n = 0; n < auxiliaryPowers.size(); n++) {

			if (auxiliaryPowers.get(n).getPs() == PowerState.ON) {

				powerSwitch = (byte) (0x01 << n | powerSwitch);

			}

			if (auxiliaryPowers.get(n).getWs() == WorkState.FAULT) {
				workState = (byte) (0x01 << n | workState);

			}

		}
		list.add(workState);
		psqd.setAuxiliariePowerStates(list);

		list = new ArrayList<>();
		list.add(powerSwitch);

		psqd.setAuxiliariePowerSwitches(list);

		short chargePowerSwitch = 0;
		short chargePowerState = 0;
		for (int n = 0; n < getChargePowerCount(); n++) {

			InverterPower power = findPowerByIndex(n);
			if (power.getPs() == PowerState.ON) {

				chargePowerSwitch = (short) (0x01 << n | chargePowerSwitch);

			}
			if (power.getWs() == WorkState.FAULT) {

				chargePowerState = (byte) (0x01 << n | chargePowerState);
			}

		}
		list = new ArrayList<>();
		list.addAll(Arrays.asList(ProtocolUtil.split((long) chargePowerState, 2, false)));
		psqd.setChargePowerStates(list);

		list = new ArrayList<>();
		list.addAll(Arrays.asList(ProtocolUtil.split((long) chargePowerSwitch, 2, false)));

		psqd.setChargePowerSwitches(list);

		return psqd;
	}

	@Override
	public PowerFaultReasonData readPowerFaultInfo(int powerIndex) throws AlertException {

		PowerFaultReasonData fault = new PowerFaultReasonData();
		fault.setCommState(CommState.NORMAL);
		fault.setFanState(FanState.FAULT);
		return fault;
	}

	@Override
	public PowerStateQueryData2 readPowersState2() throws AlertException {

		PowerStateQueryData2 psqd = new PowerStateQueryData2(getChargePowerCount(), getAuxiliaryPowerCount());

		List<Byte> list = new ArrayList<>();
		byte workState = 0, powerSwitch = 0;
		for (int n = 0; n < auxiliaryPowers.size(); n++) {

			if (auxiliaryPowers.get(n).getPs() == PowerState.ON) {

				powerSwitch = (byte) (0x01 << n | powerSwitch);

			}

			if (auxiliaryPowers.get(n).getWs() == WorkState.FAULT) {
				workState = (byte) (0x01 << n | workState);

			}

		}
		list.add(workState);
		psqd.setAuxiliariePowerStates(list);

		list = new ArrayList<>();
		list.add(powerSwitch);

		psqd.setAuxiliariePowerSwitches(list);

		short chargePowerSwitch = 0;
		short chargePowerState = 0;
		for (int n = 0; n < getChargePowerCount(); n++) {

			InverterPower power = findPowerByIndex(n);
			if (power.getPs() == PowerState.ON) {

				chargePowerSwitch = (short) (0x01 << n | chargePowerSwitch);

			}
			if (power.getWs() == WorkState.FAULT) {

				chargePowerState = (byte) (0x01 << n | chargePowerState);
			}

		}
		list = new ArrayList<>();
		list.addAll(Arrays.asList(ProtocolUtil.split((long) chargePowerState, 2, false)));
		psqd.setChargePowerStates(list);

		list = new ArrayList<>();
		list.addAll(Arrays.asList(ProtocolUtil.split((long) chargePowerSwitch, 2, false)));

		psqd.setChargePowerSwitches(list);

		return psqd;
	}

	@Override
	public void writePowerSupplyState(boolean work) throws AlertException {
		
		
		logger.info("writePowerSupplyState state = " + (work ? "work" : "wait"));
		
	}

	@Override
	public PowerErrorInfoData readTBMPowerFaultInfos() throws AlertException {
		// TODO Auto-generated method stub
		return new PowerErrorInfoData();
	}

}
