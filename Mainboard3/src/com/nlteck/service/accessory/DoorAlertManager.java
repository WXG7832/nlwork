package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.DoorInfo;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.DoorData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年3月6日 上午10:35:30 门禁开关信号管理器
 */
public abstract class DoorAlertManager {

	protected MainBoard mainBoard;
	protected boolean commErr; // 通信故障?
	private final int PEEK_TIME = 5; // 轮询秒数

	protected List<DoorData> doorDatas = new ArrayList<DoorData>();
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public DoorAlertManager(MainBoard mb) throws AlertException {

		this.mainBoard = mb;
		int index = MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.indexOf(new DoorInfo(null, true));
		if (index == -1) {

			throw new AlertException(AlertCode.INIT, "初始化门禁管理器失败:没有启用门禁");

		}
		// 初始化
		for (int n = 0; n < MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.size(); n++) {

			DoorData dd = new DoorData();
			dd.setDriverIndex(MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.get(n).index);
			doorDatas.add(dd);

		}
		boolean monitor = MainBoard.startupCfg.getDoorAlertManagerInfo().monitor;

		if (monitor) {

			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					try {
						for (int n = 0; n < MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.size(); n++) {

							int index = MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.get(n).index;
							boolean use = MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.get(n).use;
							boolean monitor = MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos.get(n).monitor;
							if (use && monitor) {
								DoorData dd = readDoorData(index);
								
								commErr = false;

								if (dd.getState() == ValveState.OPEN
										&& doorDatas.get(index).getState() == ValveState.CLOSE) {

									doorDatas.get(index).setState(dd.getState());
									throw new AlertException(AlertCode.LOGIC, "设备" + (index + 1) + "号门已打开!");
								}

								if (dd.getState() == ValveState.CLOSE
										&& doorDatas.get(index).getState() == ValveState.OPEN) {

									doorDatas.get(index).setState(dd.getState());
									throw new AlertException(AlertCode.LOGIC, "设备" + (index + 1) + "号门已关闭");
								}
							}
						}
					} catch (AlertException ex) {

						mainBoard.pushSendQueue(ex);
					} catch (Throwable t) {
						
						t.printStackTrace();
					}

				}
			}, 1, PEEK_TIME, TimeUnit.SECONDS);
		}
	}

	/**
	 * 读取门禁状态
	 * 
	 * @author wavy_zheng 2020年3月6日
	 * @return
	 */
	public abstract DoorData readDoorData(int index) throws AlertException;

	/**
	 * 获取最新门禁状态
	 * 
	 * @author wavy_zheng 2020年3月6日
	 * @return
	 */
	public DoorData getDoorData() {

		// 当前只要有一个门被打开，则为打开状态

		DoorData dd = new DoorData();

		for (DoorData d : doorDatas) {

			if (d.getState() == ValveState.OPEN) {

				dd.setState(ValveState.OPEN);
				return dd;
			}
		}

		return dd;

	}
}
