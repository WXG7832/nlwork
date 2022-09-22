package com.nlteck.junit;

import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.cal.CalEnvironment.Pole;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalibrateData;
import com.nltecklib.protocol.li.check.CheckPickupData;
import com.nltecklib.protocol.li.logic.LogicPickupData;
import com.rm5248.serial.SerialPort;

public class SerialTestor {

	public static void main(String[] args) {

		System.out.println("start to test serial port");

		int unitIndex = Integer.parseInt(args[0]);
		int driverIndex = Integer.parseInt(args[1]);
		int sleepTime = Integer.parseInt(args[2]);
		boolean turn = Integer.parseInt(args[3]) == 1;
		boolean print = false;
		if (args.length >= 5) {
			print = Integer.parseInt(args[4]) == 1;
		}

		String portName = "/dev/ttyS" + (unitIndex < 5 ? unitIndex : 4);

		System.out.println(portName);

		try {
			SerialPort serialPort = SerialUtil.openPort(portName, 57600);
			PowerState powerState = PowerState.ON;

			while (true) {

				for (int i = 0; i < MainBoard.startupCfg.getDriverCount(); i++) {

					Data data = null;
					if (unitIndex < 2) {
						data = new LogicPickupData();
						data.setUnitIndex(unitIndex);

					} else if (unitIndex < 4) {

						data = new CheckPickupData();
						data.setUnitIndex(unitIndex - 2);
					} else if (unitIndex == 4) {

						if (powerState == PowerState.ON) {

							powerState = PowerState.OFF;
						} else {

							powerState = PowerState.ON;
						}
						data.setUnitIndex(0);

					} else if (unitIndex == 5) {

						data = new CalibrateData();

						data.setUnitIndex(0);
						((CalibrateData) data).setChnIndex(0);
						((CalibrateData) data).setPole(Pole.NORMAL);
						((CalibrateData) data).setReadyState((byte) 0);
						((CalibrateData) data).setWorkMode(WorkMode.CC);

					}

					data.setDriverIndex(turn ? i : driverIndex);

					ProtocolType pt = null;
					if (unitIndex < 2) {
						pt = ProtocolType.LOGIC;
					} else if (unitIndex < 4) {
						pt = ProtocolType.CHECK;
					} else if (unitIndex == 4) {

					} else if (unitIndex == 5) {

						pt = ProtocolType.CAL;
					}

					try {

						ResponseDecorator response = null;
						if (unitIndex == 4 || unitIndex == 5) {

							response = SerialUtil.sendAndRecv(pt, serialPort, new ConfigDecorator(data), sleepTime);
						} else if (unitIndex < 4) {
							response = SerialUtil.sendAndRecv(pt, serialPort, new QueryDecorator(data), sleepTime);
						}
						
					} catch (Exception e) {

						e.printStackTrace();

					}
					CommonUtil.sleep(sleepTime);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
