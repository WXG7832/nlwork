package com.nlteck.service.accessory.manager;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.constant.Constant.Window;
import com.nlteck.exception.ScreenException;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.ProductType;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.modbus.tcp.util.CommonUtil;

/**
 * 报警管理器，管理液晶屏，三色灯， 蜂鸣器，电源等设备
 * 
 * @author Administrator
 *
 */
public class AlertManager {

	private MainBoard mainboard;
	private Logger    logger;

	/**
	 * 堆栈,各报警统计
	 */
	protected Map<AlertCode, Integer> alertMap = new ConcurrentHashMap<AlertCode, Integer>();

	public AlertManager(MainBoard mb) {

		this.mainboard = mb;
		try {
			logger = LogUtil.createLog("log/alertManager.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void handle(AlertCode code, String message, boolean cancel) throws AlertException {

		handle(code, message, cancel, null);
	}

	/**
	 * 添加报警
	 * 
	 * @param code
	 */
	protected void appendAlert(AlertCode code) {

		if (alertMap.containsKey(code)) {

			alertMap.put(code, alertMap.get(code) + 1);
		} else {

			alertMap.put(code, 1);
		}
	}

	public void handle(AlertCode code, String message, boolean cancel, ControlUnit cu) throws AlertException {

		if (mainboard.isPoweroff()) {
			// 设备处于掉电或初始化状态，不响应任何报警
			return;
		}
		if (!cancel) {

			if (code == null) {

				return;
			}
			appendAlert(code);
			if (getLastAlertData() == code) {

				Environment.infoLogger.info("alert happen : code " + code.name());
				if (code == AlertCode.DEVICE_ERROR) {

					if (mainboard.getAlertCode() != AlertCode.DEVICE_ERROR) {
                        
						logger.info("pause and shut down device:" + code);
						// 设备超压报警或设备严重故障
						Context.getCoreService().emergencyShutdownDevice();
						mainboard.setAlertCode(AlertCode.DEVICE_ERROR);
						//同时切断逻辑板电源
						if(Context.getPowerProvider() != null && MainBoard.startupCfg.getControlInfo().resetPower) {
							
							new Thread(new Runnable() {

								@Override
								public void run() {

									Context.getPowerProvider().resetPower(mainboard);
								}
								
								
							}).start();
							
						}
					}

				} else if (code == AlertCode.TEMP_OVER) {

					// 关闭加热管
					// Context.getAccessoriesService().getTemperatureManager().writeHeatpipeState(false);
					/**
					 * 因加热管会自动关闭，无需主控关闭温控系统
					 */
					// mainBoard.emergencyShutdownHeat();
				} else if (/*code == AlertCode.COMM_ERROR ||*/ code == AlertCode.LOGIC_BOARD
						|| code == AlertCode.CHECK_BOARD || code == AlertCode.OFFLINE) {
                   
					logger.info("pause device:" + code);
					Context.getCoreService().emergencyPause(cu);
				} else if (code == AlertCode.POWER_DOWN
						&& !Context.getAccessoriesService().getEnergySaveData().isUseSmartPower()) {
                   
					logger.info("pause device:" + code);
					Context.getCoreService().emergencyPause(cu);
				} else if (code == AlertCode.PRESSURE) {
                   
					logger.info("pause device:" + code);
					// 机械报警
					Context.getCoreService().emergencyPause(cu);
					// 抬起气缸
					if (Context.getAccessoriesService().getMechanismManager() != null) {

						Context.getAccessoriesService().getMechanismManager().writeValve(0, ValveState.OPEN);
					}
				}

				// 触发当前报警
				// 三色灯
				if (Context.getAccessoriesService().getTripleColorLightController() != null) {

					// 亮红闪灯
					Context.getAccessoriesService().getTripleColorLightController().light(0, LightColor.RED, true);
				}
				if (Context.getAccessoriesService().getBeepController() != null) {

					// 蜂鸣
					Context.getAccessoriesService().getBeepController().beep(0);
				}

				if (Context.getAccessoriesService().getScreenController() != null) {

					if (!message.isEmpty()) {

						try {
							Context.getAccessoriesService().getScreenController().switchScreen(Window.ALERT);
							Context.getAccessoriesService().getScreenController().showAlertInfo(code.ordinal(),
									code.toString(), new Date(), message);
						} catch (ScreenException e) {

							e.printStackTrace();
							throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ScreenCommError));
						}
					}
				}

				mainboard.setAlertCode(code);

			}

		} else {

			
			removeAlert(code);
			

		}
		// 没有任何报警时
		if (getLastAlertData() == null) {

			// 设备恢复正常
			mainboard.setAlertCode(AlertCode.NORMAL);

			if (mainboard.getState() == State.FORMATION) {

				if (Context.getAccessoriesService().getTripleColorLightController() != null) {
					Context.getAccessoriesService().getTripleColorLightController().light(0, LightColor.GREEN, false);
					System.out.println("绿灯");
				}

			} else if (mainboard.getState() == State.PAUSE) {

				if (Context.getAccessoriesService().getTripleColorLightController() != null) {
					Context.getAccessoriesService().getTripleColorLightController().light(0, LightColor.YELLOW, true);
					System.out.println("黄闪");
				}

			} else if (mainboard.getState() == State.CAL) {

				if (Context.getAccessoriesService().getTripleColorLightController() != null) {
					Context.getAccessoriesService().getTripleColorLightController().light(0, LightColor.GREEN, true);
					System.out.println("绿闪");
				}

			} else {

				if (Context.getAccessoriesService().getTripleColorLightController() != null) {
					Context.getAccessoriesService().getTripleColorLightController().light(0, LightColor.YELLOW, false);
					System.out.println("黄灯");
				}

			}

			if (Context.getAccessoriesService().getScreenController() != null) {
				try {
					if (mainboard.getState() == State.FORMATION) {

						Context.getAccessoriesService().getScreenController().switchScreen(Window.DRIVING);

					} else if (mainboard.getState() == State.CAL) {

						Context.getAccessoriesService().getScreenController().switchScreen(Window.LOADING);
						Context.getAccessoriesService().getScreenController().showStartupInfo(true,
								I18N.getVal(I18N.EnterCalMode));

					} else {

						Context.getAccessoriesService().getScreenController().switchScreen(Window.FUNC);
					}
				} catch (ScreenException e) {

					e.printStackTrace();
					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ScreenCommError));
				}
			}

		}

		if (!message.isEmpty()) {

			if (cancel) {

				Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL, message);
			} else {

				if (code != AlertCode.OFFLINE) {
					// 推送报警消息
					Context.getPcNetworkService().pushSendQueue(0xff, -1, code, message);
				}
			}
		}
	}

	/**
	 * 获取最新的报警信息，按照一定优先级
	 * 
	 * @return
	 */
	public AlertCode getLastAlertData() {

		if (alertMap.get(AlertCode.DEVICE_ERROR) != null) {

			return AlertCode.DEVICE_ERROR;
		}
		if (alertMap.get(AlertCode.COMM_ERROR) != null) {

			return AlertCode.COMM_ERROR;
		}
		if (alertMap.get(AlertCode.PRESSURE) != null) {

			return AlertCode.PRESSURE;
		}
		if (alertMap.get(AlertCode.INIT) != null) {

			return AlertCode.INIT;
		}
		if (alertMap.get(AlertCode.LOGIC_BOARD) != null) {

			return AlertCode.LOGIC_BOARD;
		}
		if (alertMap.get(AlertCode.CHECK_BOARD) != null) {

			return AlertCode.CHECK_BOARD;
		}
		if (alertMap.get(AlertCode.OFFPOWER) != null) {

			return AlertCode.OFFPOWER;
		}
		if (alertMap.get(AlertCode.OFFLINE) != null) {

			return AlertCode.OFFLINE;
		}
		if (alertMap.get(AlertCode.TEMP_OVER) != null) {

			return AlertCode.TEMP_OVER;
		}
		if (alertMap.get(AlertCode.POWER_DOWN) != null) {

			return AlertCode.POWER_DOWN;
		}
		if (alertMap.get(AlertCode.FAN) != null) {

			return AlertCode.FAN;
		}
		if (alertMap.get(AlertCode.POLE_REVERSE) != null) {

			return AlertCode.POLE_REVERSE;
		}
		if (alertMap.get(AlertCode.LOGIC) != null) {

			return AlertCode.LOGIC;
		}
		return null;
	}

	/**
	 * 删除对应的报警
	 * 
	 * @param code
	 */
	protected void removeAlert(AlertCode code) {

		if (code == null) {

			alertMap.clear();
		} else {

			if (alertMap.containsKey(code)) {

				alertMap.remove(code);

			}
		}
	}

}
