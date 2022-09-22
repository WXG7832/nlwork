package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.SmogInfo;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.li.accessory.SmogAlertData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2020年3月5日 下午4:07:16
* 类说明
*/
public class SerialSmogAlertManager extends SmogAlertManager {
    
	
	public SerialSmogAlertManager(MainBoard mainBoard) throws AlertException {
		super(mainBoard);
		
	}
	
	
	private SerialPort findSerialPort(int index) {

		SmogInfo si = MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.get(index);
		return Context.getPortManager().getPortByName(si.portName);
	}

	@Override
	public SmogAlertData readSmogState(int driverIndex) throws AlertException {
		
		
		SerialPort serialPort = findSerialPort(driverIndex);
		SmogAlertData sad = new SmogAlertData();
		sad.setDriverIndex(driverIndex);
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(sad),
					2000);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "读取烟雾报警器数据发生错误!");
		}

		return (SmogAlertData) response.getDestData();
		
		
	}


	@Override
	public void clearSmogState(int driverIndex) throws AlertException {
		
		SerialPort serialPort = findSerialPort(driverIndex);
		SmogAlertData sad = new SmogAlertData();
		sad.setDriverIndex(driverIndex);
		sad.setAlertState(AlertState.NORMAL);
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(sad),
					2000);
			if(response.getResult().getCode() != Result.SUCCESS) {
				
				throw new AlertException(AlertCode.LOGIC,"清除烟雾报警器状态失败!");
			}
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "读取烟雾报警器数据发生错误!");
		}

		
	}

}
