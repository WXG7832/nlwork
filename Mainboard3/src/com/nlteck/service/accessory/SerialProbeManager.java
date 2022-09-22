package com.nlteck.service.accessory;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.io.serialport.SerialConnector;
import com.nltecklib.protocol.power.ConfigDecorator;
import com.nltecklib.protocol.power.Entity;
import com.nltecklib.protocol.power.Entity.ProtocolType;
import com.nltecklib.protocol.power.QueryDecorator;
import com.nltecklib.protocol.power.ResponseDecorator;
import com.nltecklib.protocol.power.temper.TempSwPickData;
import com.nltecklib.protocol.power.temper.TemperAdjustData;
import com.nltecklib.utils.LogUtil;
import com.rm5248.serial.SerialPort;

/**
 * 使用通信串口
 * @author Administrator
 *
 */
public class SerialProbeManager extends ProbeManager {
   
	private SerialPort serialPort;
	
	public SerialProbeManager(MainBoard mainBoard) throws AlertException {
		
		super(mainBoard);
		//this.serialPort = Context.getPortManager().getPortByName(probeInfo.portName);
	}

	@Override
	public TempSwPickData readTempList(int driverIndex) throws Exception {
		
		String portName = probeInfos.get(driverIndex).portName;
		SerialPort port =  Context.getPortManager().getPortByName(portName);
		
		TempSwPickData tpd = new TempSwPickData();
		tpd.setDriverIndex(driverIndex);
		
		try {
			/*response = SerialUtil.sendAndRecvImmediate(ProtocolType.TEMPER, serialPort, new QueryDecorator(tpd),
					2000);*/
			
			SerialConnector  connector = new SerialConnector(new Entity(), port, null);
			
			return (TempSwPickData) ((ResponseDecorator)connector
					.sendUntillReceiveQry(new QueryDecorator(tpd))).getDestData();
			
		} catch (IOException e) {

			e.printStackTrace();
			throw new Exception("读取温度探头数据发生错误!2" + CommonUtil.getThrowableException(e));
		}
		
	}

	//private Logger logger = LogUtil.getLogger("controlTempTest");
	
	@Override
	public void setConstantTemp(int driverIndex, double temp) throws Exception {
		
		//logger.info(String.format(">>>>>>>>start 驱动板号：%d,温度设置:%.1f", driverIndex, temp));
		
		String portName = probeInfos.get(driverIndex).portName;
		SerialPort port =  Context.getPortManager().getPortByName(portName);
		
		
		TemperAdjustData tad = new TemperAdjustData();
		tad.setTemper(temp);
		tad.setDriverIndex(driverIndex);
		
		
		try {
			
			SerialConnector  connector = new SerialConnector(new Entity(), port, null);
			
			connector.sendUntillReceiveCfg(new ConfigDecorator(tad));
			
			
		} catch (IOException e) {

			e.printStackTrace();
			
			throw new Exception("设置温控恒温值发生错误!" + CommonUtil.getThrowableException(e));
		}
		
	}
    
	  
	
}
