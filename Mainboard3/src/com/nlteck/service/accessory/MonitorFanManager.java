package com.nlteck.service.accessory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.FanInfo;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.FanType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AnotherFanStateQueryData;
import com.nltecklib.protocol.li.accessory.FanControlData;
import com.nltecklib.protocol.li.accessory.FanStateQueryData;
import com.nltecklib.protocol.li.accessory.TurboFanData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
 * ADDA 风机管理器
 * 
 * @author Administrator
 *
 */
public class MonitorFanManager extends FanManager {

	private int timeOut = 1200;

	private SerialPort findSerialPort(int index, FanType fanType) {

		List<FanInfo> list = new ArrayList<FanInfo>();
		for (int n = 0; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

			FanInfo fi = MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n);
			if (fi.fanType == fanType) {

				list.add(fi);
			}
		}
		if (index > list.size() - 1) {

			return null;
		}

		return Context.getPortManager().getPortByName(list.get(index).portName);
	}

	public MonitorFanManager(MainBoard mainBoard) throws AlertException {

		super(mainBoard);
	}

	@Override
	public boolean fan(int index, Direction direction, PowerState state, int grade) throws AlertException {

		FanControlData cfd = new FanControlData();
		cfd.setUnitIndex(index);
		cfd.setGrade(grade);
		cfd.setDirection(direction);
		cfd.setPowerState(state);
		fanSpeed = grade; //风机速度

		int groupIndex = 0;
		for (int n = 0; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

			if (MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n).fanType == FanType.COOL) {
				
				SerialPort serialPort = findSerialPort(groupIndex++, FanType.COOL);
				try {
					SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cfd), timeOut);
				} catch (IOException ex) {

					CommonUtil.sleep(1000);
					try {
						SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cfd), timeOut);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new AlertException(AlertCode.COMM_ERROR, "控制风扇通信失败");
					}

				}
			}
		}
		return true;

	}
	
	public AnotherFanStateQueryData readAnotherFansState(int index) throws AlertException {
		
		
		AnotherFanStateQueryData query = new AnotherFanStateQueryData();
		query.setDriverIndex(index);
		SerialPort serialPort = findSerialPort(index, FanType.COOL);
		
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(query),
					timeOut);
			return (AnotherFanStateQueryData) response.getDestData();
			
									
		} catch (IOException e) {

			e.printStackTrace();

			throw new AlertException(AlertCode.COMM_ERROR, "读取风机状态发生错误");
		}
	}
	

	@Override
	public FanStateQueryData readFansState(int index) throws AlertException {

		Data fsqd = null;
		fsqd = new FanStateQueryData();
		SerialPort serialPort = findSerialPort(index, FanType.COOL);
		
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(fsqd),
					timeOut);
		} catch (IOException e) {

			e.printStackTrace();

			throw new AlertException(AlertCode.COMM_ERROR, "读取风机状态发生错误");
		}

		return (FanStateQueryData) response.getDestData();
				
	}

	@Override
	public void powerTurboFan(PowerState powerState) throws AlertException {

		SerialPort serialPort = findSerialPort(0, FanType.TURBO);
		TurboFanData tfd = new TurboFanData();
		tfd.setDriverIndex(0xff);
		tfd.setPowerState(powerState);
		try {
			SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(tfd), timeOut);
		} catch (IOException ex) {

			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(tfd), timeOut);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "控制涡轮风机失败");
			}

		}

	}

}
