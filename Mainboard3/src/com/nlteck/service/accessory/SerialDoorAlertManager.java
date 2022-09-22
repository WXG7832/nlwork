package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.DoorInfo;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.DoorData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2020年3月6日 下午1:24:16
* 类说明
*/
public class SerialDoorAlertManager extends DoorAlertManager {

	public SerialDoorAlertManager(MainBoard mb) throws AlertException {
		super(mb);
		
	}
	
	private SerialPort findSerialPort(int index) {

		DoorInfo si = MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.get(index);
		return mainBoard.getPortMap().get(si.portName);
	}


	@Override
	public DoorData readDoorData(int index) throws AlertException {
		
		SerialPort serialPort = findSerialPort(index);
		DoorData dd = new DoorData();
		dd.setDriverIndex(index);
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(dd),
					2000);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "读取门近报警器数据发生错误!");
		}

		return (DoorData) response.getDestData();
	}

}
