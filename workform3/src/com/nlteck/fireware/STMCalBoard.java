package com.nlteck.fireware;

import java.util.List;

import com.nlteck.connector.CalBoardConnector;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.cal.CalEnvironment;
import com.nltecklib.protocol.li.cal.CalEnvironment.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.CalUpdateModeData;
import com.nltecklib.protocol.li.cal.Calibrate2Data;
import com.nltecklib.protocol.li.cal.OverTempAlertData;
import com.nltecklib.protocol.li.cal.ResistanceModeData;
import com.nltecklib.protocol.li.cal.ResistanceModeRelayData;
import com.nltecklib.protocol.li.cal.TempControlData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.rm5248.serial.SerialPort;

/**
 * 实体校准板
 * 
 * @author guofang_ma
 *
 */
public class STMCalBoard extends CalBoard {

	private CalBoardConnector calBoardConnector;

	public STMCalBoard(CalibrateCore core, int index, SerialPort serialPort) {
		super(core, index);
		calBoardConnector = new CalBoardConnector(core, index, serialPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void cfgVoltBase(int chnIndex, WorkState workState, double voltBase) {
		// TODO Auto-generated method stub
		calBoardConnector.cfgVoltageBase(chnIndex, workState, voltBase);
	}

	@Override
	public void cfgCalibrate2(int chnIndex, WorkState workState, WorkMode workMode, int precision, Pole pole,
			int programV, int programI) {
		calBoardConnector.cfgCalibrate2(chnIndex, workState, workMode, precision, pole, programV, programI);
	}

	@Override
	public void cfgRelayControl(boolean connected) {
		calBoardConnector.cfgRelayControl(connected);
	}
	@Override
	public void cfgRelayControl2(int relayIndex, boolean connected) {
		calBoardConnector.cfgRelayControl2(relayIndex, connected);
	}

	@Override
	public boolean qryRelayControl() {
		return calBoardConnector.qryRelayControl().isConnected();
	}

	@Override
	public double qryTemperature() {
		return calBoardConnector.qryTemperature().getTemperature();
	}

	@Override
	public double qryExCalResisterFactor(int level) {
		return calBoardConnector.qryExCalResisterFactor(level).getResisterFactor();
	}

	@Override
	public void cfgCalculate2(int chnIndex, WorkState workState, WorkMode workMode, CalEnvironment.Pole pole,
			double calculateDot, int precision) {
		calBoardConnector.cfgCalculate2(chnIndex, workState, workMode, pole, calculateDot, precision);
	}

	@Override
	public void cfgUpdateMode(boolean updateMode) {
		calBoardConnector.cfgUpdateMode(updateMode);
	}

	@Override
	public CalUpdateModeData qryUpdateMode() {
		return calBoardConnector.qryUpdateMode();
	}

	@Override
	public void cfgUpdateFile(int fileSize, int packCount, int packIndex, List<Byte> packContent) {
		calBoardConnector.cfgUpdateFile(fileSize, packCount, packIndex, packContent);
	}

	/**
	 * 关闭当前万用表
	 */
	@Override
	public void closeMeterRelay() {
		if (core.getMeterParamMap().get(meter).lastCalIndex == index) {
			try {
				cfgRelayControl(false);
				core.getMeterParamMap().get(meter).lastCalIndex = -1;
			} catch (Exception e) {
				core.getLogger().error("close calboard " + index + " meter relay error:" + e.getMessage(), e);
			}
		}
	}

	@Override
	public void init() {
		try {
			
			if(!CalibrateCore.getBaseCfg().readMeasureMeter.enable) {
				
				cfgRelayControl(false);
			}else {
				
				cfgRelayControl2(0,false);
			}
			core.getMeterParamMap().get(meter).lastCalIndex = -1;
		} catch (Exception e) {
			core.getLogger().error("close calboard " + index + " meter relay error:" + e.getMessage(), e);
		}
	}

	@Override
	public ResistanceModeData qryResistanceModeData( WorkPattern wp, int range) {
		
		return calBoardConnector.qryNewCalResisterFactor(wp, range);
	}

	@Override
	public void cfgTempControlData(double temp , boolean open) {
		
		calBoardConnector.cfgTempControl(temp,open);
		
	}

	@Override
	public void cfgResistanceModeData(ResistanceModeData data) {
	
		calBoardConnector.cfgNewResistance(data);
	}

	@Override
	public OverTempAlertData qryOverTempatureData() {
		
		return calBoardConnector.qryOverTempAlertData();
	}

	@Override
	public void cfgTestMode(int chnIndex , TestType type) {
		
		calBoardConnector.cfgTestMode(chnIndex , type);
		
	}

	@Override
	public ResistanceModeRelayData qryResistanceModeData(int meterRelayIndex, WorkPattern wp, int range) {
		return calBoardConnector.qryNewCalResisterFactor2(meterRelayIndex, wp, range);
	}

	@Override
	public void cfgRelayResistanceData(ResistanceModeRelayData data) {
		calBoardConnector.cfgRelayResistance(data);
	}
}
