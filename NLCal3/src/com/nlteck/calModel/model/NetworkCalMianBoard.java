package com.nlteck.calModel.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nlteck.calModel.base.BaseCfgManager;
import com.nlteck.calModel.base.CalCfgManager;
import com.nlteck.calModel.base.I18N;
import com.nlteck.calModel.base.BaseCfgManager.CommType;
import com.nlteck.calModel.base.BaseCfgManager.IOParam;
import com.nlteck.calModel.base.DelayConfig.DetailConfig;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.TestDot;
import com.nlteck.service.CalboxService;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nltecklib.device.KeySight34461A;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.PCWorkform.CalCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.ReadMeterData;
import com.nltecklib.protocol.li.PCWorkform.RelayControlExDebugData;
import com.nltecklib.protocol.li.PCWorkform.ResistanceModeRelayDebugData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;

public class NetworkCalMianBoard extends ABSCalMainBoard{
	
	public CalboxService calboxService=new CalboxService();
	CalCfgManager calCfgManager = new CalCfgManager();
	BaseCfgManager baseCfgManager = new BaseCfgManager();
	public Meter meter;
	public NetworkCalMianBoard(CalBox calBox) {
		meter = new KeySight34461A(0);
		meter.setIpAddress(calBox.getMeterIp());
		try {
			meter.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() throws Exception {
		if(meter.isConnected()) {
			meter.disconnect();
		}
	}
	
	public static class MeterParam {
		public int lastCalIndex = -1;// 上一个校准板，-1表示空
	}
	@Override
	public void changeModel(List<CalBox> calBoxs, CalibrateCoreWorkMode mode) throws Exception {
		for(CalBox calBox:calBoxs) {
			calboxService.changeWorkMode(calBox, mode);
		}
	}

	@Override
	public void switchModule(List<CalBox> calBoxs,int channelIndex, boolean isModeChange) throws Exception {
		for(CalBox calBox:calBoxs) {
			calboxService.cfgModuleSwitch(calBox, channelIndex, isModeChange);
		}
	}


	@Override
	public void sendLogicDot(TestDot dot) throws Exception {
		DriverEnvironment.CalMode mode =  dot.getMode() ;
		long programV = 0;
		long programI = 0;

		switch (mode) {
		case CC:
		case DC:
			programV = calCfgManager.calibratePlanData.getMaxProgramV();
			programI = (long) dot.getProgramVal();
			break;
		case CV:
			programV = (long) dot.getProgramVal();
			programI = calCfgManager.calibratePlanData.getMaxProgramI();
			break;
		default:
			break;
		}
		LogicCalibrate2DebugData send=new LogicCalibrate2DebugData();
		send.setUnitIndex(0);
		send.setChnIndex(dot.getChannelDO().getDeviceChnIndex());
		send.setWorkMode(dot.getMode());
		send.setPole(dot.getPole());
		send.setPrecision(dot.getPrecision());
		send.setProgramV(programV);
		send.setProgramI(programI);
		send.setModuleIndex(dot.moduleIndex);
		
		List<DriverCalibrateData.AdcData> adcs = new ArrayList<>();
		for (int i = 0; i < calCfgManager.steadyCfgData.getSampleCount(); i++) {
			adcs.add(new DriverCalibrateData.AdcData());
		}
		send.setAdcs(adcs);
		for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
			calboxService.cfgCalibrate(calBox,send);
		}
		
	}


	@Override
	public void sendCalibrateDot(TestDot dot, boolean open) throws Exception {
		CalMode tempMode = open?CalMode.values()[dot.getMode().ordinal()]:CalMode.SLEEP;

		int programV = 0;
		int programI = 0;

		switch (tempMode) {
		case CC:
		case DC:
			programV = (int) calCfgManager.calibratePlanData.getMaxProgramV();
			programI = (int) dot.getProgramVal();
			break;
		case CV:
			programV = (int) dot.getProgramVal();
			programI = (int) calCfgManager.calibratePlanData.getMaxProgramI();
			break;
		default:
			break;
		}

		WorkMode mode = null;
		switch (tempMode) {
		case CC:
			mode = WorkMode.CC;
			break;
		case CV:
			mode = WorkMode.CV;
			break;
		case DC:
			mode = WorkMode.DC;
			break;
		case SLEEP:
			mode = WorkMode.SLEEP;
			break;
		}

		CalCalibrate2DebugData sendData = new CalCalibrate2DebugData();
		// 
//		sendData.setUnitIndex(0);
		sendData.setDriverIndex(0);
		sendData.setChnIndex(dot.getChannelDO().getDeviceChnIndex());
		sendData.setPrecision(dot.getPrecision());
		sendData.setProgramI(programI);
		sendData.setProgramV(programV);
		sendData.setWorkMode(mode);
		sendData.setWorkState(WorkState.WORK);
		com.nltecklib.protocol.li.main.PoleData.Pole pole = com.nltecklib.protocol.li.main.PoleData.Pole.values()[dot
				.getPole().ordinal()];
		sendData.setPole(pole);
		
		for(CalBox calbox:dot.getChannelDO().getDevice().getCalBoxList()) {
			calboxService.cfgCalboardCalibrate(calbox, sendData);
		}
		
	}

	@Override
	public void sendCalMeasure(TestDot dot) throws Exception {
		// TODO Auto-generated method stub
		
		WorkMode workMode = null;
		switch(dot.getMode()) {
		case CC:
			workMode=WorkMode.CC;
			break;
		case CV:
			workMode=WorkMode.CV;
			break;
		case DC:
			workMode=WorkMode.DC;
			break;
		case SLEEP:
			workMode=WorkMode.SLEEP;
			break;
		}
		com.nltecklib.protocol.li.main.PoleData.Pole pole = null;
		switch(dot.getPole()) {
		case POSITIVE:
			pole=com.nltecklib.protocol.li.main.PoleData.Pole.NORMAL;
			break;
		case NEGTIVE:
			pole=com.nltecklib.protocol.li.main.PoleData.Pole.REVERSE;
			break;
		default:
			break;
	
		}
		CalCalculate2DebugData measure = new CalCalculate2DebugData();
//		measure.setDriverIndex(dot.getChannelDO().getDriverIndex());
		measure.setDriverIndex(0);
		measure.setChnIndex(dot.getChannelDO().getChnIndex());
//		measure.setChnIndex(dot.getChannelDO().getDeviceChnIndex());
		
		measure.setWorkState(WorkState.WORK);
		measure.setWorkMode(workMode);
		measure.setPole(pole);
		measure.setPrecision(dot.getPrecision());
		measure.setCalculateDot(dot.getProgramVal());
		
	
		
		for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
			calboxService.cfgCalboardCalculate(calBox, measure);
		}
	
	}


	@Override
	public void sendLogicMeasure(TestDot dot) throws Exception {
		com.nltecklib.protocol.power.Data.setModuleCount(5);
		LogicCalculate2DebugData measure = new LogicCalculate2DebugData();
		measure.setUnitIndex(0);
		measure.setChnIndex(dot.getChannelDO().getChnIndex());
//		measure.setChnIndex(dot.getChannelDO().getDeviceChnIndex());
//		measure.setDriverIndex(dot.getChannelDO().getChnIndex() / dot.getChannelDO().getDevice().getChnNumInDriver());
		measure.setModuleIndex(dot.moduleIndex);
		measure.setMode(dot.getMode());
		measure.setPole(dot.getPole());
		measure.setCalculateDot(dot.getProgramVal());
		
		double programVal = 0;
		switch (dot.getMode()) {
		case CC:
		case DC:
			programVal = calCfgManager.calibratePlanData.getMaxProgramV();
			break;
		case CV:
			programVal = calCfgManager.calibratePlanData.getMaxProgramI();
			break;
		default:
			break;
		}
		
		measure.setProgramDot(programVal);

		List<ReadonlyAdcData> groups = new ArrayList<>();
		for (int i = 0; i < calCfgManager.steadyCfgData.getSampleCount(); i++) {
			groups.add(new ReadonlyAdcData());
		}
		measure.setAdcDatas(groups);

		for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
			calboxService.cfgCalculate(calBox, measure);
		}
		
	}

	@Override
	public void gatherMeter(TestDot dot,DetailConfig detailConfig) throws Exception {
		
//		// 切表
//		CalRelayControlDebugData relay = new CalRelayControlDebugData();
//		relay.setDriverIndex(0);
//		relay.setConnected(true);
//		WorkBench.getBoxService().cfgMeterChange(dot.getChannelDO().getDevice().getCalBoxList().get(0), relay);
//
//		double readVal=meter.ReadSingleClearBuffer();
////		double readVal=dot.getProgramVal();
//		if (dot.getMode() == DriverEnvironment.CalMode.CC || dot.getMode() == DriverEnvironment.CalMode.DC) {
//
//			double resistivity = getResistance(dot);
//			dot.setMeterVal(readVal*resistivity);
//
//			System.out.println("=============="+"表值:"+dot.getMeterVal()+"===============");
//			
//			if (dot.combine && dot.moduleIndex != 0) {
//				dot.setMeterVal(dot.getMeterVal()-dot.mainMeter); 
//			}
//
//		} else {
//			dot.setMeterVal(readVal);
//			System.out.println("=============="+"表值:"+dot.getMeterVal()+"===============");
//		}	
		


		// 读表延时
		TestSleep(detailConfig.readMeterDelay);
		if (dot.getTestType() == TestType.Cal || dot.getMode() == DriverEnvironment.CalMode.CV) {
			
			RelayControlExDebugData relayControlExDebugData= new RelayControlExDebugData();
			relayControlExDebugData.setDriverIndex(0);
			relayControlExDebugData.setRelayIndex((byte) 0);
			relayControlExDebugData.setConnected(true);
			WorkBench.getBoxService().cfgMeterRelaySwitch(dot.getChannelDO().getDevice().getCalBoxList().get(0), relayControlExDebugData);
			TestSleep(1000);
			double readVal = dot.getProgramVal();//meter.ReadSingleClearBuffer();

			if (dot.getMode() == DriverEnvironment.CalMode.CC || dot.getMode() == DriverEnvironment.CalMode.DC) {

				System.out.println("===resistivity=="+"resistivity"+"====");
				// double resistivity = getResistivity(calIndex, dot.precision);
				ResistanceModeRelayDebugData query=new ResistanceModeRelayDebugData();
				query.setDriverIndex(0);
				query.setRelayIndex((byte) 0);
				query.setWorkPattern(WorkPattern.values()[dot.getMode().ordinal()]);
				query.setRange(dot.getPrecision());

				double resistivity = WorkBench.calboxService.queryResistances(dot.getChannelDO().getDevice().getCalBoxList().get(0), query).getResistance();
				
				
				dot.setMeterVal( readVal * resistivity);


				// if (dot.combine && dot.moudleIndex != 0) {
				//
				// triggerDebugLog(dot.channel,
				// dot.meterVal + " - " + dot.mainMeter + " = " + (dot.meterVal -
				// dot.mainMeter));
				// dot.meterVal = dot.meterVal - dot.mainMeter;
				// }

			} else {
				dot.setMeterVal(readVal);

			}
		} else {
			/** 表值 合集 */
			List<Double> meterList = new ArrayList<>();
			// 获取读表次数
			int count = 1;
			
			if(dot.getProgramVal()>=300000) {
				count=2; 
			}
			System.out.println("Read meter count: " + count);
			for (int relayIndex = 0; relayIndex < count; relayIndex++) {
				
				ResistanceModeRelayDebugData query=new ResistanceModeRelayDebugData();
				query.setDriverIndex(0);
				query.setRelayIndex((byte) relayIndex);
				query.setWorkPattern(WorkPattern.values()[dot.getMode().ordinal()]);
				query.setRange(dot.getPrecision());

				double resistivity = WorkBench.calboxService.queryResistances(dot.getChannelDO().getDevice().getCalBoxList().get(0), query).getResistance();
				
				RelayControlExDebugData relayControlExDebugData= new RelayControlExDebugData();
				relayControlExDebugData.setDriverIndex(0);
				relayControlExDebugData.setRelayIndex((byte) relayIndex);
				relayControlExDebugData.setConnected(true);
				WorkBench.getBoxService().cfgMeterRelaySwitch(dot.getChannelDO().getDevice().getCalBoxList().get(0), relayControlExDebugData);
				
				
				TestSleep(1000);
				meterList.add(meter.ReadSingleClearBuffer() * resistivity);
				
				relayControlExDebugData.setConnected(false);
				WorkBench.getBoxService().cfgMeterRelaySwitch(dot.getChannelDO().getDevice().getCalBoxList().get(0), relayControlExDebugData);
				
				TestSleep(1000);
				
				System.out.println(String.format("===this is " + relayIndex + " meterval : %f ", dot.getMeterVal()));

				

			}
			double meterVal = 0;
			for (Double meterValue : meterList) {
				meterVal += meterValue;
			}
			dot.setMeterVal(meterVal);
			System.out.println("=============meterValue======"+dot.getMeterVal());
		}

	}

	public double readMeter() throws IOException, InterruptedException {
		double meterVal=0;
		meterVal=meter.ReadSingleClearBuffer();
	
		return meterVal;
	}
	
	private double getResistance(TestDot dot) throws Exception {
		
		CalResistanceDebugData query = new CalResistanceDebugData();
		query.setDriverIndex(0);
		query.setWorkPattern(WorkPattern.values()[dot.getMode().ordinal()]);
		query.setRange(dot.getPrecision());
		
		try {
			for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
				
				query = WorkBench.calboxService.readNewResistanceDebug(calBox, query);
			}
			

		} catch (Exception e1) {

			e1.printStackTrace();
		}

		
		
		return query.getResistance();
	}
	

}
