package com.nlteck.service;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Environment;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.li.accessory.FanControlData;
import com.nltecklib.protocol.li.accessory.FanStateQueryData;
import com.nltecklib.protocol.li.accessory.HeartBeatData;
import com.nltecklib.protocol.li.accessory.PoleLightData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData;
import com.nltecklib.protocol.li.accessory.PowerSwitchData;
import com.nltecklib.protocol.li.cal.CalEnvironment.Pole;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.CalResisterFactorData;
import com.nltecklib.protocol.li.cal.CalibrateData;
import com.nltecklib.protocol.li.cal.ExCalResisterFactorData;
import com.nltecklib.protocol.li.cal.NewCalibrateData;
import com.nltecklib.protocol.li.cal.RelayControlData;
import com.nltecklib.protocol.li.cal.TemperatureData;
import com.nltecklib.protocol.li.cal.VoltageBaseData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.workform.CalBaseVoltageData;
import com.rm5248.serial.SerialPort;

/**
 * STM控制板控制器
 * 
 * @author Administrator
 *
 */
public class STMDeviceController {

	private SerialPort serialPort;
	private final static int TIME_OUT = 1200;

	private String lightCtrlCode = "01"; // 0表示灯灭1s，1表示绿灯亮1s ， 2 表示黄灯亮1s
	private Timer timer;
	private int lampIndex = 0;

	public STMDeviceController(SerialPort port) {

		this.serialPort = port;

		// 心跳链接
		if (MainBoard.startupCfg.getControlInfo().heartbeat) {
			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {

						heartBeat();
					} catch (Exception ex) {

						ex.printStackTrace();
						Environment.infoLogger.info(CommonUtil.getThrowableException(ex));
					}
				}

			}, 5, 10, TimeUnit.SECONDS);
		}
	}

	public synchronized boolean power(int index, PowerState state) throws AlertException {

		return power(index, state, TIME_OUT);
	}

	/**
	 * 打开或关闭充放电源
	 * 
	 * @param index 当前只有1组；不支持该功能
	 * @param state
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean power(int index, PowerState state, int timeOut) throws AlertException {

		PowerSwitchData cpd = new PowerSwitchData();
		cpd.setUnitIndex(index);
		cpd.setPowerType(PowerType.CHARGE);
		cpd.setState(state);

		try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cpd), timeOut);
		} catch (IOException ex) {

			throw new AlertException(AlertCode.COMM_ERROR, "启动或关闭电源通信失败");

		}

		return true;
	}

	public synchronized PowerSwitchData getPowerState(int index) throws AlertException {

		return getPowerState(index, TIME_OUT);

	}

	public synchronized PowerSwitchData getPowerState(int index, int timeOut) throws AlertException {

		PowerSwitchData cpd = new PowerSwitchData();
		cpd.setUnitIndex(index);
		cpd.setPowerType(PowerType.CHARGE);

		ResponseDecorator response;
		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new QueryDecorator(cpd),
					timeOut);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, "获取总开关状态失败");
		}

		return (PowerSwitchData) response.getDestData();

	}

	/**
	 * 风扇控制
	 * 
	 * @param index     风扇组号，暂时不支持该功能；
	 * @param direction
	 * @param state
	 * @param grade     风速档位
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean fan(int index, Direction direction, PowerState state, int grade) throws AlertException {

		FanControlData cfd = new FanControlData();
		cfd.setUnitIndex(index);
		cfd.setGrade(grade);
		cfd.setDirection(direction);
		cfd.setPowerState(state);

		try {
			SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cfd), TIME_OUT);
		} catch (IOException ex) {

			throw new AlertException(AlertCode.COMM_ERROR, "控制风扇通信失败");
		}
		return true;
	}

	/**
	 * 控制极性灯
	 * 
	 * @param index
	 * @param color
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean lightPole(int index, byte colorFlag, short lightFlag) throws AlertException {

		PoleLightData pld = new PoleLightData();
		pld.setUnitIndex(index);
		pld.setColorFlag(colorFlag);
		pld.setLightFlag(lightFlag);
		try {
			SerialUtil.sendAndRecv(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(pld), TIME_OUT);
		} catch (IOException ex) {

			throw new AlertException(AlertCode.COMM_ERROR, "控制极性灯通信失败");
		}
		return true;
	}

	/**
	 * 设置校准板基准电压
	 * 
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean setCalBoardBaseVoltage(int boardIndex, int chnIndexInBoard, WorkState ws,
			double baseVolt) throws IOException {

		VoltageBaseData vbd = new VoltageBaseData();
		vbd.setDriverIndex(boardIndex);
		vbd.setChnIndex(chnIndexInBoard);
		vbd.setWorkState(ws);
		vbd.setVoltBase(baseVolt);
		SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort, new ConfigDecorator(vbd), TIME_OUT);

		return true;

	}

	/**
	 * 获取校准板上的通道基准电压值
	 * 
	 * @param boardIndex
	 * @param chnIndexInBoard
	 * @return
	 * @throws IOException
	 */
	public synchronized CalBaseVoltageData getCalBoardBaseVoltage(int boardIndex, int chnIndexInBoard)
			throws IOException {

		VoltageBaseData vbd = new VoltageBaseData();
		vbd.setDriverIndex(boardIndex);
		vbd.setChnIndex(chnIndexInBoard);

		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort,
				new QueryDecorator(vbd), TIME_OUT);
		vbd = (VoltageBaseData) response.getDestData();
		CalBaseVoltageData cbvd = new CalBaseVoltageData();
		cbvd.setUnitIndex(vbd.getDriverIndex());
		cbvd.setChnIndex(vbd.getChnIndex());
		cbvd.setWorkState(vbd.getWorkState());
		cbvd.setVoltBase(vbd.getVoltBase());

		return cbvd;

	}

	/**
	 * 设置校准板工作状态
	 * 
	 * @param driverIndex
	 * @param chnIndex
	 * @param pole
	 * @param workMode
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public synchronized void setCalBoardState(int driverIndex, int chnIndex, Pole pole, WorkMode workMode, WorkState ws,
			int rangeLevel) throws IOException {

		if (!MainBoard.startupCfg.getRange().use) {

			CalibrateData cd = new CalibrateData();
			cd.setDriverIndex(driverIndex);
			cd.setChnIndex(chnIndex);
			cd.setWorkMode(workMode);
			cd.setPole(pole);
			cd.setWorkState(ws);
			cd.setReadyState((byte) 0);
			cd.setPrecision(rangeLevel);

			SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort, new ConfigDecorator(cd), TIME_OUT);

		} else {
			NewCalibrateData ecd = new NewCalibrateData();
			// 多量程
			ecd.setDriverIndex(driverIndex);
			ecd.setChnIndex(chnIndex);
			ecd.setWorkMode(workMode);
			ecd.setPole(pole);
			ecd.setWorkState(ws);
			ecd.setReadyState((byte) 0);
			ecd.setPrecision(rangeLevel);

			SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort, new ConfigDecorator(ecd), TIME_OUT);

		}

	}

	/**
	 * 读取校准板状态
	 * 
	 * @param driverIndex
	 * @param chnIndex
	 * @return
	 * @throws IOException
	 */
	public synchronized CalibrateData getCalBoardState(int driverIndex, int chnIndex) throws IOException {

		if (!MainBoard.startupCfg.getRange().use) {
			CalibrateData cd = new CalibrateData();
			cd.setDriverIndex(driverIndex);
			cd.setChnIndex(chnIndex);

			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort,
					new QueryDecorator(cd), TIME_OUT);
			return (CalibrateData) response.getDestData();
		} else {

			NewCalibrateData ecd = new NewCalibrateData();
			ecd.setDriverIndex(driverIndex);
			ecd.setChnIndex(chnIndex);
			ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort,
					new QueryDecorator(ecd), TIME_OUT);
			ecd = (NewCalibrateData) response.getDestData();
			CalibrateData cd = new CalibrateData();

			cd.setChnIndex(ecd.getChnIndex());
			cd.setDriverIndex(ecd.getDriverIndex());
			cd.setUnitIndex(ecd.getUnitIndex());
			cd.setPole(ecd.getPole());
			cd.setReadyState(ecd.getReadyState());
			cd.setPrecision(ecd.getPrecision());
			cd.setWorkMode(ecd.getWorkMode());
			cd.setResult(ecd.getResult());
			cd.setWorkState(ecd.getWorkState());
			cd.setErrCode(ecd.getErrCode());
			return cd;

		}

	}

	public synchronized boolean switchMeter(int boardIndex, boolean connected) throws IOException {

		RelayControlData rcd = new RelayControlData();
		rcd.setDriverIndex(boardIndex);
		rcd.setConnected(connected);

		SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort, new ConfigDecorator(rcd), TIME_OUT);
		return true;

	}

	public synchronized RelayControlData readSwitchMeter(int boardIndex) throws IOException {

		RelayControlData rcd = new RelayControlData();
		rcd.setDriverIndex(boardIndex);

		ResponseDecorator decorator = SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort,
				new QueryDecorator(rcd), TIME_OUT);
		return (RelayControlData) decorator.getDestData();

	}

	public synchronized Data readCalBoardTemperature(int boardIndex) throws IOException {

		TemperatureData td = new TemperatureData();
		td.setDriverIndex(boardIndex);

		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort,
				new QueryDecorator(td), TIME_OUT);

		return response.getDestData();

	}

	/**
	 * 增强型读取校准板电阻系数(多量程)
	 * 
	 * @author wavy_zheng 2020年8月21日
	 * @param boardIndex
	 * @return
	 * @throws IOException
	 */
	public synchronized Data readCalResisterFactorEx(int boardIndex, int rangeLevel) throws IOException {

		ExCalResisterFactorData crfd = new ExCalResisterFactorData();
		crfd.setDriverIndex(boardIndex);
		crfd.setRangeLevel(rangeLevel);

		Logger logger = null;
		try {
			logger = LogUtil.createLog("log/register.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(crfd);

		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort,
				new QueryDecorator(crfd), TIME_OUT);

		logger.info(response.getDestData());

		return response.getDestData();

	}

	/**
	 * 读取校准板电阻系数
	 * 
	 * @param boardIndex
	 * @return
	 * @throws IOException
	 */
	public synchronized Data readCalResisterFactor(int boardIndex) throws IOException {

		CalResisterFactorData crfd = new CalResisterFactorData();
		crfd.setDriverIndex(boardIndex);

		Logger logger = null;
		try {
			logger = LogUtil.createLog("log/register.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(crfd);

		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.CAL, serialPort,
				new QueryDecorator(crfd), TIME_OUT);

		logger.info(response.getDestData());

		return response.getDestData();

	}

	/**
	 * 读取电源监控状态数据
	 * 
	 * @return
	 * @throws IOException
	 */
	public synchronized PowerStateQueryData readPowersState() throws IOException {

		PowerStateQueryData psqd = new PowerStateQueryData();
		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort,
				new QueryDecorator(psqd), TIME_OUT);

		return (PowerStateQueryData) response.getDestData();

	}

	public synchronized FanStateQueryData readFansState() throws IOException {

		FanStateQueryData fsqd = new FanStateQueryData();
		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort,
				new QueryDecorator(fsqd), TIME_OUT);

		return (FanStateQueryData) response.getDestData();
	}

	public HeartBeatData heartBeat() throws IOException {

		HeartBeatData hbd = new HeartBeatData();
		ResponseDecorator response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort,
				new QueryDecorator(hbd), TIME_OUT);
		return (HeartBeatData) response.getDestData();

	}

}
