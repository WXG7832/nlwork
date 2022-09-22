package com.nlteck.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.DriverInfo;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.ProtocolUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.serialport.SerialConnector;
import com.nltecklib.io.serialport.SerialListener;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.power.ConfigDecorator;
import com.nltecklib.protocol.power.Decorator;
import com.nltecklib.protocol.power.Entity;
import com.nltecklib.protocol.power.Entity.ProtocolType;
import com.nltecklib.protocol.power.Environment.Result;
import com.nltecklib.protocol.power.QueryDecorator;
import com.nltecklib.protocol.power.ResponseDecorator;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalculateData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverChannelTemperData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.power.driver.DriverHeartbeatData;
import com.nltecklib.protocol.power.driver.DriverInfoData;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.nltecklib.protocol.power.driver.DriverModuleSwitchData;
import com.nltecklib.protocol.power.driver.DriverOperateData;
import com.nltecklib.protocol.power.driver.DriverPickupData;
import com.nltecklib.protocol.power.driver.DriverPoleData;
import com.nltecklib.protocol.power.driver.DriverProtectData;
import com.nltecklib.protocol.power.driver.DriverResumeData;
import com.nltecklib.protocol.power.driver.DriverStepData;
import com.nltecklib.protocol.power.driver.DriverUpgradeData;
import com.rm5248.serial.SerialPort;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年1月25日 下午8:09:55 类说明
 */
public class SerialDriverBoardService extends AbsDriverBoardService {

	private final static int OPERATE_TIMEOUT = 2000;

	public SerialDriverBoardService(MainBoard mainboard) {
		super(mainboard);

	}

	private SerialPort findPort(int driverIndex) {

		DriverInfo di = MainBoard.startupCfg.getDriverInfo(driverIndex);
		SerialPort serialPort = Context.getPortManager().getPortByName(di.portName);
		return serialPort;
	}

	@Override
	public DriverPickupData pickupDriver(int driverIndex) throws AlertException {

		DriverPickupData pick = new DriverPickupData();
		pick.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		DriverInfo di = MainBoard.startupCfg.getDriverInfo(driverIndex);
		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(pick), (int) di.communication);
			return (DriverPickupData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, "采集驱动板" + (driverIndex + 1) + "数据通信超时");
		}

	}

	@Override
	public void writePole(DriverPoleData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data), OPERATE_TIMEOUT,
				0, 2);
		} catch (AlertException ex) {
            
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
				
				throw new AlertException(AlertCode.COMM_ERROR,
						"驱动板" + (driverIndex + 1) + "写入极性通信超时");
			} else {
				
				throw new AlertException(AlertCode.LOGIC,
						"驱动板" + (driverIndex + 1) + "写入极性" + ex.getMessage());
			}
		}

	}

	@Override
	public void writeHeartbeat(DriverHeartbeatData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		   configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data), OPERATE_TIMEOUT,
				0, 2);
		} catch (AlertException ex) {
			
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			   throw new AlertException(AlertCode.COMM_ERROR,
					"驱动板" + (driverIndex + 1) + "写入心跳通信超时");
			} else {
				
				throw new AlertException(AlertCode.LOGIC,
						"驱动板" + (driverIndex + 1) + "写入心跳" + ex.getMessage());
			}
		}

	}

	@Override
	public DriverInfoData readDriverInfo(int driverIndex, int chnIndexInDriver) throws AlertException {

		DriverInfoData query = new DriverInfoData();
		query.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));

		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(query), OPERATE_TIMEOUT);
			return (DriverInfoData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, "读取驱动板" + (driverIndex + 1) + "自检信息通信超时");
		}
	}

	@Override
	public void writeBaseProtect(DriverProtectData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		     configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data), OPERATE_TIMEOUT,
				0, 2);
		} catch (AlertException ex) {
           
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
				 throw new AlertException(AlertCode.COMM_ERROR,
						"驱动板" + (driverIndex + 1) + "下发超压保护通信超时");
			} else {
				
				throw new AlertException(AlertCode.LOGIC,
						"驱动板" + (driverIndex + 1) + "下发超压保护" + ex.getMessage());
			}
		}
	}

	@Override
	public void writeOperate(DriverOperateData data) throws AlertException {
       
		Environment.infoLogger.info("write operate:" + data);
		
		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		     configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data), OPERATE_TIMEOUT,
				0, 1);
		} catch (AlertException ex) {
         
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
				throw new AlertException(AlertCode.COMM_ERROR,
						"驱动板" + (driverIndex + 1) + (data.isOpen() ? "打开": "关闭") + "通道通信超时");
			} else {
				
				throw new AlertException(AlertCode.COMM_ERROR,
						"驱动板" + (driverIndex + 1) + (data.isOpen() ? "打开": "关闭") + "通道" + ex.getMessage());
			}
		}

	}

	@Override
	public void writeSteps(DriverStepData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data), OPERATE_TIMEOUT,
				0, 2);
		} catch (AlertException ex) {
           
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
				throw new AlertException(AlertCode.COMM_ERROR,
						"驱动板" + (driverIndex + 1) + "写入流程步次通信超时");
			} else {
				
				throw new AlertException(AlertCode.COMM_ERROR,
						"驱动板" + (driverIndex + 1) + "写入流程步次" + ex.getMessage());
			}
		}

	}

	@Override
	public void writeResume(DriverResumeData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data), OPERATE_TIMEOUT,
				0, 2);
		} catch (AlertException ex) {
           
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			      throw new AlertException(AlertCode.COMM_ERROR,
					"驱动板" + (driverIndex + 1) + "写入流程步次恢复标记通信超时");
			} else {
				
				throw new AlertException(AlertCode.COMM_ERROR,
						"驱动板" + (driverIndex + 1) + "写入流程步次恢复标记" + ex.getMessage());
			}
		}

	}

	@Override
	public void writeUpgrade(DriverUpgradeData upgrade) throws AlertException {

		int driverIndex = upgrade.getDriverIndex();
		upgrade.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(upgrade),
				OPERATE_TIMEOUT, 0, 2);
		} catch (AlertException ex) {
           
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			    throw new AlertException(AlertCode.COMM_ERROR,
					"驱动板" + (driverIndex + 1) + "升级通信超时");
			} else {
				
				throw new AlertException(AlertCode.LOGIC,
						"驱动板" + (driverIndex + 1) + "升级" + ex.getMessage());
			}
		}

	}

	@Override
	public void writeWorkMode(DriverModeData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
			configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data),
					OPERATE_TIMEOUT, 0, 1);
		} catch (AlertException ex) {
            
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			    throw new AlertException(AlertCode.COMM_ERROR,
					"驱动板" + (driverIndex + 1) + "修改工作模式:" + data.getMode().name() + "通信超时");
			} else {
				
				throw new AlertException(AlertCode.LOGIC,
						"驱动板" + (driverIndex + 1) + "修改工作模式:" + data.getMode().name() + ex.getMessage());
			}
		}

	}

	@Override
	public void writeModuleSwitch(DriverModuleSwitchData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));

		try {
			configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data),
					OPERATE_TIMEOUT, 0, 1);
		} catch (AlertException ex) {
           
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			      throw new AlertException(AlertCode.COMM_ERROR,
					"驱动板" + (driverIndex + 1) + "使能:" + (data.isOpen() ? "打开" : "关闭") + "通信超时");
			} else {
				
				 throw new AlertException(AlertCode.LOGIC,
							"驱动板" + (driverIndex + 1) + "使能:" + (data.isOpen() ? "打开" : "关闭") + ex.getMessage());
			}

		}

	}

	@Override
	public void writeCalibrate(DriverCalibrateData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
			configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data),
					OPERATE_TIMEOUT, 0, 1);
		} catch (AlertException ex) {
           
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			    throw new AlertException(AlertCode.COMM_ERROR, "驱动板" + (driverIndex + 1) + "写入校准参数配置" + "通信超时");
			} else {
				
				throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "写入校准参数配置" + ex.getMessage());
			}

		}

	}

	@Override
	public DriverCalibrateData readCalibrate(int driverIndex, int chnIndexInDriver) throws AlertException {

		DriverCalibrateData query = new DriverCalibrateData();
		query.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		query.setChnIndex(chnIndexInDriver);
		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(query), OPERATE_TIMEOUT);
			return (DriverCalibrateData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "读取校准配置参数超时");
		}

	}

	@Override
	public void writeCalculate(DriverCalculateData data) throws AlertException {

		int driverIndex = data.getDriverIndex();
		data.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
			configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(data),
					OPERATE_TIMEOUT, 0, 1);
		} catch (AlertException ex) {
             
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			   throw new AlertException(AlertCode.COMM_ERROR, "驱动板" + (driverIndex + 1) + "写入计量配置参数超时");
			} else {
				
				throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "写入计量配置参数" + ex.getMessage());
			}
		}

	}

	@Override
	public void writeFlash(DriverCalParamSaveData flash) throws AlertException {

		int driverIndex = flash.getDriverIndex();
		flash.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		try {
		configAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex), new ConfigDecorator(flash), OPERATE_TIMEOUT,
				0, 1);
		} catch(AlertException ex) {
			
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
			    throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "写入flash系数通信超时");
			} else {
				throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "写入flash系数" + ex.getMessage());
				
			}
		}

	}

	@Override
	public DriverCalculateData readCalculate(int driverIndex, int chnIndexInDriver) throws AlertException {

		DriverCalculateData query = new DriverCalculateData();
		query.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		query.setChnIndex(chnIndexInDriver);
		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(query), OPERATE_TIMEOUT);
			return (DriverCalculateData) response.getDestData();

		} catch (IOException e) {
             
			
			throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "读取计量点参数通信超时");
			
		}
	}

	@Override
	public DriverCalParamSaveData readFlash(int driverIndex, int chnIndexInDriver , int moduleIndex) throws AlertException {

		DriverCalParamSaveData query = new DriverCalParamSaveData();
		query.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		query.setChnIndex(chnIndexInDriver);
		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(query,(byte) moduleIndex), OPERATE_TIMEOUT);
			return (DriverCalParamSaveData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "读取flash系数通信超时");
		}
	}

	@Override
	public DriverMatchAdcData readMatchAdcs(int driverIndex) throws AlertException {

		DriverMatchAdcData query = new DriverMatchAdcData();
		query.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));

		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(query), OPERATE_TIMEOUT);
			return (DriverMatchAdcData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.CommError) + e.getMessage());
		}
	}

	@Override
	public DriverCheckData readDriverSelfCheckInfo(int driverIndex) throws AlertException {

		DriverCheckData query = new DriverCheckData();
		query.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));

		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(query), OPERATE_TIMEOUT);
			return (DriverCheckData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "读取自检系数通信超时");
		}
	}

	@Override
	public DriverInfoData readDriverSoftInfo(int driverIndex) throws AlertException {

		DriverInfoData query = new DriverInfoData();
		query.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));

		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(query), OPERATE_TIMEOUT);
			return (DriverInfoData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, "驱动板" + (driverIndex + 1) + "读取软件版本通信超时");
		}
	}

	public static byte[] readMessageOnce(SerialPort serialPort) throws IOException {

		if (serialPort != null) {

			InputStream is = serialPort.getInputStream();

			if (is.available() > 0) {
				byte[] input = new byte[is.available()];

				int numberRead = serialPort.getInputStream().read(input);

				return input;
			}
		}
		return null;
	}

	public static void sendMessage(SerialPort serialPort, List<Byte> data) {

		if (serialPort != null) {

			try {

				byte[] array = ProtocolUtil.convertArrayType(data.toArray(new Byte[0]));
				// System.out.println(Entity.printList(data));
				serialPort.getOutputStream().write(array);
				serialPort.getOutputStream().flush();
				serialPort.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ResponseDecorator sendAndRecvImmediate2(ProtocolType pt, SerialPort serialPort, Decorator decorator,
			int timeOut) throws IOException {

		SerialConnector connector = new SerialConnector(new Entity(), serialPort, null);
		ResponseDecorator response = (ResponseDecorator) connector.sendUntillReceive(decorator, false, 2000);

		return response;

	}

	/**
	 * 发送命令完后立即读取，拼接缓存区数据,判断是否完整；完整则立即返回
	 * 
	 * @param pt
	 * @param serialPort
	 * @param decorator
	 * @param timeOut
	 * @return
	 * @throws IOException
	 */
	public static ResponseDecorator sendAndRecvImmediate(ProtocolType pt, SerialPort serialPort, Decorator decorator,
			int timeOut) throws IOException {

		synchronized (serialPort) {

			List<Byte> sendData = null;
			List<Byte> recvData = new ArrayList<Byte>();
			try {

				try {
					sendData = Entity.encode(decorator);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 添加事件监听

				// 清空串口
				readMessageOnce(serialPort);

				if (decorator.getCode() != DriverCode.PickupCode) {

					// 记录逻辑板发生日志
					Environment.protocolLogger.info("SEND:" + Entity.printList(sendData));
				}

				sendMessage(serialPort, sendData);

				long tick = System.currentTimeMillis();

				InputStream is = serialPort.getInputStream();

				int len = 0;
				do {

					CommonUtil.sleep(20);

					if (len > 0 && is.available() >= len + Entity.SHELL_LEN - recvData.size()) {

						readToBuff(is, recvData);

						ResponseDecorator dec = (ResponseDecorator) Entity.decode(recvData);
						CommonUtil.sleep(10);
						if (decorator.getCode() != DriverCode.PickupCode) {

							// 记录逻辑板发生日志
							Environment.protocolLogger.info("RECV:" + Entity.printList(recvData));
						}

						if (dec.getResult() != Result.SUCCESS) {

							Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
							Environment.infoLogger.error("RECV:" + Entity.printList(recvData));
							throw new IOException("comm communicate failed ,failed code: " + dec.getResult());
						}

						return dec;

					} else if (is.available() >= Entity.MIN_DECODE_LEN) {

						readToBuff(is, recvData);
						byte[] arr = com.nltecklib.protocol.util.ProtocolUtil.convertListToArray(recvData);
						int j = findHeadPos(arr);

						if (j > 0) {

							recvData = recvData.subList(j, recvData.size());
						}

						// 解析长度
						if (recvData.size() >= Entity.MIN_DECODE_LEN) {

							len = (int) ProtocolUtil.compose(recvData
									.subList(Entity.MIN_DECODE_LEN - 4, Entity.MIN_DECODE_LEN).toArray(new Byte[0]),
									true);

						}

					} else {

					}
				} while (System.currentTimeMillis() - tick <= timeOut);

				throw new IOException("Entity.printList(recvData) = " + Entity.printList(recvData)
						+ "\nis.available() = " + is.available() + "," + System.currentTimeMillis() + " - " + tick
						+ " > " + timeOut + " , received data from port time out");
			} catch (IOException e) {

				Environment.infoLogger.error(CommonUtil.getThrowableException(e));
				// Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
				// Environment.infoLogger.error("RECV:" + Entity.printList(recvData));

				throw e;
			} catch (Exception e) {

				Environment.infoLogger.error(CommonUtil.getThrowableException(e));
				Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
				Environment.infoLogger.error("RECV:" + Entity.printList(recvData));
				throw new IOException(e.getMessage());
			} finally {

			}

		}

	}

	private static int readToBuff(InputStream is, List<Byte> buff) throws IOException {

		byte[] array = new byte[is.available()];
		int readNum = is.read(array, 0, array.length);
		for (int i = 0; i < readNum; i++) {
			buff.add(array[i]);
		}
		return readNum;
	}

	public static int findHeadPos(byte[] data) {
		int start = -1;
		if (data.length < Entity.HEAD.length) {

			return start;
		}
		for (int i = 0; i <= data.length - Entity.HEAD.length; i++) {

			byte[] sec = Arrays.copyOfRange(data, i, i + Entity.HEAD.length);
			if (Arrays.equals(sec, Entity.HEAD)) {

				start = i;
				// return start;
				break;
			}

		}
		return start;
	}

	public static void configAndRecvImmediate(ProtocolType pt, SerialPort port, ConfigDecorator cmd, int timeout,
			int commCountIndex, int retryCount) throws AlertException {
        
		
		try {
			commCountIndex++;
			ResponseDecorator response = sendAndRecvImmediate(pt, port, cmd, timeout);
			if (response.getResult() != Result.SUCCESS) {

				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ResponseError, response.getResult()));
			}
		} catch (IOException e) {

			if (commCountIndex < retryCount) { // 重发

				CommonUtil.sleep(1000);
				configAndRecvImmediate(pt, port, cmd, timeout, commCountIndex, retryCount);
			} else {

				e.printStackTrace();
				// 抛异常
				throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError));
			}

		}

	}

	@Override
	public DriverChannelTemperData pickupTemperature(int driverIndex) throws AlertException {
		
		DriverChannelTemperData pick = new DriverChannelTemperData();
		pick.setDriverIndex(DriverBoard.getDownloadDriverIndex(driverIndex));
		DriverInfo di = MainBoard.startupCfg.getDriverInfo(driverIndex);
		try {
			ResponseDecorator response = sendAndRecvImmediate(ProtocolType.DRIVER, findPort(driverIndex),
					new QueryDecorator(pick), (int) di.communication);
			return (DriverChannelTemperData) response.getDestData();

		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC, "采集驱动板" + (driverIndex + 1) + "温度数据通信超时");
		}
	}

}
