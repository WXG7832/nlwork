package com.nlteck.service.accessory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.TempBoardInfo;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.OverTempFlag;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.TempBoardType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.TempQueryData;
import com.nltecklib.protocol.li.accessory.TempStateQueryData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.TempData;

/**
 * 温控管理器
 * 
 * @author Administrator
 *
 */
public abstract class TemperatureManager {

	public final static String CFG_PATH = "./config/temperature.xml";
	protected MainBoard mainBoard;
	protected TempBoardInfo meterInfo;
	protected TempBoardInfo protectMeterInfo;
	protected TempMeter meter;
	protected TempMeter protectedMeter;
	protected int protectMinute; // 时间保护
	protected double temperature; // 缓存读取到的温度
	protected double constTemp; // 恒定温度配置

	protected TempStateQueryData stateQueryData; // 状态数据
	protected OverTempFlag overTempFlag = OverTempFlag.NORMAL; // 超温报警标志

	protected boolean alert; // 是否已报警
	protected boolean reset; // 是否重置过温控表
	protected boolean commErr; // 通信故障
	protected boolean   use; //是否使用
	protected boolean   monitor; //是否监控
    
	protected int    commErrCount; //通信失联次数
	protected int elapsedSeconds; // 温控打开后累计的时间s
	

	private final int PEEK_TIME = 4; // 轮询秒数
	// 温度控制精度
	public final static double PRECISION = 3;

	protected int waitSeconds; // 待机累计时长

	protected FanManager fanManager; // 涡轮风机

	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	protected TemperatureManager(MainBoard mainBoard) throws AlertException {

		this.mainBoard = mainBoard;
		this.use = MainBoard.startupCfg.getOMRManagerInfo().use;
		this.monitor = MainBoard.startupCfg.getOMRManagerInfo().monitor;
		int index = MainBoard.startupCfg.getOMRManagerInfo().meterInfos
				.indexOf(new TempBoardInfo(TempBoardType.OMR, true, null));
		if (index != -1) {

			meterInfo = MainBoard.startupCfg.getOMRManagerInfo().meterInfos.get(index);
		}
		index = MainBoard.startupCfg.getOMRManagerInfo().meterInfos
				.indexOf(new TempBoardInfo(TempBoardType.OMR_PROTECT, true, null));
		if (index != -1) {

			protectMeterInfo = MainBoard.startupCfg.getOMRManagerInfo().meterInfos.get(index);
		}
	}

	/**
	 * 开始监视温度
	 * 
	 * @author wavy_zheng 2020年12月12日
	 * @throws AlertException
	 */
	public void monitorTemperature()  {

		try {
			if (mainBoard.isPoweroff() || !mainBoard.isInitOk()) {

				return; // 设备停电或初始化失败，不监视温度
			}

			TempQueryData tqd = readTempQueryData(); // 从温控表读取温度信息

			if (constTemp == 0) {

				constTemp = meter.readConstTemperature();
			}

			// 报警检测
			checkTempAlert(tqd);

			// 更新缓存
			temperature = tqd.getMainTemp();
			if (protectedMeter != null) {
				overTempFlag = protectedMeter.getOverFlag();
			}

			
			// 对读到的状态数据进行解码,如故障也会报警
			decodeTempState(readTempState());
			

			if (!reset && isTempControlOpen() && constTemp > 0 && temperature < constTemp - PRECISION) {

				// 恒温系统即将失控，临时关闭温控置位温控表
				power(PowerState.OFF);
				// 再重新开启
				power(PowerState.ON);

				reset = true;
			}
			// 待机检测
			checkWait();

			// 恢复
			if (commErr) {

				// 消除通信错误
				Context.getAlertManager().handle(AlertCode.COMM_ERROR, "", true);
				commErrCount = 0;
			}
		} catch (AlertException ex) {
            
			
			if(ex.getAlertCode() == AlertCode.COMM_ERROR) {
				
				//计数
				commErrCount++;
				if(commErrCount < 3) { //3次冗余通信
					
					 return ;
				}
				
			} 
			//生产报警	
			try {
				Context.getAlertManager().handle(ex.getAlertCode(),ex.getMessage(),ex.getMessage().contains("恢复正常") ? true : false);
			} catch (AlertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 读取表状态
	 * 
	 * @return
	 * @throws AlertException
	 */
	public abstract TempStateQueryData readTempState() throws AlertException;

	/**
	 * 配置恒定温度
	 */
	public abstract void writeTemperature(double temperature) throws AlertException;

	/**
	 * 从温控表读取温控信息
	 * 
	 * @return
	 * @throws AlertException
	 */
	protected abstract TempQueryData readTempQueryData() throws AlertException;

	/**
	 * 读取温度
	 */
	public double readTemperature() {

		return temperature;
	}

	/**
	 * 配置温度报警上限
	 */
	public abstract void writeTempUpper(double temperature) throws AlertException;

	/**
	 * 读取温度报警上限
	 */
	public abstract double readTempUpper() throws AlertException;

	/**
	 * 配置温度报警下限
	 */
	public abstract void writeTempLower(double temperature) throws AlertException;

	/**
	 * 读取温度报警下限
	 */
	public abstract double readTempLower() throws AlertException;

	/**
	 * 打开或关闭温控
	 */
	public abstract void power(PowerState ps) throws AlertException;

	/**
	 * 读取超温报警标志
	 * 
	 * @return
	 * @throws AlertException
	 */
	public OverTempFlag readOverTempFlag() {

		return overTempFlag;
	}

	public boolean isTempControlOpen() {

		return meter.getPs() == PowerState.ON;
	}

	public TempMeter getTempMeter() {

		return meter;
	}

	public TempMeter getProtectedTempMeter() {

		return protectedMeter;
	}

	/**
	 * 恒温已经生效;处于恒温
	 * 
	 * @return
	 */
	public boolean isTemperatureInRange() {

		if (temperature < constTemp - PRECISION) {

			return false;
		}
		if (temperature > constTemp + PRECISION) {

			return false;
		}

		return true;
	}

	/**
	 * 检测是否发生温度超限报警
	 */
	private void checkTempAlert(TempQueryData tqd) throws AlertException {

		// 检测超温报警标志
		if (!alert) {

			if (overTempFlag != tqd.getOverTempFlag() && tqd.getOverTempFlag() == OverTempFlag.ALERT) {

				alert = true;
				throw new AlertException(AlertCode.TEMP_OVER, I18N.getVal(I18N.TempMeterOver));
			} else {

				// 双重检测
				if (isTempControlOpen() && tqd.getMainTemp() > meter.getTempUpper() && meter.getTempUpper() > 0) {

					alert = true;
					throw new AlertException(AlertCode.TEMP_OVER,
							I18N.getVal(I18N.DeviceTempUpper, tqd.getMainTemp(), meter.getTempUpper()));
				}
			}

			// 在设备运行阶段检测低温保护
			if (mainBoard.getState() == State.FORMATION && isTempControlOpen()) {

				if (meter.getTempLower() > 0 && tqd.getMainTemp() < meter.getTempLower()) {

					alert = true;
					// 触发超低温报警
					throw new AlertException(AlertCode.TEMP_OVER,
							I18N.getVal(I18N.DeviceTempLower, tqd.getMainTemp(), meter.getTempLower()));

				}
			}

			// 时间保护
			if (protectMinute > 0 && isTempControlOpen()) {

				if (!isTemperatureInRange()) {
					if (elapsedSeconds / 60 >= protectMinute) {

						alert = true;
						// 触发时间保护
						throw new AlertException(AlertCode.TEMP_OVER, "设备温度"
								+ CommonUtil.formatNumber(tqd.getMainTemp(), 1) + "在" + protectMinute + "分钟内未达到恒温范围");
					}
					elapsedSeconds += PEEK_TIME;
				} else {

					elapsedSeconds = 0; // 已达到恒温范围则清空累计时间
				}

			}

			// 主控温度和副控温度比较
			if (protectedMeter != null && isTemperatureInRange()) {

				// 当主控温度稳定时，开始比较主副温度差
				if (Math.abs(tqd.getMainTemp() - protectedMeter.getTemperature()) > PRECISION) {

					alert = true;
					throw new AlertException(AlertCode.TEMP_OVER,
							"设备主控温度" + CommonUtil.formatNumber(tqd.getMainTemp(), 1) + "和设备副控温度"
									+ CommonUtil.formatNumber(protectedMeter.getTemperature(), 1) + "相差过大");

				}

			}

		} else {

			if (tqd.getOverTempFlag() == OverTempFlag.NORMAL && !isAnyMeterFault()) {

				if (tqd.getMainTemp() <= meter.getTempUpper()) {

					if (mainBoard.getState() == State.FORMATION) {

						if (meter.getTempLower() > 0 && tqd.getMainTemp() >= meter.getTempLower()) {

							alert = false; // 温控报警恢复
						}
					} else {

						alert = false; // 待机下恢复报警
					}
				}
				if (isTemperatureInRange() && protectedMeter != null) {

					// 当主控温度稳定时，开始比较主副温度差
					if (Math.abs(tqd.getMainTemp() - protectedMeter.getTemperature()) <= PRECISION) {

						alert = false; // 主副控温度恢复
					}

				} else {

					alert = false; // 加热或冷却中恢复
				}

			}
			if(!alert) {
				
				//恢复
				throw new AlertException(AlertCode.TEMP_OVER,
						"设备温控系统恢复正常");
				
			}
		}

	}

	public boolean isAlert() {
		return alert;
	}

	public boolean isReset() {
		return reset;
	}

	public boolean isAnyMeterFault() {

		if (meter.getWs() == WorkState.FAULT) {

			return true;
		}
		if (protectedMeter != null && protectedMeter.getWs() == WorkState.FAULT) {

			return true;
		}

		return false;
	}

	/**
	 * 待机检测
	 * 
	 * @throws AlertException
	 */
	private void checkWait() throws AlertException {

		if (!isTempControlOpen()) {

			waitSeconds = 0;
			return; // 已关闭则
		}
		if (mainBoard.getState() == State.FORMATION || mainBoard.getState() == State.CAL) {

			waitSeconds = 0;
			return;
		}
		if (mainBoard.getEnergySaveData().getMaxTempControlWaitMin() == 0) {

			waitSeconds = 0;
			return;
		}
		waitSeconds += PEEK_TIME;
		if (mainBoard.getEnergySaveData().getMaxTempControlWaitMin() > 0
				&& waitSeconds >= mainBoard.getEnergySaveData().getMaxTempControlWaitMin() * 60) {

			mainBoard.pushSendQueue(0xff, -1, AlertCode.NORMAL, "设备待机时长" + waitSeconds / 60 + "min,开始关闭温控系统");
			// 关闭温控
			power(PowerState.OFF);

			waitSeconds = 0; // 清除待机时长
			// 关闭涡轮风机
			// if(getFanManager() != null) {
			//
			// getFanManager().powerTurboFan(PowerState.OFF);
			// }
		}

	}

	/**
	 * 检测是否能正常启动流程
	 * 
	 * @throws AlertException
	 */
	public void checkStartup() throws AlertException {

		if (isAnyMeterFault()) {

			throw new AlertException(AlertCode.TEMP_OVER, I18N.getVal(I18N.TempMeterError));
		}
		// 温度有无超上限
		if (meter.getTempUpper() > 0 && temperature > meter.getTempUpper()) {

			throw new AlertException(AlertCode.TEMP_OVER, I18N.getVal(I18N.DeviceTempUpper,
					temperature, meter.getTempUpper()));
		}

		if (meter.getProcedureStartup() == PowerState.ON && !isTempControlOpen()) {

			throw new AlertException(AlertCode.TEMP_OVER, I18N.getVal(I18N.TempProtectNotOpen));
		}

		if (isTempControlOpen()) {

			if (meter.getProcedureStartup() == PowerState.ON && !isTemperatureInRange()) {

				throw new AlertException(AlertCode.TEMP_OVER,
						I18N.getVal(I18N.TempProtectNotInRange,temperature));
			} else if (meter.getProcedureStartup() == PowerState.OFF && meter.getTempLower() > 0
					&& temperature < meter.getTempLower()) {

				throw new AlertException(AlertCode.TEMP_OVER,
						I18N.getVal(I18N.DeviceTempLower,temperature, meter.getTempLower()));
			}
		} else {

			if (temperature > AccessoryEnvironment.CONTROL_OFF_TEMP_UPPER) {

				throw new AlertException(AlertCode.TEMP_OVER, I18N.getVal(I18N.TempOverUnderCloseSystem , temperature,
						AccessoryEnvironment.CONTROL_OFF_TEMP_UPPER ));
			}
		}

	}

	public void saveCfg(TempData tempData) throws AlertException {

		Context.getFileSaveService().writeTempControlFile(tempData);

	}

	public TempData readCfg() throws AlertException {

		TempData tempData = Context.getFileSaveService().readTempControlFile();

		constTemp = tempData.getTempConstant();
		meter.setTempLower(tempData.getLower());
		meter.setTempUpper(tempData.getUpper());
		meter.setProcedureStartup(tempData.isSyncTempOpen() ? PowerState.ON : PowerState.OFF);

		if (protectedMeter != null) {

			protectedMeter.setTempUpper(tempData.getUpper()); // 超温保护
		}

		return tempData;
	}

	/**
	 * 设置随温开关
	 * 
	 * @param open
	 */
	public void setSyncTempOpen(boolean open) {

		meter.setProcedureStartup(open ? PowerState.ON : PowerState.OFF);
	}
	
	/**
	 * 随温模式是否打开?
	 * @author  wavy_zheng
	 * 2020年12月14日
	 * @return
	 */
	public boolean isSyncTempOpen() {
		
		return meter.getProcedureStartup() == PowerState.ON;
	}

	public double getConstTemp() {
		return constTemp;
	}

	public int getProtectMinute() {
		return protectMinute;
	}

	public void setProtectMinute(int protectMinute) {
		this.protectMinute = protectMinute;
	}

	public FanManager getFanManager() {
		return fanManager;
	}

	public void setFanManager(FanManager fanManager) {
		this.fanManager = fanManager;
	}
	
	/**
	 * 设置表累计运行时长
	 * @author  wavy_zheng
	 * 2021年1月29日
	 * @param index
	 * @param miliseconds
	 */
	public void setMeterRunMiliseconds(int index , long miliseconds) {
		
		if(index == 0) {
			
			meter.setRunMiliseconds(miliseconds);
		} else if(index == 1 && protectedMeter != null) {
			
			protectedMeter.setRunMiliseconds(miliseconds);
			
		}
		
	}
	
	
	public long getMeterRunMiliseconds(int index) {
		
		long miliseconds = 0;
        if(index == 0) {
			
        	miliseconds = meter.getRunMiliseconds();
		} else if(index == 1 && protectedMeter != null) {
			
			miliseconds = protectedMeter.getRunMiliseconds();
			
		}
        
        return miliseconds;
	}
	

	/**
	 * 解码
	 * 
	 * @param otsqd
	 * @throws AlertException
	 */
	private void decodeTempState(TempStateQueryData otsqd) throws AlertException {

		meter.setPs((otsqd.getRunFlag() & 0x01 << meter.getIndex()) > 0 ? PowerState.ON : PowerState.OFF);
		WorkState oldWs = meter.getWs();
		meter.setWs((otsqd.getErrFlag() & 0x01 << meter.getIndex()) > 0 ? WorkState.FAULT : WorkState.NORMAL);
		if (oldWs == WorkState.NORMAL && meter.getWs() == WorkState.FAULT) {

			alert = true;
			throw new AlertException(AlertCode.TEMP_OVER, "温控表" + (meter.getIndex() + 1) + "发生故障!");
		}

		if (protectedMeter != null) {

			protectedMeter.setPs(
					(otsqd.getRunFlag() & 0x01 << protectedMeter.getIndex()) > 0 ? PowerState.ON : PowerState.OFF);
			oldWs = protectedMeter.getWs();

			protectedMeter.setWs(
					(otsqd.getErrFlag() & 0x01 << protectedMeter.getIndex()) > 0 ? WorkState.FAULT : WorkState.NORMAL);
			if (oldWs == WorkState.NORMAL && protectedMeter.getWs() == WorkState.FAULT) {

				alert = true;
				throw new AlertException(AlertCode.TEMP_OVER, "温控表" + (protectedMeter.getIndex() + 1) + "发生故障!");
			}
		}
	}

	/**
	 * 打开或关闭温控系统加热管
	 * 
	 * @author wavy_zheng 2020年12月12日
	 * @param open
	 */
	public abstract void writeHeatpipeState(boolean open) throws AlertException;

	public boolean isUse() {
		return use;
	}

	public boolean isMonitor() {
		return monitor;
	}
	
	
	

}
