package com.nlteck.firmware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.TestItemDataDO;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;

/**
 * 通道信息对象
 * 
 * @author Administrator
 *
 */
@Deprecated
public class Channel {
	// 作为驱动板通道时
	private DriverBoard driver;
	private int channelIndexInDriver;
	private PushData pushData;
	private PushLog pushLog;
	private UploadTestDot uploadTestDot;
	private boolean isSelect;
	private Channel matchedCalBoardChannel;
	// 作为校准板通道时
	private CalBoard calBoard;
	private int channelIndexInCal;
	private Channel matchedDeviceChannel;

	// 测试项目
	private Map<RunMode, List<TestItemDataDO>> testMap = new HashMap<>();
	private RunMode runningMode; // 正在测试的catalog目录

	public Channel getMatchedDeviceChannel() {
		return matchedDeviceChannel;
	}

	public Channel getMatchedCalBoardChannel() {
		return matchedCalBoardChannel;
	}

	public void setMatchedCalBoardChannel(Channel matchedCalBoardChannel) {
		this.matchedCalBoardChannel = matchedCalBoardChannel;
	}

	public void setMatchedDeviceChannel(Channel matchedDeviceChannel) {
		this.matchedDeviceChannel = matchedDeviceChannel;
	}

	public CalBoard getCalBoard() {
		return calBoard;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public void setCalBoard(CalBoard calBoard) {
		this.calBoard = calBoard;
	}

	public int getChannelIndexInCal() {
		return channelIndexInCal;
	}

	public void setChannelIndexInCal(int channelIndexInCal) {
		this.channelIndexInCal = channelIndexInCal;
	}

	public PushData getPushData() {
		return pushData;
	}

	public void setPushData(PushData pushData) {
		this.pushData = pushData;
	}

	public PushLog getPushLog() {
		return pushLog;
	}

	public void setPushLog(PushLog pushLog) {
		this.pushLog = pushLog;
	}

	public Channel(DriverBoard db, int channelIndexDriver) {

		this.driver = db;
		this.channelIndexInDriver = channelIndexDriver;
	}

	public Channel() {
	}

	public DriverBoard getDriver() {
		return driver;
	}

	public void setDriver(DriverBoard driver) {
		this.driver = driver;
	}

	public int getChannelIndexInDriver() {
		return channelIndexInDriver;
	}

	public UploadTestDot getUploadTestDot() {
		return uploadTestDot;
	}

	public void setUploadTestDot(UploadTestDot uploadTestDot) {
		this.uploadTestDot = uploadTestDot;
	}

	public void setChannelIndexInDriver(int channelIndexInDriver) {
		this.channelIndexInDriver = channelIndexInDriver;
	}

	/**
	 * 获取该通道在逻辑板内序号
	 * 
	 * @return
	 */
	public int getIndexInLogic() {
		int driverIndex = driver.getDriverIndex();
		int chnNumInDriver = driver.getChannels().size();
		return driverIndex * chnNumInDriver + channelIndexInDriver;
	}

	/**
	 * 获取该通道在设备内序号
	 * 
	 * @return
	 */
	public int getIndexInDevice() {
		int logicIndex = driver.getLogicBoard().getLogicIndex();
		int chnNumInDriver = driver.getChannels().size();
		int driverNumInLogic = driver.getLogicBoard().getDrivers().size();
		return logicIndex * chnNumInDriver * driverNumInLogic + getIndexInLogic();
	}

	public RunMode getRunningMode() {
		return runningMode;
	}

	public void setRunningMode(RunMode runningMode) {
		this.runningMode = runningMode;
	}
	
	public void putTestItems(RunMode runMode , List<TestItemDataDO> list) {
		
		testMap.put(runMode, list);
	}
	
	public List<TestItemDataDO>  getTestItemsBy(RunMode runMode) {
		
		return testMap.get(runMode);
	}
	

}
