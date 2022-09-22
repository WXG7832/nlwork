package com.nlteck.service.connector;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.check.CheckCalProcessData;
import com.nltecklib.protocol.li.check.CheckCalculateData;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.check.CheckFaultCheckData;
import com.nltecklib.protocol.li.check.CheckHKCalibrateData;
import com.nltecklib.protocol.li.check.CheckHKDriverStateData;
import com.nltecklib.protocol.li.check.CheckPickupData;
import com.nltecklib.protocol.li.check.CheckPoleData;
import com.nltecklib.protocol.li.check.CheckStartupData;
import com.nltecklib.protocol.li.check.CheckVoltProtectData;
import com.nltecklib.protocol.li.check.CheckWriteCalFlashData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
 * 回检板通信控制器
 * 
 * @author Administrator
 *
 */
public class CheckConnector {

	private int timeOut = 200;
	private SerialPort serialPort;
	private int checkIndex;

	public CheckConnector(int checkIndex, SerialPort port) {

		this.checkIndex = checkIndex;
		this.serialPort = port;
	}

	/**
	 * 设置超时时间
	 * 
	 * @param timeout
	 */
	public void setRecvTimeout(int timeout) {

		this.timeOut = timeout;
	}

	private boolean config(Data data, StringBuffer err) throws IOException {

		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
				new ConfigDecorator(data), 1200);

		return response.getResult().getCode() == Result.SUCCESS;

	}

	/**
	 * 读取后立即返回
	 * 
	 * @param data
	 * @param err
	 * @return
	 */
	private Data readImmediate(Data data, StringBuffer err, int timeOut) {

		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort, new QueryDecorator(data),
					timeOut);
			if (response.getResult() .getCode()== Result.SUCCESS) {

				if (data.getCode() != CheckCode.PickupCode) {
					System.out.println("end read imm " + data);
				}
				// System.out.println("pickup logic " + data.getUnitIndex() + ",driver " +
				// data.getDriverIndex() + " successed");
				return response.getDestData();
			} else {

				System.out.println(
						"pickup logic " + data.getUnitIndex() + ",driver " + data.getDriverIndex() + " failed");
				return null;
			}
		} catch (IOException e) {

			System.out.println(CommonUtil.getThrowableException(e));
			return null;
		}

	}

	private Data read(Data data, StringBuffer err) {

		if (data.getCode() != CheckCode.PickupCode) {
			System.out.println("start read" + data);
		}
		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecv(ProtocolType.CHECK, serialPort, new QueryDecorator(data), timeOut);
			if (response.getResult().getCode() == Result.SUCCESS) {

				if (data.getCode() != CheckCode.PickupCode) {
					System.out.println("end read" + data);
				}
				return response.getDestData();
			} else {

				System.out.println(
						"pickup logic " + data.getUnitIndex() + ",driver " + data.getDriverIndex() + " failed");
				return null;
			}
		} catch (IOException e) {

			// System.out.println("pickup logic " + data.getUnitIndex() + ",driver " +
			// driverIndex + " failed");
			System.out.println(CommonUtil.getThrowableException(e));
			return null;
		}

	}

	/**
	 * 配置极性
	 * 
	 * @param lpd
	 * @param err
	 * @return
	 * @throws AlertException
	 */
	public synchronized boolean configPole(CheckPoleData lpd, StringBuffer err) throws AlertException {

		try {
			return config(lpd, err);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {

				return config(lpd, err);
			} catch (IOException ex) {

				throw new AlertException(AlertCode.COMM_ERROR, "配置回检板" + (lpd.getUnitIndex() + 1) + "通信错误");
			}
		}

	}

	/**
	 * 配置设备电压保护值
	 * 
	 * @param ldpd
	 * @param err
	 * @return
	 */
	public synchronized boolean configDeviceProtect(CheckVoltProtectData cvpd, StringBuffer err) throws AlertException {
		try {
			return config(cvpd, err);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {

				return config(cvpd, err);
			} catch (IOException ex) {

				throw new AlertException(AlertCode.COMM_ERROR, "配置回检板" + (cvpd.getUnitIndex() + 1) + "保护电压错误");
			}
		}
	}

	public synchronized boolean configStartup(CheckStartupData csd, StringBuffer err) throws AlertException {
		try {
			return config(csd, err);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {

				return config(csd, err);
			} catch (IOException ex) {

				throw new AlertException(AlertCode.COMM_ERROR, "启动回检板" + (csd.getUnitIndex() + 1) + "通信错误");
			}
		}
	}

	public synchronized CheckPickupData pickup(int driverIndex) {

		CheckPickupData cpd = new CheckPickupData();
		cpd.setUnitIndex(checkIndex);
		cpd.setDriverIndex(driverIndex);

		StringBuffer err = new StringBuffer();
		Data recv = null;
		/*int timeOut = MainBoard.startupCfg.getCheckInfo(checkIndex).communicateTimeout;
		if ((recv = readImmediate(cpd, err, timeOut)) == null) {

			return null;
		}*/

		return (CheckPickupData) recv;
	}

	public synchronized boolean writeCalFlash(CheckWriteCalFlashData cwcfd, StringBuffer err) {

		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new ConfigDecorator(cwcfd), 3000);
			return response.getResult() .getCode()== Result.SUCCESS;
		} catch (IOException e) {

			err.append("send serial port failed!");
			return false;
		}
	}

	public synchronized CheckWriteCalFlashData readCalFlash(CheckWriteCalFlashData cwcfd, StringBuffer err) {

		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new QueryDecorator(cwcfd), 1500);
			if (response.getResult() .getCode()== Result.SUCCESS) {

				return (CheckWriteCalFlashData) response.getDestData();
			}
			return null;
		} catch (IOException e) {

			err.append("send serial port failed!");
			return null;
		}
	}

	public synchronized boolean configHKCalibration(CheckHKCalibrateData ccpd, StringBuffer err) {

		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new ConfigDecorator(ccpd), 1500);
			return response.getResult() .getCode()== Result.SUCCESS;
		} catch (IOException e) {

			err.append("send serial port failed!");
			return false;
		}
	}
	
	/**
	 * 配置计量点
	 * @author  wavy_zheng
	 * 2020年8月21日
	 * @param ccld
	 * @return
	 * @throws AlertException
	 */
	public synchronized boolean configCalculate(CheckCalculateData ccld) throws AlertException {
		
		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new ConfigDecorator(ccld), 1500);
			return response.getResult() .getCode()== Result.SUCCESS;
		} catch (IOException e) {

			throw new AlertException(AlertCode.LOGIC,"send serial port fail");
		}
		
	}

	public synchronized boolean configCalProcess(CheckCalProcessData ccpd, StringBuffer err) {

		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new ConfigDecorator(ccpd), 1500);
			return response.getResult() .getCode()== Result.SUCCESS;
		} catch (IOException e) {

			err.append("send serial port failed!");
			return false;
		}
	}

	/**
	 * 从回检板读取校准系数
	 * 
	 * @param err
	 * @return
	 */
	public synchronized CheckCalProcessData readCalProcess(CheckCalProcessData ccpd, StringBuffer err) {

		try {

			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new QueryDecorator(ccpd), 2500);
			if (response.getResult().getCode() == Result.SUCCESS) {

				return (CheckCalProcessData) response.getDestData();
			} else {

				return null;
			}
		} catch (IOException e) {

			err.append("send serial port failed!");
			return null;
		}
	}

	public synchronized CheckHKDriverStateData readHKDriverState(int logicIndex, int driverIndex, StringBuffer err) {
        
		CheckHKDriverStateData chdsd = new CheckHKDriverStateData();
		chdsd.setUnitIndex(logicIndex);
		chdsd.setDriverIndex(driverIndex);
		try {

			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new QueryDecorator(chdsd), 3500);
			if (response.getResult() .getCode()== Result.SUCCESS) {

				return (CheckHKDriverStateData) response.getDestData();
			} else {

				return null;
			}
		} catch (IOException e) {

			err.append("send serial port failed!");
			return null;
		}

	}
	
	/**
	 * 读取计量数据
	 * @author  wavy_zheng
	 * 2020年8月21日
	 * @param ccld
	 * @return
	 * @throws AlertException
	 */
	public synchronized CheckCalculateData readCalculate(CheckCalculateData ccld) throws AlertException {
		
		try {

			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new QueryDecorator(ccld), 2500);
			if (response.getResult() .getCode()== Result.SUCCESS) {

				return (CheckCalculateData) response.getDestData();
			} else {

				return null;
			}
		} catch (IOException e) {

			throw new AlertException(AlertCode.COMM_ERROR , "send serial port failed!");
			
		}
		
	}

	public synchronized CheckHKCalibrateData readHKCalibration(CheckHKCalibrateData ccpd, StringBuffer err) {

		try {

			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CHECK, serialPort,
					new QueryDecorator(ccpd), 2500);
			if (response.getResult() .getCode()== Result.SUCCESS) {

				return (CheckHKCalibrateData) response.getDestData();
			} else {

				return null;
			}
		} catch (IOException e) {

			err.append("send serial port failed!");
			return null;
		}
	}

	public void clearBuff() {

		synchronized (serialPort) {
			try {
				SerialUtil.readMessageOnce(serialPort);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 将回检板的驱动板位置上下颠倒后换算后的通道序号
	 * 
	 * @param chnIndex
	 * @return
	 */
	public static int getReverseDriverChnIndex(int chnIndex) {

//		int driverIndex = chnIndex / MainBoard.startupCfg.getDriverChnCount();
//		int chnIndexInDriver = chnIndex % MainBoard.startupCfg.getDriverChnCount();
//		driverIndex = MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;

		return 0;
	}

	/*public CheckFaultCheckData readFaultCheckData(int checkIndex) throws AlertException {

		CheckFaultCheckData cfcd = new CheckFaultCheckData();
		cfcd.setUnitIndex(checkIndex);

		return (CheckFaultCheckData) readImmediate(cfcd, new StringBuffer(),
				MainBoard.startupCfg.getCheckInfo(checkIndex).communicateTimeout);

	}*/

}
