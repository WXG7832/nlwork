package com.nlteck.fireware;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nlteck.base.BaseCfgManager.BoardParam;
import com.nlteck.connector.DeviceConnector;
import com.nlteck.base.I18N;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.ConfigDecorator;
import com.nltecklib.protocol.power.Environment.Result;
import com.nltecklib.protocol.power.QueryDecorator;
import com.nltecklib.protocol.power.ResponseDecorator;
import com.nltecklib.protocol.power.calBox.calBox_device.MBChannelSwitchData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbBaseInfoData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbCalibrateChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbDriverModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMatchAdcData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbSelfCheckData;

/**
 * 实体主控核心板，网口连接
 * 
 * @author guofang_ma
 *
 */
public class ARMDeviceCore extends DeviceCore {

	public static final int TIMEOUT = 5000;

	private DeviceConnector deviceConnector;

	public ARMDeviceCore(CalibrateCore core) {
		super(core);
		deviceConnector = new DeviceConnector(this);
		deviceConnector.setIp(ip);
		deviceConnector.setPort(port);
	}

	@Override
	public void setIp(String ip) {
		super.setIp(ip);
		deviceConnector.setIp(ip);
	}

	@Override
	public void setPort(int port) {
		super.setPort(port);
		deviceConnector.setPort(port);
	}

	/**
	 * 
	 * @param chnIndex 设备通道
	 * @param open
	 * @throws Exception
	 */
	@Override
	public void cfgModuleSwitch(int driverIndex, int chnIndex, boolean open) throws Exception {

		MBChannelSwitchData data = new MBChannelSwitchData();
		data.setDriverIndex(driverIndex);
		data.setChnIndex(chnIndex);
		data.setEnabled(open);

		ResponseDecorator result = deviceConnector.sendAndResponse(new ConfigDecorator(data), TIMEOUT);
		data = (MBChannelSwitchData) result.getDestData();
		if(data.getResult() != Result.SUCCESS) {
			
			throw new Exception("使能模式失败,错误码:" + data.getResult().toString());
		}
	}
	
	@Override
	public MbModeChangeData qryModeChange() {
		
		MbModeChangeData data = new MbModeChangeData();
		ResponseDecorator response = deviceConnector.sendAndResponse(new QueryDecorator(data), TIMEOUT);
		return (MbModeChangeData) response.getDestData();
	}

	@Override
	public boolean qryModuleSwitch(int driverIndex, int chnIndex) {
		MBChannelSwitchData data = new MBChannelSwitchData();
		data.setDriverIndex(driverIndex);
		data.setChnIndex(chnIndex);

		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(data), TIMEOUT);
		data = (MBChannelSwitchData) result.getDestData();
		return data.isEnabled();
	}

	@Override
	public  void cfgModeChange(MbModeChangeData  modeChange) throws Exception {

		ResponseDecorator result = deviceConnector.sendAndResponse(new ConfigDecorator(modeChange), 5000);
		MbModeChangeData data = (MbModeChangeData) result.getDestData();
		if(data.getResult() != Result.SUCCESS) {
			
			throw new Exception("切换设备模式失败,错误码:" + data.getResult().toString());
		}
	}

	

	@Override
	public  void cfgCalibrate(MbCalibrateChnData calData) throws Exception {

		ResponseDecorator result = deviceConnector.sendAndResponse(new ConfigDecorator(calData), TIMEOUT);
		MbCalibrateChnData data = (MbCalibrateChnData) result.getDestData();
		if(data.getResult() != Result.SUCCESS) {
			
			throw new Exception("配置校准点失败，原因:" + data.getResult().toString());
		}
	}

	@Override
	public MbCalibrateChnData qryCalibrate(int driverIndex, int chnIndex) {
		MbCalibrateChnData data = new MbCalibrateChnData();
		data.setDriverIndex(driverIndex);
		data.setChnIndex(chnIndex);

		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(data), TIMEOUT);
		data = (MbCalibrateChnData) result.getDestData();
		return data;
	}

	
	@Override
	public void cfgCalculate(MbMeasureChnData  measureData) throws Exception {
		
		ResponseDecorator result = deviceConnector.sendAndResponse(new ConfigDecorator(measureData), TIMEOUT);
		MbMeasureChnData data = (MbMeasureChnData) result.getDestData();
		if(data.getResult() != Result.SUCCESS) {
			
			throw new Exception("配置计量点失败,原因:" + data.getResult().toString());
		}
	}

	@Override
	public MbMeasureChnData qryCalculate(int driverIndex, int chnIndex) {

		MbMeasureChnData data = new MbMeasureChnData();
		data.setDriverIndex(driverIndex);
		data.setChnIndex(chnIndex);

		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(data), TIMEOUT);
		data = (MbMeasureChnData) result.getDestData();
		return data;
	}


	@Override
	public void cfgFlash(MbFlashParamData flash) throws Exception {
		
		ResponseDecorator result = deviceConnector.sendAndResponse(new ConfigDecorator(flash), 5000);
		MbFlashParamData data = (MbFlashParamData) result.getDestData();
        if(data.getResult() != Result.SUCCESS) {
			
			throw new Exception("写入flash参数失败,原因:" + data.getResult().toString());
		}

	}

	@Override
	public MbFlashParamData qryFlash(int driverIndex, int chnIndex) {
		MbFlashParamData data = new MbFlashParamData();
		data.setDriverIndex(driverIndex);
		data.setChnIndex(chnIndex);

		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(data,(byte)0), 5000);
		data = (MbFlashParamData) result.getDestData();
		return data;
	}

	@Override
	public MbMatchAdcData qryCalMatch() {
		MbMatchAdcData data = new MbMatchAdcData();
		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(data), TIMEOUT);
		data = (MbMatchAdcData) result.getDestData();
		return data;
	}

	@Override
	public void connect() throws Exception{
		// TODO Auto-generated method stub
		if (!deviceConnector.connect()) {
			throw new Exception(I18N.getVal(I18N.ConnectFail, I18N.getVal(I18N.Device)));
		}
	}

	@Override
	public void disConnect() {
		// TODO Auto-generated method stub
		deviceConnector.disConnect();
		baseInfoData = null;
	}

	@Override
	public MbBaseInfoData qryDeviceBaseInfo() {

		MbBaseInfoData data = new MbBaseInfoData();
		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(data), TIMEOUT);
		data = (MbBaseInfoData) result.getDestData();

		return data;
	}


	@Override
	public MbSelfCheckData qrySelfTestInfo() {
		
		MbSelfCheckData data = new MbSelfCheckData();
		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(data), 30000);
		data = (MbSelfCheckData) result.getDestData();
		return data;

	}

	@Override
	public void startInit() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				try {
					core.getNetworkService().pushLog(I18N.getVal(I18N.InitDevice), false);
					getBaseInfoData();
					init();
					core.getNetworkService().pushLog(I18N.getVal(I18N.InitDeviceSuccess), false);
				} catch (Exception e) {
					core.getNetworkService().pushLog(I18N.getVal(I18N.InitDeviceFailed, e.getMessage()), true);
				}
			}
		}).start();
	}

	@Override
	public void cfgDriverModeChange(MbDriverModeChangeData driverModeChange) throws Exception {
		
		ResponseDecorator result = deviceConnector.sendAndResponse(new ConfigDecorator(driverModeChange), 5000);
		MbDriverModeChangeData data = (MbDriverModeChangeData) result.getDestData();
		if(data.getResult() != Result.SUCCESS) {
			
			throw new Exception("切换驱动板" +  (driverModeChange.getDriverIndex() + 1 )+"模式失败,错误码:" + data.getResult().toString());
		}
		
	}

	@Override
	public MbSelfCheckData qrySelfCheck() {
		
		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(new MbSelfCheckData()), 5000);
		return (MbSelfCheckData) result.getDestData();
	}

	@Override
	public MbFlashParamData qryLogicFlash(int driverIndex, int chnIndex) {
		MbFlashParamData send=new MbFlashParamData();
		send.setDriverIndex(driverIndex);
		send.setChnIndex(chnIndex);
		ResponseDecorator result = deviceConnector.sendAndResponse(new QueryDecorator(send), 5000);
		return (MbFlashParamData)result.getDestData();
	}
}
