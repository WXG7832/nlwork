package com.nlteck.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.ParameterName;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.BeepAlertInfo;
import com.nlteck.service.StartupCfgManager.ColorLightInfo;
import com.nlteck.service.StartupCfgManager.DoorInfo;
import com.nlteck.service.StartupCfgManager.DriverInfo;
import com.nlteck.service.StartupCfgManager.FanInfo;
import com.nlteck.service.StartupCfgManager.PowerInfo;
import com.nlteck.service.StartupCfgManager.ProbeInfo;
import com.nlteck.service.StartupCfgManager.ProductType;
import com.nlteck.service.StartupCfgManager.RangeSection;
import com.nlteck.service.StartupCfgManager.StateLightInfo;
import com.nlteck.service.StartupCfgManager.TempBoardInfo;
import com.nlteck.service.accessory.SmogAlertManager;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.FileUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.FanType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.AirPressureStateData;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.nltecklib.protocol.li.accessory.PingStateData;
import com.nltecklib.protocol.li.main.AlertData;
import com.nltecklib.protocol.li.main.AllowStepSkipData;
import com.nltecklib.protocol.li.main.BaseCountData;
import com.nltecklib.protocol.li.main.CCProtectData;
import com.nltecklib.protocol.li.main.CVProtectData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.ClearSmogData;
import com.nltecklib.protocol.li.main.ControlUnitData;
import com.nltecklib.protocol.li.main.CylinderCfgData;
import com.nltecklib.protocol.li.main.CylinderControlData;
import com.nltecklib.protocol.li.main.CylinderPressureProtectData;
import com.nltecklib.protocol.li.main.DCProtectData;
import com.nltecklib.protocol.li.main.DateData;
import com.nltecklib.protocol.li.main.DebugControlData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.DeviceStateQueryData;
import com.nltecklib.protocol.li.main.DriverChnIndexDefineData;
import com.nltecklib.protocol.li.main.EnableCheckProtectionData;
import com.nltecklib.protocol.li.main.EnergySaveData;
import com.nltecklib.protocol.li.main.ExChnsOperateData;
import com.nltecklib.protocol.li.main.FirstCCProtectData;
import com.nltecklib.protocol.li.main.IPAddressData;
import com.nltecklib.protocol.li.main.JsonProcedureData;
import com.nltecklib.protocol.li.main.JsonProcedureExData;
import com.nltecklib.protocol.li.main.JsonProductCfgData;
import com.nltecklib.protocol.li.main.JsonProductCfgData.BaseCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.BeepAlertManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.ColorLightManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.ControlboardCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.DoorAlertManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.DriversCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.FanManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.LimitCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.PingControllerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.PoleLightManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.PowerManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.ProbeManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.ProcedureSupportCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.RangeCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.SmogManagerCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.SwitchCfg;
import com.nltecklib.protocol.li.main.JsonProductCfgData.TempControlCfg;
import com.nltecklib.protocol.li.main.JsonProtectionData;
import com.nltecklib.protocol.li.main.JsonProtectionData.PlanKey;
import com.nltecklib.protocol.li.main.JsonProtectionData.ProtectionPlan;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ControlType;
import com.nltecklib.protocol.li.main.MainEnvironment.ControlUnitMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.CoreData;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.li.main.MainEnvironment.SelfCheckState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.SwitchState;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.li.main.OfflineAcquireData;
import com.nltecklib.protocol.li.main.OfflinePickupData;
import com.nltecklib.protocol.li.main.OfflineRunningData;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.li.main.PressureChangeProtectData;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.PushSwitchData;
import com.nltecklib.protocol.li.main.RecoveryData;
import com.nltecklib.protocol.li.main.ResetData;
import com.nltecklib.protocol.li.main.SaveParamData;
import com.nltecklib.protocol.li.main.SelfCheckData;
import com.nltecklib.protocol.li.main.SlpProtectData;
import com.nltecklib.protocol.li.main.SolenoidValveData;
import com.nltecklib.protocol.li.main.SolenoidValveTempProtectData;
import com.nltecklib.protocol.li.main.StartEndCheckData;
import com.nltecklib.protocol.li.main.StartupData;
import com.nltecklib.protocol.li.main.SyncPressureParamData;
import com.nltecklib.protocol.li.main.TempData;
import com.nltecklib.protocol.li.main.TemperAdjustData;
import com.nltecklib.protocol.li.main.TestNameData;
import com.nltecklib.protocol.li.main.UnitChannelSwitchData;
import com.nltecklib.protocol.li.main.UpgradeProgramExData;
import com.nltecklib.protocol.li.main.UpgradeProgressData;
import com.nltecklib.protocol.li.main.VersionData;
import com.nltecklib.protocol.li.main.VoiceAlertData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月23日 下午5:35:41 主控与上位机的命令交互
 */
public class PCController implements Controller {

	private MainBoard mainboard; // 主控容器
	private Logger logger;

	public PCController(MainBoard mainboard) {

		this.mainboard = mainboard;
		try {
			logger = LogUtil.createLog("log/pcController.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
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

	@Override
	public ResponseDecorator requestCmdToNetwork(Decorator command, int timeOut) {

		return null;
	}

	/**
	 * 处理一些上位机回复主控的命令；如报警回复，推送回复
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param command
	 */
	private void processResponse(Decorator command) {

		Data data = command.getDestData();
		if (data instanceof PickupData) {

			// 收到PC的推送数据确认回复
			Context.getPcNetworkService().recvPcPushResponse();

		} else if (data instanceof AlertData || data instanceof OfflinePickupData) {

			Context.getPcNetworkService().confirmAlertOrOfflineResponse();

		}

	}

	/**
	 * 处理查询类命令
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param command
	 * @return
	 */
	private ResponseDecorator processQuery(Decorator command) {

		Data data = command.getDestData();
		ResponseDecorator response = new ResponseDecorator(data, false);
		int unitIndex = data.getUnitIndex(); // 分区号
        
		try {

			if (data instanceof OfflineAcquireData) {

				// 上报离线数据
				Environment.infoLogger.info("start to push offline data to pc");

				// 查看离线队列是否有数据，如有立即发送给PC
				OfflineAcquireData offline = new OfflineAcquireData();
				response = new ResponseDecorator(offline, true);

			}  else if (data instanceof DateData) {

				// 查询系统时间
				DateData dd = new DateData();
				dd.setDate(new Date());
				response = new ResponseDecorator(dd, true);

			} else if (data instanceof DeviceStateQueryData) {

				DeviceStateQueryData dsqd = new DeviceStateQueryData();
				CoreData core = Context.getCoreService().pickCoreData();
				List<ControlUnitMonitorData> list = Context.getCoreService().pickUnitMonitorData();
				dsqd.setCoreData(core);
				dsqd.setControlUnitStates(list);
				dsqd.setLogicsData(new ArrayList<>());
				dsqd.setChecksData(new ArrayList<>());
				dsqd.setDriverStates(Context.getCoreService().pickupDriverMonitorData());
				if(Context.getAccessoriesService().getPingController() != null) {
				   dsqd.setPingsData(Context.getAccessoriesService().getPingController().createPingStateMonitorData());
				}
				// 通道不能上传，否则会导致协议数据长度溢出；暂时禁用
				// dsqd.setChannelsData(Context.getCoreService().pickupChannelMonitorData());
				dsqd.setTempBoardsData(Context.getAccessoriesService().pickTempMeterMonitorData());
				dsqd.setPowerDatas(Context.getAccessoriesService().pickPowerMonitorData());
				dsqd.setFanDatas(Context.getAccessoriesService().pickupFanMonitorData());

				response = new ResponseDecorator(dsqd, true);

			} else if (data instanceof BaseCountData) {

				// 查询基本分区信息
				BaseCountData bcd = new BaseCountData();
				bcd.setUnitCount(mainboard.isInitOk() ? (byte) mainboard.getControlUnitCount() : 0);
				bcd.setLogicCount((byte) MainBoard.startupCfg.getLogicBoardCount());
				bcd.setBaseLogicDriverCount((byte) MainBoard.startupCfg.getDriverCount());

				System.out.println("driverChnCount = " + MainBoard.startupCfg.getDriverChnCount());
				bcd.setBaseDriverChnCount((byte) MainBoard.startupCfg.getDriverChnCount());

				System.out.println(bcd.toString());
				response = new ResponseDecorator(bcd, true);

			} else if (data instanceof ControlUnitData) {

				System.out.println("query control unit info");
				ControlUnitData cud = new ControlUnitData();
				cud.setMode(mainboard.getProcedureMode());

				List<Byte> list = new ArrayList<>();
				for (int n = 0; n < mainboard.getDriverBoards().size(); n++) {

					list.add((byte) (mainboard.getDriverBoards().get(n).getControlUnit().getIndex() + 1));
				}

				System.out.println(list);
				cud.setControls(list);
				response = new ResponseDecorator(cud, true);

			} else if (data instanceof PoleData) {

				response = new ResponseDecorator(mainboard.getControlUnitByIndex(unitIndex).getPole(), true);

			} else if (data instanceof DeviceProtectData) {

				response = new ResponseDecorator(mainboard.getControlUnitByIndex(unitIndex).getDpd(), true);

			} else if (data instanceof CCProtectData) {

				CCProtectData cpd = null;
				if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
					cpd = mainboard.getControlUnitByIndex(unitIndex).getPnAG().getCcProtect();
				} else {

					cpd = mainboard.getControlUnitByIndex(unitIndex).getPnIC().getCcProtect();
				}

				response = new ResponseDecorator(cpd, true);

			} else if (data instanceof DCProtectData) {

				// 查询dc保护

				DCProtectData dcp = null;
				if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
					dcp = mainboard.getControlUnitByIndex(unitIndex).getPnAG().getDcProtect();
				} else {
					dcp = mainboard.getControlUnitByIndex(unitIndex).getPnIC().getDcProtect();
				}

				response = new ResponseDecorator(dcp, true);

			} else if (data instanceof CVProtectData) {

				CVProtectData vpd = null;
				if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
					vpd = mainboard.getControlUnitByIndex(unitIndex).getPnAG().getCvProtect();
				} else {

					vpd = mainboard.getControlUnitByIndex(unitIndex).getPnIC().getCvProtect();
				}
				response = new ResponseDecorator(vpd, true);

			} else if (data instanceof FirstCCProtectData) {

				FirstCCProtectData first = null;
				if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
					first = mainboard.getControlUnitByIndex(unitIndex).getPnAG().getFirstCCProtect();
				} else {

					first = mainboard.getControlUnitByIndex(unitIndex).getPnIC().getFirstCCProtect();
				}
				response = new ResponseDecorator(first, true);

			} else if (data instanceof TestNameData) {

				String testName = null;
				// 查询设备测试名
				testName = mainboard.getControlUnitByIndex(unitIndex).getTestName();
				TestNameData tnd = new TestNameData();
				tnd.setTestName(testName != null ? testName : "");
				response = new ResponseDecorator(tnd, true);

			} else if (data instanceof ProcedureData) {

				ProcedureData pd = null;
				// 查询设备测试名
				pd = mainboard.getControlUnitByIndex(unitIndex).getProcedure();
				if (pd == null) {

					throw new AlertException(AlertCode.LOGIC,
							(mainboard.getProcedureMode() == ProcedureMode.DEVICE ? I18N.getVal(I18N.Device)
									: I18N.getVal(I18N.UnitWithIndex, unitIndex + 1))
									+ I18N.getVal(I18N.NoProcess_QueryError));
				}
				response = new ResponseDecorator(pd, true);

			} else if (data instanceof SlpProtectData) {

				SlpProtectData slp = null;
				if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
					slp = mainboard.getControlUnitByIndex(unitIndex).getPnAG().getSlpProtect();
				} else {

					slp = mainboard.getControlUnitByIndex(unitIndex).getPnIC().getSlpProtect();
				}
				response = new ResponseDecorator(slp, true);

			} else if (data instanceof CheckVoltProtectData) {

				CheckVoltProtectData cvpd = null;
				if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
					cvpd = mainboard.getControlUnitByIndex(unitIndex).getPnAG().getCheckVoltProtect();
				} else {

					cvpd = mainboard.getControlUnitByIndex(unitIndex).getPnIC().getCheckVoltProtect();
				}
				response = new ResponseDecorator(cvpd, true);

			} else if (data instanceof StartupData) {

				StartupData sd = new StartupData();
				sd.setUnitIndex(unitIndex);
				sd.setState(mainboard.getControlUnitByIndex(unitIndex).getState());
				response = new ResponseDecorator(sd, true);

			} else if (data instanceof PressureChangeProtectData) {

				// ControlUnit cu = mainboard.getControlUnitByIndex(unitIndex);
				// PressureChangeProtectData pcpd = null;
				// if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
				//
				// pcpd =
				// mainboard.getControlUnitByIndex(unitIndex).getPnAG().getPressureProtect();
				// } else {
				//
				// pcpd =
				// mainboard.getControlUnitByIndex(unitIndex).getPnIC().getPressureProtect();
				// }
				// response = new ResponseDecorator(pcpd, true);
			} else if (data instanceof SaveParamData) {

				SaveParamData spd = new SaveParamData();

				ParameterName pn = WorkType.values()[data.getDriverIndex()] == WorkType.AG
						? mainboard.getControlUnitByIndex(unitIndex).getPnAG()
						: mainboard.getControlUnitByIndex(unitIndex).getPnIC();
				System.out.println("workType:" + pn.getWorkType());
				spd.setName(pn.getName().isEmpty() ? "default" : pn.getName());

				spd.setWorkType(pn.getWorkType());
				spd.setDefaultPlan(pn.isDefaultPlan());
				mainboard.setTempParameterNameCfg(pn); // 设置临时查询保护方案

				response = new ResponseDecorator(spd, true);
			} else if (data instanceof TempData) {

				TempData td = new TempData();
				if (Context.getAccessoriesService().getTemperatureManager() != null) {
					td.setTempConstant(Context.getAccessoriesService().getTemperatureManager().getConstTemp());
					td.setLower(Context.getAccessoriesService().getTemperatureManager().readTempLower());
					td.setUpper(Context.getAccessoriesService().getTemperatureManager().readTempUpper());

					td.setSyncTempOpen(Context.getAccessoriesService().getTemperatureManager().isSyncTempOpen());
					td.setProtectMinute(Context.getAccessoriesService().getTemperatureManager().getProtectMinute());
					td.setTempControlOpen(Context.getAccessoriesService().getTemperatureManager().isTempControlOpen());
				}

				response = new ResponseDecorator(td, true);
			} else if (data instanceof VoiceAlertData) {

				VoiceAlertData beepData = Context.getFileSaveService().readBeepAlertFile();
				response = new ResponseDecorator(beepData, true);

			} else if (data instanceof StartEndCheckData) {

				StartEndCheckData secd = null;
				if (mainboard.getTempParameterNameCfg().getWorkType() == WorkType.AG) {
					secd = mainboard.getControlUnitByIndex(unitIndex).getPnAG().getStartEndCheckProtect();
				} else {

					secd = mainboard.getControlUnitByIndex(unitIndex).getPnIC().getStartEndCheckProtect();
				}
				response = new ResponseDecorator(secd, true);

			} else if (data instanceof EnergySaveData) {

				// 节能方案
				EnergySaveData esd = Context.getAccessoriesService().getEnergySaveData();
				if (esd == null) {

					esd = new EnergySaveData();
				}
				response = new ResponseDecorator(esd, true);

			} else if (data instanceof SolenoidValveData) {

				if (mainboard.getMechanismManager() != null) {

					ValveState vs = mainboard.getMechanismManager().readValve(0);
					Environment.infoLogger.info("read valve state = " + vs);
					SolenoidValveData svd = new SolenoidValveData();
					svd.setState(SwitchState.values()[vs.ordinal()]);
					response = new ResponseDecorator(svd, true);
				} else {

					throw new AlertException(AlertCode.LOGIC, "未初始化机构管理器!");
				}

			} else if (data instanceof SolenoidValveTempProtectData) {

				if (mainboard.getMechanismManager() != null) {

					double tempUpper = mainboard.getMechanismManager().readValveTempUpper(0);
					SolenoidValveTempProtectData ttud = new SolenoidValveTempProtectData();
					ttud.setUnitIndex(unitIndex);
					ttud.setTemperatureUpper(tempUpper);

					response = new ResponseDecorator(ttud, true);

				} else {

					throw new AlertException(AlertCode.LOGIC, "未初始化机构管理器!");
				}
			} else if (data instanceof CylinderPressureProtectData) {

				if (mainboard.getMechanismManager() != null) {

					CylinderPressureProtectData cpd = new CylinderPressureProtectData();
					cpd = mainboard.getMechanismManager().readPressureRange(cpd);

					response = new ResponseDecorator(cpd, true);

				} else {

					throw new AlertException(AlertCode.LOGIC, "未初始化机构管理器!");
				}

			} else if (data instanceof OfflineRunningData) {

				response = new ResponseDecorator(Context.getCoreService().getOfflineRunningCfg(), true);
			} else if (data instanceof VersionData) {

				// 读取软件版本
				VersionData vd = (VersionData) data;
				logger.info("reading soft verison," + vd.getVersionType());
				if (vd.getVersionType() == UpgradeType.Core) {

					vd.setVersion(MainBoard.VERSION);
					vd.getSubVersions().clear();

				} else {

					throw new AlertException(AlertCode.LOGIC, "illegal version type:" + vd.getVersionType());
				}
				response = new ResponseDecorator(vd, true);

			} else if (data instanceof SelfCheckData) {

				SelfCheckData scd = new SelfCheckData();
				scd.setState(mainboard.isChecking() ? SelfCheckState.NONE : SelfCheckState.CHECKED);
				scd.setDate(new Date());
				response = new ResponseDecorator(scd, true);

			} else if (data instanceof DriverChnIndexDefineData) {

				if (mainboard.getDcdd() == null) {

					DriverChnIndexDefineData dcdd = new DriverChnIndexDefineData();
					dcdd.setEnable(false);
					List<Byte> list = new ArrayList<>();
					for (int n = 0; n < MainBoard.startupCfg.getDriverChnCount(); n++) {

						list.add((byte) n);
					}
					dcdd.setChnIndexDefineList(list);
					mainboard.setDcdd(dcdd);

				}

				response = new ResponseDecorator(mainboard.getDcdd(), true);

			} else if (data instanceof JsonProcedureData) {

				JsonProcedureData jpd = new JsonProcedureData();
				for (int n = 0; n < mainboard.getControlUnitCount(); n++) {

					ProcedureData procedure = mainboard.getControlUnitByIndex(n).getProcedure();
					jpd.appendProcedure(procedure);

				}
				response = new ResponseDecorator(jpd, true);

			} else if (data instanceof JsonProtectionData) {

				JsonProtectionData jpd = new JsonProtectionData();
				for (int n = 0; n < mainboard.getControlUnitCount(); n++) {

					ParameterName pnAg = mainboard.getControlUnitByIndex(n).getPnAG();
					ParameterName pnIC = mainboard.getControlUnitByIndex(n).getPnIC();
					ProtectionPlan plan = createPlan(pnAg);
					jpd.appendPlan(n, plan);
					plan = createPlan(pnIC);
					jpd.appendPlan(n, plan);

				}
				response = new ResponseDecorator(jpd, true);

			} else if(data instanceof JsonProductCfgData) {
				
				JsonProductCfgData jpd = (JsonProductCfgData)data;
				BaseCfg base = new BaseCfg();
				base.disableDefaultProtection = MainBoard.startupCfg.isDisableDefaultProtection();
				base.driverChnCount = MainBoard.startupCfg.getDriverChnCount();
				base.language       = MainBoard.startupCfg.getLanguage();
				base.productType    = MainBoard.startupCfg.getProductType().name();
				jpd.setBase(base);
				
				SwitchCfg  switcher = new SwitchCfg();
				switcher.useVirtualData = MainBoard.startupCfg.isUseVirtualData();
				switcher.useAlert       = MainBoard.startupCfg.isUseAlert();
				switcher.useDebug       = MainBoard.startupCfg.isUseDebug();
				switcher.useReverseChnIndex = MainBoard.startupCfg.isUseReverseChnIndex();
				switcher.useStepChangeProtect = MainBoard.startupCfg.isUseStepChangeProtect();
				switcher.useSTM32Time   = MainBoard.startupCfg.isUseSTM32Time();
				jpd.setSwitcher(switcher);
				
				RangeCfg  range = new RangeCfg();
				range.use = MainBoard.startupCfg.getRange().use;
				range.disableVoltageLine = MainBoard.startupCfg.getRange().disableVoltageLine;
				range.disableCurrentLine = MainBoard.startupCfg.getRange().disableCurrentLine;
				range.voltageFilterRange = MainBoard.startupCfg.getRange().voltageFilterRange;
				range.voltagePrecision   = MainBoard.startupCfg.getRange().voltagePrecision;
				range.voltageStartOffset = MainBoard.startupCfg.getRange().voltageStartOffset;
				range.continueAlertFilter = MainBoard.startupCfg.getRange().continueAlertFilter;
				range.useCvCurrentFilter = MainBoard.startupCfg.getRange().useCvCurrentFilter;
				
				for(RangeSection sec : MainBoard.startupCfg.getRange().sections) {
					
					com.nltecklib.protocol.li.main.JsonProductCfgData.RangeSection rs = new com.nltecklib.protocol.li.main.JsonProductCfgData.RangeSection();
					rs.currentFilterRange = sec.currentFilterRange;
					rs.filter             = sec.filter;
					rs.level              = sec.level;
					rs.lower              = sec.lower;
					rs.upper              = sec.upper;
					rs.precision          = sec.precision;
					range.sections.add(rs);
				}
				
				jpd.setRange(range);
				
				LimitCfg  limit = new LimitCfg();
				limit.maxDeviceVoltage = MainBoard.startupCfg.getMaxDeviceVoltage();
				limit.maxDeviceCurrent = MainBoard.startupCfg.getMaxDeviceCurrent();
				limit.minDeviceVoltage = MainBoard.startupCfg.getMinDeviceVoltage();
				limit.minDeviceCurrent = MainBoard.startupCfg.getMinDeviceCurrent();
				jpd.setLimit(limit);
				
				List<DriversCfg> driverList = new ArrayList<>();
				for(DriverInfo di : MainBoard.startupCfg.listDriverInfos()) {
					
					DriversCfg dc = new DriversCfg();
					dc.index   =  di.index;
					dc.portName = di.portName;
					dc.use      = di.use;
					dc.communication = di.communication;
					dc.pickupTime   = di.pickupTime;
					driverList.add(dc);
				}
				
				jpd.setDriverList(driverList);
				
				
				ProcedureSupportCfg psc = new ProcedureSupportCfg();
				psc.supportDriver =  MainBoard.startupCfg.getProcedureSupportInfo().supportDriver;
				psc.supportImportantData = MainBoard.startupCfg.getProcedureSupportInfo().supportImportantData;
				jpd.setProcedureSupport(psc);
				
				ControlboardCfg control = new ControlboardCfg();
				control.communicateTimeout = MainBoard.startupCfg.getControlInfo().communicateTimeout;
				control.heartbeat          = MainBoard.startupCfg.getControlInfo().heartbeat;
				control.portName           = MainBoard.startupCfg.getControlInfo().portName;
				control.use                = MainBoard.startupCfg.getControlInfo().use;
				jpd.setControlBoard(control);
				
				PowerManagerCfg power = new PowerManagerCfg();
				power.use = MainBoard.startupCfg.getPowerManagerInfo().use;
				for(PowerInfo pi : MainBoard.startupCfg.getPowerManagerInfo().powerInfos) {
					
					if(pi.powerType == PowerType.CHARGE) {
						
						power.invertCount =  pi.powerCount;
					} else if(pi.powerType == PowerType.AUXILIARY) {
						
						power.auxiliaryCount = pi.powerCount;
					}
					
				}
				jpd.setPowerManager(power);
				
				FanManagerCfg fan = new FanManagerCfg();
				fan.use = MainBoard.startupCfg.getFanManagerInfo().use;
                for(FanInfo fi : MainBoard.startupCfg.getFanManagerInfo().fanInfos) {
					
					if(fi.fanType == FanType.COOL) {
						
						fan.coolFanCount =  fi.fanCount;
					} else if(fi.fanType == FanType.TURBO) {
						
						fan.turboFanCount = fi.fanCount;
					}
					
				}
                jpd.setFanManager(fan);
                
                
                SmogManagerCfg smog = new SmogManagerCfg();
                smog.use = MainBoard.startupCfg.getSmogAlertManagerInfo().use;
                jpd.setSmogManager(smog);
                
                DoorAlertManagerCfg door = new DoorAlertManagerCfg();
                door.use = MainBoard.startupCfg.getDoorAlertManagerInfo().use;
                jpd.setDoorManager(door);
                
                TempControlCfg    temp = new TempControlCfg();
                temp.use  = MainBoard.startupCfg.getOMRManagerInfo().use;
                jpd.setTempControl(temp);
                
                ProbeManagerCfg probe = new ProbeManagerCfg();
                probe.use = MainBoard.startupCfg.getProbeManagerInfo().use;
                jpd.setProbeManager(probe);
                
                ColorLightManagerCfg clrLight = new ColorLightManagerCfg();
                clrLight.use = MainBoard.startupCfg.getColorLightManagerInfo().use;
                jpd.setColorLightManager(clrLight);
                
                PoleLightManagerCfg pole = new PoleLightManagerCfg();
                pole.use = MainBoard.startupCfg.getStateLightControllerInfo().use;
                jpd.setPoleLightManager(pole);
                
                BeepAlertManagerCfg beep = new BeepAlertManagerCfg();
                beep.use = MainBoard.startupCfg.getBeepAlertManagerInfo().use;
                jpd.setBeepManager(beep);
                
                PingControllerCfg ping = new PingControllerCfg();
                ping.enable = MainBoard.startupCfg.getPingController().enable;
                ping.portName = MainBoard.startupCfg.getPingController().portName;
                ping.communicateTimeout = MainBoard.startupCfg.getPingController().communicateTimeout;
                jpd.setPingController(ping);

				response = new ResponseDecorator(jpd, true);
				
				
			} else if(data instanceof CylinderControlData) {
				
				CylinderControlData ccd = new CylinderControlData();
				ccd.setDriverIndex(0);
				if(Context.getAccessoriesService().getPingController() != null) {
				   PingStateData psd = Context.getAccessoriesService().getPingController().getState();
				   boolean press = psd.isTrayCylinder1PosOk() && psd.isTrayCylinder3PosOk();
				   ccd.setPress(press);
				}
				response = new ResponseDecorator(ccd, true);

			} else if(data instanceof CylinderCfgData) {
				
				if(Context.getAccessoriesService().getPingController() != null) {
				   response = new ResponseDecorator(Context.getAccessoriesService().getPingController().getCfgData(), true);
				}
				
			} else if(data instanceof JsonProcedureExData) {
				
				JsonProcedureExData jpe = new JsonProcedureExData();
				// 查询设备测试名
				ProcedureData pd = mainboard.getControlUnitByIndex(unitIndex).getProcedure();
				if (pd == null) {

					throw new AlertException(AlertCode.LOGIC,
							(mainboard.getProcedureMode() == ProcedureMode.DEVICE ? I18N.getVal(I18N.Device)
									: I18N.getVal(I18N.UnitWithIndex, unitIndex + 1))
									+ I18N.getVal(I18N.NoProcess_QueryError));
				}
				jpe.setProcedureData(pd);
				jpe.setDeviceProtect(mainboard.getControlUnitByIndex(unitIndex).getDpd());
				jpe.setCheckProtect(mainboard.getControlUnitByIndex(unitIndex).getTouch());
				jpe.setPoleProtect(mainboard.getControlUnitByIndex(unitIndex).getPole());
				jpe.setFirstEndProtect(mainboard.getControlUnitByIndex(unitIndex).getSec());
				response = new ResponseDecorator(jpe, true);
			}
			
		} catch (Exception e) {

			logger.error(CommonUtil.getThrowableException(e));
			if (response.getResult().getCode() == Result.SUCCESS) {
				response.setResult(DefaultResult.FAIL);
			}
			// 设置错误信息
			response.setInfo(e.getMessage());

		}

		return response;

	}

	private ProtectionPlan createPlan(ParameterName pn) {

		ProtectionPlan plan = new ProtectionPlan();
		SaveParamData spd = new SaveParamData();
		spd.setName(pn.getName());
		spd.setWorkType(pn.getWorkType());
		spd.setDefaultPlan(pn.isDefaultPlan());
		plan.pn = spd;
		plan.cc = pn.getCcProtect();
		plan.cv = pn.getCvProtect();
		plan.dc = pn.getDcProtect();
		plan.checkVolt = pn.getCheckVoltProtect();
		plan.fcpd = pn.getFirstCCProtect();
		plan.secd = pn.getStartEndCheckProtect();
		plan.sleep = pn.getSlpProtect();

		return plan;

	}

	/**
	 * 处理配置类命令
	 * 
	 * @author wavy_zheng 2020年11月12日
	 * @param command
	 * @return
	 */
	private ResponseDecorator processConfig(Decorator command) {

		Data data = command.getDestData();
		ResponseDecorator response = new ResponseDecorator(data, false);
		int unitIndex = data.getUnitIndex(); // 分区号
        
		try {
			// 配置命令
			if (data instanceof DateData) {

				if (Environment.isLinuxEnvironment()) {

					Date downloadDate = ((DateData) data).getDate();

					// TODO Auto-generated method stub
					// 只有时间偏差大于5s才进行调整
					Environment.infoLogger.info("set system time "
							+ CommonUtil.formatTime(((DateData) data).getDate(), "yyyy-MM-dd HH:mm:ss"));
					try {
						String result = Environment.setSysDatetime(downloadDate);
						if (!result.isEmpty()) {

							// throw new AlertException(AlertCode.COMM_ERROR, "校准时间失败:" + result);
							throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.DateTimeAdjustFail, result));
						}
					} catch (IOException e) {

						Environment.errLogger.info(CommonUtil.getThrowableException(e));
						e.printStackTrace();
						// throw new AlertException(AlertCode.COMM_ERROR, "校准时间发生错误:" + e.getMessage());
						throw new AlertException(AlertCode.LOGIC,
								I18N.getVal(I18N.DateTimeAdjustCauseError, e.getMessage()));

					} catch (InterruptedException e) {

						Environment.errLogger.info(CommonUtil.getThrowableException(e));
						e.printStackTrace();
						// throw new AlertException(AlertCode.COMM_ERROR, "校准时间发生错误:" + e.getMessage());
						throw new AlertException(AlertCode.LOGIC,
								I18N.getVal(I18N.DateTimeAdjustCauseError, e.getMessage()));
					}

				}
				response = new ResponseDecorator(new DateData(), false);

			} else if (data instanceof IPAddressData) {

				IPAddressData iad = (IPAddressData) data;
				logger.info("change ip address " + iad.getIpAddress());
				if (Environment.isLinuxEnvironment()) {

					if (!Environment.writeIpAddress("/etc/network/interfaces", iad.getIpAddress())) {

						// throw new AlertException(AlertCode.LOGIC, "修改IP地址失败");
						throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.IpAddressSetFail));
					}

				}
				response = new ResponseDecorator(new IPAddressData(), false);

			} else if (data instanceof ControlUnitData) {

				ControlUnitData cud = (ControlUnitData) data;
				response = new ResponseDecorator(new ControlUnitData(), false);

				// 生成新的控制分区

				if (cud.getMode() == ProcedureMode.LOGIC
						&& !MainBoard.startupCfg.getProcedureSupportInfo().supportLogic) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.LogicProcedureNotSupport));
				}

				if (cud.getMode() == ProcedureMode.DRIVER
						&& !MainBoard.startupCfg.getProcedureSupportInfo().supportDriver) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.DriverProcedureNotSupport));
				}

				Context.getCoreService().createControlUnits(cud);

			} else if (data instanceof UnitChannelSwitchData) {

				// 配置通道关闭打开状态
				UnitChannelSwitchData csd = (UnitChannelSwitchData) data;
				response = new ResponseDecorator(new UnitChannelSwitchData(), false);

				mainboard.switchChannelState(csd);
				Context.getFileSaveService().writeChannelSwitchState();

			} else if (data instanceof PressureChangeProtectData) {

				logger.info("reve pressure change ok signal!");
				PressureChangeProtectData pcpd = (PressureChangeProtectData) data;
				mainboard.setPressureOk(pcpd.isPressureOk());

			} else if (data instanceof DeviceProtectData) {

				response = new ResponseDecorator(new DeviceProtectData(), false);
				// 设备保护
				DeviceProtectData dpd = (DeviceProtectData) data;
				if (dpd.getBatVoltUpper() > MainBoard.startupCfg.getMaxDeviceVoltage()) {

					response.setResult(DefaultResult.LOGIC);

					String info = I18N.getVal(I18N.ChnVoltSetUpper, dpd.getBatVoltUpper(),
							MainBoard.startupCfg.getMaxDeviceVoltage());

					throw new AlertException(AlertCode.LOGIC, info);

				} else if (dpd.getDeviceVoltUpper() > MainBoard.startupCfg.getMaxDeviceVoltage()) {

					String info = I18N.getVal(I18N.DeviceVoltSetUpper, dpd.getBatVoltUpper(),
							MainBoard.startupCfg.getMaxDeviceVoltage());

					response.setResult(DefaultResult.LOGIC);
					throw new AlertException(AlertCode.LOGIC, info);
				} else if (dpd.getCurrUpper() > MainBoard.startupCfg.getMaxDeviceCurrent()) {

					String info = I18N.getVal(I18N.ChnCurrSetUpper, dpd.getCurrUpper(),
							MainBoard.startupCfg.getMaxDeviceCurrent());
					response.setResult(DefaultResult.LOGIC);
					throw new AlertException(AlertCode.LOGIC, info);
				} else {

					if (mainboard.getControlUnitCount() > 1 && unitIndex == 0xff) {

						for (ControlUnit cu : mainboard.getControls()) {
							File file = new File("./config/struct/" + cu.getIndex() + "/device.xml");

							Context.getCoreService().writeVoltageProtection(cu, dpd);
							cu.setDpd(dpd);
							Context.getFileSaveService().writeDeviceProtectFile(file, dpd);
						}

					} else {

						logger.info("write voltage protect:" + data);
						File file = new File("./config/struct/"
								+ (mainboard.getControlUnitCount() == 1 ? "device" : unitIndex) + "/device.xml");
						Context.getCoreService().writeVoltageProtection(mainboard.getControlUnitByIndex(unitIndex),
								dpd);
						mainboard.getControlUnitByIndex(unitIndex).setDpd(dpd);
						Context.getFileSaveService().writeDeviceProtectFile(file, dpd);

					}

				}

			} else if (data instanceof PoleData) {

				response = new ResponseDecorator(new PoleData(), false);
				// 极性保护
				PoleData pd = (PoleData) data;
				if (pd.getPoleDefine() < MainBoard.startupCfg.getMinDefineVoltage()
						|| pd.getPoleDefine() > MainBoard.startupCfg.getMaxDefineVoltage()) {

					response.setResult(DefaultResult.LOGIC);

					String info = I18N.getVal(I18N.PoleVoltDefineError, pd.getPoleDefine(),
							MainBoard.startupCfg.getMinDefineVoltage(), MainBoard.startupCfg.getMaxDefineVoltage());

					throw new AlertException(AlertCode.LOGIC, info);
				} else {

					if (mainboard.getProcedureMode() != ProcedureMode.DEVICE && unitIndex == 0xff) {

						logger.info("write per unit pole cfg");
						for (ControlUnit cu : mainboard.getControls()) {

							File file = new File("./config/struct/" + cu.getIndex() + "/pole.xml");
							// 写入逻辑板和回检板
							Context.getCoreService().writePoleProtection(cu, pd);
							cu.setPole(pd);// 缓存极性配置
							Context.getFileSaveService().writePoleFile(file, pd);
						}

					} else {

						File file = new File("./config/struct/"
								+ (mainboard.getControlUnitCount() == 1 ? "device" : unitIndex) + "/pole.xml");
						// 写入逻辑板和回检板
						Context.getCoreService().writePoleProtection(mainboard.getControlUnitByIndex(unitIndex), pd);
						mainboard.getControlUnitByIndex(unitIndex).setPole(pd);// 缓存极性配置
						Context.getFileSaveService().writePoleFile(file, pd);
					}

					if (Context.getAccessoriesService().getStateLightController() != null) {

						System.out.println("light pole : " + pd.getPole());
						// 点亮
						Context.getAccessoriesService().getStateLightController().light(unitIndex,
								pd.getPole() == Pole.NORMAL ? LightColor.GREEN : LightColor.YELLOW, false);
					}

				}
			} else if (data instanceof CCProtectData) {

				response = new ResponseDecorator(new CCProtectData(), false);
				CCProtectData ccpd = (CCProtectData) data;

				if (!MainBoard.startupCfg.isDisableDefaultProtection()) {
					if (ccpd.getVoltAscUnitSeconds() == 0) {

						ccpd.setVoltAscUnitSeconds(300); // 默认设置检测时间
					}
					if (ccpd.getVoltAscValLower() == 0) {

						ccpd.setVoltAscValLower(1); // 默认最小充电上升电压
					}
					if (ccpd.getCurrOffsetPercent() == 0) { // 默认电流波动超差

						ccpd.setCurrOffsetPercent(10);
					}
				}

				mainboard.getTempParameterNameCfg().setCcProtect(ccpd);

			} else if (data instanceof CVProtectData) {

				response = new ResponseDecorator(new CVProtectData(), false);
				CVProtectData cvpd = (CVProtectData) data;
				if (!MainBoard.startupCfg.isDisableDefaultProtection()) {
					if (cvpd.getVoltOffsetPercent() == 0) {

						cvpd.setVoltOffsetPercent(10);
					}
				}
				mainboard.getTempParameterNameCfg().setCvProtect(cvpd);

			} else if (data instanceof DCProtectData) {

				response = new ResponseDecorator(new DCProtectData(), false);
				DCProtectData dcpd = (DCProtectData) data;

				if (!MainBoard.startupCfg.isDisableDefaultProtection()) {
					if (dcpd.getVoltDescUnitSeconds() == 0) {

						dcpd.setVoltDescUnitSeconds(300); // 默认设置检测时间
					}
					if (dcpd.getVoltDescValLower() == 0) {

						dcpd.setVoltDescValLower(1); // 默认最小充电上升电压
					}
					if (dcpd.getCurrOffsetPercent() == 0) {

						dcpd.setCurrOffsetPercent(10); // 默认电流波动超差
					}
				}

				mainboard.getTempParameterNameCfg().setDcProtect(dcpd);

			} else if (data instanceof SlpProtectData) {

				response = new ResponseDecorator(new SlpProtectData(), false);
				SlpProtectData slpd = (SlpProtectData) data;
				mainboard.getTempParameterNameCfg().setSlpProtect(slpd);

			} else if (data instanceof FirstCCProtectData) {

				response = new ResponseDecorator(new FirstCCProtectData(), false);
				FirstCCProtectData tpd = (FirstCCProtectData) data;
				if (tpd.getVoltLower() > tpd.getVoltUpper()) {

					response.setResult(DefaultResult.LOGIC);

					throw new AlertException(AlertCode.LOGIC,
							I18N.getVal(I18N.FirstStepCCVoltLower, tpd.getVoltLower(), tpd.getVoltUpper()));
				} else if (tpd.getVoltUpper() > MainBoard.startupCfg.getMaxDeviceVoltage()
						|| tpd.getVoltLower() > MainBoard.startupCfg.getMaxDeviceVoltage()) {

					response.setResult(DefaultResult.LOGIC);

					throw new AlertException(AlertCode.LOGIC,
							I18N.getVal(I18N.FirstStepCCVoltUpper, MainBoard.startupCfg.getMaxDeviceVoltage()));
				} else {

					mainboard.getTempParameterNameCfg().setFirstCCProtect(tpd);
				}
			} else if (data instanceof ProcedureData) {

				response = new ResponseDecorator(new ProcedureData(), false);
				mainboard.checkProcedureValid((ProcedureData) data);

				if (mainboard.getProcedureMode() != ProcedureMode.DEVICE && unitIndex == 0xff) {

					// 多分区写入
					for (ControlUnit cu : mainboard.getControls()) {

						cu.setProcedure((ProcedureData) data);
						cu.writeProcedureFile((ProcedureData) data);
					}

				} else {

					mainboard.getControlUnitByIndex(unitIndex).setProcedure((ProcedureData) data);
					// 写入文件并保存流程

					mainboard.getControlUnitByIndex(unitIndex).writeProcedureFile((ProcedureData) data); // 写入文件

				}

			} else if (data instanceof StartupData) {

				response = new ResponseDecorator(new StartupData(), false);
				// StringBuffer errInfo = new StringBuffer();
				StartupData sd = (StartupData) data;
				logger.info(sd);
				if (sd.getState() == State.NORMAL || sd.getState() == State.CAL || sd.getState() == State.JOIN
						|| sd.getState() == State.INIT || sd.getState() == State.UPGRADE
						|| sd.getState() == State.MAINTAIN) {

					logger.info("change work mode :" + sd.getState());
					Context.getCoreService().changeWorkMode(sd);

				} else {

					logger.info("exeprocedure()");

					if (mainboard.getProcedureMode() != ProcedureMode.DEVICE && unitIndex == 0xff) {

						for (ControlUnit cu : mainboard.getControls()) {

							if (cu.getProcedure() != null) {

								Context.getCoreService().executeProcedure(cu, sd.getState()); // 操作分区

							}

						}

					} else {

						Context.getCoreService().executeProcedure(mainboard.getControlUnitByIndex(unitIndex),
								sd.getState());
					}

				}

			} else if (data instanceof ExChnsOperateData) {

				response = new ResponseDecorator(new ExChnsOperateData(), false);
				ExChnsOperateData ctd = (ExChnsOperateData) data;
				System.out.println(ctd);

				List<Channel> channels = new ArrayList<>();
				ControlUnit cu = mainboard.getControlUnitByIndex(unitIndex);
				List<Channel> cuChns = cu.listAllChannels(null);
				for (short chnIndex : ctd.getChnIndexs()) {

					if (chnIndex < cuChns.size()) {
						channels.add(cuChns.get(chnIndex));
					}
				}

				if (!channels.isEmpty()) {
					Context.getCoreService().executeChannelsProcedure(ctd.getOptType(),
							channels.toArray(new Channel[0]));
				}

			} else if (data instanceof TestNameData) {

				response = new ResponseDecorator(new TestNameData(), false);

				mainboard.getControlUnitByIndex(unitIndex).writeTestNameFile(((TestNameData) data).getTestName());

				System.out.println("config test name :" + ((TestNameData) data).getTestName());

			} else if (data instanceof CheckVoltProtectData) {

				response = new ResponseDecorator(new CheckVoltProtectData(), false);
				CheckVoltProtectData cvpd = (CheckVoltProtectData) data;
				if (cvpd.getResisterOffset() == 0) {

					cvpd.setResisterOffset(MainBoard.startupCfg.getProductType() == ProductType.CAPACITY ? 500 : 800);
				}
				mainboard.getTempParameterNameCfg().setCheckVoltProtect(cvpd);

			} else if (data instanceof SaveParamData) {

				// 将上位机下发的保护到本地文件
				SaveParamData spd = (SaveParamData) data;
				mainboard.getTempParameterNameCfg().setName(spd.getName());
				mainboard.getTempParameterNameCfg().setWorkType(spd.getWorkType());
				mainboard.getTempParameterNameCfg().setDefaultPlan(spd.isDefaultPlan());

				if (mainboard.getProcedureMode() != ProcedureMode.DEVICE && unitIndex == 0xff) {

					for (ControlUnit cu : mainboard.getControls()) {

						cu.writeProtectionFile(mainboard.getTempParameterNameCfg());
					}

				} else {

					mainboard.getControlUnitByIndex(unitIndex).writeProtectionFile(mainboard.getTempParameterNameCfg());

				}
				// 清除配置缓存
				mainboard.clearTempCfg();

			} else if (data instanceof TempData) {

				response = new ResponseDecorator(new TempData(), false);

				TempData tempData = (TempData) data;

				mainboard.writeTempControlFile(tempData);

				// 配置温控板温度
				if (Context.getAccessoriesService().getTemperatureManager() != null) {

					double upper = ((TempData) data).getUpper();
					double lower = ((TempData) data).getLower();
					Context.getAccessoriesService().getTemperatureManager().writeTempUpper(upper);
					Context.getAccessoriesService().getTemperatureManager().writeTempLower(lower);
					Context.getAccessoriesService().getTemperatureManager()
							.setProtectMinute(((TempData) data).getProtectMinute());
					Context.getAccessoriesService().getTemperatureManager()
							.setSyncTempOpen(((TempData) data).isSyncTempOpen());
					Context.getAccessoriesService().getTemperatureManager()
							.writeTemperature(((TempData) data).getTempConstant());

					Environment.infoLogger.info(data);

					if (((TempData) data).isTempControlOpen()) {
						if (Context.getAccessoriesService().getFanManager() != null) {
							Context.getAccessoriesService().getFanManager().powerTurboFan(PowerState.ON); // 先打开涡轮风机
						}
						Context.getAccessoriesService().getTemperatureManager().power(PowerState.ON);
					} else {

						Context.getAccessoriesService().getTemperatureManager().power(PowerState.OFF); // 先关闭加热管
						if (Context.getAccessoriesService().getFanManager() != null) {
							Context.getAccessoriesService().getFanManager().powerTurboFan(PowerState.OFF);
						}
					}

				}

			} else if (data instanceof VoiceAlertData) {

				response = new ResponseDecorator(new VoiceAlertData(), false);
				VoiceAlertData beep = (VoiceAlertData) data;
				// 保存到文件配置
				Context.getFileSaveService().writeBeepAlertFile(beep);
				Context.getAccessoriesService().getBeepController().setBeep(beep);

			} else if (data instanceof StartEndCheckData) {

				response = new ResponseDecorator(new StartEndCheckData(), false);
				mainboard.getTempParameterNameCfg().setStartEndCheckProtect((StartEndCheckData) data);

			} else if (data instanceof DebugControlData) {

				response = new ResponseDecorator(new DebugControlData(), false);
				DebugControlData dd = (DebugControlData) data;
				if (dd.getControlType() == ControlType.Fan) {

					// 打开或关闭所有风机
					mainboard.getFanManager().fan(0, Direction.IN, dd.isOpen() ? PowerState.ON : PowerState.OFF, 0);

				} else if (dd.getControlType() == ControlType.ChargePower) {

					// 打开或关闭所有电源
					mainboard.getPowerManager().power(dd.isOpen() ? PowerState.ON : PowerState.OFF);

				} else if (dd.getControlType() == ControlType.GreenTricolourLight
						|| dd.getControlType() == ControlType.YellowTricolourLight
						|| dd.getControlType() == ControlType.RedTricolourLight
						|| dd.getControlType() == ControlType.Buzzer) {

					if (mainboard.getAudioLightAlarmController() != null) {

						byte colorFlag = 0;
						short audioFlag = 0, lightFlag = 0;
						switch (dd.getControlType()) {
						case GreenTricolourLight:
							colorFlag = 0x01;
							lightFlag = dd.isOpen() ? (short) 0xffff : (short) 0x00;
							break;
						case YellowTricolourLight:
							colorFlag = 0x02;
							lightFlag = dd.isOpen() ? (short) 0xffff : (short) 0x00;
							break;
						case RedTricolourLight:
							colorFlag = 0x04;
							lightFlag = dd.isOpen() ? (short) 0xffff : (short) 0x00;
							break;
						case Buzzer:
							audioFlag = (short) 0xf0f0;
							lightFlag = (short) 0x00;
							break;

						}

						mainboard.getAudioLightAlarmController().configLightAndAudio(colorFlag, lightFlag, audioFlag);

					} else {

						response.setResult(DefaultResult.LOGIC);
					}
				} else if (dd.getControlType() == ControlType.GreenPoleLight
						|| dd.getControlType() == ControlType.YellowPole) {

					if (mainboard.getStateLightControllers().size() > 0) {

						for (int n = 0; n < mainboard.getStateLightControllers().size(); n++) {

						}

					} else {

						response.setResult(DefaultResult.LOGIC);
					}
				}
			} else if (data instanceof EnergySaveData) {

				// 节能方案
				response = new ResponseDecorator(new EnergySaveData(), false);
				EnergySaveData esd = (EnergySaveData) data;
				Context.getAccessoriesService().setEnergySaveData(esd);
				if (!esd.isUseSmartFan()) {

					if (Context.getAccessoriesService().getFanManager() != null) {
						// 不使用智能风机则直接常开风机
						Context.getAccessoriesService().getFanManager().fan(0, Direction.OUT, PowerState.ON, 2);
					}
				}

			} else if (data instanceof PushSwitchData) {

				System.out.println(data);
				response = new ResponseDecorator(new PushSwitchData(), false);
				PushSwitchData pwd = (PushSwitchData) data;
				if (pwd.isOpen()) {

					// 启动采集
					Context.getCoreService().startWork();

				} else {
					// 关闭采集
					Context.getCoreService().stopWork();
				}
			} else if (data instanceof SolenoidValveData) {

				response = new ResponseDecorator(new SolenoidValveData(), false);
				if (mainboard.getMechanismManager() != null) {

					mainboard.getMechanismManager().writeValve(0,
							ValveState.values()[((SolenoidValveData) data).getState().ordinal()]);
				}

				System.out.println(response);

			} else if (data instanceof SolenoidValveTempProtectData) {

				response = new ResponseDecorator(new SolenoidValveTempProtectData(), false);
				if (mainboard.getMechanismManager() != null) {

					mainboard.getMechanismManager().writeValveTempUpper(0,
							((SolenoidValveTempProtectData) data).getTemperatureUpper());
				}
			} else if (data instanceof CylinderPressureProtectData) {

				response = new ResponseDecorator(new CylinderPressureProtectData(), false);
				if (mainboard.getMechanismManager() != null) {

					CylinderPressureProtectData cpd = (CylinderPressureProtectData) data;
					mainboard.getMechanismManager().writePressureRange(cpd.getPressureLower(), cpd.getPressureUpper());
				}

			} else if (data instanceof OfflineRunningData) {

				response = new ResponseDecorator(new OfflineRunningData(), false);
				OfflineRunningData offlineRunningData = (OfflineRunningData) data;
				mainboard.writeOfflineFile(offlineRunningData);

			} else if (data instanceof AllowStepSkipData) {

				response = new ResponseDecorator(new AllowStepSkipData(), false);
				ControlUnit cu = mainboard.getControlUnitByIndex(data.getUnitIndex());
				// 上位机下发同步跳转命令
				// Context.getSyncStepControlService().skipNextSyncStep(cu);

			} else if (data instanceof UpgradeProgramExData) {

				logger.info(data);
				// 通知主控在线升级
				UpgradeProgramExData upd = (UpgradeProgramExData) data;
				response = new ResponseDecorator(new UpgradeProgramExData(), false);

				if (mainboard.getState() != State.UPGRADE) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeFailNoInUpgradeMode));
				}

				final short driverFlag = upd.getDriverFlag();

				System.out.println("upgrade driver flag:" + driverFlag);

				if (driverFlag > 0) {

					// 整机升级
					final Map<String, List<DriverBoard>> map = Context.getCoreService()
							.getDriverByPortMap(mainboard.getDriverBoards());

					for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {

						final String portName = it.next();
						new Thread(new Runnable() {

							@Override
							public void run() {

								int upgradeDriverIndex = -1;
								List<DriverBoard> list = map.get(portName);
								try {
									for (DriverBoard db : list) {

										if ((0x01 << db.getDriverIndex() & driverFlag) > 0) {

											upgradeDriverIndex = db.getDriverIndex();
											Context.getCoreService().upgradeSingleDriver(db.getDriverIndex(),
													upd.getUpgradeType(), upd.getPath());

										}

									}
								} catch (AlertException ex) {

									logger.error(CommonUtil.getThrowableException(ex));
									UpgradeProgressData progressData = new UpgradeProgressData();
									progressData.setUnitIndex(upd.getUnitIndex());
									progressData.setDriverIndex(upgradeDriverIndex);
									progressData.setUpgradeType(upd.getUpgradeType());
									progressData.setRange(100);
									progressData.setPos(-1);
									Context.getPcNetworkService().pushSendQueue(new AlertDecorator(progressData));
								}

							}

						}).start();

					}
				}

			} else if (data instanceof SelfCheckData) {

				if (mainboard.getState() == State.FORMATION) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.SelfCheckingMustBeUdt));
				}

				// 系统开始自检
				Context.getCoreService().startSelfChecking();

				response = new ResponseDecorator(data, false);

			} else if (data instanceof ResetData) {

				if (mainboard.getState() == State.FORMATION) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ResetRunning));
				}
				Context.getCoreService().reset(mainboard.getControlUnitByIndex(unitIndex)); // 复位设备

			} else if (data instanceof RecoveryData) {

				if (mainboard.getState() == State.FORMATION) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ResetRunning));
				}
				Context.getCoreService().recovery(mainboard.getControlUnitByIndex(unitIndex));

			} else if (data instanceof DriverChnIndexDefineData) {

				DriverChnIndexDefineData dcdd = (DriverChnIndexDefineData) data;
				if (mainboard.getState() == State.FORMATION) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.CannotOptInRunning));
				}
				Context.getFileSaveService().writeDriverChnIndexDefineFile(dcdd);
				mainboard.setDcdd(dcdd);

			} else if (data instanceof JsonProcedureData) {
				// 多分区流程配置

				JsonProcedureData jpd = (JsonProcedureData) data;
				for (int n = 0; n < jpd.getProcedures().size(); n++) {

					ProcedureData procedure = jpd.getProcedures().get(n);
					mainboard.checkProcedureValid(procedure);

					mainboard.getControlUnitByIndex(n).setProcedure(procedure);
					// 写入文件并保存流程

					mainboard.getControlUnitByIndex(n).writeProcedureFile(procedure); // 写入文件

				}

			} else if (data instanceof JsonProtectionData) {

				// 多分区保护方案下发
				JsonProtectionData jpd = (JsonProtectionData) data;
				for (int n = 0; n < jpd.getPlans().size(); n++) {

					Map<PlanKey, ProtectionPlan> map = jpd.getPlans().get(n);

					for (Iterator<PlanKey> it = map.keySet().iterator(); it.hasNext();) {

						PlanKey key = it.next();
						ProtectionPlan plan = map.get(key);

						// 将上位机下发的保护到本地文件
						SaveParamData spd = plan.pn;
						ParameterName pn = new ParameterName(spd.getWorkType());

						pn.setName(spd.getName());
						pn.setDefaultPlan(spd.isDefaultPlan());

						pn.setCcProtect(plan.cc);
						pn.setCheckVoltProtect(plan.checkVolt);
						pn.setCvProtect(plan.cv);
						pn.setDcProtect(plan.dc);
						pn.setFirstCCProtect(plan.fcpd);
						pn.setSlpProtect(plan.sleep);
						pn.setStartEndCheckProtect(plan.secd);

						mainboard.getControlUnitByIndex(n).writeProtectionFile(pn);

					}

				}

			} else if (data instanceof ClearSmogData) {

				SmogAlertManager manager = Context.getAccessoriesService().getSmogManager();
				if (manager == null) {

					throw new AlertException(AlertCode.LOGIC, "主控未启用烟雾报警模块");
				}
				manager.clearSmogState(0);

			} else if (data instanceof EnableCheckProtectionData) {

				EnableCheckProtectionData ecpd = (EnableCheckProtectionData) data;

			} else if(data instanceof SyncPressureParamData) {
				//同步到达气压到达超时时间
				SyncPressureParamData mpd = (SyncPressureParamData)data;
				mainboard.getControlUnitByIndex(mpd.getUnitIndex())
				          .setPressureCompleteParam(mpd.isPressureComplete(), mpd.getTimeout());
			
			} else if(data instanceof TemperAdjustData) {
				
				TemperAdjustData tad = (TemperAdjustData)data;
				
				if(Context.getAccessoriesService().getProbeManager() != null) {
					Context.getAccessoriesService().getProbeManager().setConstantTemp(tad.getTemper());
				}
				
			} else if(data instanceof JsonProductCfgData) {
				
				//修改产品参数
				JsonProductCfgData jpd = (JsonProductCfgData)data;
				
				MainBoard.startupCfg.setProductType(ProductType.valueOf(jpd.getBase().productType));
				MainBoard.startupCfg.setDriverChnCount(jpd.getBase().driverChnCount);
				MainBoard.startupCfg.setDisableDefaultProtection(jpd.getBase().disableDefaultProtection);
				MainBoard.startupCfg.setLanguage(jpd.getBase().language);
				
				
				MainBoard.startupCfg.setUseVirtualData(jpd.getSwitcher().useVirtualData);
				MainBoard.startupCfg.setUseAlert(jpd.getSwitcher().useAlert);
				MainBoard.startupCfg.setUseDebug(jpd.getSwitcher().useDebug);
				MainBoard.startupCfg.setUseReverseChnIndex(jpd.getSwitcher().useReverseChnIndex);
				MainBoard.startupCfg.setUseStepChangeProtect(jpd.getSwitcher().useStepChangeProtect);
				MainBoard.startupCfg.setUseSTM32Time(jpd.getSwitcher().useSTM32Time);
				
				MainBoard.startupCfg.getDriverInfos().clear();
				//驱动板配置
				for(DriversCfg dc : jpd.getDriverList()) {
					
					DriverInfo di = new DriverInfo();
					di.index = dc.index;
					
					di.communication = dc.communication;
					di.pickupTime = dc.pickupTime;
					di.portName = dc.portName;
					di.use = dc.use;
					
					MainBoard.startupCfg.getDriverInfos().add(di);
					
				}
				
				//过滤
				MainBoard.startupCfg.getRange().continueAlertFilter = jpd.getRange().continueAlertFilter;
				MainBoard.startupCfg.getRange().disableCurrentLine = jpd.getRange().disableCurrentLine;
				MainBoard.startupCfg.getRange().disableVoltageLine = jpd.getRange().disableVoltageLine;
				MainBoard.startupCfg.getRange().voltageFilterRange = jpd.getRange().voltageFilterRange;
				MainBoard.startupCfg.getRange().voltagePrecision   = jpd.getRange().voltagePrecision;
				MainBoard.startupCfg.getRange().voltageStartOffset = jpd.getRange().voltageStartOffset;
				MainBoard.startupCfg.getRange().use   = jpd.getRange().use;
				MainBoard.startupCfg.getRange().useCvCurrentFilter = jpd.getRange().useCvCurrentFilter;
				MainBoard.startupCfg.getRange().sections.clear();
				for(com.nltecklib.protocol.li.main.JsonProductCfgData.RangeSection rs : jpd.getRange().sections) {
					
					RangeSection sec = new RangeSection();
					sec.level = rs.level;
					sec.lower = rs.lower;
					sec.upper = rs.upper;
					sec.currentFilterRange = rs.currentFilterRange;
					//sec.currentFilterPercent = rs.
					sec.precision = rs.precision;
					sec.filter = rs.filter;
					
					MainBoard.startupCfg.getRange().sections.add(sec);
				}
				
				//limit
				MainBoard.startupCfg.setMaxDeviceVoltage(jpd.getLimit().maxDeviceVoltage);
				MainBoard.startupCfg.setMaxDeviceCurrent(jpd.getLimit().maxDeviceCurrent);
				MainBoard.startupCfg.setMinDeviceCurrent(jpd.getLimit().minDeviceCurrent);
				MainBoard.startupCfg.setMinDeviceVoltage(jpd.getLimit().minDeviceVoltage);
				
				//流程控制
				MainBoard.startupCfg.getProcedureSupportInfo().supportDriver = jpd.getProcedureSupport().supportDriver;
				MainBoard.startupCfg.getProcedureSupportInfo().supportImportantData = jpd.getProcedureSupport().supportImportantData;
				
				//配件
				MainBoard.startupCfg.getControlInfo().use = jpd.getControlBoard().use;
				MainBoard.startupCfg.getControlInfo().communicateTimeout = (int) jpd.getControlBoard().communicateTimeout;
				MainBoard.startupCfg.getControlInfo().heartbeat = jpd.getControlBoard().heartbeat;
				MainBoard.startupCfg.getControlInfo().portName = jpd.getControlBoard().portName;
				
				MainBoard.startupCfg.getPowerManagerInfo().use = jpd.getPowerManager().use;
				
				int groupLeft = jpd.getPowerManager().invertCount / 2;
				
				
				MainBoard.startupCfg.getPowerManagerInfo().powerGroups.clear();
				MainBoard.startupCfg.getPowerManagerInfo().powerGroups.add(groupLeft);
				MainBoard.startupCfg.getPowerManagerInfo().powerGroups.add(jpd.getPowerManager().invertCount - groupLeft);
				
				
				for(PowerInfo pi : MainBoard.startupCfg.getPowerManagerInfo().powerInfos) {
					
					pi.portName = jpd.getControlBoard().portName;
					if(pi.powerType == PowerType.CHARGE) {
						
						pi.powerCount = jpd.getPowerManager().invertCount;
					} else if(pi.powerType == PowerType.AUXILIARY) {
						
						pi.powerCount = jpd.getPowerManager().auxiliaryCount;
					}
				}
				
				MainBoard.startupCfg.getFanManagerInfo().use = jpd.getFanManager().use;
				
				for(FanInfo fi : MainBoard.startupCfg.getFanManagerInfo().fanInfos) {
					
					fi.portName  = jpd.getControlBoard().portName;
					if(fi.fanType == FanType.COOL) {
						
						fi.fanCount = jpd.getFanManager().coolFanCount;
					} else if(fi.fanType == FanType.TURBO) {
						
						fi.fanCount = jpd.getFanManager().turboFanCount;
					}
				}
				
				MainBoard.startupCfg.getProbeManagerInfo().use = jpd.getProbeManager().use;
				for(ProbeInfo pi : MainBoard.startupCfg.getProbeManagerInfo().probeInfos) {
					
					pi.portName = jpd.getControlBoard().portName;
					
				}
				
				MainBoard.startupCfg.getBeepAlertManagerInfo().use = jpd.getBeepManager().use;
				for(BeepAlertInfo bi : MainBoard.startupCfg.getBeepAlertManagerInfo().beepInfos) {
					
					bi.portName = jpd.getControlBoard().portName;
					
				}
				
				MainBoard.startupCfg.getColorLightManagerInfo().use = jpd.getColorLightManager().use;
				for(ColorLightInfo ci : MainBoard.startupCfg.getColorLightManagerInfo().colorLights) {
					
					ci.portName = jpd.getControlBoard().portName;
					
				}
				
				MainBoard.startupCfg.getStateLightControllerInfo().use = jpd.getPoleLightManager().use;
				for(StateLightInfo si : MainBoard.startupCfg.getStateLightControllerInfo().stateLights) {
					
					si.portName = jpd.getControlBoard().portName;
					
				}
				
				MainBoard.startupCfg.getOMRManagerInfo().use = jpd.getTempControl().use;
				for(TempBoardInfo ti : MainBoard.startupCfg.getOMRManagerInfo().meterInfos) {
					
					ti.portName = jpd.getControlBoard().portName;
					
				}
				
				MainBoard.startupCfg.getDoorAlertManagerInfo().use = jpd.getDoorManager().use;
				for(DoorInfo di : MainBoard.startupCfg.getDoorAlertManagerInfo().doorInfos) {
					
					di.portName = jpd.getControlBoard().portName;
					
				}
				
				MainBoard.startupCfg.getPingController().enable = jpd.getPingController().enable;
				
				
				//删除struct.xml
				FileUtil.removeAllFiles("./config/struct");
				// 删除struct.xml
				new File("./config/struct.xml").delete();
				
				// 删除struct.xml
				new File("./config/life.xml").delete();
				//更新配置文件cfg.xml
				MainBoard.startupCfg.changeDocument();
				
				
				
				
			} else if(data instanceof CylinderControlData) {
				
				CylinderControlData ccd = (CylinderControlData)data;
				if(Context.getAccessoriesService().getPingController() != null) {
					
					Context.getAccessoriesService().getPingController().writeAirPressureControl(ccd.isPress());
					
				}
				
			} else if(data instanceof CylinderCfgData) {
				
				CylinderCfgData ccd = (CylinderCfgData)data;
				if(Context.getAccessoriesService().getPingController() != null) {
					
					Context.getAccessoriesService().getPingController().setCfgData(ccd);
					
				}
				
			} else if(data instanceof JsonProcedureExData) {
				
				//进阶流程对象，需要拆解
				JsonProcedureExData jpe = (JsonProcedureExData)data;
				
				response = new ResponseDecorator(new JsonProcedureExData(), false);
				//检查流程
				ProcedureData procedure = (ProcedureData)jpe.getProcedureData();
				mainboard.checkProcedureValid(procedure);

				if (mainboard.getProcedureMode() != ProcedureMode.DEVICE && unitIndex == 0xff) {

					// 多分区写入
					for (ControlUnit cu : mainboard.getControls()) {

						cu.writeProcedureExFile(jpe);
					}

				} else {

				
					mainboard.getControlUnitByIndex(unitIndex).writeProcedureExFile(jpe); // 写入文件

				}
				
			}

		} catch (AlertException ae) {

			if (response.getResult().getCode() == Result.SUCCESS) {
				response.setResult(DefaultResult.FAIL);
			}
			if (data.supportUnit()) {
				response.setUnitIndex(unitIndex);
			}

			if (data.getCode() instanceof MBWorkformCode) {

				if (data.supportDriver()) {

					response.setDriverIndex(data.getDriverIndex());
				}
				if (data.supportChannel()) {

					response.setChnIndex(data.getChnIndex());
				}
			}
			// 设置错误信息
			response.setInfo(ae.getMessage());
			logger.error(CommonUtil.getThrowableException(ae));

		} catch (Exception e) {

			logger.error(CommonUtil.getThrowableException(e));
			if (response.getResult().getCode() == Result.SUCCESS) {
				response.setResult(DefaultResult.FAIL);
			}
			// 设置错误信息
			response.setInfo(e.getMessage());

		}

		return response;

	}

}
