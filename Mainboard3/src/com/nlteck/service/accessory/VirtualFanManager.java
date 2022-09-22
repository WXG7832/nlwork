package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.FanInfo;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.FanType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.AnotherFanStateQueryData;
import com.nltecklib.protocol.li.accessory.FanStateQueryData;

public class VirtualFanManager extends FanManager {

	public VirtualFanManager(MainBoard mainBoard) throws AlertException {
		super(mainBoard);
		for (Fan fan : coolFans) {
			
			//fan.setWs(WorkState.FAULT);
		}
		

	}

	public boolean fan(int index, Direction direction, PowerState state, int grade) throws AlertException {

		this.fanSpeed = grade;
		for (Fan fan : coolFans) {

			if (fan.getWs() == WorkState.FAULT) {

				continue;
			}
			fan.setPs(state);
		}
		System.out.println("fan:" + coolFans);
		triggerListener(state == PowerState.ON);
		return true;
	}

	@Override
	public FanStateQueryData readFansState(int index) throws AlertException {

		FanStateQueryData fsqd = new FanStateQueryData();
		fsqd.setHeatFanCount(coolFans.size());
		fsqd.setTurboFanCount(turboFans.size());
		for (FanInfo fi : MainBoard.startupCfg.getFanManagerInfo().fanInfos) {

			if (fi.fanType == FanType.COOL) {
				fsqd.setHeatFanStateFlag(encodeCoolFanStates(fi));
				fsqd.setHeatFanSwitchFlag(encodeCoolFanSwitchs(fi));
			} else if (fi.fanType == FanType.TURBO) {

				List<Byte> list = new ArrayList<>();
				list.add((byte) 0x0f);
				fsqd.setTurboFanSwitchFlag(encodeTurboFanSwitches(fi));
				fsqd.setTurboFanStateFlag(encodeTurboFanStates(fi));
			}
		}

		

		return fsqd;
	}

	@Override
	public void powerTurboFan(PowerState powerState) throws AlertException {

		for (Fan fan : turboFans) {

			if (fan.getWs() == WorkState.FAULT) {
				continue;
			}
			fan.setPs(powerState);

		}

	}

	private Fan getFanByFanInfo(int driverIndex, int fanIndex) {

		int index = 0;
		for (int n = 0; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

			FanInfo f = MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n);
			if (driverIndex == f.getIndex()) {

				return coolFans.get(index + fanIndex);
			}
			index += f.fanCount;
		}

		return null;

	}

	private List<Byte> encodeTurboFanStates(FanInfo fi) {

		List<Byte> list = new ArrayList<Byte>();

		byte b = 0;
		for (int n = 0; n < fi.fanCount; n++) {
			Fan fan = getTurboFanByIndex(n);
			if (fan.getWs() == WorkState.FAULT) {

				b = (byte) (0x01 << n | b);
			}
		}
		list.add(b);

		return list;
	}

	private List<Byte> encodeTurboFanSwitches(FanInfo fi) {

		List<Byte> list = new ArrayList<Byte>();

		byte b = 0;
		for (int n = 0; n < fi.fanCount; n++) {
			Fan fan = getTurboFanByIndex(n);
			if (fan.getPs() == PowerState.ON) {

				b = (byte) (0x01 << n | b);
			}
		}
		list.add(b);

		return list;
	}

	private List<Byte> encodeCoolFanStates(FanInfo fi) {

		List<Byte> list = new ArrayList<Byte>();
		for (int n = 0; n < AnotherFanStateQueryData.HEAT_FAN_BYTE_SIZE; n++) {

			list.add((byte) 0);
		}

		for (int n = 0; n < fi.fanCount; n++) {

			Fan fan = getFanByFanInfo(fi.getIndex(), n);
			if (fan.getWs() == WorkState.FAULT) {

				byte b = (byte) (list.get(n / 8) | 0x01 << (n % 8));
				list.set(n / 8, b);
			}

		}

		return list;
	}

	private List<Byte> encodeCoolFanSwitchs(FanInfo fi) {

		List<Byte> list = new ArrayList<Byte>();
		for (int n = 0; n < AnotherFanStateQueryData.HEAT_FAN_BYTE_SIZE; n++) {

			list.add((byte) 0);
		}

		for (int n = 0; n < fi.fanCount; n++) {

			Fan fan = getFanByFanInfo(fi.getIndex(), n);
			if (fan.getPs() == PowerState.ON) {

				byte b = (byte) (list.get(n / 8) | 0x01 << (n % 8));
				list.set(n / 8, b);
			}

		}

		return list;
	}

	@Override
	public AnotherFanStateQueryData readAnotherFansState(int index) throws AlertException {

		for (int n = 0; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

			FanInfo fi = MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n);

			if (fi.fanType == FanType.COOL && fi.getIndex() == index) {

				AnotherFanStateQueryData fsqd = new AnotherFanStateQueryData();
				fsqd.setHeatFanCount(fi.fanCount);

				fsqd.setHeatFanSwitchFlag(encodeCoolFanSwitchs(fi));
				fsqd.setHeatFanStateFlag(encodeCoolFanStates(fi));

				if (fi.getIndex() == 0) {
					fsqd.setTurboFanCount(turboFans.size());
					// fsqd.setTurboFanSwitchFlag(encodeTurboFanSwitches());
					// fsqd.setTurboFanStateFlag(encodeTurboFanStates());
				}

				return fsqd;
			}

		}

		return null;
	}

}
