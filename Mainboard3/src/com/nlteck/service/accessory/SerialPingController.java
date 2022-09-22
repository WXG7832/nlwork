package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.SerialUtil;
import com.nltecklib.io.serialport.SerialConnector;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AirPressureStateData;
import com.nltecklib.protocol.li.accessory.AirValveSwitchData;
import com.nltecklib.protocol.li.accessory.AirValveSwitchData.SwitchState;
import com.nltecklib.protocol.li.accessory.FourLightStateData;
import com.nltecklib.protocol.li.accessory.PingStateData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.power.Entity;
import com.nltecklib.protocol.power.temper.TempSwPickData;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2022年6月20日 下午4:18:44
* 类说明
*/
public class SerialPingController extends AbsPingController {

	public SerialPingController(MainBoard mainboard) {
		super(mainboard);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PingStateData readPingState(int driverIndex) throws Exception {
		
		String portName = MainBoard.startupCfg.getPingController().portName;
		SerialPort port =  Context.getPortManager().getPortByName(portName);
		PingStateData tpd = new PingStateData();
		tpd.setDriverIndex(driverIndex);
		
		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, port, new QueryDecorator(tpd),
					MainBoard.startupCfg.getPingController().communicateTimeout);
			
			return (PingStateData)response.getDestData();
			
		} catch (IOException ex) {

			throw new AlertException(AlertCode.COMM_ERROR, ex.getMessage());

		}
		
	}

	@Override
	public void writeFourLightState(FourLightStateData lightData) throws Exception {
		
		String portName = MainBoard.startupCfg.getPingController().portName;
		SerialPort port =  Context.getPortManager().getPortByName(portName);
		
		SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, port, new ConfigDecorator(lightData),
				MainBoard.startupCfg.getPingController().communicateTimeout);
		
		

	}

	@Override
	public AirPressureStateData readAirPressure(int driverIndex) throws Exception {
		
		
		String portName = MainBoard.startupCfg.getPingController().portName;
		SerialPort port =  Context.getPortManager().getPortByName(portName);
		AirPressureStateData tpd = new AirPressureStateData();
		tpd.setUnitIndex(driverIndex);
		
		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, port, new QueryDecorator(tpd),
					MainBoard.startupCfg.getPingController().communicateTimeout);
			
			return (AirPressureStateData)response.getDestData();
			
		} catch (IOException ex) {

			throw new AlertException(AlertCode.COMM_ERROR, ex.getMessage());

		}
	}

	@Override
	public void writeAirPressureControl(boolean lift) throws Exception {
		
		
		String portName = MainBoard.startupCfg.getPingController().portName;
		SerialPort port =  Context.getPortManager().getPortByName(portName);
		
		AirValveSwitchData  data = new AirValveSwitchData();
		data.setUnitIndex(0);
		data.setSwitchState(lift ? SwitchState.OPEN : SwitchState.CLOSE);
		
		SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, port, new ConfigDecorator(data),
				MainBoard.startupCfg.getPingController().communicateTimeout);
		

	}

}
