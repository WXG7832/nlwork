package com.nlteck.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.CheckBoard;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.check2.Check2CalProcessData;
import com.nltecklib.protocol.li.check2.Check2CalculateData;
import com.nltecklib.protocol.li.check2.Check2ConfirmCloseData;
import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.check2.Check2FaultCheckData;
import com.nltecklib.protocol.li.check2.Check2Heartbeat;
import com.nltecklib.protocol.li.check2.Check2OverVoltProcessData;
import com.nltecklib.protocol.li.check2.Check2PickupData;
import com.nltecklib.protocol.li.check2.Check2PoleData;
import com.nltecklib.protocol.li.check2.Check2ProgramStateData;
import com.nltecklib.protocol.li.check2.Check2ProtectionSwitchData;
import com.nltecklib.protocol.li.check2.Check2SoftversionData;
import com.nltecklib.protocol.li.check2.Check2StartupData;
import com.nltecklib.protocol.li.check2.Check2UUIDData;
import com.nltecklib.protocol.li.check2.Check2UpgradeData;
import com.nltecklib.protocol.li.check2.Check2VoltProtectData;
import com.nltecklib.protocol.li.check2.Check2WriteCalFlashData;
import com.nltecklib.protocol.li.logic2.Logic2PickupData;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.logic2.Logic2UUIDData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月30日 下午1:10:46 回检板核心服务，包括命令发送，采集
 */
public abstract class AbsCheckBoardService {

	protected MainBoard mainboard;
	private Logger logger;

	public AbsCheckBoardService(MainBoard mainboard) {

		this.mainboard = mainboard;
		try {
			logger = LogUtil.createLog("log/checkboardService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopWork(int checkIndex) {

//		CheckBoard checkboard = mainboard.getCheckBoards().get(checkIndex);
//		if (!checkboard.isUse()) {
//
//			return;
//		}
//		if (checkboard.getExecutor() != null) {
//
//			// checkboard.getExecutor().shutdown();
//			CommonUtil.exitThread(checkboard.getExecutor(), (int) checkboard.getCommTimeout());
//			System.out.println("ok,stop " + checkIndex + " checkboard work thread!");
//			checkboard.setExecutor(null);
//		}

	}
    
	/**
	 * 采集回检板数据
	 * @author  wavy_zheng
	 * 2021年7月17日
	 * @param checkIndex
	 * @throws AlertException
	 */
	public void pickCheckboardData(int checkIndex) throws AlertException {

//		CheckBoard checkboard = mainboard.getCheckBoards().get(checkIndex);
//
//		if (!checkboard.isUse() && !checkboard.isCommOk() /* && !checkboard.isProgramBurnOk() */) {
//
//			return;
//		}
//
//		checkboard.setSendPickupCount(checkboard.getSendPickupCount() + 1);
//
//		// 采集
//
//		int pickDriverIndex = checkboard.getPickupDriverIndex();
//		if ((checkboard.getDriverEnableFlag() & 0x01 << pickDriverIndex) > 0) {
//
//			Check2PickupData pickupData = pickupDriver(checkboard.getCheckIndex(),
//					checkboard.getActualDriverIndex(pickDriverIndex));
//			pickupData.setDriverIndex(checkboard.getActualDriverIndex(pickupData.getDriverIndex())); // 注意板号
//			checkboard.setRecvPickupCount(checkboard.getRecvPickupCount() + 1);
//
//			// 将采集到的回检电压绑定到当前通道对象上
//			for (int n = 0; n < pickupData.getChnDatas().size(); n++) {
//
//				// 转换为实际
//				Check2PickupData.ChnData chnData = pickupData.getChnDatas().get(n);
//				DriverBoard db = mainboard.getLogicBoards().get(checkIndex).getDrivers().get(pickDriverIndex);
//				db.getChannel(Context.getChannelIndexService().getDriverChnIndexByActual(n)).setCheckChnData(chnData);
//			}
//		}
//
//		if (++pickDriverIndex % MainBoard.startupCfg.getLogicDriverCount() == 0) {
//
//			pickDriverIndex = 0;
//		}
//		// 更新采集驱动板号
//		checkboard.setPickupDriverIndex(pickDriverIndex);

	}

	/**
	 * 回检板采集工作线程
	 * 
	 * @author wavy_zheng 2020年11月28日
	 * @param checkIndex
	 */
	public synchronized void startWork(int checkIndex) {

//		CheckBoard checkboard = mainboard.getCheckBoards().get(checkIndex);
//		if (!checkboard.isUse() && !checkboard.isCommOk() /* && !checkboard.isProgramBurnOk() */) {
//
//			return;
//		}
//		if (checkboard.getExecutor() == null) {
//
//			long pickupTime = checkboard.getPickupTimeSpan();
//			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//			executor.scheduleAtFixedRate(new Runnable() {
//
//				@Override
//				public void run() {
//
//					boolean pickupOver = false; // 是否采集完一轮
//
//					checkboard.setSendPickupCount(checkboard.getSendPickupCount() + 1);
//
//					// 采集
//					try {
//						int pickDriverIndex = checkboard.getPickupDriverIndex();
//						if ((checkboard.getDriverEnableFlag() & 0x01 << pickDriverIndex) > 0) {
//
//							Check2PickupData pickupData = pickupDriver(checkboard.getCheckIndex(),
//									checkboard.getActualDriverIndex(pickDriverIndex));
//							pickupData.setDriverIndex(checkboard.getActualDriverIndex(pickupData.getDriverIndex())); // 注意板号
//							checkboard.setRecvPickupCount(checkboard.getRecvPickupCount() + 1);
//
//							// 将采集到的回检电压绑定到当前通道对象上
//							for (int n = 0; n < pickupData.getChnDatas().size(); n++) {
//
//								// 转换为实际
//								Check2PickupData.ChnData chnData = pickupData.getChnDatas().get(n);
//								DriverBoard db = mainboard.getLogicBoards().get(checkIndex).getDrivers()
//										.get(pickDriverIndex);
//								db.getChannel(Context.getChannelIndexService().getDriverChnIndexByActual(n))
//										.setCheckChnData(chnData);
//							}
//						}
//
//						if (++pickDriverIndex % MainBoard.startupCfg.getLogicDriverCount() == 0) {
//
//							pickDriverIndex = 0;
//							pickupOver = true;
//						}
//						// 更新采集驱动板号
//						checkboard.setPickupDriverIndex(pickDriverIndex);
//
//					} catch (AlertException e) {
//
//						e.printStackTrace();
//						logger.error(CommonUtil.getThrowableException(e));
//
//					} catch (Throwable t) {
//
//						// 防止线程退出
//						logger.error(CommonUtil.getThrowableException(t));
//
//					}
//
//				}
//
//			}, 100, pickupTime, TimeUnit.MILLISECONDS);
//
//			checkboard.setExecutor(executor);
//		}

	}

	/**
	 * 配置回检板极性
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param poleData
	 * @throws AlertException
	 */
	public abstract void writePole(Check2PoleData poleData) throws AlertException;

	/**
	 * 读取软件版本
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param checkIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Check2SoftversionData readVersionInfo(int checkIndex) throws AlertException;

	/**
	 * 写入心跳检测
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param heartbeat
	 * @throws AlertException
	 */
	public abstract void writeHearbeat(Check2Heartbeat heartbeat) throws AlertException;

	/**
	 * 采集数据
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param checkIndex
	 * @param driverIndexInCheck
	 * @return
	 * @throws AlertException
	 */
	public abstract Check2PickupData pickupDriver(int checkIndex, int driverIndexInCheck) throws AlertException;

	/**
	 * 写入工作模式
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param startupData
	 * @throws AlertException
	 */
	public abstract void writeWorkMode(Check2StartupData startupData) throws AlertException;

	/**
	 * 写入设备电压保护
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param voltData
	 * @throws AlertException
	 */
	public abstract void writeVoltageProtection(Check2VoltProtectData voltData) throws AlertException;

	/**
	 * 写入回检板校准参数
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param flashData
	 * @throws AlertException
	 */
	public abstract void writeCalibrateFlashData(Check2WriteCalFlashData flashData) throws AlertException;

	/**
	 * 读取回检板校准参数
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param checkIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Check2WriteCalFlashData readCalibrateFlashData(int checkIndex, int chnIndex) throws AlertException;

	/**
	 * 配置校准参数
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeCalibrateData(Check2CalProcessData data) throws AlertException;

	/**
	 * 读取校准参数
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param checkIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Check2CalProcessData readCalibrateData(int checkIndex, int chnIndex) throws AlertException;

	/**
	 * 配置计量参数
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeCalculateData(Check2CalculateData data) throws AlertException;

	/**
	 * 读取计量值
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param checkIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Check2CalculateData readCalculateData(int checkIndex, int chnIndex) throws AlertException;

	/**
	 * 写入回检板flash参数
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param flash
	 * @throws AlertException
	 */
	public abstract void writeFlashData(Check2WriteCalFlashData flash) throws AlertException;

	/**
	 * 写入过压回馈配置
	 * 
	 * @author wavy_zheng 2020年12月4日
	 * @param protectData
	 * @throws AlertException
	 */
	public abstract void writeOverVoltageFeedback(Check2OverVoltProcessData protectData) throws AlertException;

	/**
	 * 写入升级包
	 * 
	 * @author wavy_zheng 2020年12月4日
	 * @param upgrade
	 * @throws AlertException
	 */
	public abstract void writeUpgradeData(Check2UpgradeData upgrade) throws AlertException;
	
	/**
	 * 启用或禁用回检超压保护
	 * @author  wavy_zheng
	 * 2021年9月13日
	 * @param protectData
	 * @throws AlertException
	 */
	public abstract void writeEnableCheckboardProtection(Check2ProtectionSwitchData protectData) throws AlertException ;

	/**
	 * 读取故障信息
	 * 
	 * @author wavy_zheng 2020年12月4日
	 * @param checkIndex
	 * @return
	 * @throws AlertException
	 */
	// public abstract Check2FaultCheckData readFaultData(int checkIndex) throws
	// AlertException;

	/**
	 * 写入确认关闭信息
	 * 
	 * @author wavy_zheng 2020年12月29日
	 * @param confirmData
	 * @throws AlertException
	 */
	public abstract void writeConfirmCloseData(Check2ConfirmCloseData confirmData) throws AlertException;

	/**
	 * 写入uuid
	 * 
	 * @author wavy_zheng 2020年12月31日
	 * @param uuidData
	 * @throws AlertException
	 */
	public abstract void writeUuidData(Check2UUIDData uuidData) throws AlertException;

	/**
	 * 读取uuid
	 * 
	 * @author wavy_zheng 2021年1月5日
	 * @param checkIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Check2UUIDData readUuidData(int checkIndex) throws AlertException;

	/**
	 * 读取程序烧录状态
	 * 
	 * @author wavy_zheng 2021年1月5日
	 * @param checkIndex
	 * @throws AlertException
	 */
	public abstract Check2ProgramStateData readProgramStateData(int checkIndex) throws AlertException;

	/**
	 * 读取回检故障信息
	 * 
	 * @author wavy_zheng 2021年1月5日
	 * @param checkIndex
	 * @throws AlertException
	 */
	public abstract Check2FaultCheckData readFaultInfoData(int checkIndex) throws AlertException;

	/**
	 * 读取软件版本
	 * 
	 * @author wavy_zheng 2021年1月15日
	 * @param checkIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Check2SoftversionData readSoftversionData(int checkIndex) throws AlertException;

}
