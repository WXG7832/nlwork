package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.OMRTempData;
import com.nltecklib.protocol.li.accessory.OMRTempSwitchData;
import com.nltecklib.protocol.li.accessory.OMRTempUpperData;
import com.nltecklib.protocol.li.accessory.TempQueryData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
 * OMR温控表
 * 
 * @author Administrator
 *
 */
public class OMRTempMeter extends TempMeter {

	private SerialPort serialPort;
	private int timeOut = 3000;
	
	

	public OMRTempMeter(int index, SerialPort serialPort, int timeOut) {

		super(index);
		this.serialPort = serialPort;
		this.timeOut = timeOut;
		
	}

	public OMRTempMeter(int index, SerialPort serialPort) {

		this(index, serialPort, 3000);
	}

	@Override
	public void power(PowerState ps) throws AlertException {
          
		OMRTempSwitchData osd = new OMRTempSwitchData();
		osd.setDriverIndex(index);
		osd.setWorking(ps == PowerState.ON);
		try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(osd),
					timeOut);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(osd),
						timeOut);
			} catch (IOException ex) {

				ex.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "与欧姆容温控表" + (index + 1) + "通信发生错误:" + ( ps == PowerState.ON ? "开启" : "关闭" )+"温控失败");
			}

		}
		
	}

	@Override
	public TempQueryData readTemperatureData() throws AlertException {
		TempQueryData trd = new TempQueryData();
		trd.setDriverIndex(index);
		try {
			
			
			ResponseDecorator dr = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(trd),
					timeOut);
			return ((TempQueryData) dr.getDestData());
		} catch (IOException e) {
            
			CommonUtil.sleep(1000);
			try {
				ResponseDecorator dr = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(trd),
					timeOut);
				return ((TempQueryData) dr.getDestData());
			} catch (IOException ex) {
				
				ex.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR,"与温控板通信发生错误:查询温度失败");
			}
			
		}
	}

	@Override
	public void writeConstTemperature(double temp) throws AlertException {
		
		OMRTempData otd = new OMRTempData();
		otd.setDriverIndex(index);
		otd.setTemperature(temp);
		try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(otd),
					timeOut);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(otd),
						timeOut);
			} catch (IOException ex) {

				ex.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "与欧姆容温控表" + (index + 1) + "通信发生错误:配置温度失败");
			}

		}
		
		

	}

	@Override
	public void writeTempUpper(double temp) throws AlertException {
		
		OMRTempUpperData otud = new OMRTempUpperData();
		// 是否双表，双表需要配置第2块表温度超限值
		otud.setDriverIndex(index);
		otud.setTempUpper(temp);
		try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(otud),
					timeOut);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(otud),
						timeOut);
			} catch (IOException ex) {

				ex.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "与欧姆容温控表" + (index + 1) + "通信发生错误:配置二次超温上限失败");
			}

		}

	}

	@Override
	public double readTempUpper() throws AlertException {
		OMRTempUpperData otud = new OMRTempUpperData();
		otud.setDriverIndex(index);
		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(otud),
					timeOut);
			if (response.getResult().getCode() != Result.SUCCESS) {

				throw new AlertException(AlertCode.LOGIC, "读取欧姆容温控表" + (index + 1) + "返回错误码:" + response.getResult());
			}
			return ((OMRTempUpperData) response.getDestData()).getTempUpper();
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(otud),
						timeOut);

				if (response.getResult().getCode() != Result.SUCCESS) {

					throw new AlertException(AlertCode.LOGIC,
							"读取欧姆容温控表" + (index + 1) + "返回错误码:" + response.getResult());
				}
				return ((OMRTempUpperData) response.getDestData()).getTempUpper();
			} catch (IOException ex) {

				ex.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "与欧姆容温控表" + (index + 1) + "通信发生错误:读取超温上限失败");
			}

		}

	}

	@Override
	public double readConstTemperature() throws AlertException {
		
		OMRTempData otd = new OMRTempData();
		otd.setDriverIndex(index);
		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(otd),
					timeOut);
			if(response.getResult() .getCode()!= Result.SUCCESS) {
				
				throw new AlertException(AlertCode.LOGIC,"读取设定温度发生错误，错误代码:" + response.getResult());
			}
			return ((OMRTempData)response.getDestData()).getTemperature();
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				response =  SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(otd),
						timeOut);
				if(response.getResult() .getCode()!= Result.SUCCESS) {
					
					throw new AlertException(AlertCode.LOGIC,"读取设定温度发生错误，错误代码:" + response.getResult());
				}
				return ((OMRTempData)response.getDestData()).getTemperature();
			} catch (IOException ex) {

				ex.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "与欧姆容温控表" + (index + 1) + "通信发生错误:配置温度失败");
			}

		}
	}

}
