package com.nlteck.service.accessory;

import java.io.IOException;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.ChannelLightController;
import com.nlteck.service.StartupCfgManager.DoorInfo;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.ChannelLightOptData;
import com.nltecklib.protocol.li.accessory.DoorData;
import com.nltecklib.protocol.li.accessory.ChannelLightOptData.ChannelLightData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2022年8月9日 上午8:56:31
* 类说明
*/
public class SerialChannelLightController extends AbsChannelLightController {

	public SerialChannelLightController(MainBoard mb) {
		super(mb);
		
	}

	@Override
	public void writeChannelLight(List<ChannelLightData> list) throws AlertException {
		
		SerialPort serialPort = findSerialPort();
		ChannelLightOptData clod = new ChannelLightOptData();
		clod.setChannelLightDatas(list);
		
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(clod),
					2000);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "写入通道灯发生错误!");
		}

	}
	
	private SerialPort findSerialPort() {

		ChannelLightController controller = MainBoard.startupCfg.getChannelLightController();
		return mainboard.getPortMap().get(controller.portName);
	}

}
