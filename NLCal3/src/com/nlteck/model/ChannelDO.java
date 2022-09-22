package com.nlteck.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nlteck.calModel.model.CalBoardChannel;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.WorkBench;
import com.nlteck.firmware.WorkBench.CalType;
import com.nlteck.listener.ChnDataChangeLisener;
import com.nlteck.listener.ChnInfoShowListener;
import com.nlteck.model.BaseCfg.RunMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDotData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData.DriverCheckInfoData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
/**
 * 
 * @Description: xingguo_wang 添加校准需要的属性
 * @date 2022年4月11日
 *
 */
public class ChannelDO {
	
	private int id;
	private int unitIndex;
	private int chnIndex;
	private int deviceId;
	private CalState state = CalState.NONE;
	private Date startTime;
	private Date endTime;
	private int result;
	private String info;
	private Device device;
	private CalType calType;

	
	
	// 显示信息
	private Pole pole;
	private CalMode mode;
	private double programeVal;
	private double meterVal;
	private double adc;
	private int pos;
	private int range;
	private int precision;
	private int seconds;

	private List<MeasureDotDO> measures = new ArrayList<>();
	private List<TestLog> logs = Collections.synchronizedList(new ArrayList<>());
	private List<UploadTestDot> debugDatas = Collections.synchronizedList(new ArrayList<>());
	private List<StableDataDO> stableDatas = Collections.synchronizedList(new ArrayList<>());

	private List<ChnDataChangeLisener> listeners = new ArrayList<>();
	private List<ChnInfoShowListener>  showListeners = new ArrayList<>();

	private long startTestTick; // 启动时间戳

	private Map<RunMode, List<TestLog>> logMap = new HashMap<>();

	private double connectVoltage; // 对接电压
	private boolean connectCalboard; // 对接校准板成功?
	private boolean selected; // 通道被选中

	// 测试项目
	private Map<RunMode, List<TestItemDataDO>> testMap = new HashMap<>();
	private RunMode runningMode; // 正在测试的catalog目录
	private CalibrateCoreWorkMode workMode;

	private DriverCheckInfoData selftCheck; // 自检数据
	private ChannelData         channelData;  //当前扫描到的最新通道数据
	private boolean  readyCommonTest; //准备性能测试?
	// 校准计量
	private CalBoardChannel bindingCalBoardChannel;
	private List<TestDot> calDotList=new ArrayList<>();
	private List<TestDot> measureDotList=new ArrayList<>();
	private TestDot lastTestDot;
	private boolean ready;// 准备状态
	private int calculateIndex;
	
	public int getCalculateIndex() {
		return calculateIndex;
	}

	public void setCalculateIndex(int calculateIndex) {
		this.calculateIndex = calculateIndex;
	}

	public CalBoardChannel getBindingCalBoardChannel() {
		return bindingCalBoardChannel;
	}

	public void setBindingCalBoardChannel(CalBoardChannel calBoardChannel) {

		if (this.bindingCalBoardChannel == calBoardChannel) {
			return;
		}

		if (calBoardChannel == null) {// 解绑
			if (this.bindingCalBoardChannel != null) {
				this.bindingCalBoardChannel.setBindingChannel(null);
				this.bindingCalBoardChannel = null;
			}

		} else {
			if (calBoardChannel.getBindingChannel() != null) {
				calBoardChannel.getBindingChannel().bindingCalBoardChannel = null;
			}
			if (this.bindingCalBoardChannel != null) {
				this.bindingCalBoardChannel.setBindingChannel(null);
			}
			this.bindingCalBoardChannel = calBoardChannel;
			calBoardChannel.setBindingChannel(this);
		}
	}

	public List<TestDot> getMeasureDotList() {
		return measureDotList;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public void setMeasureDotList(List<TestDot> measureDotList) {
		this.measureDotList = measureDotList;
	}

	public List<TestDot> getCalDotList() {
		return calDotList;
	}

	public void setCalDotList(List<TestDot> calDotList) {
		this.calDotList = calDotList;
	}

	public TestDot getLastTestDot() {
		return lastTestDot;
	}

	public void setLastTestDot(TestDot lastTestDot) {
		this.lastTestDot = lastTestDot;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public CalState getState() {
		return state;
	}

	public void setState(CalState state) {
		this.state = state;
	}

	public CalType getCalType() {
		return calType;
	}

	public void setCalType(CalType calType) {
		this.calType = calType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getDeviceChnIndex() {

		return device.getDriverNumInLogic() * device.getChnNumInDriver() * unitIndex + chnIndex;
	}

	public int getDriverIndex() {

		return getDeviceChnIndex() / device.getDriverNumInLogic();
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public List<MeasureDotDO> getMeasures() {
		return measures;
	}

	public void setMeasures(List<MeasureDotDO> measures) {
		this.measures = measures;
	}

	public List<TestLog> getLogs() {
		return logs;
	}

	public void setLogs(List<TestLog> logs) {
		this.logs = logs;
	}

	public double getConnectVoltage() {
		return connectVoltage;
	}

	public void setConnectVoltage(double connectVoltage) {
		this.connectVoltage = connectVoltage;
	}

	public boolean isConnectCalboard() {
		return connectCalboard;
	}

	public void setConnectCalboard(boolean connectCalboard) {
		this.connectCalboard = connectCalboard;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void appendTestDot(MeasureDotDO mdd) {

		measures.add(mdd);
	}
	/**
	 * 查找是否存在的同个计量点
	 * @author  wavy_zheng
	 * 2022年5月12日
	 * @param mdd
	 * @return
	 */
	public MeasureDotDO findSameDot(MeasureDotDO mdd) {
		
		
		Optional<MeasureDotDO> find = measures.stream().filter(x->x.getMode().equals(mdd.getMode()) && x.getLevel() == mdd.getLevel() &&
				mdd.getPole().equals(mdd.getPole()) && mdd.getCalculateDot() == x.getCalculateDot()).findFirst();
		if(!find.isPresent()) {
			
			return null;
		}
		
		return find.get();
	}
	
	

	public void clearTestDots() {

		measures.clear();
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public CalMode getMode() {
		return mode;
	}

	public void setMode(CalMode mode) {
		this.mode = mode;
	}

	public double getProgrameVal() {
		return programeVal;
	}

	public void setProgrameVal(double programeVal) {
		this.programeVal = programeVal;
	}

	public double getMeterVal() {
		return meterVal;
	}

	public void setMeterVal(double meterVal) {
		this.meterVal = meterVal;
	}

	public double getAdc() {
		return adc;
	}

	public void setAdc(double adc) {
		this.adc = adc;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public void appendTestLog(TestLog tl) {

		logs.add(tl);
	}

	public void clearTestLog() {

		logs.clear();
	}

	public List<StableDataDO> getStableDatas() {
		return stableDatas;
	}

	public void appendStableData(StableDataDO stableData) {

		stableDatas.add(stableData);
	}

	public void clearStableDatas() {

		stableDatas.clear();
	}

	public void appendLog(RunMode rm, TestLog log) {

		/*
		 * List<TestLog> logs = logMap.get(rm); if(logs == null) {
		 * 
		 * logs = new ArrayList<>(); logMap.put(rm, logs); } logs.add(log);
		 */
		appendTestLog(log);

	}

	public void clearLog(RunMode rm) {

		if (rm == null) {

			logMap.clear();
		} else {

			List<TestLog> logs = logMap.get(rm);
			if (logs != null) {

				logs.clear();
			}
		}

	}

	public List<TestLog> getLog(RunMode rm) {

		List<TestLog> logs = logMap.get(rm);
		if (logs == null) {

			logs = new ArrayList<>();
			logMap.put(rm, logs);
		}

		return logs;
	}
	

	public List<UploadTestDot> getDebugDatas() {
		return debugDatas;
	}

	public void appendDebugData(UploadTestDot debugData) {

		debugDatas.add(debugData);
	}

	public void clearDebugDatas() {

		debugDatas.clear();
	}

	public RunMode getRunningMode() {
		return runningMode;
	}

	public void setRunningMode(RunMode runningMode) {
		this.runningMode = runningMode;
	}

	public void putTestItems(RunMode runMode, List<TestItemDataDO> list) {

		testMap.put(runMode, list);
	}

	public List<TestItemDataDO> getTestItemsBy(RunMode runMode) {

		return testMap.get(runMode);
	}

	public long getStartTestTick() {
		return startTestTick;
	}

	public void setStartTestTick(long startTestTick) {
		this.startTestTick = startTestTick;
	}

	public CalibrateCoreWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalibrateCoreWorkMode workMode) {
		this.workMode = workMode;
	}

	public void loadData(RunMode rm) throws SQLException {

		if (rm == null) {

			for (RunMode mode : WorkBench.baseCfg.runModes) {

				loadData(mode);
			}
		} else {

			if (rm != RunMode.Cal) {

				if (rm == RunMode.StableTest && getStableDatas().isEmpty()) {

					getStableDatas().addAll(WorkBench.dataManager.listStableDatas(this));
				} else {

					if (getTestItemsBy(rm) == null || getTestItemsBy(rm).isEmpty()) {

						List<TestItemDataDO> items = WorkBench.dataManager.listTestItems(this, rm);
						if (items.isEmpty()) {

							putTestItems(rm, WorkBench.baseCfg.testItemMap.get(rm));
						} else {

							putTestItems(rm, items);
						}
					}
				}

			}

		}

	}
	
	public void addShowListener(ChnInfoShowListener listener) {

		showListeners.add(listener);
	}

	public void removeShowListener(ChnInfoShowListener listener) {

		showListeners.remove(listener);
	}
	
	
	public void triggerShowInfoChange() {

		for (ChnInfoShowListener listener : showListeners) {

			listener.onTestItemInfo(this);
		}

	}
	
	

	public void addListener(ChnDataChangeLisener lisener) {

		listeners.add(lisener);
	}

	public void removeListener(ChnDataChangeLisener lisener) {

		listeners.remove(lisener);
	}

	public void triggerItemDataChange(RunMode rm, TestItemDataDO item) {

		for (ChnDataChangeLisener listener : listeners) {

			listener.onItemChange(rm, item);
		}

	}

	public void triggerStableDataChange(StableDataDO stable) {

		for (ChnDataChangeLisener listener : listeners) {

			listener.onStableDataChange(stable);
		}

	}

	public void triggerCalDataChange(UploadTestDotData data) {

		for (ChnDataChangeLisener listener : listeners) {

			listener.onCalDataChange(data);
		}

	}

	public DriverCheckInfoData getSelftCheck() {
		return selftCheck;
	}

	public void setSelftCheck(DriverCheckInfoData selftCheck) {
		this.selftCheck = selftCheck;
	}
	
	public ChannelData getChannelData() {
		return channelData;
	}

	public void setChannelData(ChannelData channelData) {
		this.channelData = channelData;
	}
	
	
	
	

	public boolean isReadyCommonTest() {
		return readyCommonTest;
	}

	public void setReadyCommonTest(boolean readyCommonTest) {
		this.readyCommonTest = readyCommonTest;
	}

	@Override
	public String toString() {
		return "Channel [id=" + id + ", unitIndex=" + unitIndex + ", chnIndex=" + chnIndex + ", deviceId=" + deviceId
				+ ", state=" + state + ", startTime=" + startTime + ", endTime=" + endTime + ", result=" + result
				+ ", info=" + info + "]";
	}

}
