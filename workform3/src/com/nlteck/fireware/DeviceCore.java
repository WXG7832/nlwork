package com.nlteck.fireware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import com.nlteck.base.I18N;
import com.nlteck.model.CalBoardChannel;
import com.nlteck.model.Channel;
import com.nltecklib.protocol.li.MBWorkform.MBLogicFlashWriteData;
import com.nltecklib.protocol.li.main.MainEnvironment.SelfCheckState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.calBox.calBox_device.MbBaseInfoData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbCalibrateChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbDriverModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMatchAdcData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbSelfCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;

/**
 * 设备主控抽象
 * 
 * @author guofang_ma
 *
 */
public abstract class DeviceCore {

	protected CalibrateCore core;
	protected MbBaseInfoData baseInfoData;
	protected String ip;
	protected int port = 8163;

	// 锁对象
	private Map<Integer, UnitLocker> unitLockerMap = new HashMap<>();

	public static class UnitLocker implements Lock {
		public int unitIndex;

		public UnitLocker(int unitIndex) {
			this.unitIndex = unitIndex;
		}

		@Override
		public void lock() {
			// TODO Auto-generated method stub

		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean tryLock() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void unlock() {
			// TODO Auto-generated method stub

		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	protected Map<Integer, Channel> channelMap = new HashMap<>();

	public DeviceCore(CalibrateCore core) {
		this.core = core;
	}

	public CalibrateCore getCore() {
		return core;
	}

	public void initLocker(int logicCount) {
		unitLockerMap.clear();
		// 初始化同步对象
		for (int unit = 0; unit < logicCount; unit++) {
			unitLockerMap.put(unit, new UnitLocker(unit));
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Map<Integer, UnitLocker> getUnitLockerMap() {
		return unitLockerMap;
	}

	public void setBaseInfoData(MbBaseInfoData baseInfoData) {
		this.baseInfoData = baseInfoData;
	}

	public void initDevideBaseInfo() {
		MbBaseInfoData data = qryDeviceBaseInfo();
		baseInfoData = data;
	}

	public MbBaseInfoData getBaseInfoData() {
		if (baseInfoData == null) {
			synchronized (this) {
				if (baseInfoData == null) {
					initDevideBaseInfo();
				}
			}
		}
		return baseInfoData;
	}

	/**
	 * 逻辑板数量
	 * 
	 * @return
	 */
	public int getLogicCount() {
		return 1;
	}

	/**
	 * 驱动板数量
	 * 
	 * @return
	 */
	public int getDriverCount() {
		return getBaseInfoData().getDriverCount();
	}

	/**
	 * 驱动板通道数量
	 * 
	 * @return
	 */
	public int getDriverChnCount() {
		return CalibrateCore.getBaseCfg().base.driverChnCount;
	}

	/**
	 * 分区通道数
	 * 
	 * @return
	 */
	public int getChnCount() {
		return getDriverChnCount() * getDriverCount();
	}

	public Map<Integer, Channel> getChannelMap() {
		return channelMap;
	}

	protected void init() {

		core.getLogger().info("device init");

		// 协议配置
		// Data.set
		Data.setDriverChnCount(CalibrateCore.getBaseCfg().base.driverChnCount);

		channelMap.clear();

		for (int board = 0; board < getDriverCount(); board++) {
			
			DeviceDriverboard db = new DeviceDriverboard(board);
			for (int chn = 0; chn < getDriverChnCount(); chn++) {
				
				Channel channel = new Channel(this, board, chn);
				channelMap.put(channel.getDeviceChnIndex(), channel);
				db.addChannel(channel);
				
				if(core.getChnMapService().isEnable()) {
					
					
					CalBoardChannel calBoardChannel = core.findCalboardChnByDeviceChnIndex(channel.getDeviceChnIndex());
					if(calBoardChannel != null) {
					   channel.setBindingCalBoardChannel(calBoardChannel);
					   
					}
				}
				
			}
		}

		try {
			core.getDiskService().createDeviceXml(this);
		} catch (IOException e1) {
			core.getLogger().error("createDeviceXml error :" + e1.getMessage(), e1);
		}
		
		core.getNetworkService().pushChnData(core.getDeviceCore().getChannelMap().entrySet().stream()
				.map(e -> e.getValue()).collect(Collectors.toList()));

		// 推送
//		core.getNetworkService()
//				.pushChnData(channelMap.entrySet().stream().filter(x -> isLogicUse(x.getValue().getUnitIndex()))
//						.map(x -> x.getValue()).collect(Collectors.toList()));
		// core.getDeviceCore().getChannelMap().entrySet().stream().filter(x ->
		// isLogicUse(x.getValue().getUnitIndex()))
		// .forEach(e -> core.getScreen().setState(e.getValue()));

		/*try {
			core.getScreen().updateAllChannel(
					channelMap.entrySet().stream().filter(x -> isLogicUse(x.getValue().getUnitIndex()))
							.map(x -> x.getValue()).collect(Collectors.toList()));

			// core.getScreen().updateAllChannel(Arrays.asList(new Channel[] {
			// channelMap.get(0) }));

		} catch (Exception e) {
			
			e.printStackTrace();
		} */
	}

	/**
	 * 膜片使能开关
	 * 
	 * @param chnIndex
	 *            设备通道
	 * @param open
	 * @throws Exception
	 */
	public abstract void cfgModuleSwitch(int driverIndex, int chnIndexInDriver, boolean open) throws Exception;

	/**
	 * 查询膜片使能
	 * 
	 * @param unitIndex
	 * @param chnIndex
	 * @return
	 * @throws Exception
	 */
	public abstract boolean qryModuleSwitch(int driverIndex, int chnIndexInDriver);

	/**
	 * 设备工作模式配置
	 * 
	 * @param state
	 * @throws Exception
	 */
	public abstract void cfgModeChange(MbModeChangeData  modeChange) throws Exception ;
	
	
	/**
	 * 单驱动板工作模式配置
	 * @author  wavy_zheng
	 * 2022年3月28日
	 * @param driverModeChange
	 * @throws Exception
	 */
	public abstract void cfgDriverModeChange(MbDriverModeChangeData driverModeChange) throws Exception;

	/**
	 * 设备工作模式查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract MbModeChangeData qryModeChange();

	/**
	 * 配置校准点参数
	 * 
	 * @param unitIndex
	 * @param chnIndex
	 * @param pole
	 * @param workMode
	 * @param programV
	 * @param programI
	 * @param precision
	 * @param adcs
	 * @throws Exception
	 */
	public abstract void cfgCalibrate(MbCalibrateChnData calData) throws Exception;

	/**
	 * 查询驱动板ADC
	 * 
	 * @param unitIndex
	 * @param chnIndex
	 * @return
	 * @throws Exception
	 */
	public abstract MbCalibrateChnData qryCalibrate(int driverIndex, int chnIndex);
	
	/**
	 * 查询设备故障情况
	 * @author  wavy_zheng
	 * 2022年3月31日
	 * @return
	 */
	public abstract MbSelfCheckData  qrySelfCheck();
	

	/**
	 * 配置驱动板计量参数
	 * 
	 * @param unitIndex
	 * @param chnIndex
	 * @param pole
	 * @param workMode
	 * @param calculateDot
	 * @param groups
	 * @throws Exception
	 */
	public abstract void cfgCalculate(MbMeasureChnData  measureData) throws Exception;

	/**
	 * 查询逻辑板最终ADC
	 * 
	 * @param unitIndex
	 * @param chnIndex
	 * @return
	 * @throws Exception
	 */
	public abstract MbMeasureChnData qryCalculate(int driverIndex, int chnIndex);

	/**
	 * 写入驱动板flash
	 * 
	 * @param unitIndex
	 * @param chnIndex
	 * @param dots
	 * @throws Exception
	 */
	public abstract void cfgFlash(MbFlashParamData flash) throws Exception;

	/**
	 * 查询逻辑板flash
	 * 
	 * @param unitIndex
	 * @param chnIndex
	 * @return
	 * @throws Exception
	 */
	public abstract MbFlashParamData qryFlash(int driverIndex, int chnIndex);


	/**
	 * 查询逻辑板对接电压
	 * 
	 * @param unitIndex
	 * @return
	 * @throws Exception
	 */
	public abstract MbMatchAdcData qryCalMatch();

	public abstract void connect() throws Exception ;

	public abstract void disConnect();

	/**
	 * 查询设备信息
	 * 
	 * @throws Exception
	 */
	public abstract MbBaseInfoData qryDeviceBaseInfo();


	public abstract MbSelfCheckData qrySelfTestInfo();

	public abstract void startInit();

	public abstract MbFlashParamData qryLogicFlash(int unitIndex, int chnIndex);
}
