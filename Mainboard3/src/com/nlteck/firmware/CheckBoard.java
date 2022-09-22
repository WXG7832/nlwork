package com.nlteck.firmware;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import com.nlteck.AlertException;

import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.check2.Check2ProgramStateData;
import com.nltecklib.protocol.li.check2.Check2Environment.CheckWorkMode;
import com.nltecklib.protocol.li.check2.Check2Environment.PowerState;
import com.nltecklib.protocol.li.logic2.Logic2ProgramStateData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.StartupData;
import com.nltecklib.protocol.li.workform.CalCheckCalculateData;
import com.nltecklib.protocol.li.workform.CalCheckFlashWriteData;
import com.nltecklib.protocol.li.workform.CalCheckProcessData;
import com.nltecklib.protocol.li.workform.CalHKCheckCalibrateData;
import com.rm5248.serial.SerialPort;

/**
 * 回检板抽象基类
 * @author Administrator
 *
 */
public  class CheckBoard {
     
	protected List<DriverBoard> drivers = new ArrayList<DriverBoard>();
	
	protected  int checkIndex;
	protected  MainBoard mb;
	
	protected CheckWorkMode workMode = CheckWorkMode.STANDBY;
	protected PowerState  powerState = PowerState.POWEROFF;
	
	protected ScheduledExecutorService executor = null;
	protected Date lastPickupDate = new Date(); // 上一次采集时间
	protected boolean boardConnected = true;
	protected int      driverIndex = 0 ;
	protected long     recvPickupCount; //采集次数
	protected long     sendPickupCount; //发送采样次数
	protected FaultCheckData   faultCheckData = new FaultCheckData();
	
	private SerialPort   serialPort;
	private boolean use; // 使用情况
	private long pickupTimeSpan; // 采集时间间隔
	private boolean reverseDriverIndex; // 驱动板反序
	private long commTimeout; //通信超时时间，单位ms
	private int pickupDriverIndex = 0; // 当前采集板号
	
	private int  driverEnableFlag = 0xffff; //驱动板启用情况
	
	//private State   state  = State.NORMAL;
	private String  softversion;
	private boolean  programBurnOk;
	private boolean  commOk; //是否通信正常
	private String   uuid="";
	private long     runMiliseconds;
	
	/**
	 * 回检板故障详情
	 * @author Administrator
	 *
	 */
	public static class FaultCheckData {
		
		 public boolean AD_OK = false;
		 public boolean FLASH_OK = false;
		 public boolean CAL_OK = false;
		 
		 public boolean ADC_FLASH_OK = false;
	}
	
	
	public CheckBoard(MainBoard mb , int index , SerialPort port) {
		
		this.mb = mb;
		this.checkIndex = index;
		
		this.serialPort = port;
		/*use = MainBoard.startupCfg.getCheckInfo(checkIndex).use;
		reverseDriverIndex = MainBoard.startupCfg.getCheckInfo(checkIndex).reverseDriverIndex;
		pickupTimeSpan = MainBoard.startupCfg.getCheckInfo(checkIndex).pickupTime;
		commTimeout = MainBoard.startupCfg.getCheckInfo(checkIndex).communicateTimeout;
		driverEnableFlag = MainBoard.startupCfg.getCheckInfo(checkIndex).enableFlag;*/
		
//		commOk = true;
//		faultCheckData.AD_OK = true;
		
	}
	
	public MainBoard getMainBoard() {

		return mb;
	}
	
	/**
	 * 初始化
	 * @author  wavy_zheng
	 * 2020年11月30日
	 */
	public void init() {
		
		//匹配对应的逻辑板
		
		
	}
	
	
	

	
	
	
	
	
	
	
	public List<DriverBoard> getDrivers() {
		return drivers;
	}

	public ScheduledExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * 将回检板的驱动板位置上下颠倒
	 * 
	 * @param chnIndex
	 * @return
	 */
	protected static int specialProcessChnIndex(int chnIndex) {

		return 0;
	}
	
	
	public int getCheckIndex() {
		return checkIndex;
	}

	public boolean isBoardConnected() {
		return boardConnected;
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public long getRecvPickupCount() {
		return recvPickupCount;
	}


	public CheckWorkMode getWorkMode() {
		return workMode;
	}

	public PowerState getPowerState() {
		return powerState;
	}

	public FaultCheckData getFaultCheckData() {
		return faultCheckData;
	}
	
	public void setFaultCheckData(FaultCheckData faultCheckData) {
		this.faultCheckData = faultCheckData;
	}

	public long getSendPickupCount() {
		return sendPickupCount;
	}
	
	
	

	public void setRecvPickupCount(long recvPickupCount) {
		this.recvPickupCount = recvPickupCount;
	}

	public void setSendPickupCount(long sendPickupCount) {
		this.sendPickupCount = sendPickupCount;
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public long getPickupTimeSpan() {
		return pickupTimeSpan;
	}

	public boolean isReverseDriverIndex() {
		return reverseDriverIndex;
	}

	public long getCommTimeout() {
		return commTimeout;
	}
	
	
	/**
	 * 因驱动板接线问题导致序号会出现正序和反序两种接法 获取实际驱动板号
	 * 
	 * @author wavy_zheng 2020年10月24日
	 * @param driverIndex
	 *            软件上显示的驱动板号
	 * @return 实际真正下发的驱动板号
	 */
	public int getActualDriverIndex(int driverIndex) {

		/*if (reverseDriverIndex) {

			return MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;
		}*/
		return driverIndex;
	}

	public int getPickupDriverIndex() {
		return pickupDriverIndex;
	}

	public void setPickupDriverIndex(int pickupDriverIndex) {
		this.pickupDriverIndex = pickupDriverIndex;
	}

	public int getDriverEnableFlag() {
		return driverEnableFlag;
	}

	public boolean isProgramBurnOk() {
		return programBurnOk;
	}

	public void setProgramBurnOk(boolean programBurnOk) {
		this.programBurnOk = programBurnOk;
	}

	public String getSoftversion() {
		return softversion;
	}

	public void setSoftversion(String softversion) {
		this.softversion = softversion;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setWorkMode(CheckWorkMode workMode) {
		this.workMode = workMode;
	}

	public boolean isCommOk() {
		return commOk;
	}

	public void setCommOk(boolean commOk) {
		this.commOk = commOk;
	}

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}
	
	
	
}
