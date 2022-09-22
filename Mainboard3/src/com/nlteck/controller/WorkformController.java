package com.nlteck.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.CheckBoard;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.camera.Entity;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.main.AlertData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.OfflinePickupData;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.power.ConfigDecorator;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Decorator;
import com.nltecklib.protocol.power.Environment.Result;
import com.nltecklib.protocol.power.QueryDecorator;
import com.nltecklib.protocol.power.ResponseDecorator;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.WorkMode;
import com.nltecklib.protocol.power.calBox.calBox_device.MBChannelSwitchData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbBaseInfoData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbCalibrateChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbDriverModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbHeartbeatData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMatchAdcData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbSelfCheckData;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalculateData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverHeartbeatData;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData.AdcData;
import com.nltecklib.protocol.power.driver.DriverModuleSwitchData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.utils.LogUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月23日 下午5:43:12 校准命令处理器
 */
public class WorkformController {

	private MainBoard mainboard;
	private Logger logger;

	public WorkformController(MainBoard mainboard) {

		this.mainboard = mainboard;
		logger = LogUtil.getLogger("workformController");
	}

	public ResponseDecorator processCmdFromNetwork(Decorator command) {

		if (command instanceof ConfigDecorator) {

			return processConfig(command);

		} else if (command instanceof QueryDecorator) {

			return processQuery(command);
		} else if (command instanceof ResponseDecorator) {

			processResponse(command);
		}

		return null;
	}

	private void processResponse(Decorator command) {

		Data data = command.getDestData();
		/*
		 * if (data instanceof PickupData) {
		 * 
		 * // 收到PC的推送数据确认回复 int unitIndex = data.getUnitIndex(); if (unitIndex != -1 &&
		 * unitIndex != 0xff) {
		 * 
		 * // 收到数据接收回复，记录时间
		 * mainboard.getLogicBoards().get(unitIndex).revcPushResponseData();
		 * 
		 * }
		 * 
		 * } else if (data instanceof AlertData || data instanceof OfflinePickupData) {
		 * 
		 * Context.getPcNetworkService().confirmAlertOrOfflineResponse();
		 * 
		 * }
		 */
	}

	private ResponseDecorator processQuery(Decorator command) {

		logger.info("processQuery command");
		Data data = command.getDestData();

		ResponseDecorator response = new ResponseDecorator(data, false);
		int driverIndex = data.getDriverIndex(); // 驱动板号
		int chnIndex = data.getChnIndex();
		
		// LogicBoard lb = unitIndex == 0xff || unitIndex == -1 ? null :
		// mainboard.getLogicBoards().get(unitIndex);
		try {

			if (data instanceof MbCalibrateChnData) {

				logger.info("recv:" + data);
				DriverCalibrateData cal = Context.getDriverboardService().readCalibrate(driverIndex, chnIndex);
				MbCalibrateChnData responseData = new MbCalibrateChnData();
				responseData.setDriverIndex(driverIndex);
				responseData.setChnIndex(chnIndex);
				responseData.setCurrentDA((int) cal.getProgramI());
				responseData.setVoltageDA((int) cal.getProgramV());
				responseData.setModuleIndex(cal.getModuleIndex());
				responseData.setAdcDatas(cal.getAdcDatas());
				responseData.setPole(cal.getPole());
				responseData.setMode(cal.getMode());
				responseData.setRange(cal.getRange());
				responseData.setQueryAdcNum(cal.getPickCount());

				logger.info(
						"response chnIndex:" + responseData.getChnIndex() + " ,adc = " + responseData.getAdcDatas());
				response = new ResponseDecorator(responseData, true);
			} else if (data instanceof MbMeasureChnData) {

				logger.info(data);
				DriverCalculateData lccd = Context.getDriverboardService().readCalculate(driverIndex, chnIndex);
				MbMeasureChnData responseData = new MbMeasureChnData();
				responseData.setDriverIndex(driverIndex);
				responseData.setChnIndex(chnIndex);

				responseData.setPole(lccd.getPole());
				responseData.setMode(lccd.getMode());
				responseData.setCalculateDot(lccd.getCalculateDot());
				responseData.setProgramDot(lccd.getProgramDot());
				responseData.setProgramKReadonly(lccd.getProgramKReadonly());
				responseData.setProgramBReadonly(lccd.getProgramBReadonly());
				responseData.setProgramDotReadonly(lccd.getProgramDotReadonly());
				responseData.setAdcKReadonly(lccd.getAdcKReadonly());
				responseData.setAdcBReadonly(lccd.getAdcBReadonly());
				responseData.setBackAdcKReadonly1(lccd.getBackAdcKReadonly1());
				responseData.setBackAdcBReadonly1(lccd.getBackAdcBReadonly1());
				responseData.setBackAdcKReadonly2(lccd.getBackAdcKReadonly2());
				responseData.setBackAdcBReadonly2(lccd.getBackAdcBReadonly2());
				responseData.setModuleIndex(lccd.getMouduleIndex());
				responseData.setAdcDatas(lccd.getAdcDatas());

				logger.info("push orig data:" + lccd);
				for (int n = 0; n < lccd.getAdcDatas().size(); n++) {

					logger.info(n + ":main " + lccd.getAdcDatas().get(n).adcList.get(0));
					logger.info(n + ":sub " + lccd.getAdcDatas().get(n).adcList.get(1));
				}

				logger.info("push mb data:" + responseData);
				for (int n = 0; n < responseData.getAdcDatas().size(); n++) {
					logger.info(n + ":main " + responseData.getAdcDatas().get(n).adcList.get(0));
					logger.info(n + ":sub " + responseData.getAdcDatas().get(n).adcList.get(1));
				}
				logger.info("complete ---- mb encode");
				response = new ResponseDecorator(responseData, true);

			} else if (data instanceof MbFlashParamData) {

				logger.info(data);
				QueryDecorator decorator=(QueryDecorator)command;
				int length =  decorator.getDestData().getEncodeData().size();
				int moduleIndex=decorator.getDestData().getEncodeData().get(length - 1);
				System.out.println("modulIndex:"+decorator.getDestData().getEncodeData());
				DriverCalParamSaveData dcpsd = Context.getDriverboardService().readFlash(driverIndex, chnIndex ,moduleIndex);
				
				MbFlashParamData mlfwd = new MbFlashParamData();
				mlfwd.setDriverIndex(driverIndex);
				mlfwd.setChnIndex(chnIndex);
				mlfwd.setCv1DotCount(dcpsd.getCv1DotCount());
				mlfwd.setCv2DotCount(dcpsd.getCv2DotCount());
				mlfwd.setModuleIndex(dcpsd.getModuleIndex());
				if(dcpsd.getSaveDataMap() != null && dcpsd.getSaveDataMap().containsKey(CalMode.SLEEP)) {
					mlfwd.append(CalMode.SLEEP, dcpsd.getSaveDataMap().get(CalMode.SLEEP));
				}
				if(dcpsd.getSaveDataMap() != null && dcpsd.getSaveDataMap().containsKey(CalMode.CC)) {
					mlfwd.append(CalMode.CC, dcpsd.getSaveDataMap().get(CalMode.CC));
				}
				if(dcpsd.getSaveDataMap() != null && dcpsd.getSaveDataMap().containsKey(CalMode.CV)) {
					mlfwd.append(CalMode.CV, dcpsd.getSaveDataMap().get(CalMode.CV));
				}
				if(dcpsd.getSaveDataMap() != null && dcpsd.getSaveDataMap().containsKey(CalMode.DC)) {
					mlfwd.append(CalMode.DC, dcpsd.getSaveDataMap().get(CalMode.DC));
				}
				mlfwd.append(CalMode.SLEEP, dcpsd.getSaveDataMap().get(CalMode.SLEEP));
				mlfwd.append(CalMode.CC, dcpsd.getSaveDataMap().get(CalMode.CC));
				mlfwd.append(CalMode.CV, dcpsd.getSaveDataMap().get(CalMode.CV));
				mlfwd.append(CalMode.DC, dcpsd.getSaveDataMap().get(CalMode.DC));
				
				logger.info("response chnIndex:" + mlfwd.getChnIndex() + " " + mlfwd);
				response = new ResponseDecorator(mlfwd ,true);

			} else if (data instanceof MbMatchAdcData) {

				logger.info(data);

				final Map<String, List<DriverBoard>> map = Context.getCoreService()
						.getDriverByPortMap(mainboard.getDriverBoards());
				final CountDownLatch latch = new CountDownLatch(map.size());
				final List<AlertException> exceptions = new ArrayList<>();
				final MbMatchAdcData mscd = new MbMatchAdcData();
				for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {

					final String portName = it.next();
					new Thread(new Runnable() {

						@Override
						public void run() {

							try {

								for (DriverBoard db : map.get(portName)) {

									DriverMatchAdcData dmad = Context.getDriverboardService()
											.readMatchAdcs(db.getDriverIndex());

									// 防止同时塞入引发ConcurrentModifyException
									synchronized (mscd) {

										for (AdcData adc : dmad.getAdcList()) {

											mscd.addAdcData(adc);
										}

									}

								}
							} catch (AlertException ex) {

								ex.printStackTrace();
								exceptions.add(ex);
							} finally {

								latch.countDown();
							}

						}

					}).start();

				}

				latch.await();
				if (!exceptions.isEmpty()) {

					throw exceptions.get(0);
				}

				response = new ResponseDecorator(mscd, true);

			} else if (data instanceof MbSelfCheckData) {

				logger.info(data);

				final Map<String, List<DriverBoard>> map = Context.getCoreService()
						.getDriverByPortMap(mainboard.getDriverBoards());
				final CountDownLatch latch = new CountDownLatch(map.size());
				final List<AlertException> exceptions = new ArrayList<>();
				final MbSelfCheckData mscd = new MbSelfCheckData();
				for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {

					final String portName = it.next();
					new Thread(new Runnable() {

						@Override
						public void run() {

							try {

								for (DriverBoard db : map.get(portName)) {

									if (db.isUse()) {
										DriverCheckData dcd = Context.getDriverboardService()
												.readDriverSelfCheckInfo(db.getDriverIndex());

										// 防止同时塞入引发ConcurrentModifyException
										synchronized (mscd) {
											mscd.getCheckDataList().add(dcd);
										}
									}

								}
							} catch (AlertException ex) {

								ex.printStackTrace();
								exceptions.add(ex);
							} finally {

								latch.countDown();
							}

						}

					}).start();

				}

				latch.await();
				if (!exceptions.isEmpty()) {

					throw exceptions.get(0);
				}

				mscd.setDriverIndex(driverIndex);
				response = new ResponseDecorator(mscd, true);

			} else if (data instanceof MbBaseInfoData) {

				MbBaseInfoData mbid = new MbBaseInfoData();

				mbid.setDriverCount(MainBoard.startupCfg.getDriverCount());
				long flag = 0;
				for (int n = 0; n < MainBoard.startupCfg.getDriverCount(); n++) {

					if (MainBoard.startupCfg.getDriverInfo(n).use) {
						flag |= 0x01 << n;
					}
				}
				mbid.setEnableFlag(flag);
				response = new ResponseDecorator(mbid, true);

				logger.info(mbid);

			} else {

				logger.info("unkown data :" + data);
			}

		} catch (Exception e) {

			logger.error(CommonUtil.getThrowableException(e));
			if (response.getResult() == Result.SUCCESS) {

				response.setResult(Result.FAIL);
			}
			// 设置错误信息
			response.setInfo(e.getMessage());

		}
		return response;

	}

	private ResponseDecorator processConfig(Decorator command) {

		Data data = command.getDestData();
		ResponseDecorator response = new ResponseDecorator(data, false);
		int driverIndex = data.getDriverIndex(); // 驱动板号
		int chnIndex = data.getChnIndex();
		try {

			if (MainBoard.startupCfg.isUseVirtualData()) {

//				throw new AlertException(AlertCode.LOGIC, "test!");
			}

			if (data instanceof MbCalibrateChnData) {

				logger.info("recv:" + data);
				MbCalibrateChnData mccbd = (MbCalibrateChnData) data;
				DriverCalibrateData ccbd = new DriverCalibrateData();
				ccbd.setDriverIndex(driverIndex);
				ccbd.setChnIndex(chnIndex);
				ccbd.setMode(mccbd.getMode());
				ccbd.setPole(mccbd.getPole());
				ccbd.setModuleIndex(mccbd.getModuleIndex());
				ccbd.setProgramV(mccbd.getVoltageDA());
				ccbd.setProgramI(mccbd.getCurrentDA());
				ccbd.setRange(mccbd.getRange());
				ccbd.setPickCount(mccbd.getAdcDatas().size());

				Context.getDriverboardService().writeCalibrate(ccbd);

			} else if (data instanceof MbMeasureChnData) {

				logger.info("recv:" + data);
				MbMeasureChnData mmcd = (MbMeasureChnData) data;
				DriverCalculateData dccd = new DriverCalculateData();
				dccd.setDriverIndex(driverIndex);
				dccd.setChnIndex(chnIndex);
				dccd.setCalculateDot(mmcd.getCalculateDot());
				dccd.setMouduleIndex((byte) mmcd.getModuleIndex());
				dccd.setPole(mmcd.getPole());
				dccd.setMode(mmcd.getMode());
				dccd.setProgramDot(mmcd.getProgramDot());
				dccd.setDataCount(mmcd.getAdcDatas().size());

				logger.info("config:" + dccd);
				Context.getDriverboardService().writeCalculate(dccd);
			} else if (data instanceof MbFlashParamData) {

				MbFlashParamData flashData = (MbFlashParamData) data;
				logger.info("recv: cc dot count = " + flashData.getKb_dotMap().get(CalMode.CC).size()
						+ ",cv dot count = " + flashData.getKb_dotMap().get(CalMode.CV).size() + ",dc dot count="
						+ flashData.getKb_dotMap().get(CalMode.DC).size());

				if (!flashData.getKb_dotMap().get(CalMode.CC).isEmpty()) {
					logger.info("first cc adc:" + flashData.getKb_dotMap().get(CalMode.CC).get(0).adc);
					logger.info("first cc meter:" + flashData.getKb_dotMap().get(CalMode.CC).get(0).meter);
					logger.info("first cc adcB:" + flashData.getKb_dotMap().get(CalMode.CC).get(0).adcB);
				}

				DriverCalParamSaveData lfwd = new DriverCalParamSaveData();
				lfwd.setDriverIndex(driverIndex);
				lfwd.setChnIndex(chnIndex);
				lfwd.setModuleIndex(flashData.getModuleIndex());
				lfwd.setCv1DotCount(flashData.getCv1DotCount());
				lfwd.setCv2DotCount(flashData.getCv2DotCount());

				lfwd.getSaveDataMap().put(CalMode.CC, flashData.getKb_dotMap().get(CalMode.CC));
				lfwd.getSaveDataMap().put(CalMode.CV, flashData.getKb_dotMap().get(CalMode.CV));
				lfwd.getSaveDataMap().put(CalMode.DC, flashData.getKb_dotMap().get(CalMode.DC));
				lfwd.getSaveDataMap().put(CalMode.SLEEP, flashData.getKb_dotMap().get(CalMode.SLEEP));

				logger.info("config:" + lfwd);
				Context.getDriverboardService().writeFlash(lfwd);
				
				
//				MbFlashParamData result=new MbFlashParamData();
//				result.setDriverIndex(driverIndex);
//				result.setChnIndex(chnIndex);
//				result.setModuleIndex(flashData.getModuleIndex());
//				result.setCv1DotCount(flashData.getCv1DotCount());
//				result.setCv2DotCount(flashData.getCv2DotCount());
//				result.append(CalMode.CC, flashData.getKb_dotMap().get(CalMode.CC));
//				result.append(CalMode.CV, flashData.getKb_dotMap().get(CalMode.CV));
//				result.append(CalMode.DC, flashData.getKb_dotMap().get(CalMode.DC));
//				result.append(CalMode.SLEEP, flashData.getKb_dotMap().get(CalMode.SLEEP));
//				
////				response = new ResponseDecorator(new MbFlashParamData(), true);
//				response = new ResponseDecorator(result, true);
//				logger.info("config2:" + command);

			} else if (data instanceof MbHeartbeatData) {

				System.out.println("心跳");
				DriverHeartbeatData heartbeat = new DriverHeartbeatData();
				heartbeat.setDriverIndex(driverIndex);
				Context.getDriverboardService().writeHeartbeat(heartbeat);

			} else if (data instanceof MBChannelSwitchData) {

				// 模片使能
				MBChannelSwitchData msd = (MBChannelSwitchData) data;
				logger.info("recv:" + msd);
				DriverModuleSwitchData lmsd = new DriverModuleSwitchData();
				lmsd.setDriverIndex(driverIndex);
				lmsd.setChnIndex(chnIndex);
				lmsd.setOpen(msd.isEnabled());
				logger.info("config:" + lmsd);
				Context.getDriverboardService().writeModuleSwitch(lmsd);

			} else if (data instanceof MbModeChangeData) {

				logger.info("recv:" + data);
				changeWorkMode((MbModeChangeData) data);

			} else if (data instanceof MbDriverModeChangeData) {

				logger.info("recv:" + data);
				changeDriverWorkMode((MbDriverModeChangeData) data);
			}

		} catch (Exception ex) {

			logger.error(CommonUtil.getThrowableException(ex));
			if (response.getResult() == Result.SUCCESS) {
				response.setResult(Result.FAIL);
			}
			// 设置错误信息
			response.setInfo(ex.getMessage());

		}

		return response;

	}

	/**
	 * 单板切换工作模式
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param modeData
	 * @throws AlertException
	 */
	public void changeDriverWorkMode(MbDriverModeChangeData modeData) throws AlertException {

		// 打开电源和风机
		if (Context.getAccessoriesService().getPowerManager() != null) {

			if (Context.getAccessoriesService().getPowerManager().getPowerSwitchState() == PowerState.OFF) {
				System.out.println("open inverter");
				Context.getAccessoriesService().getPowerManager().power(PowerState.ON);
				CommonUtil.sleep(300);
			}
		}
		if (Context.getAccessoriesService().getFanManager() != null) {

			Context.getAccessoriesService().getFanManager().fan(0, Direction.IN, PowerState.ON, 2);
		}

		if (modeData.getMode() == WorkMode.CAL || modeData.getMode() == WorkMode.JOINT) {

			// 关闭单板采集
			Context.getDriverboardService().stopWork(modeData.getDriverIndex());

		} else if (modeData.getMode() == WorkMode.NORMAL) {

			if (!MainBoard.startupCfg.isUseDebug()) {

				Context.getDriverboardService().startWork(modeData.getDriverIndex());
			}
		}

		DriverModeData lsd = new DriverModeData();
		lsd.setDriverIndex(modeData.getDriverIndex());
		boolean change = false;
		switch (modeData.getMode()) {

		case CAL:
			lsd.setMode(DriverMode.CAL);
			change = true;
			break;
		case NORMAL:
			lsd.setMode(DriverMode.NORMAL);
			change = true;
			break;
		case JOINT:
			lsd.setMode(DriverMode.JOINT);
			change = true;
			break;
		}

		if (change) {

			Context.getDriverboardService().writeWorkMode(lsd);

		}

	}

	/**
	 * 切换模式
	 * 
	 * @author wavy_zheng 2022年1月26日
	 * @param modeData
	 * @throws AlertException
	 */
	public void changeWorkMode(MbModeChangeData modeData) throws AlertException {

		if (mainboard.getState() == State.FORMATION) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.EnterCalModeFail, "设备正在运行!"));

		}
		// 打开电源和风机
		if (Context.getAccessoriesService().getPowerManager() != null) {

			if (Context.getAccessoriesService().getPowerManager().getPowerSwitchState() == PowerState.OFF) {
				System.out.println("open inverter");
				Context.getAccessoriesService().getPowerManager().power(PowerState.ON);
				CommonUtil.sleep(300);
			}
		}
		if (Context.getAccessoriesService().getFanManager() != null) {

			Context.getAccessoriesService().getFanManager().fan(0, Direction.IN, PowerState.ON, 2);
		}

		if (modeData.getMode() == WorkMode.CAL || modeData.getMode() == WorkMode.JOINT) {

			Context.getCoreService().stopWork();
			// Context.getAccessoriesService().stopWork();
		} else if (modeData.getMode() == WorkMode.NORMAL) {

			if (mainboard.getState() != State.NORMAL) {
				Context.getFileSaveService().writeMaintainFile(false);
				if (!MainBoard.startupCfg.isUseDebug()) {

					Context.getCoreService().startWork();
				}
			}
		}

		final Map<String, List<DriverBoard>> map = Context.getCoreService()
				.getDriverByPortMap(mainboard.getDriverBoards());
		final List<AlertException> exceptions = new ArrayList<>();
		final CountDownLatch latch = new CountDownLatch(map.size());
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {

			final String portName = it.next();
			new Thread(new Runnable() {

				@Override
				public void run() {

					List<DriverBoard> driverList = map.get(portName);

					try {

						for (DriverBoard db : driverList) {

							if (!db.isUse()) {

								continue;
							}
							DriverModeData lsd = new DriverModeData();
							lsd.setDriverIndex(db.getDriverIndex());
							boolean change = false;
							switch (modeData.getMode()) {

							case CAL:
								lsd.setMode(DriverMode.CAL);
								change = true;
								break;
							case NORMAL:
								lsd.setMode(DriverMode.NORMAL);
								change = true;
								break;
							case JOINT:
								lsd.setMode(DriverMode.JOINT);
								change = true;
								break;
							}

							if (change) {

								Context.getDriverboardService().writeWorkMode(lsd);

							}

						}

					} catch (AlertException ex) {

						ex.printStackTrace();
						exceptions.add(ex);

					} finally {

						latch.countDown();
					}

				}

			}).start();

		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!exceptions.isEmpty()) {

			throw exceptions.get(0);
		}

		State state = null;
		switch (modeData.getMode()) {
		case CAL:
			state = State.CAL;
			break;
		case NORMAL:
			state = State.NORMAL;
			break;
		case JOINT:
			state = State.JOIN;
			break;

		}

		if (state != null) {
			mainboard.setState(state);
		}

	}

}
