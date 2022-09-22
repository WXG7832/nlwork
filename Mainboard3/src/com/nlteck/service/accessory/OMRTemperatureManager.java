package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.OverTempFlag;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.HeatPipeStatusData;
import com.nltecklib.protocol.li.accessory.TempQueryData;
import com.nltecklib.protocol.li.accessory.TempStateQueryData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

public class OMRTemperatureManager extends TemperatureManager {

	private SerialPort serialPort;
	private int timeOut = 3000;

	public OMRTemperatureManager(MainBoard mb) throws AlertException {

		super(mb);

		serialPort = Context.getPortManager().getPortByName(meterInfo.portName);
		
		// 初始化表
		meter = new OMRTempMeter(meterInfo.index, serialPort);
		meter.setUse(meterInfo.use);
		if (protectMeterInfo != null ) {
			protectedMeter = new OMRTempMeter(protectMeterInfo.index, serialPort);
			protectedMeter.setUse(protectMeterInfo.use);
		}
		timeOut = meterInfo.communicateTimeout;
		//读取配置
		readCfg();

	}

	@Override
	public TempStateQueryData readTempState() throws AlertException {

		TempStateQueryData otsqd = new TempStateQueryData();
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(otsqd),
					timeOut);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "读取温度表状态发生错误!");
		}

		return (TempStateQueryData) response.getDestData();
	}

	@Override
	public void writeTemperature(double temperature) throws AlertException {

		meter.writeConstTemperature(temperature);
		constTemp = temperature;
		reset = false;

	}

	@Override
	public void writeTempUpper(double temperature) throws AlertException {

		if (protectedMeter != null) {

			if (temperature == 0) {

				temperature = 90;
			}
			protectedMeter.writeTempUpper(temperature);
			protectedMeter.writeConstTemperature(temperature); //表二显示超温阀值

		}
		meter.setTempUpper(temperature);
	}

	@Override
	public double readTempUpper() throws AlertException {

		if (protectedMeter != null) {

			return protectedMeter.readTempUpper();
		} else {

			return meter.getTempUpper();
		}

	}
	
	public void clearWaitSeconds() {
		
		waitSeconds = 0;
	}

	@Override
	public void power(PowerState ps) throws AlertException {

		meter.power(ps);
		if (protectedMeter != null) {
			protectedMeter.power(ps);
		}
		reset = false;
		elapsedSeconds = 0;
		// 清除待机时长
		waitSeconds = 0;
		if (ps == PowerState.ON) {

			// 清除报警标志位
			alert = false;
			// 清除机械表保护标志
			overTempFlag = OverTempFlag.NORMAL;
			// 开启温控清空上一次的超温报警
			if (Context.getAlertManager() != null) {
				Context.getAlertManager().handle(AlertCode.TEMP_OVER, "", true);
			}
		}

	}

	@Override
	public void writeTempLower(double temperature) throws AlertException {

		meter.setTempLower(temperature);

	}

	@Override
	public double readTempLower() throws AlertException {

		return meter.getTempLower();
	}

	@Override
	protected TempQueryData readTempQueryData() throws AlertException {

		TempQueryData tqd = meter.readTemperatureData();
		meter.setTemperature(tqd.getMainTemp());
		meter.setOverFlag(tqd.getOverTempFlag());

		if (protectedMeter != null) {

			// 读取辅助表温度
			TempQueryData tqd2 = protectedMeter.readTemperatureData();
			System.out.println(tqd2);
			protectedMeter.setTemperature(tqd2.getMainTemp());
			protectedMeter.setOverFlag(tqd2.getOverTempFlag());
		}
		return tqd;
	}

	@Override
	public void writeHeatpipeState(boolean open) throws AlertException {
		
		HeatPipeStatusData hpsd = new HeatPipeStatusData();
		hpsd.setCanOpen(open);
		SerialUtil.configAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, 
				new ConfigDecorator(hpsd), timeOut, 0, 2);
		
	}

}
