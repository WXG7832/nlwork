package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.SmogInfo;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.li.accessory.SmogAlertData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年3月5日 下午1:39:49 烟雾管理器
 */
public abstract class SmogAlertManager {

	protected int driverIndex; // 烟雾报警板序号
	protected AlertState alertState = AlertState.NORMAL; // 报警状态
	protected int timeOut = 3000;
	protected MainBoard mainBoard;
	protected boolean commErr; // 通信故障?
	private final int PEEK_TIME = 5; // 轮询秒数

	protected List<SmogAlertData> smogDatas = new ArrayList<SmogAlertData>();
	protected Logger   logger;

	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public SmogAlertManager(MainBoard mainBoard) throws AlertException {
  
		this.mainBoard = mainBoard;
		
		try {
			logger = LogUtil.createLog("log/smogAlert.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int index = MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.indexOf(new SmogInfo(null, true));
		if (index == -1) {

			throw new AlertException(AlertCode.INIT, "初始化烟雾管理器失败:没有启用烟雾探头");

		}
		// 初始化
		for (int n = 0; n < MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.size(); n++) {

			SmogAlertData sad = new SmogAlertData();
			sad.setDriverIndex(MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.get(n).index);
			smogDatas.add(sad);
			
			logger.info(MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.get(n));

		}
		boolean monitor = MainBoard.startupCfg.getSmogAlertManagerInfo().monitor;
      
		if (monitor) {

			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					try {
						for (int n = 0; n < MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.size(); n++) {

							int index = MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.get(n).index;
							boolean use = MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.get(n).use;
							boolean monitor = MainBoard.startupCfg.getSmogAlertManagerInfo().smogInfos.get(n).monitor;
							
							if (use && monitor) {
								//logger.info("start to read smog state : " + index);
								SmogAlertData sad = readSmogState(index);
								//logger.info("smog state:" + sad);
								commErr = false;
								if (smogDatas.get(index).getAlertState() == AlertState.NORMAL
										&& sad.getAlertState() == AlertState.ALERT) {

									driverIndex = index;
									smogDatas.get(index).setAlertState(sad.getAlertState());
									throw new AlertException(AlertCode.DEVICE_ERROR, "设备发生烟雾报警:" + (index + 1) + "号探头");
								} else if (smogDatas.get(index).getAlertState() == AlertState.ALERT
										&& sad.getAlertState() == AlertState.NORMAL) {

									smogDatas.get(index).setAlertState(AlertState.NORMAL); // 恢复
									Context.getAlertManager().handle(AlertCode.DEVICE_ERROR, "", true); // 消除设备故障报警
								}
							}
						}
					} catch (AlertException ex) {
                         
						logger.info(CommonUtil.getThrowableException(ex));
						if (!commErr) {
							if (Context.getAlertManager() != null) {
								try {
									Context.getAlertManager().handle(ex.getAlertCode(), ex.getMessage(), false);
								} catch (AlertException e1) {

									e1.printStackTrace();
								}
							}
							commErr = true;
						}
					} catch (Throwable t) {

						t.printStackTrace();
					}

				}
			}, 1, PEEK_TIME, TimeUnit.SECONDS);
		}

	}

	public SmogAlertData getSmogAlertData() {

		SmogAlertData sad = new SmogAlertData();
		for (SmogAlertData s : smogDatas) {

			if (s.getAlertState() == AlertState.ALERT) {

				sad.setAlertState(AlertState.ALERT);
				return sad;
			}
		}

		return sad;
	}

	public int getAlertIndex() {

		return driverIndex;
	}

	/**
	 * 读取烟雾报警器状态
	 * 
	 * @author wavy_zheng 2020年3月5日
	 * @param driverIndex
	 * @return
	 */
	public abstract SmogAlertData readSmogState(int driverIndex) throws AlertException;
	
	/**
	 * 写入烟雾报警器状态，清除报警
	 * @author  wavy_zheng
	 * 2021年8月14日
	 * @param driverIndex
	 * @throws AlertException
	 */
	public abstract void  clearSmogState(int driverIndex) throws AlertException ;

}
