package com.nlteck.connector;

import java.io.IOException;
import java.util.List;

import com.nlteck.base.I18N;
import com.nlteck.fireware.CalibrateCore;
import com.nltecklib.io.serialport.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.cal.CalEnvironment.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.CalUpdateFileData;
import com.nltecklib.protocol.li.cal.CalUpdateModeData;
import com.nltecklib.protocol.li.cal.Calculate2Data;
import com.nltecklib.protocol.li.cal.Calibrate2Data;
import com.nltecklib.protocol.li.cal.ExCalResisterFactorData;
import com.nltecklib.protocol.li.cal.OverTempAlertData;
import com.nltecklib.protocol.li.cal.RelayControlData;
import com.nltecklib.protocol.li.cal.RelayControlExData;
import com.nltecklib.protocol.li.cal.ResistanceModeData;
import com.nltecklib.protocol.li.cal.ResistanceModeRelayData;
import com.nltecklib.protocol.li.cal.TempControlData;
import com.nltecklib.protocol.li.cal.TemperatureData;
import com.nltecklib.protocol.li.cal.TestPatternData;
import com.nltecklib.protocol.li.cal.VoltageBaseData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.utils.LogUtil;
import com.rm5248.serial.SerialPort;

/**
 * 校准板通信
 * 
 * @author guofang_ma
 *
 */
public class CalBoardConnector {

	public static final int TIMEOUT = 3000;
	private CalibrateCore core;
	private int boardIndex;
	private SerialPort serialPort;

	public CalBoardConnector(CalibrateCore core, int boardIndex, SerialPort serialPort) {
		this.core = core;
		this.boardIndex = boardIndex;
		this.serialPort = serialPort;

	}

	/**
	 * 配置工作模式
	 * 
	 * @param chnIndex
	 * @param workState
	 * @param workMode
	 * @param precision
	 * @param pole
	 * @param programV
	 * @param programI
	 * @param workMode  @
	 */
	public void cfgCalibrate2(int chnIndex, WorkState workState, WorkMode workMode, int precision, Pole pole,
			int programV, int programI) {

		Calibrate2Data data = new Calibrate2Data();
		data.setDriverIndex(0); //默认使用0地址
		data.setChnIndex(chnIndex);
		data.setWorkState(workState);
		data.setWorkMode(workMode);
		data.setPrecision(precision);
		data.setPole(pole);
		data.setProgramV(programV);
		data.setProgramI(programI);

		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (Calibrate2Data) result.getDestData();
	}
	
	
	public void cfgTestMode(int chnIndex , TestType type) {
		
		TestPatternData  tpd = new TestPatternData();
		tpd.setDriverIndex(boardIndex);
		tpd.setChnIndex(chnIndex);
		tpd.setTestType(type);
		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(tpd), TIMEOUT);
		CheckResult(result);
		
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
				core.getLogger().error("calboard connect error:" + e1.getMessage(), e1);
				throw new RuntimeException(I18N.getVal(I18N.DeviceCommunicationTimeOut,
						I18N.getVal(decorator.getDestData().getClass().getSimpleName())));
			}
		} catch (Exception e) {
			core.getLogger().error("calboard connect error:" + e.getMessage(), e);
			throw new RuntimeException(I18N.getVal(I18N.DeviceCommunicationTimeOut,
					I18N.getVal(decorator.getDestData().getClass().getSimpleName())));
//					"[" ++ "]" + e.getMessage(), e);
		}
		return response;
	}

	/**
	 * 查询工作模式
	 * 
	 * @param chnIndex @
	 */
	public Calibrate2Data qryCalibrate2(int chnIndex) {
		Calibrate2Data data = new Calibrate2Data();
		data.setDriverIndex(boardIndex);
		data.setChnIndex(chnIndex);

//		core.getNetworkService().pushDebugLog(new DebugLog(data.toString()));

		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (Calibrate2Data) result.getDestData();
		return data;
	}

	/**
	 * 设置识别模式
	 * 
	 * @param chnIndex
	 * @param workState
	 * @param voltBase  @
	 */
	public void cfgVoltageBase(int chnIndex, WorkState workState, double voltBase) {
		VoltageBaseData data = new VoltageBaseData();
		data.setDriverIndex(boardIndex);
		data.setChnIndex(chnIndex);
		data.setWorkState(workState);
		data.setVoltBase(voltBase);

		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (VoltageBaseData) result.getDestData();
	}

	/**
	 * 查询识别模式
	 * 
	 * @param chnIndex @
	 */
	public VoltageBaseData qryVoltageBase(int chnIndex) {
		VoltageBaseData data = new VoltageBaseData();
		data.setDriverIndex(boardIndex);
		data.setChnIndex(chnIndex);

		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (VoltageBaseData) result.getDestData();
		return data;
	}

	/**
	 * 配置万用表连接
	 * 
	 * @param connected @
	 */
	public void cfgRelayControl( boolean connected) {
		RelayControlData data = new RelayControlData();
		data.setDriverIndex(0 /*boardIndex*/);
		data.setConnected(connected);
		
		System.out.println(data.getDriverIndex() + ":" + data.isConnected());
//		core.getNetworkService().pushDebugLog(new DebugLog(data.toString()));
		
		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (RelayControlData) result.getDestData();
	}
	/**
	 * 配置万用表继电器连接
	 * 
	 * @param connected @
	 */
	public void cfgRelayControl2(int index, boolean connected) {
		RelayControlExData data = new RelayControlExData();
		data.setDriverIndex(0 /*boardIndex*/);
		data.setRelayIndex((byte) index);
		data.setConnected(connected);
        
		System.out.println(data.getDriverIndex() + ":" + data.isConnected());
//		core.getNetworkService().pushDebugLog(new DebugLog(data.toString()));

		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (RelayControlExData) result.getDestData();
	}

	/**
	 * 查询万用表连接
	 * 
	 * @return @
	 */
	public RelayControlData qryRelayControl() {
		RelayControlData data = new RelayControlData();
		data.setDriverIndex(/*boardIndex*/ 0);

//		core.getNetworkService().pushDebugLog(new DebugLog(data.toString()));

		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (RelayControlData) result.getDestData();
		return data;
	}
	
	/**
	 * 查询校准板恒温情况
	 * @author  wavy_zheng
	 * 2022年2月13日
	 * @return
	 */
	public  OverTempAlertData qryOverTempAlertData() {
		OverTempAlertData data = new OverTempAlertData();
		data.setDriverIndex(/*boardIndex*/ 0);

		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (OverTempAlertData) result.getDestData();
		return data;
	}

	/**
	 * 查询校准板温度
	 * 
	 * @return @
	 */
	public TemperatureData qryTemperature() {
		TemperatureData data = new TemperatureData();
		data.setDriverIndex(/*boardIndex*/ 0);

//		core.getNetworkService().pushDebugLog(new DebugLog(data.toString()));

		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (TemperatureData) result.getDestData();
		return data;
	}
	
	public ResistanceModeData  qryNewCalResisterFactor(WorkPattern wm , int range) {
		ResistanceModeData data = new ResistanceModeData();
		data.setDriverIndex(/*boardIndex*/ 0);
		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data , (byte)wm.ordinal() , (byte)range), TIMEOUT);
		CheckResult(result);
		data = (ResistanceModeData) result.getDestData();
		return data;
	}
	
	
	public ResistanceModeRelayData  qryNewCalResisterFactor2(int relayIndex, WorkPattern wm , int range) {
		ResistanceModeRelayData data = new ResistanceModeRelayData();
		data.setDriverIndex(/*boardIndex*/ 0);
		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data ,(byte) relayIndex, (byte)wm.ordinal() , (byte)range), TIMEOUT);
		CheckResult(result);
		data = (ResistanceModeRelayData) result.getDestData();
		return data;
	}
	

	/**
	 * 查询增强校准系数
	 * 
	 * @param level
	 * 
	 * @return @
	 */
	public ExCalResisterFactorData qryExCalResisterFactor(int level) {
		ExCalResisterFactorData data = new ExCalResisterFactorData();
		data.setDriverIndex(boardIndex);
		data.setRangeLevel(level);

//		core.getNetworkService().pushDebugLog(new DebugLog(data.toString()));

		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (ExCalResisterFactorData) result.getDestData();
		return data;
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

	/**
	 * 
	 * @param index
	 * @return
	 */

	public void cfgCalculate2(int chnIndex, WorkState workState, WorkMode workMode,
			com.nltecklib.protocol.li.cal.CalEnvironment.Pole pole, double calculateDot, int precision) {
		Calculate2Data data = new Calculate2Data();
		data.setDriverIndex(/*boardIndex*/ 0); //默认使用0地址
		data.setChnIndex(chnIndex);
		data.setWorkState(workState);
		data.setWorkMode(workMode);
		data.setPole(pole);
		data.setCalculateDot(calculateDot);
		data.setPrecision(precision);

		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (Calculate2Data) result.getDestData();
	}

	/**
	 * 升级模式
	 * 
	 * @param driverIndex
	 * @param updateMode  @
	 */
	public void cfgUpdateMode(boolean updateMode) {
		CalUpdateModeData data = new CalUpdateModeData();
		data.setDriverIndex(boardIndex);
		data.setUpdateMode(updateMode);
		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (CalUpdateModeData) result.getDestData();

	}
	
	public void cfgNewResistance(ResistanceModeData resistance) {
		
		resistance.setDriverIndex(0);
		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(resistance), TIMEOUT);
		CheckResult(result);
		
	}
	public void cfgRelayResistance(ResistanceModeRelayData resistance) {
		
		resistance.setDriverIndex(0);
		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(resistance), TIMEOUT);
		CheckResult(result);
		
	}
	
	
	public void cfgTempControl(double temp, boolean open) {
		
		TempControlData data = new TempControlData();
	    data.setTemperature((int)temp);
	    data.setDriverIndex(boardIndex);
	    data.setHotTime(60);
	    data.setLowTemp(17);
	    data.setUpTemp(80);
	    data.setUpTemper(80);
	    data.setOpen(open);
	    
	
		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), TIMEOUT);
		CheckResult(result);

	}
	
	
	

	/**
	 * 查询是否升级模式
	 * 
	 * @param driverIndex
	 * @return @
	 */
	public CalUpdateModeData qryUpdateMode() {
		CalUpdateModeData data = new CalUpdateModeData();
		data.setDriverIndex(boardIndex);
		ResponseDecorator result = sendAndRecvImmediate(new QueryDecorator(data), TIMEOUT);
		CheckResult(result);
		data = (CalUpdateModeData) result.getDestData();
		return data;
	}

	/**
	 * 下发升级文件
	 * 
	 * @param fileSize
	 * @param packCount
	 * @param packIndex
	 * @param packContent
	 */
	public void cfgUpdateFile(int fileSize, int packCount, int packIndex, List<Byte> packContent) {
		CalUpdateFileData data = new CalUpdateFileData();
		data.setDriverIndex(boardIndex);
		data.setFileSize(fileSize);
		data.setPackCount(packCount);
		data.setPackIndex(packIndex);
		data.setPackContent(packContent);
		ResponseDecorator result = sendAndRecvImmediate(new ConfigDecorator(data), 10000);
		CheckResult(result);
		data = (CalUpdateFileData) result.getDestData();
	}

	

}
