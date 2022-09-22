package com.nlteck.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.IDType;
import com.nltecklib.protocol.li.check2.Check2UUIDData;
import com.nltecklib.protocol.li.logic2.Logic2BatExistData;
import com.nltecklib.protocol.li.logic2.Logic2BatExistSwitchData;
import com.nltecklib.protocol.li.logic2.Logic2CalMatchData;
import com.nltecklib.protocol.li.logic2.Logic2CalProcessData;
import com.nltecklib.protocol.li.logic2.Logic2CalculateData;
import com.nltecklib.protocol.li.logic2.Logic2CheckFlashWriteData;
import com.nltecklib.protocol.li.logic2.Logic2DeviceProtectData;
import com.nltecklib.protocol.li.logic2.Logic2DeviceProtectExData;
import com.nltecklib.protocol.li.logic2.Logic2Heartbeat;
import com.nltecklib.protocol.li.logic2.Logic2ModuleSwitchData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.OptMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.SwitchState;
import com.nltecklib.protocol.li.logic2.Logic2FaultCheckData;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.logic2.Logic2PickupData;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.logic2.Logic2PoleData;
import com.nltecklib.protocol.li.logic2.Logic2PoleExData;
import com.nltecklib.protocol.li.logic2.Logic2ProcedureData;
import com.nltecklib.protocol.li.logic2.Logic2ProgramStateData;
import com.nltecklib.protocol.li.logic2.Logic2SoftversionData;
import com.nltecklib.protocol.li.logic2.Logic2StartupInitData;
import com.nltecklib.protocol.li.logic2.Logic2StateData;
import com.nltecklib.protocol.li.logic2.Logic2SyncStepSkipData;
import com.nltecklib.protocol.li.logic2.Logic2UUIDData;
import com.nltecklib.protocol.li.logic2.Logic2UpgradeData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月13日 下午1:35:49 逻辑板核心服务，包括采集，发送命令等
 * 动力电池系统架构已经弃用逻辑板
 */
@Deprecated
public abstract class AbsLogicBoardService {

	protected MainBoard mainboard;
	private Logger logger;
	

	protected AbsLogicBoardService(MainBoard mainboard) {

		this.mainboard = mainboard;
		try {
			logger = LogUtil.createLog("log/logicboardService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 启动逻辑板工作
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param logicIndex
	 */
	public synchronized void startWork(int logicIndex) {

//		LogicBoard logicboard = mainboard.getLogicBoards().get(logicIndex);
//		if (!logicboard.isUse() && !logicboard.isCommOk() /* && !logicboard.isProgramBurnOk() */) {
//
//			return;
//		}
//		if (logicboard.getExecutor() == null) {
//
//			long pickupTime = logicboard.getPickupTimeSpan();
//			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//			executor.scheduleAtFixedRate(new Runnable() {
//
//				@Override
//				public void run() {
//
//					boolean pickupOver = false; // 是否采集完一轮
//
//					logicboard.setSendPickupCount(logicboard.getSendPickupCount() + 1);
//
//					// 采集
//					try {
//						int pickDriverIndex = logicboard.getPickupDriverIndex();
//						if ((logicboard.getDriverEnableFlag() & 0x01 << pickDriverIndex) > 0) {
//
//							Logic2PickupData pickupData = pickupDriver(logicboard.getLogicIndex(),
//									logicboard.getActualDriverIndex(pickDriverIndex));
//							
//							
//							pickupData.setDriverIndex(logicboard.getActualDriverIndex(pickupData.getDriverIndex())); // 注意板号
//							logicboard.setRecvPickupCount(logicboard.getSendPickupCount()); //采集次数和发送同步
//
//							DriverBoard db = logicboard.getDrivers().get(pickDriverIndex);
//							
//
//							try {
//								db.checkTemp(0 , pickupData.getTemperature1());
//								db.checkTemp(1 , pickupData.getTemperature2());
//							} catch (AlertException ae) {
//                                
//								ae.printStackTrace();
//								
//							}
//							db.setTemp1(pickupData.getTemperature1());
//							db.setTemp2(pickupData.getTemperature2());
//
//							// 将采集到的数据推送raw缓存区
//							for (ChnData chnData : pickupData.getChnDatas()) {
//
//								Channel channel = logicboard.getDrivers().get(pickDriverIndex)
//										.getChannel(Context.getChannelIndexService().
//												getDriverChnIndexByActual(chnData.getChnIndex()));
//								/* logicboard.getChannelByLogicChnIndex(chnData.getChnIndex()) */;
//								//channel.pushRawData(chnData);
//								Context.getDataProvider().provideChannelData(channel); // 打包
//
//							}
//						}
//
//						if (++pickDriverIndex % MainBoard.startupCfg.getLogicDriverCount() == 0) {
//
//							pickDriverIndex = 0;
//							pickupOver = true;
//						}
//						// 更新采集驱动板号
//						logicboard.setPickupDriverIndex(pickDriverIndex);
//						
//						//自动在同一个线程里采集备份版信息
//						//Context.getCheckboardService().pickCheckboardData(logicIndex);
//
//						if (pickupOver) {
//
//							Context.getDataProvider().pushRuntimeCacheToPC(logicIndex); // 推送数据
//
//						}
//
//					} catch (AlertException e) {
//
//						e.printStackTrace();
//						logger.error(CommonUtil.getThrowableException(e));
//						if(logicboard.getRecvPickupCount() != -1 && logicboard.getSendPickupCount() - logicboard.getRecvPickupCount() > 30) {
//							
//							logicboard.setRecvPickupCount(-1); //防止重复报警
//							
//							//逻辑板长时间失联，开始切断逆变电源，暂停所有流程
//							try {
//								Context.getAlertManager().handle(MainEnvironment.AlertCode.DEVICE_ERROR,
//										I18N.getVal(I18N.LogicCommError,logicboard.getLogicIndex() + 1), false);
//							} catch (AlertException ex) {
//
//								ex.printStackTrace();
//							}
//							
//						}
//						
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
//			}, 1000, pickupTime, TimeUnit.MILLISECONDS);
//			logicboard.setExecutor(executor);
//
//		}

	}

	/**
	 * 停止逻辑板工作
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param logicIndex
	 */
	public synchronized void stopWork(int logicIndex) {

//		LogicBoard logicboard = mainboard.getLogicBoards().get(logicIndex);
//		if (logicboard.getExecutor() != null) {
//
//			CommonUtil.exitThread(logicboard.getExecutor(), (int) logicboard.getCommTimeout());
//			logicboard.setExecutor(null);
//			System.out.println("ok,stop " + logicIndex + " logicboard work thread!");
//			// logicboard.getExecutor().shutdown();
//			// logicboard.setExecutor(null);
//		}

	}

	/**
	 * 采集逻辑板中单板数据
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param logicIndex
	 *            逻辑板序号，0开始
	 * @param driverIndexInLogic
	 *            驱动板在逻辑板内的序号
	 * @return
	 */
	public abstract Logic2PickupData pickupDriver(int logicIndex, int driverIndexInLogic) throws AlertException;

	/**
	 * 注意：逻辑板在整个设备运行中只扮演底层执行角色，并不会保存通道或设备的状态及数据
	 * 所有的状态和数据由主控负责完成；因此逻辑板并不知道通道是暂停还是保护，它只负责通道开和关的命令执行！ 对逻辑板通道或整板进行开或关操作;
	 * 在新版本中，在流程模式为异步执行情况下，逻辑板将负责挣个流程的步次转步，此时该方法就只作为初始启动使用1次！ 暂停，停止，恢复，保护，启动
	 * 都将使用该命令执行
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param om
	 *            同步还是异步；同步模式下将对逻辑板所有通道进行操作，此时channels参数无意义，可以为空
	 * @param ss
	 *            通道开关？
	 * @param channels
	 *            异步模式下，将要执行命令的通道集合
	 * @throws AlertException
	 */
	public abstract void operateChns(int logicIndex, OptMode om, SwitchState ss, List<Channel> channels)
			throws AlertException;

	/**
	 * 逻辑板必须下发流程后才能正常打开通道，否则返回错误
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param procedure
	 * @throws AlertException
	 */
	public abstract void writeProcedure(Logic2ProcedureData procedure) throws AlertException;

	/**
	 * 下发逻辑板接续的状态，一般用于暂停恢复时告诉逻辑板在暂停前的状态
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param logicIndex
	 * @param recovery
	 * @throws AlertException
	 */
	public abstract void writeProceedStates(Logic2StartupInitData recovery) throws AlertException;

	/**
	 * 注意逻辑板的参数一般只下发，不读取;所有的参数流程等数据参数都保存在主控内部 给逻辑板下发极性保护
	 * 
	 * @author wavy_zheng 2020年10月24日
	 * @param poleData
	 * @throws AlertException
	 */
	public abstract void writePole(Logic2PoleData poleData) throws AlertException;
	
	/**
	 * 适用于单驱动独立流程时，给逻辑板多个驱动板下发极性配置
	 * @author  wavy_zheng
	 * 2021年6月23日
	 * @param poleData
	 * @throws AlertException
	 */
	public abstract void writePoleEx(Logic2PoleExData poleData) throws AlertException;

	/**
	 * 给逻辑板下发设备保护
	 * 
	 * @author wavy_zheng 2020年10月24日
	 * @param protect
	 * @throws AlertException
	 */
	public abstract void writeDeviceProtect(Logic2DeviceProtectData protect) throws AlertException;
	
	
	/**
	 * 给逻辑板下多个驱动板下发设备超压保护，适用于多板独立流程环境
	 * @author  wavy_zheng
	 * 2021年6月23日
	 * @param protect
	 * @throws AlertException
	 */
	public abstract void writeDeviceProtectEx(Logic2DeviceProtectExData protect) throws AlertException;
	

	/**
	 * 
	 * 通知逻辑板如何判断电芯是否存在? 必须先打开开关
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeBatsExist(Logic2BatExistData data) throws AlertException;
	
	
	/**
	 * 是否打开通过上位机扫码情况来判定电芯是否存在的开关
	 * @author  wavy_zheng
	 * 2021年2月15日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeBatsExistSwitch(Logic2BatExistSwitchData data) throws AlertException;
	

	/**
	 * 修改逻辑板的工作模式
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeWorkMode(Logic2StateData data) throws AlertException;

	/**
	 * 写入配置校准点
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeCalibrateData(Logic2CalProcessData data) throws AlertException;

	/**
	 * 读取校准点ADC
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param logicIndex
	 * @param chnIndexInLogic
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2CalProcessData readCalibrateData(int logicIndex, int chnIndexInLogic) throws AlertException;

	/**
	 * 配置计量点
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeCalculateData(Logic2CalculateData data) throws AlertException;

	/**
	 * 读取计量数据
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param logicIndex
	 * @param chnIndexInLogic
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2CalculateData readCalculateData(int logicIndex, int chnIndexInLogic) throws AlertException;

	/**
	 * 心跳检测,用于校准模式对接模式
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param heartbeat
	 * @throws AlertException
	 */
	public abstract void writeHeartbeatData(Logic2Heartbeat heartbeat) throws AlertException;

	/**
	 * 模片使能
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param data
	 * @throws AlertException
	 */
	public abstract void writeModuleEnabled(Logic2ModuleSwitchData data) throws AlertException;

	/**
	 * 写入驱动板回检板系数
	 * 
	 * @author wavy_zheng 2020年12月31日
	 * @param lcfwd
	 * @throws AlertException
	 */
	public abstract void writeCheckFlash(Logic2CheckFlashWriteData lcfwd) throws AlertException;

	/**
	 * 读取驱动板中回检板flash系数
	 * 
	 * @author wavy_zheng 2020年12月30日
	 * @param logicIndex
	 * @param chnIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2CheckFlashWriteData readCheckFlash(int logicIndex, int chnIndex) throws AlertException;

	/**
	 * 读取基准电压匹配情况
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param logicIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2CalMatchData readBaseVoltMatch(int logicIndex) throws AlertException;

	/**
	 * 写入逻辑板flash参数
	 * 
	 * @author wavy_zheng 2020年12月3日
	 * @param flash
	 * @throws AlertException
	 */
	public abstract void writeCalFlashData(Logic2FlashWriteData flash) throws AlertException;

	/**
	 * 读取逻辑板flash校准参数
	 * 
	 * @author wavy_zheng 2020年12月5日
	 * @param logicIndex
	 * @param chnIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2FlashWriteData readCalFlashData(int logicIndex, int chnIndex) throws AlertException;

	/**
	 * 升级单包
	 * 
	 * @author wavy_zheng 2020年12月4日
	 * @param upgrade
	 * @throws AlertException
	 */
	public abstract void writeUpgradeData(Logic2UpgradeData upgrade , int timeout) throws AlertException;

	/**
	 * 同步步次跳转
	 * 
	 * @author wavy_zheng 2020年12月16日
	 * @param lsskd
	 * @throws AlertException
	 */
	public abstract void writeSyncStepSkip(Logic2SyncStepSkipData lsskd) throws AlertException;

	/**
	 * 写入uuid
	 * 
	 * @author wavy_zheng 2020年12月31日
	 * @param uuidData
	 * @throws AlertException
	 */
	public abstract void writeUuidData(Logic2UUIDData uuidData) throws AlertException;

	/**
	 * 读取uuid
	 * 
	 * @author wavy_zheng 2020年12月31日
	 * @param logicIndex
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2UUIDData readUuidData(int logicIndex, int driverIndex, IDType type) throws AlertException;

	/**
	 * 读取逻辑板故障信息
	 * 
	 * @author wavy_zheng 2021年1月5日
	 * @param logicIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2FaultCheckData readFaultData(int logicIndex) throws AlertException;

	/**
	 * 读取逻辑板及其附属驱动板的程序烧录状态
	 * 
	 * @author wavy_zheng 2021年1月5日
	 * @param logicIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2ProgramStateData readProgramStateData(int logicIndex) throws AlertException;

	/**
	 * 读取逻辑板软件版本
	 * 
	 * @author wavy_zheng 2021年1月15日
	 * @param logicIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract Logic2SoftversionData readSoftversionData(int logicIndex) throws AlertException;

}
