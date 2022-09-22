package com.nlteck.fireware;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.io.DotTerminatedMessageReader;

import com.nlteck.model.CalBoardChannel;
import com.nltecklib.protocol.li.cal.CalEnvironment.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.CalUpdateModeData;
import com.nltecklib.protocol.li.cal.OverTempAlertData;
import com.nltecklib.protocol.li.cal.ResistanceModeData;
import com.nltecklib.protocol.li.cal.ResistanceModeRelayData;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * ÐéÄâÐ£×¼°å
 * 
 * @author guofang_ma
 *
 */
public class VirtualCalBoard extends CalBoard {

	private boolean relayConnected;
	private CalBoardChannel currentCalBoardChannel;

	public VirtualCalBoard(CalibrateCore core, int index) {
		super(core, index);
		// TODO Auto-generated constructor stub
	}

	public CalBoardChannel getCurrentCalBoardChannel() {
		return currentCalBoardChannel;
	}

	public void setCurrentCalBoardChannel(CalBoardChannel currentCalBoardChannel) {
		this.currentCalBoardChannel = currentCalBoardChannel;
	}

	@Override
	public void cfgVoltBase(int chnIndex, WorkState workState, double voltBase) {
		core.getVirtualService().cfgVoltBase(index, chnIndex, workState, voltBase);
	}

	@Override
	public void cfgCalibrate2(int chnIndex, WorkState workState, WorkMode workMode, int precision, Pole pole,
			int programV, int programI) {
	}

	@Override
	public void cfgRelayControl(boolean connected) {
		this.relayConnected = connected;
	}

	@Override
	public boolean qryRelayControl() {
		return relayConnected;
	}

	public boolean isRelayConnected() {
		return relayConnected;
	}

	@Override
	public double qryTemperature() {
		// TODO Auto-generated method stub
		return 42;
	}

	@Override
	public double qryExCalResisterFactor(int level) {
		return 1;
	}

	@Override
	public void cfgCalculate2(int chnIndex, WorkState workState, WorkMode workMode,
			com.nltecklib.protocol.li.cal.CalEnvironment.Pole pole, double calculateDot, int precision) {

	}

	@Override
	public void cfgUpdateMode(boolean updateMode) {
		// TODO Auto-generated method stub

	}

	@Override
	public CalUpdateModeData qryUpdateMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cfgUpdateFile(int fileSize, int packCount, int packIndex, List<Byte> packContent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeMeterRelay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public ResistanceModeData qryResistanceModeData(WorkPattern wp, int range) {
		// TODO Auto-generated method stub
		ResistanceModeData resistanceModeData=new ResistanceModeData();
		resistanceModeData.setDriverIndex(0);
		resistanceModeData.setResistance(1);
		return resistanceModeData;
	}

	@Override
	public void cfgTempControlData(double temp, boolean open) {
		
		
		
	}

	@Override
	public void cfgResistanceModeData(ResistanceModeData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OverTempAlertData qryOverTempatureData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cfgTestMode(int chnIndex, TestType type) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public ResistanceModeRelayData qryResistanceModeData(int resistanceIndex, WorkPattern wp, int range) {
		// TODO Auto-generated method stub
		ResistanceModeRelayData resistanceModeData=new ResistanceModeRelayData();
		resistanceModeData.setResistance(1);
		return resistanceModeData;
	}



	@Override
	public void cfgRelayControl2(int chnIndex, boolean connected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cfgRelayResistanceData(ResistanceModeRelayData data) {
		// TODO Auto-generated method stub
		
	}

	

}
