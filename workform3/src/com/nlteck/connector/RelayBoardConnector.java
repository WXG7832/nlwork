package com.nlteck.connector;

import java.io.IOException;

import com.nlteck.base.I18N;
import com.nlteck.fireware.CalibrateCore;
import com.nltecklib.io.serialport.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.cal.RelaySwitchData;
import com.rm5248.serial.SerialPort;

public class RelayBoardConnector {
	public static final int TIMEOUT = 3000;
	private CalibrateCore core;
	private SerialPort serialPort;

	public RelayBoardConnector(CalibrateCore core, SerialPort serialPort) {
		this.core = core;
		this.serialPort = serialPort;

	}
	
	public void cfgCalRelay(int blockIndex,int chnIndex) {
		RelaySwitchData relaySwitchData=new RelaySwitchData();
		relaySwitchData.setDriverIndex(blockIndex);
		relaySwitchData.setChannel((byte) chnIndex);
		ResponseDecorator result=sendAndRecvImmediate(new ConfigDecorator(relaySwitchData), TIMEOUT);
		CheckResult(result);
		relaySwitchData=(RelaySwitchData) result.getDestData();
	}
	
	private ResponseDecorator sendAndRecvImmediate(Decorator decorator, int timeOut) {
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(null, serialPort, decorator, timeOut);
		} catch (IOException e) {
			try {
				Thread.sleep(1000);
				response = SerialUtil.sendAndRecvImmediate(null, serialPort, decorator, timeOut);
			} catch (Exception e1) {
				core.getLogger().error("relayboard connect error:" + e1.getMessage(), e1);
				throw new RuntimeException(I18N.getVal(I18N.DeviceCommunicationTimeOut,
						I18N.getVal(decorator.getDestData().getClass().getSimpleName())));
			}
		} catch (Exception e) {
			core.getLogger().error("relayboard connect error:" + e.getMessage(), e);
			throw new RuntimeException(I18N.getVal(I18N.DeviceCommunicationTimeOut,
					I18N.getVal(decorator.getDestData().getClass().getSimpleName())));
//					"[" ++ "]" + e.getMessage(), e);
		}
		return response;
	}
	
	private void CheckResult(ResponseDecorator response) {
//		if (response == null) {
//			throw new RuntimeException("response time out");
//		}
		if (response.getResult().getCode() != Result.SUCCESS) {
			throw new RuntimeException(I18N.getVal(response.getDestData().getClass().getSimpleName()) + ":"
					+ response.getResult().getDescription());
		}
	}

}
