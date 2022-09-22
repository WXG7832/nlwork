package com.nlteck.fireware;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.nlteck.base.BaseCfgManager.BoardParam;
import com.nlteck.base.I18N;
import com.nlteck.model.CalBoardChannel;
import com.nlteck.model.Channel;
import com.nlteck.model.Channel.VirtualChannelData;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.WorkMode;
import com.nltecklib.protocol.li.MBWorkform.MBLogicFlashWriteData;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.calBox.calBox_device.MbBaseInfoData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbCalibrateChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbDriverModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMatchAdcData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbSelfCheckData;
import com.nltecklib.protocol.power.driver.DriverCalculateData.AdcEntry;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData.AdcData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;

/**
 * 虚拟设备主控
 * 
 * @author guofang_ma
 *
 */
public class VirtualDeviceCore extends DeviceCore {

	public VirtualDeviceCore(CalibrateCore core) {
		super(core);
		// TODO Auto-generated constructor stub
		random = new Random();
	}

	private Random random;
	private WorkMode workmode = WorkMode.NORMAL; //设备当前状态

	@Override
	public void cfgModuleSwitch(int driverIndex, int chnIndex, boolean open) {
		
		if (workmode != WorkMode.CAL) {
			throw new RuntimeException("请先进入校准模式");
		}
		
		int deviceChnIndex = getDeviceChnIndex(driverIndex , chnIndex);
		channelMap.get(deviceChnIndex).getVirtualChannelData().open = open;
	}
	
	private int getDeviceChnIndex(int driverIndex , int chnIndexInDriver) {
		
		return driverIndex * CalibrateCore.getBaseCfg().base.driverChnCount + chnIndexInDriver;
		
	}

	@Override
	public boolean qryModuleSwitch(int driverIndex, int chnIndex) {
		
		if (workmode != WorkMode.CAL) {
			throw new RuntimeException("请先进入校准模式");
		}
		return channelMap.get(getDeviceChnIndex(driverIndex , chnIndex)).getVirtualChannelData().open;
	}

	@Override
	public void cfgModeChange(MbModeChangeData modeChange) {
		
		this.workmode = modeChange.getMode();
		
	}
	
	@Override
	public MbModeChangeData qryModeChange() {
		
		MbModeChangeData mmcd = new MbModeChangeData();
		mmcd.setMode(workmode);
		return mmcd;
	}

	@Override
	public void cfgCalibrate(MbCalibrateChnData calibrate) {

		Channel channel = channelMap.get(getDeviceChnIndex(calibrate.getDriverIndex(), calibrate.getChnIndex()));

		CalBoardChannel calBoardChannel = channel.getBindingCalBoardChannel();
		if (calBoardChannel != null) {
			((VirtualCalBoard) core.getCalBoardMap().get(calBoardChannel.getBoardIndex()))
					.setCurrentCalBoardChannel(calBoardChannel);
		}
		VirtualChannelData chnData = channel.getVirtualChannelData();

		chnData.calMode = calibrate.getMode();
		chnData.pole = calibrate.getPole();
		chnData.programV = calibrate.getVoltageDA();
		chnData.programI = calibrate.getCurrentDA();
		chnData.precision = calibrate.getRange();
		chnData.adcSize =  calibrate.getAdcDatas().size();

		switch (chnData.calMode) {
		case CC:
		case DC:
			chnData.meter = (double) chnData.programI * 0.203 + random.nextDouble() * 0.2 - 0.1;
			break;
		case CV:
			chnData.meter = (double) chnData.programV * 0.101 + random.nextDouble() * 0.2 - 0.1;
			break;
		case SLEEP:
			break;
		default:
			break;
		}

	}

	@Override
	public MbCalibrateChnData qryCalibrate(int driverIndex, int chnIndex) {

		// TODO Auto-generated method stub
		MbCalibrateChnData data = new MbCalibrateChnData();
		data.setDriverIndex(driverIndex);
		data.setChnIndex(chnIndex);

		Channel channel = channelMap.get(getDeviceChnIndex(driverIndex, chnIndex));


		VirtualChannelData chnData = channel.getVirtualChannelData();
		data.setMode(chnData.calMode);
		data.setPole(chnData.pole);
		data.setRange(chnData.precision);
		data.setVoltageDA((int) chnData.programV);
		data.setCurrentDA((int) chnData.programI);

		List<AdcData> adcs = new ArrayList<>();
		for (int i = 0; i < chnData.adcSize; i++) {
			AdcData adc = new AdcData();
			if (chnData.open) {
				switch (chnData.calMode) {
				case CC:
				case DC:
					adc.mainAdc = (double) chnData.programI * 0.0762 + random.nextDouble() * 0.2 - 0.1;
					adc.backAdc1=(double) chnData.programI * 0.0762 + random.nextDouble() * 0.2 - 0.1;
					adc.backAdc2=(double) chnData.programI * 0.0762 + random.nextDouble() * 0.2 - 0.1;
					break;
				case CV:
					adc.mainAdc = (double) chnData.programV * 0.01 + random.nextDouble() * 0.2 - 0.1;
					adc.backAdc1 = (double) chnData.programV * 0.01 + random.nextDouble() * 0.2 - 0.1;
					adc.backAdc2 = (double) chnData.programV * 0.01 + random.nextDouble() * 0.2 - 0.1;
					break;
				case SLEEP:
					break;
				}
			}
			adcs.add(adc);
		}

		data.setAdcDatas(adcs);

		return data;

	}

	@Override
	public void cfgCalculate(MbMeasureChnData  measureData) {

		Channel channel = channelMap.get(getDeviceChnIndex(measureData.getDriverIndex(), measureData.getChnIndex()));

		CalBoardChannel calBoardChannel = channel.getBindingCalBoardChannel();
		if (calBoardChannel != null) {
			((VirtualCalBoard) core.getCalBoardMap().get(calBoardChannel.getBoardIndex()))
					.setCurrentCalBoardChannel(calBoardChannel);
		}
		VirtualChannelData chnData = channel.getVirtualChannelData();

		chnData.calMode = measureData.getMode();
		chnData.pole = measureData.getPole();
		chnData.calculateDot = measureData.getCalculateDot();
		chnData.adcSize = measureData.getAdcDatas().size();

		chnData.meter = (double) chnData.calculateDot + random.nextDouble() * 0.2 - 0.1;

	}

	@Override
	public MbMeasureChnData qryCalculate(int driverIndex, int chnIndex) {

		// TODO Auto-generated method stub
		MbMeasureChnData data = new MbMeasureChnData();
		data.setDriverIndex(driverIndex);
		data.setChnIndex(chnIndex);

		Channel channel = channelMap.get(getDeviceChnIndex(driverIndex, chnIndex));

		VirtualChannelData chnData = channel.getVirtualChannelData();
		data.setMode(chnData.calMode);
		data.setPole(chnData.pole);
		data.setCalculateDot(chnData.calculateDot);

		List<ReadonlyAdcData> adcs = new ArrayList<>();
		for (int i = 0; i < chnData.adcSize; i++) {
			ReadonlyAdcData adc = new ReadonlyAdcData();
			
			if (chnData.open) {
				System.out.println(Data.getModuleCount());
				for(int n = 0 ; n < Data.getModuleCount() ; n++) {
					for(AdcEntry adcEntry:adc.adcList) {
						adcEntry.finalAdc=chnData.calculateDot/Data.getModuleCount() + random.nextDouble()*20  - 0.1;
						adcEntry.primitiveAdc=chnData.calculateDot/Data.getModuleCount() + random.nextDouble()*20  - 0.1;
					}
				}
				if(data.getMode().equals(CalMode.CV)) {
					adc.finalBackAdc1=chnData.calculateDot + random.nextDouble() * 0.2 - 0.1;
					adc.finalBackAdc2=chnData.calculateDot + random.nextDouble() * 0.2 - 0.1;
					adc.primitiveBackAdc1=chnData.calculateDot + random.nextDouble() * 0.2 - 0.1;
					adc.primitiveBackAdc2=chnData.calculateDot + random.nextDouble() * 0.2 - 0.1;
					
				}
				
			}
			adcs.add(adc);
		}

		data.setAdcDatas(adcs);

		return data;

	}
 
	
	@Override
	public void cfgFlash(MbFlashParamData  flash) {
		int chnIndex=getDeviceChnIndex(flash.getDriverIndex(), flash.getChnIndex());
		Channel channel = channelMap.get(chnIndex);
		VirtualChannelData chnData = channel.getVirtualChannelData();
		chnData.flash = flash;
		
	}
	

	@Override
	public MbFlashParamData qryFlash(int driverIndex, int chnIndex) {
		
		Channel channel = channelMap.get(getDeviceChnIndex(driverIndex, chnIndex));
		VirtualChannelData chnData = channel.getVirtualChannelData();
		chnData.flash=new MbFlashParamData();
		chnData.flash.setModuleIndex(0);
		
		return chnData.flash;
	}

	

	@Override
	public MbMatchAdcData qryCalMatch() {
		// TODO Auto-generated method stub
		MbMatchAdcData data = new MbMatchAdcData();
		for (Iterator<Integer> it = channelMap.keySet().iterator(); it.hasNext() ; ) {
			
			int channelIndex = it.next();
			DriverMatchAdcData.AdcData data2 = new DriverMatchAdcData.AdcData();
			data2.chnIndex = channelIndex;

			data2.adc = channelMap.get(channelIndex).getVirtualChannelData().virtualVolt;
			data.getAdcList().add(data2);
		}
		return data;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		// qryDeviceBaseInfo();
	}

	@Override
	public void disConnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public MbBaseInfoData qryDeviceBaseInfo() {
		// TODO Auto-generated method stub
		MbBaseInfoData data = new MbBaseInfoData();
		data.setDriverCount(CalibrateCore.getBaseCfg().virtual.driverCount);

		
		long flag = 0;
		for (BoardParam bp : CalibrateCore.getBaseCfg().virtual.driverboards) {
			if (!bp.disabled) {
				flag |= 1 << bp.index;
			}
		}
		data.setEnableFlag(flag);
		return data;

	}


	@Override
	public MbSelfCheckData qrySelfTestInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startInit() {
		new Thread(new Runnable() {
			public void run() {

				try {
					core.getNetworkService().pushLog(I18N.getVal(I18N.InitDevice), false);

					initDevideBaseInfo();
					init();
					core.getVirtualService().virtualBinding();

					core.getNetworkService().pushLog(I18N.getVal(I18N.InitDeviceSuccess), false);
				} catch (Exception e) {
					core.getNetworkService().pushLog(I18N.getVal(I18N.InitDeviceFailed, e.getMessage()), true);
				}

			}
		}).start();
	}

	@Override
	public void cfgDriverModeChange(MbDriverModeChangeData driverModeChange) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MbSelfCheckData qrySelfCheck() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MbFlashParamData qryLogicFlash(int unitIndex, int chnIndex ) {
		// TODO Auto-generated method stub
		return null;
	}

}
