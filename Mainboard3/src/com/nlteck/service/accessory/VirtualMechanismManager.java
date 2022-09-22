package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.MechanismStateQueryData;
import com.nltecklib.protocol.li.main.CylinderPressureProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

public class VirtualMechanismManager extends MechanismManager {

	private ValveState solenoidValve = ValveState.OPEN;
	private ValveState trayState = ValveState.OPEN;

	private double pressureLower = 0;
	private double pressureUpper = 0;
	private double pressure = 1.2;

	private int count;

	public VirtualMechanismManager(MainBoard mb) throws AlertException {
		super(mb);
		valveTempUpper = 40;
	}

	@Override
	public boolean writeValve(int driverIndex, ValveState vs) throws AlertException {

        if(exception && vs == ValveState.CLOSE) {
			
			throw new AlertException(AlertCode.LOGIC , "机构已经发生异常，请修复后再操作");
		}
		solenoidValve = vs;
		trayState = vs;
		System.out.println(vs == ValveState.OPEN ? "张开机构" : "闭合机构");
		return true;
	}

	@Override
	public ValveState readValve(int driverIndex) throws AlertException {
		// TODO Auto-generated method stub
		return solenoidValve;
	}

	@Override
	public boolean writeValveTempUpper(int driverIndex, double tempUpper) throws AlertException {

		valveTempUpper = tempUpper;
		return true;
	}

	@Override
	public double readValveTempUpper(int driverIndex) throws AlertException {
		// TODO Auto-generated method stub
		return this.valveTempUpper;
	}

	@Override
	public MechanismStateQueryData readMechanismState(int driverIndex) throws AlertException {

		MechanismStateQueryData msqd = new MechanismStateQueryData();
		msqd.setSolenoidState(solenoidValve);
		msqd.setTrayState(trayState);

		if (pressure < pressureLower || (pressure > pressureUpper && pressureUpper > 0)) {

			msqd.setPressureState(AlertState.ALERT);
		} else {
			msqd.setPressureState( /*count > 10 ? AlertState.ALERT : */AlertState.NORMAL);
		}
		msqd.setPressure(pressure);

//		 if(count > 10) {
//		
//		 solenoidValve = ValveState.OPEN;
//		 }
		for (int n = 0; n < cylinderInfo.count; n++) {

			msqd.getCylinderStates().add(solenoidValve);
		}

		for (int n = 0; n < tcInfo.count; n++) {

			msqd.getTcStates().add(AlertState.NORMAL);
			msqd.getTcReads().add(25.0);
		}

		count++;

		return msqd;
	}

	@Override
	public boolean writePressureRange(double pressureLower, double pressureUpper) throws AlertException {

		this.pressureLower = pressureLower;
		this.pressureUpper = pressureUpper;
		this.pressureRange.setPressureLower(pressureLower);
		this.pressureRange.setPressureUpper(pressureUpper);
		return true;
	}

	@Override
	public CylinderPressureProtectData readPressureRange(CylinderPressureProtectData cpd) throws AlertException {

		CylinderPressureProtectData response = new CylinderPressureProtectData();
		response.setDriverIndex(cpd.getDriverIndex());
		response.setPressureLower(this.pressureLower);
		response.setPressureUpper(this.pressureUpper);

		return response;
	}

}
