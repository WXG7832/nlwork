package com.nlteck.service;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.data.virtual.VirtualDriverChnDataProvider;
import com.nltecklib.protocol.fuel.Environment.WorkMode;

import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData.CalParamData;
import com.nltecklib.protocol.power.driver.DriverCalculateData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverChannelTemperData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CheckResult;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
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

/**
* @author  wavy_zheng
* @version 创建时间：2022年1月17日 下午3:07:06
* 虚拟驱动板核心服务
*/
public class VirtualDriverBoardService extends AbsDriverBoardService {
   
	private VirtualDriverChnDataProvider provider;
	
	
	public VirtualDriverBoardService(MainBoard mainboard) {
		super(mainboard);

		provider = new VirtualDriverChnDataProvider();
		
	}

	@Override
	public DriverPickupData pickupDriver(int driverIndex) throws AlertException {
		
		return provider.pickupDriver(driverIndex);
	}

	@Override
	public void writePole(DriverPoleData data) throws AlertException {
		
		provider.writePole(data.getDriverIndex(), data);

	}

	@Override
	public void writeHeartbeat(DriverHeartbeatData data) throws AlertException {
		
		

	}

	@Override
	public DriverInfoData readDriverInfo(int driverIndex, int chnIndexInDriver) throws AlertException {

		return provider.readDriverInfo(driverIndex);
	}

	@Override
	public void writeBaseProtect(DriverProtectData data) throws AlertException {
		
		provider.writeDriverProtect(data);

	}

	@Override
	public void writeOperate(DriverOperateData data) throws AlertException {
		
		provider.writeOperate(data);
		

	}

	@Override
	public void writeSteps(DriverStepData data) throws AlertException {
		
		provider.writeSteps(data);

	}

	@Override
	public void writeResume(DriverResumeData data) throws AlertException {
		
		provider.writeResume(data);

	}

	
	/*public void startInnerWork(int driverIndex) {
		
		provider.startWork(driverIndex);
	}

	
	public void stopInnerWork(int driverIndex) {
		
		provider.stopWork(driverIndex);
		

	}*/

	@Override
	public void writeWorkMode(DriverModeData data) throws AlertException {
		
		provider.writeMode(data);

	}

	@Override
	public void writeModuleSwitch(DriverModuleSwitchData data) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCalibrate(DriverCalibrateData data) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public DriverCalibrateData readCalibrate(int driverIndex, int chnIndexInDriver) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeCalculate(DriverCalculateData data) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public DriverCalculateData readCalculate(int driverIndex, int chnIndexInDriver) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void writeUpgrade(DriverUpgradeData upgrade) throws AlertException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeFlash(DriverCalParamSaveData flash) throws AlertException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DriverCalParamSaveData readFlash(int driverIndex, int chnIndexInDriver , int moduleIndex) throws AlertException {
		// TODO Auto-generated method stub
		long st = System.currentTimeMillis();
		DriverCalParamSaveData driverCalParamSaveData=new DriverCalParamSaveData();
		driverCalParamSaveData.setDriverIndex(driverIndex);
		driverCalParamSaveData.setChnIndex(chnIndexInDriver);
		driverCalParamSaveData.setModuleIndex(moduleIndex);
		driverCalParamSaveData.setCv1DotCount(3);
		driverCalParamSaveData.setCv2DotCount(3);
		List<CalParamData>sleepDots=new ArrayList<>();
		List<CalParamData>ccDots=new ArrayList<>();
		List<CalParamData>cvDots=new ArrayList<>();
		List<CalParamData>dcDots=new ArrayList<>();
		
		CalParamData calParamData=new CalParamData();
		calParamData.calMode=CalMode.CC;
		calParamData.pole=Pole.POSITIVE;
		calParamData.range=1;
		calParamData.adc=13.4d;
		calParamData.adcK=12.21d;
		calParamData.adcB=21.12d;
		calParamData.da=1020;
		calParamData.programK=2.2d;
		calParamData.programB=3.34d;
		CalParamData calParamData1=new CalParamData();
		calParamData1.calMode=CalMode.CV;
		calParamData1.pole=Pole.POSITIVE;
		calParamData1.range=1;
		calParamData1.adc=13.4d;
		calParamData1.adcK=12.21d;
		calParamData1.adcB=21.12d;
		calParamData1.da=1200;
		calParamData1.programK=22.32d;
		calParamData1.programB=15.34d;
		CalParamData calParamData2=new CalParamData();
		calParamData2.calMode=CalMode.DC;
		calParamData2.pole=Pole.POSITIVE;
		calParamData2.range=1;
		calParamData2.adc=123.4d;
		calParamData2.adcK=132.21d;
		calParamData2.adcB=231.12d;
		calParamData2.da=1100;
		calParamData2.programK=222.32d;
		calParamData2.programB=315.34d;
		for(int i=0;i<3;i++) {
			
			ccDots.add(calParamData);
			
		}
		for(int i=0;i<3;i++) {
			
			cvDots.add(calParamData1);
			
		}
		for(int i=0;i<3;i++) {
			
			dcDots.add(calParamData2);
			
		}
		if(driverCalParamSaveData.getCv1DotCount()>0) {
			for(int i1=0;i1<driverCalParamSaveData.getCv1DotCount();i1++) {
				cvDots.add(calParamData1);
			}
		}
		if(driverCalParamSaveData.getCv2DotCount()>0) {
			for(int i1=0;i1<driverCalParamSaveData.getCv2DotCount();i1++) {
				cvDots.add(calParamData1);
			}
		}
		driverCalParamSaveData.saveDatas(CalMode.SLEEP, sleepDots);
		driverCalParamSaveData.saveDatas(CalMode.CC, ccDots);
		driverCalParamSaveData.saveDatas(CalMode.CV, cvDots);
		driverCalParamSaveData.saveDatas(CalMode.DC, dcDots);
		System.out.println("耗时：" + (System.currentTimeMillis() - st));
		return driverCalParamSaveData;
	}

	@Override
	public DriverMatchAdcData readMatchAdcs(int driverIndex) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DriverCheckData readDriverSelfCheckInfo(int driverIndex) throws AlertException {
		// TODO Auto-generated method stub
		
		DriverCheckData data = new DriverCheckData();
		data.setAdPick(CheckResult.NORMAL);
		data.setCalParam(CheckResult.NORMAL);
		data.setCheckboard(CheckResult.NORMAL);
		data.setDriverFlash(CheckResult.NORMAL);
		data.setDriverSram(CheckResult.NORMAL);
		data.setTempPick(CheckResult.NORMAL);
		
		data.setPowerOk(true);
		return data;
	}

	@Override
	public DriverInfoData readDriverSoftInfo(int driverIndex) throws AlertException {
		
		
		DriverInfoData  did = new DriverInfoData();
		did.setCheckSoftVersion("check.0.0.1");
		did.setPickSoftVersion("pick.0.0.2");
		did.setDriverSoftVersion("driver.1.0.1");
		did.setTempSoftVersion("temp.0.0.2");
		
		return did;
		
	}

	@Override
	public DriverChannelTemperData pickupTemperature(int driverIndex) throws AlertException {
		
		DriverChannelTemperData data = new DriverChannelTemperData();
		
		data.setDriverIndex(driverIndex);
		
		for(int n = 0 ; n < MainBoard.startupCfg.getDriverChnCount(); n++ ) {
			
			data.getTempProbes().add(25.6);
			
		}
		
		return data;
	}

}
