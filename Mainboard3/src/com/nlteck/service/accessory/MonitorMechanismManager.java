package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.Environment;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.MechanismStateQueryData;
import com.nltecklib.protocol.li.accessory.TrayPressureData;
import com.nltecklib.protocol.li.accessory.ValveSwitchData;
import com.nltecklib.protocol.li.accessory.ValveTempData;
import com.nltecklib.protocol.li.main.CylinderPressureProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

public class MonitorMechanismManager extends MechanismManager {

	private SerialPort serialPort;

	public MonitorMechanismManager(MainBoard mb) throws AlertException {
		super(mb);
		serialPort = mb.getPortMap().get(cylinderInfo.portName);

		// 读取超温上限
		valveTempUpper = readValveTempUpper(0);
		// 读取气压范围
		pressureRange = readPressureRange(new CylinderPressureProtectData());

	}

	@Override
	public boolean writeValve(int driverIndex, ValveState vs) throws AlertException {
        
		
        if(exception && vs == ValveState.CLOSE) {
			
			throw new AlertException(AlertCode.LOGIC , "机构已经发生异常，请修复后再操作");
		}
		ValveSwitchData vsd = new ValveSwitchData();
		vsd.setDriverIndex(driverIndex);
		vsd.setVs(vs);

		try {
			SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(vsd),
					cylinderInfo.communicateTimeout);
		} catch (IOException ex) {

			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(vsd),
						cylinderInfo.communicateTimeout);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "控制电磁阀通信失败");
			}

		}
		return true;

	}

	@Override
	public ValveState readValve(int driverIndex) throws AlertException {

		ValveSwitchData vsd = new ValveSwitchData();
		vsd.setDriverIndex(driverIndex);
		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(vsd),
					cylinderInfo.communicateTimeout);
		} catch (IOException e) {

			e.printStackTrace();

			throw new AlertException(AlertCode.COMM_ERROR, "读取电磁阀状态发生错误");
		}

		return ((ValveSwitchData) response.getDestData()).getVs();
	}

	@Override
	public boolean writeValveTempUpper(int driverIndex, double tempUpper) throws AlertException {

		ValveTempData vtd = new ValveTempData();
		vtd.setDriverIndex(driverIndex);
		vtd.setTemperature(tempUpper);
		try {
			SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(vtd),
					cylinderInfo.communicateTimeout);
		} catch (IOException ex) {

			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(vtd),
						cylinderInfo.communicateTimeout);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "写入阀超温上限失败");
			}

		}
		valveTempUpper = tempUpper;
		return true;
	}

	@Override
	public double readValveTempUpper(int driverIndex) throws AlertException {

		ValveTempData vtd = new ValveTempData();
		vtd.setDriverIndex(driverIndex);
		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(vtd),
					cylinderInfo.communicateTimeout);
		} catch (IOException e) {

			e.printStackTrace();

			throw new AlertException(AlertCode.COMM_ERROR, "读取电磁阀状态发生错误");
		}
		return ((ValveTempData) response.getDestData()).getTemperature();
	}

	@Override
	public MechanismStateQueryData readMechanismState(int driverIndex) throws AlertException {

		MechanismStateQueryData msqd = new MechanismStateQueryData();
		msqd.setDriverIndex(driverIndex);
		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(msqd),
					cylinderInfo.communicateTimeout);
		} catch (IOException e) {

			Environment.infoLogger.error(CommonUtil.getThrowableException(e));

			throw new AlertException(AlertCode.COMM_ERROR, "读取机械电气状态发生错误");
		}

		return (MechanismStateQueryData) response.getDestData();
	}

	@Override
	public boolean writePressureRange(double pressureLower, double pressureUpper) throws AlertException {

		TrayPressureData tpd = new TrayPressureData();
		tpd.setDriverIndex(0);
		tpd.setMinPressure(pressureLower);
		tpd.setMaxPressure(pressureUpper);
		try {
			SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(tpd),
					cylinderInfo.communicateTimeout);
		} catch (IOException ex) {

			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(tpd),
						cylinderInfo.communicateTimeout);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "写入气缸气压范围值失败:通信超时");
			}

		}
		pressureRange.setPressureLower(pressureLower);
		pressureRange.setPressureUpper(pressureUpper);
		return true;
	}

	@Override
	public CylinderPressureProtectData readPressureRange(CylinderPressureProtectData cpd) throws AlertException {
		TrayPressureData tpd = new TrayPressureData();
		tpd.setDriverIndex(0);
		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(tpd),
					cylinderInfo.communicateTimeout);
		} catch (IOException e) {

			e.printStackTrace();

			throw new AlertException(AlertCode.COMM_ERROR, "读取机械电气状态发生错误");
		}

		TrayPressureData responseData = (TrayPressureData) response.getDestData();
		CylinderPressureProtectData tpdRecv = new CylinderPressureProtectData();
		tpdRecv.setDriverIndex(0);
		tpdRecv.setPressureLower(responseData.getMinPressure());
		tpdRecv.setPressureUpper(responseData.getMaxPressure());
		return tpdRecv;

	}

}
