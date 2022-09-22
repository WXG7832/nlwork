package com.nlteck.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.logic2.Logic2Environment.LogicState;
import com.nltecklib.protocol.li.logic2.Logic2PickupData;
import com.nltecklib.protocol.li.logic2.Logic2StateData;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnOpt;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalculateData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverChannelTemperData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverHeartbeatData;
import com.nltecklib.protocol.power.driver.DriverInfoData;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.nltecklib.protocol.power.driver.DriverModuleSwitchData;
import com.nltecklib.protocol.power.driver.DriverOperateData;
import com.nltecklib.protocol.power.driver.DriverPickupData;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;
import com.nltecklib.protocol.power.driver.DriverPoleData;
import com.nltecklib.protocol.power.driver.DriverProtectData;
import com.nltecklib.protocol.power.driver.DriverResumeData;
import com.nltecklib.protocol.power.driver.DriverStepData;
import com.nltecklib.protocol.power.driver.DriverUpgradeData;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年1月12日 下午3:02:24 驱动板控制服务模块 动力电池主控直接与驱动板模块通信，已去除逻辑板和回检板
 */
public abstract class AbsDriverBoardService {

	protected MainBoard mainboard;
	private Logger logger;

	protected AbsDriverBoardService(MainBoard mainboard) {

		this.mainboard = mainboard;
		try {
			logger = LogUtil.createLog("log/driverboardService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 采集驱动板数据
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverPickupData pickupDriver(int driverIndex) throws AlertException;

	/**
	 * 采集驱动板通道温度
	 * 
	 * @author wavy_zheng 2022年7月7日
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverChannelTemperData pickupTemperature(int driverIndex) throws AlertException;

	/**
	 * 写入极性
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writePole(DriverPoleData data) throws AlertException;

	/**
	 * 心跳检测
	 * 
	 * @author wavy_zheng 2022年1月17日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeHeartbeat(DriverHeartbeatData data) throws AlertException;

	/**
	 * 读取驱动板信息
	 * 
	 * @author wavy_zheng 2022年1月17日
	 * @param driverIndex
	 * @param chnIndexInDriver
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverInfoData readDriverInfo(int driverIndex, int chnIndexInDriver) throws AlertException;

	/**
	 * 写入基础的超压，超流等保护
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeBaseProtect(DriverProtectData data) throws AlertException;

	/**
	 * 基础通道操作
	 * 
	 * @author wavy_zheng 2022年1月17日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeOperate(DriverOperateData data) throws AlertException;

	/**
	 * 写入流程
	 * 
	 * @author wavy_zheng 2022年1月17日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeSteps(DriverStepData data) throws AlertException;

	/**
	 * 流程接续配置
	 * 
	 * @author wavy_zheng 2022年1月17日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeResume(DriverResumeData data) throws AlertException;
	
	/**
	 * 开始采集工作
	 * @author  wavy_zheng
	 * 2022年8月8日
	 * @param driverIndex
	 */
	public void pickupDriverData(int driverIndex) {
		
		DriverBoard driver = mainboard.getDriverByIndex(driverIndex);
		
		driver.setSendPickupCount(driver.getSendPickupCount() + 1);
		try {

			DriverPickupData pickupData = pickupDriver(driverIndex);

			// System.out.println("pickupData " + pickupData);
			pickupData.setDriverIndex(driverIndex); // 注意板号
			driver.setRecvPickupCount(driver.getSendPickupCount()); // 采集次数和发送同步
			driver.setTemp1(pickupData.getTemperature1());
			driver.setTemp2(pickupData.getTemperature2());
			driver.setDriverMode(pickupData.getDriverMode());

			boolean run = false;
			// 将采集到的数据推送raw缓存区
			
			for (ChnDataPack chnData : pickupData.getPacks()) {

				Channel channel = driver.getChannel(chnData.getChnIndex());
				if (MainBoard.startupCfg.getLogCfg().printChnPickLog >= 0
						&& MainBoard.startupCfg.getLogCfg().printChnPickLog < mainboard
								.getTotalChnCount()) {

					if (channel.getState() == ChnState.RUN) {
						channel.logPickup(chnData); // 打印底层采样数据

						run = true;
					}
				}
				

				channel.pushRawData(chnData);
				Context.getDataProvider().provideChannelData(channel); // 打包

			}
			
			
			// 提示数据已经采集完毕，等待推送给PC
			driver.setPickupOver(true);

			// if(driver.getSendPickupCount() % 5 == 0 && mainboard.isOffline() && run ) {
			//
			// Environment.infoLogger.info("discover mainboard offline,but driver is running
			// ,force pause ");
			// Context.getCoreService().emergencyPause(null);
			// }

			// 采集通道温度
			if (MainBoard.startupCfg.getDriversCfg().useTempPick && driver.getSendPickupCount() % 10 == 0) {

				DriverChannelTemperData dctd = pickupTemperature(driverIndex);
				if (dctd != null) {
					dctd.setDriverIndex(driverIndex);
					for (int n = 0; n < dctd.getTempProbes().size(); n++) {

						Channel chn = mainboard.getDriverByIndex(driverIndex).getChannel(n);
						chn.setTemperature(dctd.getTempProbes().get(n));

					}
				}

			}

			if (driver.getTemp1() > MainBoard.startupCfg.getDriverTemperatureAlert()
					&& driver.getTemp2() > MainBoard.startupCfg.getDriverTemperatureAlert()) {

				if (driver.isAnyChnRunning()) {

					driver.appendDriverCaches(pickupData);
					if (driver.listDriverCaches().size() >= 5) {
						driver.clearDriverCaches();

						List<Channel> runList = driver.getChannels().stream()
								.filter(x -> x.getState() == ChnState.RUN).collect(Collectors.toList());
						// 暂停驱动板
						Context.getCoreService().executeChannelsProcedure(ChnOpt.PAUSE,
								runList.toArray(new Channel[0]));

						for (Channel chn : runList) {

							chn.pushMsgQueue(
									"因驱动板温度" + driver.getTemp1() + "℃超过安全温度"
											+ MainBoard.startupCfg.getDriverTemperatureAlert() + "℃，暂停通道",
									AlertCode.NORMAL);

						}

					}

				} else {

					driver.clearDriverCaches();
				}

			} else {

				driver.clearDriverCaches();
			}

		} catch (AlertException e) {

			e.printStackTrace();
			logger.error(CommonUtil.getThrowableException(e));
			if (driver.getRecvPickupCount() != -1
					&& driver.getSendPickupCount() - driver.getRecvPickupCount() > 30) {

				driver.setRecvPickupCount(-1); // 防止重复报警

				// 驱动板长时间失联，开始切断逆变电源，暂停所有流程
				try {
					Context.getAlertManager().handle(MainEnvironment.AlertCode.DEVICE_ERROR,
							I18N.getVal(I18N.DriverCommError, driverIndex + 1), false);
				} catch (AlertException ex) {

					ex.printStackTrace();
				}

			}
		} catch (Throwable t) {

			// 防止线程退出
			logger.error(CommonUtil.getThrowableException(t));

		}
		
	}
	

	/**
	 * 驱动板开始采集工作
	 * 
	 * @author wavy_zheng 2022年1月17日
	 */
	public synchronized void startWork(int driverIndex) {

		DriverBoard driver = mainboard.getDriverByIndex(driverIndex);
		if (!driver.isUse()) {

			return;
		}

		if (driver.getExecutor() == null) {

			long pickupTime = MainBoard.startupCfg.getDriverInfo(driverIndex).pickupTime;
			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {

				     pickupDriverData(driverIndex);

				}

			}, 100, pickupTime, TimeUnit.MILLISECONDS);

			// 设置采集线程
			System.out.println(driver);
			driver.setExecutor(executor);

		}

	}

	/**
	 * 驱动板停止采集工作
	 * 
	 * @author wavy_zheng 2022年1月17日
	 */
	public synchronized void stopWork(int driverIndex) {

		DriverBoard driver = mainboard.getDriverByIndex(driverIndex);
		System.out.println(driver);
		if (driver.getExecutor() != null) {

			System.out.println("start to exit driver " + driverIndex + " thread");
			CommonUtil.exitThread(driver.getExecutor(), 2000);
			driver.setExecutor(null);
			System.out.println("ok,stop " + driverIndex + " driverboard work thread!");
		}

	}

	/**
	 * 升级驱动板程序
	 * 
	 * @author wavy_zheng 2022年1月22日
	 * @param upgrade
	 * @throws AlertException
	 */
	public abstract void writeUpgrade(DriverUpgradeData upgrade) throws AlertException;

	/******************************* 校准协议 *********************************/

	/**
	 * 修改驱动板的工作模式
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeWorkMode(DriverModeData data) throws AlertException;

	/**
	 * 模片使能开关
	 * 
	 * @author wavy_zheng 2022年1月17日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeModuleSwitch(DriverModuleSwitchData data) throws AlertException;

	/**
	 * 配置校准点
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeCalibrate(DriverCalibrateData data) throws AlertException;

	/**
	 * 读取校准点,查询ADC
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverCalibrateData readCalibrate(int driverIndex, int chnIndexInDriver) throws AlertException;

	/**
	 * 配置计量点
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeCalculate(DriverCalculateData data) throws AlertException;

	/**
	 * 写入校准系数
	 * 
	 * @author wavy_zheng 2022年1月23日
	 * @param flash
	 * @throws AlertException
	 */
	public abstract void writeFlash(DriverCalParamSaveData flash) throws AlertException;

	/**
	 * 读取计量点参数
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param driverIndex
	 * @param chnIndexInDriver
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverCalculateData readCalculate(int driverIndex, int chnIndexInDriver) throws AlertException;

	/**
	 * 从flash读出校准系数
	 * 
	 * @author wavy_zheng 2022年1月12日
	 * @param driverIndex
	 * @param chnIndexInDriver
	 * @throws AlertException
	 */
	public abstract DriverCalParamSaveData readFlash(int driverIndex, int chnIndexInDriver,int moduleIndex) throws AlertException;

	/**
	 * 读取当前驱动板所有通道的对接电压
	 * 
	 * @author wavy_zheng 2022年1月23日
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverMatchAdcData readMatchAdcs(int driverIndex) throws AlertException;

	/**
	 * 获取驱动板自检信息
	 * 
	 * @author wavy_zheng 2022年1月23日
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverCheckData readDriverSelfCheckInfo(int driverIndex) throws AlertException;

	/**
	 * 获取驱动板软件版本信息
	 * 
	 * @author wavy_zheng 2022年1月23日
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract DriverInfoData readDriverSoftInfo(int driverIndex) throws AlertException;

}
