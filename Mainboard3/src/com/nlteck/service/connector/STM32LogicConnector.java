package com.nlteck.service.connector;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import com.nltecklib.protocol.li.logic.LogicCalMatchData;
import com.nltecklib.protocol.li.logic.LogicCalProcessData;
import com.nltecklib.protocol.li.logic.LogicCalculateData;
import com.nltecklib.protocol.li.logic.LogicChnStartData;
import com.nltecklib.protocol.li.logic.LogicChnStopData;
import com.nltecklib.protocol.li.logic.LogicChnSwitchData;
import com.nltecklib.protocol.li.logic.LogicDeviceProtectData;
import com.nltecklib.protocol.li.logic.LogicEnvironment.MatchState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.li.logic.LogicFaultCheckData;
import com.nltecklib.protocol.li.logic.LogicFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKCalculateData;
import com.nltecklib.protocol.li.logic.LogicHKCalibrateData;
import com.nltecklib.protocol.li.logic.LogicHKFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKOperationData;
import com.nltecklib.protocol.li.logic.LogicHKProcedureData;
import com.nltecklib.protocol.li.logic.LogicLabPoleData;
import com.nltecklib.protocol.li.logic.LogicLabProtectData;
import com.nltecklib.protocol.li.logic.LogicModuleSwitchData;
import com.nltecklib.protocol.li.logic.LogicNewCalProcessData;
import com.nltecklib.protocol.li.logic.LogicPickupData;
import com.nltecklib.protocol.li.logic.LogicPoleData;
import com.nltecklib.protocol.li.logic.LogicStartupData;
import com.nltecklib.protocol.li.logic.LogicStateData;
import com.nltecklib.protocol.li.logic.LogicStopData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.rm5248.serial.SerialPort;

/**
 * STM32芯片的逻辑板控制器
 * 
 * @author Administrator
 *
 */
public class STM32LogicConnector implements LogicConnector {

	private SerialPort serialPort;
	private int logicIndex;
	private final static int TIMEOUT = 1200;
	private int timeOut = TIMEOUT;
	private Lock lock = new ReentrantLock();

	public STM32LogicConnector(int logicIndex, SerialPort serialPort) {

		this.serialPort = serialPort;
		this.logicIndex = logicIndex;

	}

	private boolean config(Data data, StringBuffer err) throws IOException {

		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort,
					new ConfigDecorator(data), 5000);
			return response.getResult().getCode() == Result.SUCCESS;
		} catch (IOException e) {

			System.out.println(e.getMessage());
			throw e;
		}

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
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort, new QueryDecorator(data),
					timeOut);
			if (response.getResult().getCode() == Result.SUCCESS) {

				return response.getDestData();
			} else {

				return null;
			}
		} catch (IOException e) {

			System.out.println(CommonUtil.getThrowableException(e));
			return null;
		}

	}

	private Data read(Data data, StringBuffer err) {

		ResponseDecorator response = null;
		try {
			response = SerialUtil.sendAndRecv(ProtocolType.LOGIC, serialPort, new QueryDecorator(data), timeOut);
			if (response.getResult().getCode() == Result.SUCCESS) {

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

	@Override
	public synchronized boolean configPole(LogicPoleData lpd, StringBuffer err) throws AlertException {

		try {
			if (!config(lpd, err)) {

				CommonUtil.sleep(1000);
				return config(lpd, err);
			}
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "配置逻辑板极性错误");
		}
		return true;

	}

	@Override
	public synchronized boolean configDeviceProtect(LogicDeviceProtectData ldpd, StringBuffer err)
			throws AlertException {
		try {
			return config(ldpd, err);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				return config(ldpd, err);
			} catch (IOException e1) {
				throw new AlertException(AlertCode.COMM_ERROR, "配置逻辑板" + (ldpd.getUnitIndex() + 1) + "保护值错误");
			}

		}
	}

	@Override
	public synchronized boolean configChnSwitchState(LogicChnSwitchData lcsd, StringBuffer err) throws AlertException {

		try {
			return config(lcsd, err);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				return config(lcsd, err);
			} catch (IOException e1) {

				throw new AlertException(AlertCode.COMM_ERROR, "配置逻辑板通道开关状态错误");
			}
		}

	}

	@Override
	public synchronized boolean configStartup(LogicStateData lsd, StringBuffer err) throws AlertException {
		try {
			return config(lsd, err);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				return config(lsd, err);
			} catch (IOException e1) {

				throw new AlertException(AlertCode.COMM_ERROR, "启动或关闭逻辑板" + (lsd.getUnitIndex() + 1) + "错误");
			}
		}
	}

	@Override
	public synchronized LogicPickupData pickup(int driverIndex) {

		LogicPickupData lpd = new LogicPickupData();

		if (lpd.supportUnit()) {
			lpd.setUnitIndex(logicIndex);
		}
		if (lpd.supportDriver()) {

			lpd.setDriverIndex(driverIndex);
		}

		StringBuffer err = new StringBuffer();
		Data recv = null;
		int timeOut = MainBoard.startupCfg.getLogicInfo(logicIndex).communicateTimeout;
		if ((recv = readImmediate(lpd, err, timeOut)) == null) {

			return null;
		}

		return (LogicPickupData) recv;
	}

	@Override
	public synchronized boolean stopChn(LogicChnStopData lcsd, StringBuffer err) throws AlertException {

		try {
			return config(lcsd, err);
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				return config(lcsd, err);
			} catch (IOException e1) {

				throw new AlertException(AlertCode.COMM_ERROR, "关闭逻辑板" + (lcsd.getUnitIndex() + 1) + "通道错误");
			}
		}

	}

	@Override
	public LogicCalProcessData getLogicCalProcess(LogicCalProcessData lcpd, StringBuffer err) {

		Data recv = null;

		if (MainBoard.startupCfg.getRange().use) {

			LogicNewCalProcessData lecpd = new LogicNewCalProcessData();
			lecpd.setChnIndex(lcpd.getChnIndex());
			lecpd.setUnitIndex(lcpd.getUnitIndex());
			lecpd.setDriverIndex(lcpd.getDriverIndex());
			lecpd.setPole(lcpd.getPole());
			lecpd.setPrimitiveADC(lcpd.getPrimitiveADC());
			lecpd.setPrecision(lcpd.isHighPrecision() ? 1 : 0);
			lecpd.setFinalADC(lcpd.getFinalADC());
			lecpd.setWorkMode(lcpd.getWorkMode());
			lecpd.setOrient(lcpd.getOrient());
			lecpd.setProgramI(lcpd.getProgramI());
			lecpd.setProgramV(lcpd.getProgramV());
			lecpd.setReady(lcpd.getReady());

			if ((recv = readImmediate(lecpd, err, 3000)) == null) {

				return null;
			}
			LogicNewCalProcessData response = (LogicNewCalProcessData) recv;

			lcpd = new LogicCalProcessData();
			lcpd.setChnIndex(response.getChnIndex());
			lcpd.setUnitIndex(response.getUnitIndex());
			lcpd.setDriverIndex(response.getDriverIndex());
			lcpd.setPole(response.getPole());
			lcpd.setPrimitiveADC(response.getPrimitiveADC());
			lcpd.setHighPrecision(response.getPrecision() == 1);
			lcpd.setFinalADC(response.getFinalADC());
			lcpd.setWorkMode(response.getWorkMode());
			lcpd.setOrient(response.getOrient());
			lcpd.setProgramI(response.getProgramI());
			lcpd.setProgramV(response.getProgramV());
			lcpd.setReady(response.getReady());
			lcpd.setResult(response.getResult());
			return lcpd;

		} else {

			if ((recv = readImmediate(lcpd, err, 3000)) == null) {

				return null;
			}

			return (LogicCalProcessData) recv;
		}

	}

	@Override
	public synchronized boolean setLogicCalProcess(LogicCalProcessData lcpd, StringBuffer err) throws AlertException {

		if (MainBoard.startupCfg.getRange().use) {

			LogicNewCalProcessData lecpd = new LogicNewCalProcessData();
			lecpd.setChnIndex(lcpd.getChnIndex());
			lecpd.setUnitIndex(lcpd.getUnitIndex());
			lecpd.setDriverIndex(lcpd.getDriverIndex());
			lecpd.setPole(lcpd.getPole());
			lecpd.setPrimitiveADC(lcpd.getPrimitiveADC());
			lecpd.setPrecision(lcpd.isHighPrecision() ? 1 : 0);
			lecpd.setFinalADC(lcpd.getFinalADC());
			lecpd.setWorkMode(lcpd.getWorkMode());
			lecpd.setOrient(lcpd.getOrient());
			lecpd.setProgramI(lcpd.getProgramI());
			lecpd.setProgramV(lcpd.getProgramV());
			lecpd.setReady(lcpd.getReady());

			try {
				ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort,
						new ConfigDecorator(lecpd), 1500);
				return response.getResult().getCode() == Result.SUCCESS;
			} catch (IOException e) {

				throw new AlertException(AlertCode.COMM_ERROR,
						"校准逻辑板" + (lcpd.getUnitIndex() + 1) + "通道" + (lcpd.getChnIndex() + 1) + "发生错误");

			}

		} else {

			try {
				ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort,
						new ConfigDecorator(lcpd), 1500);
				return response.getResult().getCode() == Result.SUCCESS;
			} catch (IOException e) {

				throw new AlertException(AlertCode.COMM_ERROR,
						"校准逻辑板" + (lcpd.getUnitIndex() + 1) + "通道" + (lcpd.getChnIndex() + 1) + "发生错误");

			}
		}

	}

	@Override
	public synchronized boolean writeCalFlash(LogicFlashWriteData lfwd, StringBuffer err) throws AlertException {

		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort,
					new ConfigDecorator(lfwd), 5000);
			return response.getResult().getCode() == Result.SUCCESS;
		} catch (IOException e) {

			throw new AlertException(AlertCode.COMM_ERROR, "写入逻辑板" + (lfwd.getUnitIndex() + 1) + "falsh校准系数发生错误");

		}

	}

	@Override
	public synchronized void clearBuff() {

		synchronized (serialPort) {
			try {
				SerialUtil.readMessageOnce(serialPort);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setRecvTimeout(int timeout) {

		this.timeOut = timeout;
	}

	@Override
	public synchronized LogicFlashWriteData readCalFlash(int logicIndex, int chnIndexInLogic, StringBuffer err) {

		LogicFlashWriteData lfwd = new LogicFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		lfwd.setChnIndex(chnIndexInLogic);
		return (LogicFlashWriteData) readImmediate(lfwd, err, 15000);

	}

	/**
	 * 读取逻辑板基准电压值
	 */
	@Override
	public synchronized LogicCalMatchData readBaseVoltage(int logincIndex, int chnIndexInLogic) {

		LogicCalMatchData lcmd = new LogicCalMatchData();
		lcmd.setUnitIndex(logincIndex);
		lcmd.setChnIndex(chnIndexInLogic);

		LogicCalMatchData response = (LogicCalMatchData) readImmediate(lcmd, new StringBuffer(),
				MainBoard.startupCfg.getLogicInfo(logicIndex).communicateTimeout);
		return response;

	}

	/**
	 * 设置逻辑板基准匹配电压
	 */
	@Override
	public synchronized boolean configBaseVoltage(int logicIndex, int chnIndexInLogic, Pole pole, MatchState ms)
			throws AlertException {

		LogicCalMatchData lcmd = new LogicCalMatchData();
		lcmd.setUnitIndex(logicIndex);
		lcmd.setChnIndex(chnIndexInLogic);
		lcmd.setMatchState(MatchState.MATCHED);
		lcmd.setPole(pole);

		try {
			config(lcmd, new StringBuffer());
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "设置逻辑板" + (logicIndex + 1) + "基准电压发生错误");

		}

		return true;

	}

	/**
	 * 读取逻辑板计量数据
	 * 
	 * @param logicIndex
	 * @param chnIndex
	 * @return
	 * @throws IOException
	 */
	public synchronized LogicCalculateData readCalculateData(int logicIndex, int chnIndex) throws IOException {

		LogicCalculateData lcd = new LogicCalculateData();
		lcd.setUnitIndex(logicIndex);
		lcd.setChnIndex(chnIndex);

		Data response = readImmediate(lcd, new StringBuffer(), 500);
		return (LogicCalculateData) response;

	}

	@Override
	public synchronized boolean startChn(LogicChnStartData lcsd) throws AlertException {

		try {
			return config(lcsd, new StringBuffer());
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				return config(lcsd, new StringBuffer());
			} catch (IOException e1) {
				e.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "启动通道失败!");
			}

		}

	}

	@Override
	public synchronized void stopAllChns(int logicIndex) throws AlertException {

		LogicStopData lsd = new LogicStopData();
		lsd.setUnitIndex(logicIndex);
		try {

			config(lsd, new StringBuffer());
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "关闭所有通道失败");
		}

	}

	@Override
	public synchronized void startAllChns(int logicIndex, WorkMode workMode, double voltage, double current,
			double threshold) throws AlertException {

		LogicStartupData lsd = new LogicStartupData();
		lsd.setUnitIndex(logicIndex);
		lsd.setWorkMode(workMode);
		lsd.setProgramVoltage(voltage);
		lsd.setProgramCurrent(current);
		lsd.setEndThreshold(threshold);
		try {
			config(lsd, new StringBuffer());
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "启动所有通道失败");
		}

	}

	@Override
	public synchronized boolean writeCalculateData(LogicCalculateData lcd) throws AlertException {

		try {
			return config(lcd, new StringBuffer());
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "设置逻辑板" + (lcd.getUnitIndex() + 1) + "计量点通信超时");
		}

	}

	@Override
	public LogicFaultCheckData readFaultCheckData(int logicIndex) throws AlertException {

		LogicFaultCheckData lfcd = new LogicFaultCheckData();
		lfcd.setUnitIndex(logicIndex);

		return (LogicFaultCheckData) readImmediate(lfcd, new StringBuffer(),
				MainBoard.startupCfg.getLogicInfo(logicIndex).communicateTimeout);

	}

	@Override
	public void enableModule(int chnIndexInLogic, boolean open) throws AlertException {

		LogicModuleSwitchData lmsd = new LogicModuleSwitchData();
		lmsd.setUnitIndex(logicIndex);

		lmsd.setChnIndex(chnIndexInLogic);
		lmsd.setOpen(open);

		try {
			if (!config(lmsd, new StringBuffer())) {

				throw new AlertException(AlertCode.LOGIC,
						"逻辑板" + (logicIndex + 1) + (open ? "打开" : "关闭") + "通道" + (chnIndexInLogic + 1) + "错误");
			}
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR,
					"逻辑板" + (logicIndex + 1) + (open ? "打开" : "关闭") + "通道" + (chnIndexInLogic + 1) + "失败");
		}

	}

	@Override
	public boolean configSinglePole(LogicLabPoleData llpd) throws AlertException {

		StringBuffer err = new StringBuffer();
		try {
			if (!config(llpd, err)) {

				CommonUtil.sleep(1000);
				return config(llpd, err);
			}
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "配置逻辑板通道极性通信错误:" + e.getMessage());
		}
		return true;
	}

	@Override
	public boolean configSingleProtect(LogicLabProtectData llpd) throws AlertException {

		StringBuffer err = new StringBuffer();
		try {
			if (!config(llpd, err)) {

				CommonUtil.sleep(1000);
				return config(llpd, err);
			}
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "配置逻辑板通道保护通信错误:" + e.getMessage());
		}
		return true;
	}

	@Override
	public boolean configHKLogicCalibration(LogicHKCalibrateData data, StringBuffer err) throws AlertException {

		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort,
					new ConfigDecorator(data), 1500);
			return response.getResult().getCode() == Result.SUCCESS;
		} catch (IOException e) {

			throw new AlertException(AlertCode.COMM_ERROR,
					"校准逻辑板" + (data.getUnitIndex() + 1) + "通道" + (data.getChnIndex() + 1) + "发生错误");

		}
	}

	@Override
	public LogicHKCalibrateData readLogicHKCalibration(LogicHKCalibrateData data, StringBuffer err)
			throws AlertException {

		Data recv = null;

		if ((recv = readImmediate(data, err, 3000)) == null) {

			return null;
		}

		return (LogicHKCalibrateData) recv;

	}

	@Override
	public boolean writeHKCalculation(LogicHKCalculateData lcd) throws AlertException {

		try {
			return config(lcd, new StringBuffer());
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "设置逻辑板" + (lcd.getUnitIndex() + 1) + "计量点通信超时");
		}

	}

	@Override
	public LogicHKCalculateData readHKCalculation(int logicIndex, int chnIndex) throws AlertException {
		LogicHKCalculateData lcd = new LogicHKCalculateData();
		lcd.setUnitIndex(logicIndex);
		lcd.setChnIndex(chnIndex);

		Data response = readImmediate(lcd, new StringBuffer(), 500);
		return (LogicHKCalculateData) response;

	}

	@Override
	public boolean writeHKCalFlash(LogicHKFlashWriteData lfwd, StringBuffer err) throws AlertException {
		try {
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort,
					new ConfigDecorator(lfwd), 5000);
			return response.getResult().getCode() == Result.SUCCESS;
		} catch (IOException e) {

			throw new AlertException(AlertCode.COMM_ERROR, "写入逻辑板" + (lfwd.getUnitIndex() + 1) + "falsh校准系数发生错误");

		}
	}

	@Override
	public LogicHKFlashWriteData readHKCalFlash(int logicIndex, int chnIndexInLogic, StringBuffer err) {

		LogicHKFlashWriteData lfwd = new LogicHKFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		lfwd.setChnIndex(chnIndexInLogic);
		return (LogicHKFlashWriteData) readImmediate(lfwd, err, 15000);
	}

	@Override
	public boolean operateHkChns(LogicHKOperationData lod) throws AlertException {

		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort, new ConfigDecorator(lod), 5000);

			if (response.getResult().getCode() != Result.SUCCESS) {

				throw new AlertException(AlertCode.LOGIC, "操作通道失败,错误码" + response.getResult());
			}
		} catch (IOException e) {

			CommonUtil.sleep(1000);
			try {
				response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort, new ConfigDecorator(lod),
						5000);
				if (response.getResult().getCode() != Result.SUCCESS) {

					throw new AlertException(AlertCode.LOGIC, "操作通道失败,错误码" + response.getResult());
				}
			} catch (IOException e1) {

				e1.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "操作通道超时:" + e1.getMessage());
			}

		}

		return true;
	}

	@Override
	public boolean configHKProcedureStep(WorkMode workMode, double specialVoltage, double specialCurrent,
			double threshold) throws AlertException {

		LogicHKProcedureData pd = new LogicHKProcedureData();
		pd.setUnitIndex(logicIndex);
		pd.setWorkMode(workMode);
		pd.setSpecialVoltage(specialVoltage);
		pd.setSpecialCurrent(specialCurrent);
		pd.setOvertheshold(threshold);
		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort, new ConfigDecorator(pd), 5000);

			if (response.getResult().getCode() != Result.SUCCESS) {

				throw new AlertException(AlertCode.LOGIC, "下发流程失败,错误码" + response.getResult());
			}
		} catch (IOException e) {

			e.printStackTrace();
			CommonUtil.sleep(1000);
			try {
				response = SerialUtil.sendAndRecvImmediate(ProtocolType.LOGIC, serialPort, new ConfigDecorator(pd),
						5000);

				if (response.getResult().getCode() != Result.SUCCESS) {

					throw new AlertException(AlertCode.LOGIC, "下发流程失败,错误码" + response.getResult());
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

				throw new AlertException(AlertCode.LOGIC, "下发流程失败通信超时");
			}

		}

		return true;
	}

}
