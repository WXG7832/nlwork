package com.nlteck.firmware;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.ParameterName;
import com.nlteck.alert.AlertProcessor;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.Customer;
import com.nlteck.service.StartupCfgManager.RangeSection;
import com.nlteck.service.data.DataProcessService;
import com.nlteck.service.data.ProtectionFilterService;
import com.nlteck.util.ArithUtil;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.check.CheckEnvironment;
import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.check2.Check2PickupData;
import com.nltecklib.protocol.li.logic.LogicEnvironment.StepAndLoop;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.AlertData;
import com.nltecklib.protocol.li.main.CCProtectData;
import com.nltecklib.protocol.li.main.CVProtectData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.DCProtectData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.FirstCCProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.OfflinePickupData;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.li.main.SlpProtectData;
import com.nltecklib.protocol.li.main.StartEndCheckData;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;

/**
 * 逻辑板通道模型；设计时此模型存储通道的测试数据，状态等信息；并不参与核心业务计算
 * 
 * @author Administrator
 *
 */
public class Channel {

	public final static int MAX_ATTEMPT_OPEN_COUNT = 4;
	public final static double CC_CV_VOLT_RANGE = 5;
	public static final int MAX_RUNNING_CACHE_SIZE = 100; // 缓存最大采集数据数量
	public static final int MAX_RAW_CACHE_SIZE = 30; // 缓存最大采集数据数量
	public static final int MAX_DEAD_CACHE_SIZE = 25; // 最大缓存过期数据
	public static final int MIN_RUNNING_CACHE_SIZE = 1; // 最小缓存数据

	public static final int MAX_OFFLINE_CACHE_SIZE = 20; // 最大离线缓存采集数量

	private int chnIndexInDriver; // 板内通道序号
	private ChnState state = ChnState.NONE; // 通道状态
	private WorkMode workMode; // 工作模式
	private WorkMode nextWorkmode; // 上一步次模式
	private double stepCapacity; // 当前步次电芯累计容量
	private double stepEnergy; // 当前步次电芯累计能量
	private long stepElapseMiliseconds; // 步次流逝时间,单位ms
	private long stepLastMiliseconds; // 上一个数据的流逝时间
	private long totalMiliseconds; // 流程总消耗时间,不包括暂停
	private double stepStartVotalge; // 步次开始工作电压
	private long  optElapseMiliseconds; //操作开始后累计的时间ms

	private Date procedureStartTime; // 流程启动时间戳
	private Date procedureStopTime; // 流程停止时间戳
	private double offsetValue; // 电压电流超差记录值
	
	private ChannelData  sleepLastData; //休眠最后符合要求的数据

	private ChannelData voltageOffsetProtectionData; // 已经发生了压差保护的数据

	private int leadStepCount = 4; // 步次前几个数据需要特殊处理尖峰数据
	private int leadSyncCount = 2; // 同步恢复延时轮数

	private double accumulateCapacity; // 充放电累计容量
	private double accumulateEnergy; // 充放电累计能量
	private boolean charge; // 是否处于充电模式

	private WorkMode preWorkmode; // 上一次采集的工作模式
	private ChnState preState; // 上一次采集的通道状态
	

	private AlertCode alertCode = AlertCode.NORMAL; // 当前通道报警码
	private String alertInfo; // 报警详情

	private int stepIndex; // 当前步次号；从1开始
	private int loopIndex; // 当前循环次号；从1开始

	private int alertStepIndex; // 报警时的步次序号

	private int nextStep; // 下一个即将启动的步次
	private int nextLoop; // 下一个即将启动的循环

	private boolean isSyncWaitForSkip; // 是否正在等待同步

	private int pushCount; // 推送次数
	private int attemptOpenCount; // 通道尝试启动次数
	private int maxStartupMiliseconds; // 最大启动时间

	private Date lastPickupTime; // 上次采集的时间
	private Date nowPickupTime; // 本次采集的时间
	private Date optStartTime = new Date(); // 操作开始开始时间

	private double voltage; // 最新采集的电压
	private double current; // 最新采集的电流

	private boolean ready; // 准备开始测试状态
	private boolean operated; // 是否已经执行操作

	private boolean isVoltageSteadyFromResume = true; // cv从暂停恢复时，电压是否已经稳定在恒压范围内了?
	private boolean isCurrentSteadyFromResume = true; // cv从暂停恢复时，电流是否已经趋于稳定?
	private boolean resisterOffsetAlert = false; // 是否已经触发一次电阻差报警
	private int continueAlertCount = 0; // 当前通道连续报警次数
	private int udtCountInDeviceRunning = 0; // 设备启动后通道任然处于UDT的异常次数
	
	private long  leadStepMiliseconds; //用于特殊处理前2秒的步次流逝时间
	
	private boolean  synMode; //是否进入了同步模式
	
	private boolean  cvInCccvMode; //在cccv模式下是否已经进入了CV
	

	private double   cccvDeviceVoltage; //cv备份电压节点
	private boolean  inCvCurrentMakeTime; //CV制造电流时期?
	
	
	// 最新备份采集数据
	private Check2PickupData.ChnData checkChnData;

	private DriverBoard driverBoard; // 驱动板
	// 用户操作队列
	private Queue<State> operates = new ConcurrentLinkedQueue<State>();
	private State lastOperateState = State.NORMAL; // 用户最近一次操作记录

	private ChannelData lastPushData; // 上次推送的数据
	private ChannelData lastSaveOfflineData; // 上次保存的离线数据
	private Logger logger;
	private Logger pickupLogger;
    private ChannelData udtData; //待测数据
	
	private List<ChnDataPack> rawCaches = Collections.synchronizedList(new LinkedList<ChnDataPack>()); // 未处理的逻辑板采集数据
	private List<ChnDataPack> rawNotCloseCaches = Collections.synchronizedList(new LinkedList<ChnDataPack>()); // 未正常关闭的逻辑板采集数据

	private List<ChannelData> runtimeCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 未发送的缓存数据，用于触发报警计算
	private List<ChannelData> deadCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 已发送的缓存数据
	private List<ChannelData> exceptionCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 普通异常数据缓存
	private List<ChannelData> importCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 重要保护数据缓存,如电压线脱落引起的电压异常
	private List<ChannelData> overStepCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 缓存过充或过放等异常数据
	private List<ChannelData> overStepSubcaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 缓存过充或过放等异常数据
	private List<ChannelData> slopeCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 用于斜率保护的缓存数据
	

	private List<ChannelData> offlineCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 离线运行缓存队列
	private Queue<ChannelData> alertCaches = new ConcurrentLinkedQueue<ChannelData>();
	private Map<ChannelData, AlertData> alertMap = new HashMap<ChannelData, AlertData>();
	private List<ChannelData> touchCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 压差保护数据缓存区
	private List<ChannelData> constCheckCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 恒定值偏离记录
	private List<ChannelData> overVoltCaches = Collections.synchronizedList(new LinkedList<ChannelData>()); // 电压超压后连续上升记录,引发设备超压
	
	private List<SmartPickupData> smartCaches = Collections.synchronizedList(new LinkedList<SmartPickupData>()); //智能保护需要采集的电压缓存
	
	
	private int  sleepZeroCount = 0 ; //0步次问题
	

	private boolean logicCalFlashOk; // 逻辑校准系数
	private boolean checkCalFlashOk; // 回检校准系数
	private long runMiliseconds; // 累计充放时间
	private ChnDataPack lastRawData; // 上一次的原始采集数据
	private double  temperature; //温度

	/*********************** 用于减少超压和反极性误报警 ******************************/
	// private int monitorVoltageCount; // 正在监视电压升高次数
	private int monitorPoleCount; // 正在监视极性反接次数
	private long checkMonitorVoltSt; // 回检板监视电压起始时间点
	// private double overVoltage; // 超压快照
	private boolean checkboardMonitor; // 回检板正在监视超压

	/***********************************************************************/

	public Channel(int index, DriverBoard driverBoard) {

		this.chnIndexInDriver = index;
		this.driverBoard = driverBoard;
		try {
			logger = LogUtil.createLog("log/channel/chn" + (getDeviceChnIndex() + 1) + ".log");
			pickupLogger = LogUtil.createLog("log/channel/chn" + (getDeviceChnIndex() + 1) + "_pickup.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ChnState getState() {
		return state;
	}

	public void setState(ChnState state) {
		this.state = state;

	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public WorkMode getNextWorkmode() {
		return nextWorkmode;
	}

	public double getStepCapacity() {
		return stepCapacity;
	}

	/**
	 * 检测该通道是否已经停止,此流程不会再运行
	 * 
	 * @return
	 */
	public boolean isClosed() {

		return state == ChnState.CLOSE || state == ChnState.STOP || state == ChnState.ALERT
				|| state == ChnState.COMPLETE || state == ChnState.NONE;
	}

	public long getStepElapseMiliseconds() {
		return stepElapseMiliseconds;
	}

	private int accumulateStepElapseSeconds() {

		if (lastPickupTime == null) {

			return (int) stepElapseMiliseconds;
		}
		stepElapseMiliseconds += calculateElapsedSeconds(lastPickupTime, null);

		return (int) stepElapseMiliseconds;
	}

	private int accumulateProcedureElapseSeconds() {

		return (int) calculateElapsedSeconds(procedureStartTime, procedureStopTime);
	}

	/**
	 * 计算当前时间和指定时间的间隔秒数，取整，不进行4舍5入
	 * 
	 * @param dateSt
	 * @return
	 */
	private long calculateElapsedSeconds(Date dateSt, Date dateEd) {

		if (dateSt == null) {

			return 0;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(dateSt);
		c.set(Calendar.MILLISECOND, 0);
		Date st = c.getTime();

		c.setTime(dateEd == null ? new Date() : dateEd);
		c.set(Calendar.MILLISECOND, 0);
		Date ed = c.getTime();

		return (ed.getTime() - st.getTime()) / 1000;
	}

	/**
	 * 设备未运行的通道状态处理
	 */
	private void processWaitState(ChnData rawChnData) {

		if (state == ChnState.RUN || state == ChnState.PAUSE) {

			return; // 不处理运行状态
		}

		if (getControlUnit().getState() == State.FORMATION || getControlUnit().getState() == State.PAUSE) {

			return; // 在设备分区运行或暂停时不处理通道
		}

		if (state == ChnState.NONE) {

			if (rawChnData.getState() != Logic2Environment.ChnState.NONE) {

				state = ChnState.UDT;

			}

		} else {

			if (state != ChnState.CLOSE) {
				if (rawChnData.getState() == Logic2Environment.ChnState.NONE) {

					ChnState oldState = state;
					state = ChnState.NONE;

				} else if (rawChnData.getState() == Logic2Environment.ChnState.UDT) {

					if ((state != ChnState.STOP && state != ChnState.COMPLETE && state != ChnState.CLOSE
							&& state != ChnState.ALERT && state != ChnState.UDT)
							|| (state == ChnState.ALERT && alertCode == AlertCode.POLE_REVERSE)) { // 反极性可以自己恢复

						System.out.println("chn" + (getDeviceChnIndex() + 1) + " change " + state + " -> " + "UDT");
						ChnState oldState = state;
						state = ChnState.UDT;

					}

				}
			}

		}

	}

	/**
	 * 特殊处理恒流源电流数据
	 * 
	 * @author wavy_zheng 2020年10月16日
	 * @param programCurrent
	 * @param current
	 * @return
	 */
	private double processConstCurrentData(double programCurrent, double current) {

		double currentPrecide = MainBoard.startupCfg.getCurrentPrecide(); // 普通电流精度
		double threshold = MainBoard.startupCfg.getDoublePrecision().threshold;
		double highCurrentPrecide = MainBoard.startupCfg.getDoublePrecision().precision;
		if (programCurrent <= threshold) {

			currentPrecide = highCurrentPrecide; // 采用高精度
		}
		if (Math.abs(current - programCurrent) < currentPrecide * 15) {
			while (Math.abs(current - programCurrent) > currentPrecide) {

				current = programCurrent + (programCurrent - current) / 4;
			}
		}
		return current;

	}

	/**
	 * 特殊处理cc-cv转点数据
	 * 
	 * @author wavy_zheng 2020年5月11日
	 * @param turnVoltage
	 *            转点电压
	 * 
	 * @return
	 */
	private double processTurningCCData(double turnVoltage, double programCurrent, double voltage, double current) {

		double precision = MainBoard.startupCfg.getVoltagePreCide();

		// System.out.println("turn voltage = " + turnVoltage + ",voltage=" + voltage +
		// ",precision * 5 = " + precision * 5);
		if (Math.abs(turnVoltage - voltage) <= precision * 10) {

			double currentPreside = getCurrentPrecideByProgramCurrent(programCurrent);

			// 快进入cv转点,对电流进行滤波处理
			while (Math.abs(current - programCurrent) > currentPreside) {

				current = programCurrent + (programCurrent - current) / 4;
			}

		}

		return current;

	}

	/**
	 * 根据当前步次模型获取匹配档位信息
	 * 
	 * @author wavy_zheng 2020年8月7日
	 * @param step
	 * @return
	 */
	private double getCurrentPrecideByProgramCurrent(double programCurrent) {

		if (MainBoard.startupCfg.getRange().use) {

			// 采用最新多量程模式
			for (RangeSection rs : MainBoard.startupCfg.getRange().sections) {

				if (rs.lower <= programCurrent && rs.upper > programCurrent) {

					return rs.precision;
				}

			}

			return MainBoard.startupCfg.getCurrentPrecide();

		} else {

			// 采用旧的双档位或单量程模式
			boolean useDoublePrecision = MainBoard.startupCfg.getDoublePrecision().use;

			if (useDoublePrecision) {

				if (programCurrent <= MainBoard.startupCfg.getDoublePrecision().threshold) {

					return MainBoard.startupCfg.getDoublePrecision().precision;
				} else {

					return MainBoard.startupCfg.getCurrentPrecide();
				}
			}

		}

		return 0;

	}

	/**
	 * 恒压处理
	 * 
	 * @author wavy_zheng 2020年8月7日
	 * @param destVoltage
	 * @param voltage
	 * @return
	 */
	private double processTurningCVData(double destVoltage, double voltage) {

		double precision = MainBoard.startupCfg.getVoltagePreCide();

		if (Math.abs(destVoltage - voltage) <= precision * 15) {

			// 快进入cv转点,对电流进行滤波处理
			while (Math.abs(voltage - destVoltage) > precision) {

				voltage += (destVoltage - voltage) / 4; // 慢慢调整接近恒压值
			}

		}

		return voltage;

	}

	/**
	 * 计算容量和能量
	 * 
	 * @param voltage
	 * @param current
	 */
	public void accumulateCapacityAndEnergy(Date now, double voltage, double current) {

		if (lastPickupTime == null) {

			lastPickupTime = new Date(); // 第一次设置时间点
			return;
		}
		double deltaCapacity = getDeltaCapacity(current, now.getTime() - lastPickupTime.getTime());

		stepCapacity = ArithUtil.add(stepCapacity, deltaCapacity);
		accumulateCapacity = ArithUtil.add(accumulateCapacity, deltaCapacity);

		double deltaEnergy = getDeltaEnergy(deltaCapacity, voltage);
		// 计算能量
		stepEnergy = ArithUtil.add(stepEnergy, deltaEnergy);
		accumulateEnergy = ArithUtil.add(accumulateEnergy, deltaEnergy);

	}

	/**
	 * 累计容量值
	 * 
	 * @param current
	 *            : 单位mA
	 */
	public double accumulateCapacity(double current) {

		if (lastPickupTime == null) {

			return stepCapacity;
		}
		// long systime = System.currentTimeMillis();
		Date date = new Date();
		double deltaCapacity = getDeltaCapacity(current, date.getTime() - lastPickupTime.getTime());

		stepCapacity = ArithUtil.add(stepCapacity, deltaCapacity);
		accumulateCapacity = ArithUtil.add(accumulateCapacity, deltaCapacity);

		/**
		 * test
		 */
		// double dc = getDeltaCapacityOld(current,systime- lastPickupTime.getTime());
		// stepTestCapacity += dc;

		return stepCapacity;
	}

	/**
	 * 累计能量值
	 * 
	 * @param voltage
	 * @return
	 */
	public double accumulateEnergy(double voltage, double current) {

		if (lastPickupTime == null) {

			return stepEnergy;
		}
		double deltaEnergy = getDeltaEnergy(voltage, current, System.currentTimeMillis() - lastPickupTime.getTime());
		stepEnergy = ArithUtil.add(stepEnergy, deltaEnergy);
		accumulateEnergy = ArithUtil.add(accumulateEnergy, deltaEnergy);
		return stepEnergy;

	}

	/**
	 * 通过容量差计算能量差
	 * 
	 * @param deltaCapacity
	 * @param voltage
	 * @return
	 */
	private double getDeltaEnergy(double deltaCapacity, double voltage) {

		return ArithUtil.div(ArithUtil.mul(deltaCapacity, voltage), 1000);
	}

	private double getDeltaEnergy(double voltage, double current, long timeElapsed) {

		double deltaTime = ArithUtil.div(timeElapsed, 1000 * 3600);
		return ArithUtil.div(ArithUtil.mul(ArithUtil.mul(current, voltage), deltaTime), 1000);
	}

	private double getDeltaCapacity(double current, long timeElapsed) {

		double deltaTime = ArithUtil.div(timeElapsed, 1000 * 3600);
		return ArithUtil.mul(current, deltaTime);

	}

	// public List<ChannelData> fetchAllRuntimeCacheData() {
	//
	// List<ChannelData> list = new ArrayList<>();
	// for (Iterator<ChannelData> it = runtimeCaches.iterator(); it.hasNext();) {
	//
	// list.add(it.next());
	// }
	// return list;
	// }

	public List<ChannelData> fetchAllDeadCacheData() {

		List<ChannelData> list = new ArrayList<>();

		for (Iterator<ChannelData> it = deadCaches.iterator(); it.hasNext();) {

			list.add(it.next());
		}
		return list;
	}

	public boolean isCvInCcCvMode(ChannelData rawData) {

		Step step = getProcedureStep(rawData.getStepIndex());
		if (step == null) {

			return false;
		}
		// 检测电压是否在恒定范围以内
		if (rawData.getVoltage() - step.specialVoltage < -CC_CV_VOLT_RANGE) {

			return false;
		}

		RangeSection rs = DataProcessService.findSectionByCurrent(rawData.getCurrent());

		if (rs == null) {

			return false;
		}
		// 检测电流是否下降到恒定值一个精度以下
		if (rawData.getCurrent() > step.specialCurrent - rs.precision || rawData.getCurrent() == 0) {

			return false;
		}

		return true;
	}

	/**
	 * 从cc-转为cv,这里也有可能是连续cc-cc-cv 判断条件 电压稳定在+-5mv以内的某个恒压值 电流低于恒流值一个精度以下 当前模式处于cc模式
	 * 
	 * @param rawData
	 * @return
	 */
	public boolean changeToCV(ChnData rawData) {

		// 是否处于cc模式
		if (workMode != WorkMode.CCC) {

			return false;
		}
		if (!isNextStepCv()) {
			// 下一步次是否cv模式
			return false;
		}

		Step step = getProcedureStep(stepIndex);
		if (step == null) {

			return false;
		}
		if (rawData.getState() == Logic2Environment.ChnState.COMPLETE && isNextStepCv()) {

			// 对于跳转即已完成的，默认全部转成cv
			if (Math.abs(rawData.getAlertVolt() - step.specialVoltage) >= CC_CV_VOLT_RANGE) {

				rawData.setVoltage(CommonUtil.produceRandomNumberInRange(step.specialVoltage, 0.1)); // 修饰电压
				rawData.setAlertVolt(rawData.getVoltage());
			}
			double overthreshold = getProcedureStep(stepIndex + 1).overThreshold;
			if (rawData.getAlertCurrent() >= overthreshold) {

				rawData.setCurrent(overthreshold - 0.1); // 修饰电流
				rawData.setAlertCurrent(overthreshold - 0.1);
			}

		} else {

			// 检测电压是否在恒定范围以内
			if (rawData.getVoltage() - step.specialVoltage < -CC_CV_VOLT_RANGE) {

				return false;
			}
			double currentPrecide = MainBoard.startupCfg.getCurrentPrecide();
			if (MainBoard.startupCfg.getDoublePrecision().use) {

				// 如果是高精度
				if (rawData.getCurrent() <= MainBoard.startupCfg.getDoublePrecision().threshold) {

					currentPrecide = MainBoard.startupCfg.getDoublePrecision().precision; // 采用高精度精度
				}

			}

			// 检测电流是否下降到恒定值一个精度以下
			if (rawData.getCurrent() > step.specialCurrent - currentPrecide || rawData.getCurrent() == 0) {

				return false;
			}

			// 对电压进行过滤
			if (Math.abs(rawData.getVoltage() - step.specialVoltage) > MainBoard.startupCfg.getVoltagePreCide()) {

				double offset = rawData.getVoltage() - step.specialVoltage;
				while (offset > MainBoard.startupCfg.getVoltagePreCide()) {

					offset = offset / 2;
				}
				rawData.setVoltage(step.specialVoltage + offset);
			}
			// 对电流进行过滤
			if (rawData.getCurrent() - step.specialCurrent > MainBoard.startupCfg.getCurrentPrecide()) {

				double offset = rawData.getCurrent() - step.specialCurrent;
				while (offset > MainBoard.startupCfg.getCurrentPrecide()) {

					offset = offset / 2;
				}
				rawData.setCurrent(step.specialCurrent + offset);

			}
			System.out.println(
					"rawData.getCurrent() = " + rawData.getCurrent() + ",step.specialCurrent = " + step.specialCurrent);
		}
		// 转CV
		workMode = WorkMode.CVC;
		stepIndex++;

		// Environment.infoLogger.info("stepCapacity = " + stepCapacity + " vs
		// testStepCapcity = " + stepTestCapacity);
		stepCapacity = 0;

		stepEnergy = 0;
		stepElapseMiliseconds = 0;
		stepStartVotalge = rawData.getVoltage();
		optStartTime = new Date();

		// pushMsgQueue("转入步次," + loopIndex + "-" + stepIndex + ",mode:" + workMode,
		// AlertCode.NORMAL);
		pushMsgQueue(I18N.getVal(I18N.StepInto) + "," + loopIndex + "-" + stepIndex + ",mode:" + workMode,
				AlertCode.NORMAL);

		return true;
	}

	public void clearCapacity() {

		stepCapacity = 0;
	}

	/**
	 * 将产生的离线队列数据发送到网络，并删除离线队列数据
	 */
	public void sendAllOfflineData(List<ChannelData> offlineList) {

		OfflinePickupData opd = new OfflinePickupData();
		opd.setUnitIndex(0);
		opd.setChnIndex(getDeviceChnIndex());
		opd.setChnDataList(offlineList);
		driverBoard.getMainBoard().pushSendQueue(new ResponseDecorator(opd, true));
	}

	public int getChnIndex() {
		return chnIndexInDriver;
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public Date getLastPickupTime() {
		return lastPickupTime;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public double getCurrent() {
		return current;
	}

	public void setCurrent(double current) {
		this.current = current;
	}

	public boolean isReadyToWork() {

		return runtimeCaches.size() >= MAX_RUNNING_CACHE_SIZE - 1;

	}

	public double getStepStartVotalge() {
		return stepStartVotalge;
	}

	/**
	 * 修复通道流程数据
	 * 
	 * @author wavy_zheng 2021年4月14日
	 */
	public void recoveryStep() {

		if (getCurrentProcedure() != null && getCurrentProcedure().getStepMode() == StepMode.SYNC) {

			return; // 同步模式下不会再对保护的通道进行恢复
		}
		if (state == ChnState.ALERT || state == ChnState.STOP) {
			state = ChnState.PAUSE;
			procedureStopTime = null;
			clearAlertCaches();

			clearAlertCaches();
			clearExceptionCaches();
			clearImportantData();
			clearNotClosedRawData();
			clearOverStepCaches();
			clearOverStepSubcaches();
			clearSlopeCaches();
			clearConstCache();
			clearOverVoltCache();
			sleepZeroCount = 0;

			operated = false; // 置位操作状态

			leadStepCount = 4;
			lastPickupTime = null;
			optStartTime = new Date();
			pushOperate(State.FORMATION);

		}

	}

	/**
	 * 
	 * @author wavy_zheng 2021年4月29日
	 * @param resumeAlert
	 *            恢复报警通道
	 */
	public void resumeStep(boolean resumeAlert) {
         
		// 保护的通道可以尝试恢复
		if (resumeAlert) {
			
			if(state == ChnState.RUN && workMode == WorkMode.SLEEP && stepIndex == 0) {
				
				//恢复逻辑板异常关闭的通道
				
			} else if (state != ChnState.PAUSE && state != ChnState.ALERT) {

				return;
			}
		} else {

			if (state != ChnState.PAUSE) {

				return;
			}
		}


		if (state == ChnState.ALERT) {

			state = ChnState.PAUSE;
			procedureStopTime = null;
			clearAlertCaches();

		}
		deadCaches.clear();
		clearAlertCaches();
		clearExceptionCaches();
		clearImportantData();
		clearNotClosedRawData();
		clearOverStepCaches();
		clearOverStepSubcaches();
		clearSlopeCaches();
		clearConstCache();
		clearOverVoltCache();
		sleepZeroCount = 0;

		operated = false; // 置位操作状态

		leadStepCount = 4;
		optStartTime = new Date();
		optElapseMiliseconds = 0;
		attemptOpenCount = 0;
		lastPickupTime = null;
		continueAlertCount = 0; // 复位报警次数
		pushOperate(State.FORMATION);
	}

	public void pauseStep() {

		// 通道在没有准备和运行的情况下无法暂停
		if (state != ChnState.RUN) {

			return;
		}
		optStartTime = new Date();
		lastPickupTime = null;
		optElapseMiliseconds = 0;
		

		// 推入操作队列
		pushOperate(State.PAUSE);
		

	}

	public void stopStep() {

		logger.info("stop step");
		if (state != ChnState.RUN && state != ChnState.PAUSE) {

			return;
		}
		System.out.println("push operation : STOP");
		optStartTime = new Date();
		optElapseMiliseconds = 0;
		lastPickupTime = null; 
		pushOperate(State.STOP);
        
		sleepZeroCount = 0;
		clearImportantData();
		clearExceptionCaches();
		clearOverStepCaches();
		clearOverStepSubcaches();
		clearSlopeCaches();
		clearNotClosedRawData();
		clearConstCache();
		clearTouchData();
		clearOverVoltCache();
	}

	/**
	 * 上传日志
	 * 
	 * @param log
	 */
	public void uploadLog(String log) {

		getDriverBoard().getMainBoard().pushSendQueue(new AlertException(this, AlertCode.NORMAL, log));
	}

	/**
	 * 执行跳步次
	 * 
	 * @param stepMap
	 * @return
	 */
	public Map<Step, Short> executeNextStep(Map<Step, Short> stepMap) {

		if (isNextStepSleep()) {

			return stepMap;
		}

		// Step nextStep = getProcedureStep(getNextStep());
		Step nextStep = requestNextStep();
		if (nextStep == null) {

			return stepMap;
		}
		short openChns = 0; // 打开标记
		if (stepMap.get(nextStep) != null) {

			openChns = stepMap.get(nextStep);
		}

		openChns = (short) (openChns | (0x01 << chnIndexInDriver));

		stepMap.put(nextStep, openChns);

		return stepMap;
	}

	/**
	 * 请求获取跳转到下一个步次
	 */
	public StepAndLoop askNextStep() {

		if (peekOperate() == State.STARTUP) {

			pollOperate();
			return null; // 正在请求跳转中，忽略该动作；防止重复跳转
		}
		ProcedureData pd = getCurrentProcedure();
		StepAndLoop sl = LogicBoard.skipNextStep(pd, stepIndex, loopIndex);

		if (sl.nextStep == 1) {

			if (state == ChnState.CLOSE || state == ChnState.NONE) {

				return null;
			}

		} else {

			if (state != ChnState.RUN) {

				return null;
			}
		}

		if (sl.nextStep > pd.getStepCount()) {

			// 步次结束
			complete();
			return null;
		}

		// 如果是同步模式，开始拦截跳转
		if (pd.getStepMode() == StepMode.SYNC) {

			// 获取当前分区的同步流程步次
			Step syncStep = getControlUnit().getSyncStep();
			Step nextStep = pd.getStep(sl.nextStep - 1);
			logger.info("intercept step skip,nextStep = " + nextStep.stepIndex + ",syncStep = " + syncStep.stepIndex);
			if (nextStep.workMode != WorkMode.SLEEP) {
				if (syncStep != null && (syncStep.stepIndex != sl.nextStep || syncStep.loopIndex != sl.nextLoop)) {

					logger.info("next step " + sl.nextStep + "<> syncStep " + syncStep.stepIndex + "  or next loop "
							+ sl.nextLoop + " <> syncLoop" + syncStep.loopIndex);
					// 拦截进入等待同步状态
					if (State.STARTUP != pollOperate()) { // 将跳转指令取出

						clearOperates();
					}
					// pushMsgQueue("因同步模式进行等待状态", AlertCode.NORMAL);
					pushMsgQueue(I18N.getVal(I18N.WaitingStateDueToSynchronousMode), AlertCode.NORMAL);
					return null;

				}
			}

		}

		nextStep(sl.nextStep, sl.nextLoop, pd.getStep(sl.nextStep - 1).workMode);
		pushOperate(State.STARTUP); // 准备跳转下步次
		return sl;

	}

	/**
	 * 获取下一个步次信息
	 * 
	 * @return
	 */
	public Step requestNextStep() {

		return requestNextStep(this.stepIndex);

	}

	/**
	 * 获取下一个步次信息
	 * 
	 * @param stepIndex
	 *            当前设定的基准步次
	 * @return
	 */
	public Step requestNextStep(int stepIndex) {

		ProcedureData pd = getCurrentProcedure();
		StepAndLoop sl = LogicBoard.skipNextStep(pd, stepIndex, loopIndex);
		if (sl.nextStep <= pd.getStepCount()) {

			return pd.getStep(sl.nextStep - 1);
		}

		return null;

	}

	public void nextStep(int stepIndex, int loopIndex, WorkMode mode) {

		logger.info("set nextStep:" + stepIndex + ",set next workmode = " + mode);
		//optStartTime = new Date(); // 设置预启动时间
		nextWorkmode = mode;
		this.nextStep = stepIndex;
		this.nextLoop = loopIndex;

	}

	public void complete() {

		if (state == ChnState.RUN) {
			// stepIndex = 255; // 步次完成标记
			// loopIndex = 0;
			lastPickupTime = null;
			stepCapacity = 0;
			current = 0;
			voltage = 0;
			stepElapseMiliseconds = 0;
			ChnState oldState = state;
			charge = false;
			state = ChnState.COMPLETE;

			procedureStopTime = new Date();
			continueAlertCount = 0; // 复位累计报警次数
			pushMsgQueue(I18N.getVal(I18N.ProcessCompleted), AlertCode.NORMAL);
			workMode = WorkMode.SLEEP;
			clearOperates();// 复位操作状态

			clearAlertCaches();
			clearExceptionCaches();
			clearImportantData();
			clearNotClosedRawData();
			clearOverStepCaches();
			clearOverStepSubcaches();
			clearSlopeCaches();
			clearConstCache();
			clearTouchData();
			clearOverVoltCache();
			sleepZeroCount = 0;
		}

	}

	/**
	 * 停止必然无电流,无需再判断
	 * 
	 * @param rawData
	 */
	public synchronized void stop() {

		if (state != ChnState.RUN && state != ChnState.PAUSE) {

			if (pollOperate() != State.STOP) {

				clearOperates();

			}
			return;
		}
		procedureStopTime = new Date(); // 结束时间
		// stepIndex = 254;
		current = 0;
		voltage = 0;
		charge = false;
        stepElapseMiliseconds = 0;
		workMode = WorkMode.SLEEP;

		if (state != ChnState.ALERT) {

			ChnState oldState = state;
			state = ChnState.STOP;
			pushMsgQueue(I18N.getVal(I18N.UserStopChn), AlertCode.NORMAL);

		}
		if (pollOperate() != State.STOP) {

			clearOperates();
		}

	}

	/**
	 * 暂停必然无电流
	 * 
	 * @param rawData
	 */
	public synchronized void pause() {

		if (state == ChnState.PAUSE) {

			clearOperates();
			return;
		}
		if (state != ChnState.ALERT) {

			state = ChnState.PAUSE;
			pushMsgQueue(I18N.getVal(I18N.UserPauseChn), AlertCode.NORMAL);
			// 暂停时防止缓存数据量堆积，采用离线数据队列保存

		}
		if (pollOperate() != State.PAUSE) {

			clearOperates(); // 如用户操作和通道状态不符合，则清空操作队列
		}

	}
	
	/**
	 * 启动前清除数据
	 * @author  wavy_zheng
	 * 2021年6月6日
	 */
	public void clearData() {
		
		clearAlertCaches();
		clearAllDeadCaches();
		clearExceptionCaches();
		clearImportantData();
		clearNotClosedRawData();
		clearOverStepCaches();
		clearOverStepSubcaches();
		clearSlopeCaches();
		clearConstCache();
		clearOverVoltCache();
		clearSmartCaches();

		stepIndex = 0;
		loopIndex = 1;
		leadStepCount = 3;
		totalMiliseconds = 0;
		continueAlertCount = 0; // 复位报警次数
		sleepZeroCount = 0;
		
		this.stepCapacity = 0;
		this.stepElapseMiliseconds = 0;
		this.stepLastMiliseconds = 0;
		this.stepEnergy = 0;
		this.accumulateCapacity = 0;
		this.accumulateEnergy = 0;
		this.totalMiliseconds = 0;
		this.attemptOpenCount = 0;
		this.synMode = false;
		this.cvInCccvMode = false;

		setReady(true);
		clearOperates();
	}

	/**
	 * 启动单通道
	 */
	public void startupStep() {

		if (state == ChnState.RUN || state == ChnState.CLOSE || state == ChnState.NONE) {

			return;
		}
         
		
		
		clearData();
		/*clearAlertCaches();
		clearAllDeadCaches();
		clearExceptionCaches();
		clearImportantData();
		clearNotClosedRawData();
		clearOverStepCaches();
		clearOverStepSubcaches();
		clearSlopeCaches();
		clearConstCache();
		clearOverVoltCache();

		stepIndex = 0;
		loopIndex = 1;
		leadStepCount = 3;
		totalSeconds = 0;
		continueAlertCount = 0; // 复位报警次数
		sleepZeroCount = 0;
		
		this.stepCapacity = 0;
		this.stepElapseMiliseconds = 0;
		this.stepLastMiliseconds = 0;
		this.stepEnergy = 0;
		this.accumulateCapacity = 0;
		this.accumulateEnergy = 0;
		this.totalSeconds = 0;
		
		
		

		setReady(true);
		clearOperates();
		*/
		optStartTime = new Date();
		lastPickupTime = null;
		optElapseMiliseconds = 0;
		
		pushOperate(State.STARTUP);

		pushMsgQueue(I18N.getVal(I18N.ChnStartup), AlertCode.NORMAL);

	}

	public void reset() {

		if (state != ChnState.NONE && state != ChnState.CLOSE && state != ChnState.RUN && state != ChnState.PAUSE) {

			ChnState oldState = state;
			state = ChnState.UDT;
			stepCapacity = 0;
			stepIndex = 0;
			loopIndex = 0;
			lastPickupTime = null;
			current = 0;
			voltage = 0;
			procedureStartTime = null;
			procedureStopTime = null;
			clearOperates();
			clearData(); //清空
			setSynMode(false); //清空同步模式

		}

	}

	/**
	 * 上报报警并把通道状态改为报警
	 * 
	 * @param alertData
	 */
	public void uploadAlert(AlertData alertData) {

		driverBoard.getMainBoard().pushSendQueue(new AlertDecorator(alertData));

	}

	/**
	 * 恢复运行状态
	 * 
	 * @param rawData
	 */
	public boolean resume(ChnDataPack rawData) {

		attemptOpenCount = 0; // 清空启动尝试次数
		if (State.FORMATION != pollOperate()) {

			clearOperates();
		}
		state = ChnState.RUN; // 启动
		logger.info("resume ok");
		return false;
	}

	public synchronized void alert(AlertCode alertCode, String alertInfo) {

		lastPickupTime = null;
		// current = 0;
		// voltage = 0;
		// stepIndex = 254;
		// nextStep = 254;
		sleepZeroCount = 0;
		clearImportantData();
		clearExceptionCaches();
		clearOverStepCaches();
		clearOverStepSubcaches();
		clearSlopeCaches();
		clearNotClosedRawData();
		clearConstCache();
		procedureStopTime = new Date();
		ChnState oldState = state;
		if (state != ChnState.CLOSE) {
			state = ChnState.ALERT;
		}
		if (this.alertCode != AlertCode.DEVICE_ERROR) {

			this.alertCode = alertCode;
		}
		if (oldState == ChnState.RUN) {
			plusContinueAlertCount();
		}

		if (continueAlertCount > 3 && this.alertCode != AlertCode.DEVICE_ERROR
				&& this.alertCode != AlertCode.POLE_REVERSE) {

			// 连续报警大于3次
			try {
				Context.getAlertManager().handle(AlertCode.DEVICE_ERROR,
						I18N.getVal(I18N.ChnContinueAlertException, getDeviceChnIndex() + 1), false);
			} catch (AlertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			continueAlertCount = 0; // 复位

		}

		clearOperates();
		if(alertInfo != null) {
		   pushMsgQueue(alertInfo, alertCode); // 推送日志
		}

	}

	public void setLastPickupTime(Date lastPickupTime) {
		this.lastPickupTime = lastPickupTime;
	}

	public void setStepElapseMiliseconds(long stepElapseSeconds) {
		this.stepElapseMiliseconds = stepElapseSeconds;
	}

	public Check2PickupData.ChnData getCheckChnData() {
		return checkChnData;
	}

	public void setCheckChnData(Check2PickupData.ChnData checkChnData) {
		this.checkChnData = checkChnData;
	}

	public DriverBoard getDriverBoard() {
		return driverBoard;
	}

	/**
	 * 获取逻辑板通道序号
	 * 
	 * @return
	 */
//	public int getLogicChnIndex() {
//
//		return driverBoard.getDriverIndex() * MainBoard.startupCfg.getDriverChnCount() + chnIndexInDriver;
//	}


	public int getControlUnitIndex() {

		return getControlUnit().getIndex();

	}


	/**
	 * 返回通道所属的分区对象
	 * 
	 * @author wavy_zheng 2020年7月15日
	 * @return
	 */
	public ControlUnit getControlUnit() {

		for (ControlUnit unit : getMainBoard().getControls()) {

			if (unit.containsDriver(driverBoard)) {

				return unit;
			}
		}

		return null;
	}

	/**
	 * 获取设备通道序号
	 * 
	 * @return
	 */
	public int getDeviceChnIndex() {

		return getDriverBoard().getDriverIndex() * MainBoard.startupCfg.getDriverChnCount() + chnIndexInDriver;
	}

	/**
	 * 取最新的活动区数据
	 * 
	 * @return
	 */
	// public ChannelData fetchLastRuntimeData() {
	//
	// ChannelData lastData = null;
	// for (Iterator<ChannelData> it = runtimeCaches.iterator(); it.hasNext();) {
	//
	// lastData = it.next();
	// }
	// return lastData;
	// }

	/**
	 * 
	 * @return -1表示找不到符合的CC步次；首个CC的步次序号
	 */
	public int getFirstCCStepIndex() {

		ProcedureData pd = null;
		pd = getCurrentProcedure();

		if (pd != null) {
			for (int n = 0; n < pd.getStepCount(); n++) {

				if (pd.getStep(n).workMode == WorkMode.CCC) {

					return n + 1;
				}

			}
		}
		return -1;
	}

	private MainBoard getMainBoard() {

		return driverBoard.getMainBoard();
	}

	/**
	 * 获取通道的当前绑定的流程
	 * 
	 * @return
	 */
	public ProcedureData getCurrentProcedure() {

		return getControlUnit().getProcedure();
	}

	/**
	 * 
	 * @param stepIndex
	 * @return
	 */
	public ProcedureData.Step getProcedureStep(int stepIndex) {

		ProcedureData procedure = getCurrentProcedure();
		if (procedure == null) {

			return null;
		}

		if (stepIndex > 0 && stepIndex <= procedure.getStepCount()) {
			return procedure.getStep(stepIndex - 1);
		}

		return null;
	}

	/**
	 * 是否在CC-CV模式
	 * 
	 * @return
	 */
	public boolean beCCAndCVMode() {

		if (workMode == WorkMode.CVC) {
			return true;
		}
		if (workMode == WorkMode.CCC) {

			// 确认下一步次是否CV

			if (stepIndex > 0 && stepIndex < getCurrentProcedure().getStepCount()) {

				Step nextStep = getCurrentProcedure().getStep(stepIndex);
				return nextStep.workMode == WorkMode.CVC;
			}

		}

		return false;

	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;

	}

	/**
	 * 下一个步次是否搁置
	 * 
	 * @return
	 */
	public boolean isNextStepSleep() {

		Step nextStep = requestNextStep();
		if (nextStep != null && nextStep.workMode == WorkMode.SLEEP) {

			return true;
		}

		return false;
	}

	/**
	 * cc能否跳转到cv
	 * 
	 * @param chnData
	 * @return
	 */
	public boolean canSkipCcToCv(ChannelData chnData) {

		if (chnData.getWorkMode() != WorkMode.CCC) {

			return false;
		}
		Step step = getProcedureStep(chnData.getStepIndex());
		if (step == null) {

			return false;
		}
		// 检测电压是否在恒定范围以内
		if (Math.abs(chnData.getVoltage() - step.specialVoltage) > CC_CV_VOLT_RANGE) {

			return false;
		}
		// 检测电流是否下降到恒定值一个精度以下
		if (chnData.getCurrent() > step.specialCurrent - MainBoard.startupCfg.getCurrentPrecide()) {

			return false;
		}
		return true;
	}

	/**
	 * 下一步次是cv？
	 * 
	 * @return
	 */
	public boolean isNextStepCv() {

		if (stepIndex == getCurrentProcedure().getLoopEd() && getCurrentProcedure().getLoopCount() > 0) {

			// 出现循环结尾步次号则不可能是cc-cv
			return false;
		}
		if (stepIndex == getCurrentProcedure().getStepCount()) {

			// 最后一个步次不可能是cc-cv
			return false;
		}
		return getProcedureStep(stepIndex + 1).workMode == WorkMode.CVC;

	}

	/**
	 * 下一个步次是否是cc-cv特殊步次
	 * 
	 * @return
	 */
	public boolean isNextStepCcCv() {

		Step nextStep = requestNextStep();
		if (nextStep == null || nextStep.workMode != WorkMode.CCC) {

			return false;
		}
		if (nextStep.stepIndex == getCurrentProcedure().getLoopEd() && getCurrentProcedure().getLoopCount() > 0) {

			// 出现循环结尾步次号则不可能是cc-cv
			return false;
		}
		if (nextStep.stepIndex == getCurrentProcedure().getStepCount()) {

			// 最后一个步次不可能是cc-cv
			return false;
		}
		return getProcedureStep(nextStep.stepIndex + 1).workMode == WorkMode.CVC;

	}

	public double getStepEnergy() {
		return stepEnergy;
	}

	public void setStepEnergy(double stepEnergy) {
		this.stepEnergy = stepEnergy;
	}

	public AlertCode getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(AlertCode alertCode) {
		this.alertCode = alertCode;
	}

	public int getLoopIndex() {
		return loopIndex;
	}

	public void setLoopIndex(int loopIndex) {
		this.loopIndex = loopIndex;
	}

	public int getNextStep() {
		return nextStep;
	}

	public void setNextStep(int nextStep) {
		this.nextStep = nextStep;
	}

	public int getNextLoop() {
		return nextLoop;
	}

	public void setNextLoop(int nextLoop) {
		this.nextLoop = nextLoop;
	}

	public void setNextWorkmode(WorkMode nextWorkmode) {
		this.nextWorkmode = nextWorkmode;
	}

	public void setStepCapacity(double stepCapacity) {
		this.stepCapacity = stepCapacity;
	}

	public void setStepStartVotalge(double stepStartVotalge) {
		this.stepStartVotalge = stepStartVotalge;
	}

	public double getAccumulateCapacity() {
		return accumulateCapacity;
	}

	public void setAccumulateCapacity(double accumulateCapacity) {

		if (getDeviceChnIndex() == 2) {

			System.out.println("this.accumulateCapacity = " + this.accumulateCapacity + "->" + accumulateCapacity);
		}
		this.accumulateCapacity = accumulateCapacity;
	}

	public double getAccumulateEnergy() {
		return accumulateEnergy;
	}

	public void setAccumulateEnergy(double accumulateEnergy) {
		this.accumulateEnergy = accumulateEnergy;
	}

	public boolean isCharge() {
		return charge;
	}

	public void setCharge(boolean charge) {
		this.charge = charge;
	}

	public Date getOptStartTime() {
		return optStartTime;
	}

	public void setOptStartTime(Date optStartTime) {
		this.optStartTime = optStartTime;
	}

	public void clearAllDeadCaches() {

		accumulateCapacity = 0;
		accumulateEnergy = 0;
		deadCaches.clear();
	}

	public void clearAlertCaches() {

		alertCaches.clear();
	}

	public boolean isRuntimeCachesEmpty() {

		return runtimeCaches.isEmpty();
	}

	public boolean isResisterOffsetAlert() {
		return resisterOffsetAlert;
	}

	public void setResisterOffsetAlert(boolean resisterOffsetAlert) {
		this.resisterOffsetAlert = resisterOffsetAlert;
	}

	public boolean isOperated() {
		return operated;
	}

	public void setOperated(boolean operated) {
		this.operated = operated;
	}

	public List<ChannelData> getOfflineCaches() {
		return offlineCaches;
	}

	/**
	 * 将用户操作推入队列
	 * 
	 * @param operateState
	 */
	public void pushOperate(State operateState) {

		operates.offer(operateState);
	}

	/**
	 * 取出用户操作
	 * 
	 * @param operateState
	 * @return
	 */
	public State pollOperate() {

		lastOperateState = operates.peek();
		return operates.poll();
	}

	public State peekOperate() {

		return operates.peek();
	}

	public void clearOperates() {

		operates.clear();
	}

	public Date getProcedureStartTime() {
		return procedureStartTime;
	}

	public void setProcedureStartTime(Date procedureStartTime) {
		this.procedureStartTime = procedureStartTime;
	}

	public Date getProcedureStopTime() {
		return procedureStopTime;
	}

	public void setProcedureStopTime(Date procedureStopTime) {
		this.procedureStopTime = procedureStopTime;
	}

	public ChnState getPreState() {
		return preState;
	}

	public void setPreState(ChnState preState) {
		this.preState = preState;
	}

	public int getContinueAlertCount() {
		return continueAlertCount;
	}

	public void plusContinueAlertCount() {

		continueAlertCount++;
	}
	
	public void setContinueAlertCount(int count) {
		
		continueAlertCount = count;
	}

	public ChannelData getLastPushData() {
		return lastPushData;
	}

	public void setLastPushData(ChannelData lastPushData) {
		this.lastPushData = lastPushData;
	}

	/**
	 * 推送消息队列
	 * 
	 * @param msg
	 * @param code
	 */
	public void pushMsgQueue(String msg, AlertCode code) {

		AlertData alertData = new AlertData();
		alertData.setAlertCode(code);
		alertData.setAlertInfo(msg);
		alertData.setDate(new Date());
		alertData.setUnitIndex(getControlUnitIndex());
		alertData.setChnIndex(getDeviceChnIndex());
		alertData.setDeviceChnIndex(getDeviceChnIndex());
		Context.getPcNetworkService().pushSendQueue(new AlertDecorator(alertData));

	}

	/**
	 * 在cv模式下恢复流程后是否恒定电压范围内？
	 * 
	 * @return
	 */
	public boolean isReadyVoltageInCvModeResume(double volt) {

		/**
		 * 因在cv模式下恢复通道引起的电压不稳定
		 */
		if (workMode == WorkMode.CVC && !isVoltageSteadyFromResume) {

			Step step = getProcedureStep(stepIndex);
			if (step == null) {

				return false;
			}
			// 检测电压是否在恒定范围以内
			if (Math.abs(volt - step.specialVoltage) > CC_CV_VOLT_RANGE) {

				return false;
			}
			isVoltageSteadyFromResume = true;

		}

		return isVoltageSteadyFromResume;
	}

	/**
	 * 判定是否需要保存离线数据
	 * 
	 * @param chnData
	 * @return
	 */
	public boolean needSaveOfflineData(ChannelData chnData) {

		if (chnData.getState() != ChnState.RUN) {

			return false;
		}
		if (chnData.isImportantData()) {

			lastSaveOfflineData = chnData;
			// 重要转点数据，应该保存!
			return true;
		}

		if (lastSaveOfflineData == null) {

			// 第1条离线数据应该保存
			lastSaveOfflineData = chnData;
			return true;
		}
		// // 检测电压波动
		// if (getMainBoard().getOfflineRunningData().getSaveVoltOffset() > 0
		// && Math.abs(chnData.getVoltage() - lastSaveOfflineData.getVoltage()) >=
		// getMainBoard()
		// .getOfflineRunningData().getSaveVoltOffset()) {
		//
		// lastSaveOfflineData = chnData;
		// return true;
		// }
		// // 检测到电流波动
		// if (getMainBoard().getOfflineRunningData().getSaveCurrOffset() > 0
		// && Math.abs(chnData.getCurrent() - lastSaveOfflineData.getCurrent()) >=
		// getMainBoard()
		// .getOfflineRunningData().getSaveCurrOffset()) {
		//
		// lastSaveOfflineData = chnData;
		// return true;
		// }
		// // 时间超限，开始保存节点数据
		// if (chnData.getTimeTotalSpend() - lastSaveOfflineData.getTimeTotalSpend() >=
		// getMainBoard()
		// .getOfflineRunningData().getSaveTime()) {
		//
		// lastSaveOfflineData = chnData;
		// return true;
		// }

		return false;
	}

	public ChannelData getLastSaveOfflineData() {
		return lastSaveOfflineData;
	}

	public void setLastSaveOfflineData(ChannelData lastSaveOfflineData) {
		this.lastSaveOfflineData = lastSaveOfflineData;
	}

	public int getLeadStepCount() {
		return leadStepCount;
	}

	public Map<ChannelData, AlertData> getAlertMap() {
		return alertMap;
	}

	public Date getNowPickupTime() {
		return nowPickupTime;
	}

	public void setNowPickupTime(Date nowPickupTime) {
		this.nowPickupTime = nowPickupTime;
	}

	public int getAttemptOpenCount() {
		return attemptOpenCount;
	}

	public void setAttemptOpenCount(int attemptOpenCount) {
		this.attemptOpenCount = attemptOpenCount;
	}

	public List<ChnDataPack> getRawCaches() {
		return rawCaches;
	}

	public void setRawCaches(List<ChnDataPack> list) {

		this.rawCaches = list;
	}

	public void appendNotClosedRawData(ChnDataPack rawData) throws AlertException {

		this.rawNotCloseCaches.add(rawData);

	}

	public void clearNotClosedRawData() {

		this.rawNotCloseCaches.clear();
	}

	public List<ChnDataPack> getNotClosedRawData() {

		return this.rawNotCloseCaches;
	}

	public void pushRawData(ChnDataPack rawData) throws AlertException {

		rawCaches.add(rawData);
		if (rawCaches.size() > MAX_RAW_CACHE_SIZE) {
			System.out.println("raw cache size = " + rawCaches.size());
			rawCaches.subList(0, rawCaches.size() - 3).clear();
			

		}
	}

	public long getStepLastMiliseconds() {
		return stepLastMiliseconds;
	}

	public void setStepLastMiliseconds(long stepLastMiliseconds) {
		this.stepLastMiliseconds = stepLastMiliseconds;
	}

	public long getTotalMiliseconds() {
		return totalMiliseconds;
	}

	public void setTotalMiliseconds(long totalSeconds) {
		this.totalMiliseconds = totalSeconds;
	}
	
	
	/**
	 * 处理步次第一条数据
	 * @author  wavy_zheng
	 * 2022年7月28日
	 * @param channel
	 * @param chnData
	 */
	
    /*private void processFirstData(int stepIndex , int loopIndex , ChnData rawData) {
         
    	WorkMode workMode = getControlUnit().getProcedureStep(stepIndex).workMode;

			if (workMode == WorkMode.CC_CV || workMode == WorkMode.CCC) {

				// s获取上一条数据

				if (rawData.getVoltage() < getVoltage() ) {

					// cc第一条电压异常，出现下降
					chnData.setVoltage(getVoltage() + new Random().nextDouble() / 5);
				}

			} else if (chnData.getWorkMode() == WorkMode.CCD) {

				if (chnData.getVoltage() > getVoltage()) {

					chnData.setVoltage(getVoltage() - new Random().nextDouble() / 5);
				}
			} else if (chnData.getWorkMode() == WorkMode.SLEEP) {

				// 前1个工步为充电模式,休眠第一条
				if (getWorkMode() == WorkMode.CCC || getWorkMode() == WorkMode.CC_CV
						|| getWorkMode() == WorkMode.CVC) {

					if (chnData.getVoltage() > getVoltage()) {

						// 修改异常电压
						chnData.setVoltage(getVoltage() - new Random().nextDouble() / 5);
					}
				} else if (getWorkMode() == WorkMode.CCD) {

					if (chnData.getVoltage() < getVoltage()) {

						chnData.setVoltage(getVoltage() + new Random().nextDouble() / 5);
					}
				}

			}

		

	}*/

	/**
	 * 发生了转步次
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param stepIndex
	 * @param loopIndex
	 */
	public void skipStep(int stepIndex, int loopIndex) {

		logger.info("skip to stepIndex = " + stepIndex + ",loopIndex = " + loopIndex + ",total seconds="
				+ this.stepElapseMiliseconds);
		
		int oldStepIndex = this.stepIndex; 
		
		this.stepIndex = stepIndex;
		this.loopIndex = loopIndex;
		this.leadStepCount = 4;
		this.workMode = getControlUnit().getProcedureStep(stepIndex).workMode;

		//this.totalMiliseconds += this.stepElapseMiliseconds;
		this.accumulateCapacity += this.stepCapacity; // 累计步次容量
		this.accumulateEnergy += this.stepEnergy; // 累计步次能量

		this.stepCapacity = 0;
		this.stepElapseMiliseconds = 0;
	
		this.stepEnergy = 0;
		sleepZeroCount = 0;
		
		this.procedureStartTime = new Date();

		this.state = ChnState.RUN;
		
		this.cvInCccvMode = false;
		
		

		// 清除异常数据
		clearExceptionCaches();
		clearImportantData();
		clearOverStepCaches();
		clearOverStepSubcaches();
		clearSlopeCaches();
		clearConstCache();
		
		Context.getDataProvider().getDataProcessService().clearData(this);

		if (needClearAccumulateData() && oldStepIndex > 0) { //注意0步次不能算跳转

			this.accumulateCapacity = 0;
			this.accumulateEnergy = 0;

		}
		if (MainBoard.startupCfg.isUseSTM32Time()) {

			setStepLastMiliseconds(0);
		}

	}

	/**
	 * 寻找流程的上1个步次
	 * 
	 * @author wavy_zheng 2021年6月2日
	 * @param procedure
	 * @param step
	 * @return
	 */
	private Step findPreviousStep(ProcedureData procedure, Step step) {

		if (loopIndex > 1 && procedure.getLoopSt() == step.stepIndex) {

			if (procedure.getLoopEd() == 0) {

				return null;
			}
			// 跳到上一个循环的最后一个步次
			return procedure.getStep(procedure.getLoopEd() - 1);

		} else {

			if (step.stepIndex == 1) {

				return null;
			}
            
			//获取前一步次
			return procedure.getStep(step.stepIndex - 2);

		}

	}

	/**
	 * 需要清空累计容量或能量?
	 * 
	 * @author wavy_zheng 2020年12月12日
	 */
	public boolean needClearAccumulateData() {

		if (stepIndex > 0) {

			ProcedureData procedure = getControlUnit().getProcedure();
			// 当前工作模式
			Step currentStep = procedure.getStep(stepIndex - 1);

			WorkMode currentWorkMode = currentStep.workMode;
			if (currentWorkMode == WorkMode.SLEEP) {

				return false;
			}
			Step previousStep = currentStep;
			do {

				previousStep = findPreviousStep(procedure, previousStep);

			} while (previousStep != null && previousStep.getWorkMode() == WorkMode.SLEEP);

			if (previousStep != null) {
				if (currentWorkMode == WorkMode.CCD) {

					// 充电->放电
					return previousStep.getWorkMode() != WorkMode.CCD;
				} else {

					// 放电->充电
					return previousStep.getWorkMode() == WorkMode.CCD;
				}
			} else {

				return false;
			}

			/*
			 * for (int n = stepIndex - 1; n >= 1; n--) { Step step = procedure.getStep(n -
			 * 1); if (step.getWorkMode() == WorkMode.SLEEP) {
			 * 
			 * continue; }
			 * 
			 * if (currentWorkMode == WorkMode.CCD) {
			 * 
			 * // 充电->放电 return step.getWorkMode() != WorkMode.CCD; } else {
			 * 
			 * // 放电->充电 return step.getWorkMode() == WorkMode.CCD; }
			 * 
			 * }
			 */

		}

		return false;

	}

	public List<ChannelData> getRuntimeCaches() {

		return runtimeCaches;
	}

	// public List<ChannelData> fetchCacheList(ChnState state, WorkMode workMode) {
	//
	// List<ChannelData> list = new ArrayList<>();
	//
	// for (ChannelData data : runtimeCaches) {
	//
	// if (state == data.getState() && workMode == data.getWorkMode()) {
	//
	// list.add(data);
	// }
	// }
	//
	// return list;
	//
	// }

	public int getAlertStepIndex() {
		return alertStepIndex;
	}

	public void setAlertStepIndex(int alertStepIndex) {
		this.alertStepIndex = alertStepIndex;
	}

	public void log(String log) {
        
		if(MainBoard.startupCfg.getLogCfg().printChnLog) {
		   logger.info(log);
		}
	}

	/**
	 * 启动成功
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param chnData
	 */
	public void startup() {

		attemptOpenCount = 0;
		// 将操作队列移除
		if (State.STARTUP != pollOperate()) {

			clearOperates();
		}
		clearImportantData();
		clearExceptionCaches();
		clearOverStepCaches();
		clearOverStepSubcaches();
		clearSlopeCaches();
		clearNotClosedRawData();

		state = ChnState.RUN; // 启动
		alertCode = AlertCode.NORMAL;
		workMode = getControlUnit().getProcedureStep(1).workMode;
		stepIndex = 1;
		loopIndex = 1;

	}

	public int getMaxStartupMiliseconds() {
		return maxStartupMiliseconds;
	}

	public boolean isSyncWaitForSkip() {
		return isSyncWaitForSkip;
	}

	public void setSyncWaitForSkip(boolean isSyncWaitForSkip) {
		this.isSyncWaitForSkip = isSyncWaitForSkip;
	}

	public boolean isLogicCalFlashOk() {
		return logicCalFlashOk;
	}

	public void setLogicCalFlashOk(boolean logicCalFlashOk) {
		this.logicCalFlashOk = logicCalFlashOk;
	}

	public boolean isCheckCalFlashOk() {
		return checkCalFlashOk;
	}

	public void setCheckCalFlashOk(boolean checkCalFlashOk) {
		this.checkCalFlashOk = checkCalFlashOk;
	}

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	public int getMonitorPoleCount() {
		return monitorPoleCount;
	}

	public void setMonitorPoleCount(int monitorPoleCount) {
		this.monitorPoleCount = monitorPoleCount;
	}

	public boolean isCheckboardMonitor() {
		return checkboardMonitor;
	}

	public void setCheckboardMonitor(boolean checkboardMonitor) {
		this.checkboardMonitor = checkboardMonitor;
	}

	public void appendExceptionData(ChannelData chnData) {

		exceptionCaches.add(chnData);
	}

	public void clearExceptionCaches() {

		exceptionCaches.clear();
	}

	public List<ChannelData> getExceptionCaches() {
		return exceptionCaches;
	}

	public void appendImportantData(ChannelData chnData) {

		importCaches.add(chnData);
	}

	public void clearImportantData() {

		importCaches.clear();
	}

	public List<ChannelData> getImportantCaches() {
		return importCaches;
	}

	public long getCheckMonitorVoltSt() {
		return checkMonitorVoltSt;
	}

	public void setCheckMonitorVoltSt(long checkMonitorVoltSt) {
		this.checkMonitorVoltSt = checkMonitorVoltSt;
	}

	public ChnDataPack getLastRawData() {
		return lastRawData;
	}

	public void setLastRawData(ChnDataPack lastRawData) {
		this.lastRawData = lastRawData;
	}

	public void appendOverStepCaches(ChannelData chnData) {

		this.overStepCaches.add(chnData);
	}

	public void clearOverStepCaches() {

		this.overStepCaches.clear();
	}

	public List<ChannelData> getOverStepCaches() {
		return overStepCaches;
	}

	public void appendOverStepSubcaches(ChannelData chnData) {

		this.overStepSubcaches.add(chnData);
	}

	public void clearOverStepSubcaches() {

		this.overStepSubcaches.clear();
	}

	public List<ChannelData> getOverStepSubcaches() {
		return overStepSubcaches;
	}

	public void clearSlopeCaches() {

		this.slopeCaches.clear();
	}

	public List<ChannelData> getSlopeCaches() {

		return this.slopeCaches;
	}

	public void appendSlopeCaches(ChannelData chnData) {

		this.slopeCaches.add(chnData);
	}

	public ChannelData getVoltageOffsetProtectionData() {
		return voltageOffsetProtectionData;
	}

	public void setVoltageOffsetProtectionData(ChannelData voltageOffsetProtectionData) {
		this.voltageOffsetProtectionData = voltageOffsetProtectionData;
	}

	public void appendTouchData(ChannelData channelData) {

		this.touchCaches.add(channelData);
	}

	public List<ChannelData> getTouchData() {

		return this.touchCaches;
	}

	public void clearTouchData() {

		this.touchCaches.clear();
	}

	public void appendConstData(ChannelData channelData) {

		this.constCheckCaches.add(channelData);
	}

	public void clearConstCache() {

		this.constCheckCaches.clear();
	}

	public List<ChannelData> getConstData() {

		return this.constCheckCaches;
	}
	
	/**
	 * 添加智能保护采样的数据
	 * @author  wavy_zheng
	 * 2022年5月16日
	 * @param channelData
	 */
	public void appendSmartChannelData(SmartPickupData channelData) {

		this.smartCaches.add(channelData);
	}
	
	
	public void clearSmartCaches() {
		
		this.smartCaches.clear();
	}

	public List<SmartPickupData> getSmartCaches() {
		return smartCaches;
	}

	public void appendOverVoltData(ChannelData channelData) {

		this.overVoltCaches.add(channelData);
	}

	public void clearOverVoltCache() {

		this.overVoltCaches.clear();
	}

	public List<ChannelData> getOverVoltCaches() {

		return this.overVoltCaches;
	}

	public void minusLeadCount() {

		if (leadStepCount > 0) {
			leadStepCount--;
		}
	}

	public double getOffsetValue() {
		return offsetValue;
	}

	public void setOffsetValue(double offsetValue) {
		this.offsetValue = offsetValue;
	}

	public int getSleepZeroCount() {
		return sleepZeroCount;
	}

	public void setSleepZeroCount(int sleepZeroCount) {
		this.sleepZeroCount = sleepZeroCount;
	}
	
	/**
	 * 该通达是否启动过?
	 * @author  wavy_zheng
	 * 2021年6月6日
	 * @return
	 */
	public  boolean isStartuped() {
		
		if(stepIndex == 1 && loopIndex == 1) {
			
			return false;
		}
		
		return true;
	}

	public int getLeadSyncCount() {
		return leadSyncCount;
	}

	public void setLeadSyncCount(int leadSyncCount) {
		this.leadSyncCount = leadSyncCount;
	}
	
	public void decreaseSyncCount() {
		
		this.leadSyncCount--;
	}

	public boolean isSynMode() {
		return synMode;
	}

	public void setSynMode(boolean synMode) {
		
		log((synMode ? "enter" : "cancel")  +  " sync mode");
		this.synMode = synMode;
	}

	public ChannelData getUdtData() {
		return udtData;
	}

	public void setUdtData(ChannelData udtData) {
		this.udtData = udtData;
	}
	/**
	 * 是否正在同步,同步时不能进行恢复操作
	 * @author  wavy_zheng
	 * 2022年2月28日
	 * @return
	 */
	public boolean isSyncSteping() {
		
		if(!synMode) {
			
			return false;
		}
		if(state == ChnState.PAUSE) {
			
			return true;
		}
		
		
		return false;
		
	}

	public long getLeadStepMiliseconds() {
		return leadStepMiliseconds;
	}

	public void setLeadStepMiliseconds(long leadStepMiliseconds) {
		this.leadStepMiliseconds = leadStepMiliseconds;
	}

	public long getOptElapseMiliseconds() {
		return optElapseMiliseconds;
	}

	public void setOptElapseMiliseconds(long optElapseMiliseconds) {
		this.optElapseMiliseconds = optElapseMiliseconds;
	}

	public boolean isCvInCccvMode() {
		return cvInCccvMode;
	}

	public void setCvInCccvMode(boolean cvInCccvMode) {
		this.cvInCccvMode = cvInCccvMode;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public ChannelData getSleepLastData() {
		return sleepLastData;
	}

	public void setSleepLastData(ChannelData sleepLastData) {
		this.sleepLastData = sleepLastData;
	}
	
	public void logPickup(ChnDataPack pack) {
		
		this.pickupLogger.info(pack.toString());
	}
	

	public double getCccvDeviceVoltage() {
		return cccvDeviceVoltage;
	}

	public void setCccvDeviceVoltage(double cccvDeviceVoltage) {
		this.cccvDeviceVoltage = cccvDeviceVoltage;
	}

	public boolean isInCvCurrentMakeTime() {
		return inCvCurrentMakeTime;
	}

	public void setInCvCurrentMakeTime(boolean inCvCurrentMakeTime) {
		this.inCvCurrentMakeTime = inCvCurrentMakeTime;
	}
	
	
	

}
