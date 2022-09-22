package com.nlteck.calModel.model;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.calModel.base.BaseCfgManager;
import com.nlteck.calModel.base.CalCfgManager;
import com.nlteck.calModel.base.DelayConfig.DetailConfig;
import com.nlteck.firmware.CalBox;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestDot;
import com.nltecklib.protocol.li.PCWorkform.CalCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;

public class VirtureCalMainBoard extends ABSCalMainBoard{
	CalCfgManager calCfgManager = new CalCfgManager();
	BaseCfgManager baseCfgManager = new BaseCfgManager();
	private ChannelDO channelDO;
	private int calBoardIndex;
	private int chnIndexIncalBoard;
	
	
	
	public ChannelDO getChannelDO() {
		return channelDO;
	}
	public void setChannelDO(ChannelDO channelDO) {
		this.channelDO = channelDO;
	}
	public int getCalBoardIndex() {
		return calBoardIndex;
	}
	public void setCalBoardIndex(int calBoardIndex) {
		this.calBoardIndex = calBoardIndex;
	}
	public int getChnIndexIncalBoard() {
		return chnIndexIncalBoard;
	}
	public void setChnIndexIncalBoard(int chnIndexIncalBoard) {
		this.chnIndexIncalBoard = chnIndexIncalBoard;
	}
	@Override
	public void changeModel(List<CalBox> calBoxs,CalibrateCoreWorkMode mode) {
		System.out.println("模式切换:"+mode.toString());
	}
	
	@Override
	public void switchModule(List<CalBox> calboxs,int channelIndex, boolean isModeChange) {
		System.out.println("膜片状态："+(isModeChange?"打开":"关闭"));
		
	}
	@Override
	public void sendCalibrateDot(TestDot dot,boolean open) {


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
			// TODO CV2:
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
//		sendData.setDriverIndex(dot.getChannelDO().getBindingCalBoardChannel().getCalBoardIndex());
//		sendData.setChnIndex(dot.getChannelDO().getBindingCalBoardChannel().getCalBoardIndex());
		sendData.setChnIndex(dot.getChannelDO().getChnIndex());
		
		sendData.setPrecision(dot.getPrecision());
		sendData.setProgramI(programI);
		sendData.setProgramV(programV);
		sendData.setWorkMode(mode);
		sendData.setWorkState(WorkState.WORK);
		com.nltecklib.protocol.li.main.PoleData.Pole pole = com.nltecklib.protocol.li.main.PoleData.Pole.values()[dot
				.getPole().ordinal()];
		sendData.setPole(pole);
		System.out.println("下发校准板校准");
	
		
	}
	@Override
	public void sendLogicDot(TestDot dot) {
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

		LogicCalibrate2DebugData sendData = new LogicCalibrate2DebugData();
		sendData.setDriverIndex(dot.getChannelDO().getDriverIndex());
		sendData.setChnIndex(dot.getChannelDO().getChnIndex());
		sendData.setModuleIndex(dot.moduleIndex);
		sendData.setWorkMode(mode);
		sendData.setPole(dot.getPole());
		sendData.setPrecision(dot.getPrecision());
		sendData.setProgramV(programV);
		sendData.setProgramI(programI);

		List<DriverCalibrateData.AdcData> adcs = new ArrayList<>();
		for (int i = 0; i < calCfgManager.steadyCfgData.getSampleCount(); i++) {
			adcs.add(new DriverCalibrateData.AdcData());
		}
		sendData.setAdcs(adcs);
		System.out.println("下发逻辑板校准");
	}
	@Override
	public void sendLogicMeasure(TestDot dot) {
		
	}
	@Override
	public void sendCalMeasure(TestDot dot) {
		
	}
	@Override
	public void gatherMeter(TestDot dot,DetailConfig detailConfig) {

		try {
			if (dot.getTestType() == TestType.Cal) {
				CalMode mode = dot.getMode();
				switch (mode) {
				case CC:
				case DC:
					dot.setMeterVal(
							(dot.getProgramVal() - 3.14) / 1.5);
					break;
				
				case CV:
					dot.setMeterVal(
							(dot.getProgramVal() - 2.718) / 1.7);
					break;
				default:
					break;
				}

			} else {
				
				double midProgramVal=0;
				if(dot.getMode().equals(CalMode.CC)||dot.getMode().equals(CalMode.DC)) {
					midProgramVal=dot.getProgramVal()*1.5+3.14-0.05+Math.random()*0.1;
				}else {
					midProgramVal=dot.getProgramVal()*1.7+2.718-0.05+Math.random()*0.1;
				}
				// 只计量时
				if(dot.getChannelDO().getCalDotList()==null||dot.getChannelDO().getCalDotList().size()==0) {
					dot.setMeterVal(dot.getProgramVal()+0.5-Math.random());
				}
				for(TestDot usedot:dot.getChannelDO().getCalDotList()) {
					if(usedot.moduleIndex==dot.moduleIndex&&	// usedot.getPrecision()==dot.getPrecision()&&
							usedot.getMode().equals(dot.getMode())&&usedot.getPole().equals(dot.getPole())) {
						if(usedot.getProgramk()==0&&usedot.getProgramb()==0) {
							continue;
						}
						if(dot.getProgramVal()<usedot.getMeterVal());
						dot.setMeterVal((midProgramVal-usedot.getProgramb())/usedot.getProgramk());
						break;
					}
						
				}

			}
			System.out.println("meter :" + dot.getMeterVal());

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
}
