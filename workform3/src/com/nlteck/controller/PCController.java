package com.nlteck.controller;

import java.util.Date;

import com.nlteck.base.Env;
import com.nlteck.base.I18N;
import com.nlteck.fireware.CalibrateCore;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.Environment.Orient;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.PCWorkform.BaseCfgData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoConfigData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoQueryData;
import com.nltecklib.protocol.li.PCWorkform.BindCalBoardData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardTestModeData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardUpdateFileData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardUpdateModeData;
import com.nltecklib.protocol.li.PCWorkform.CalCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempQueryDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibrateTerminalData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.ChnSelectData;
import com.nltecklib.protocol.li.PCWorkform.ConnectDeviceData;
import com.nltecklib.protocol.li.PCWorkform.DeviceConnectData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.DriverModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.HeartbeatData;
import com.nltecklib.protocol.li.PCWorkform.IPCfgData;
import com.nltecklib.protocol.li.PCWorkform.LivePushData;
import com.nltecklib.protocol.li.PCWorkform.LogPushData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWrite2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.MeterConnectData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.ModuleSwitchData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfTestInfoData;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.ReadMeterData;
import com.nltecklib.protocol.li.PCWorkform.RelayControlExDebugData;
import com.nltecklib.protocol.li.PCWorkform.RelaySwitchDebugData;
import com.nltecklib.protocol.li.PCWorkform.RequestCalculateData;
import com.nltecklib.protocol.li.PCWorkform.ResistanceModeRelayDebugData;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.protocol.li.PCWorkform.SwitchMeterData;
import com.nltecklib.protocol.li.PCWorkform.TestModeData;
import com.nltecklib.protocol.li.PCWorkform.TimeData;
import com.nltecklib.protocol.li.PCWorkform.WorkformUpdateData;
import com.nltecklib.protocol.li.cal.CalUpdateModeData;
import com.nltecklib.protocol.li.cal.RelayControlExData;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.utils.BaseUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月26日 下午3:39:07 校准主控与PC电脑(校准软件)的通信控制器
 */
public class PCController implements Controller {

	private CalibrateCore core;

	public PCController(CalibrateCore core) {

		this.core = core;
	}

	@Override
	public ResponseDecorator process(Decorator decorator) throws Exception {

		if (decorator.getOrient() != Orient.RESPONSE && !(decorator.getDestData() instanceof HeartbeatData)) {
			// 打印上位机下发操作
			core.getLogger().info(
					"pc process " + decorator.getDestData().getClass().getSimpleName() + "-" + decorator.getOrient());
		}

		Data data = decorator.getDestData();
		data.setResult(DefaultResult.SUCCESS);

		ResponseDecorator response = null;

		try {

			if (decorator instanceof ResponseDecorator) {

				// ResponseDecorator responseDec = (ResponseDecorator) decorator;

				if (decorator.getDestData() instanceof LogPushData) {
					// dataRecvIndex++;

				} else if (decorator.getDestData() instanceof LivePushData) {
					// dataRecvIndex++;

				} else {
					// 回复命令
					core.getNetworkService().getResponseMap().put(decorator.getDestData().getCodeKey(),
							(ResponseDecorator) decorator);
				}

				return null;

			}
			if (decorator instanceof ConfigDecorator) {

				response = new ResponseDecorator(data, false);

				if (data instanceof TimeData) {// 时间

					if (Env.isLinuxEnvironment()) {
						Date downloadDate = ((TimeData) data).getTime();
						Env.setSysDatetime(downloadDate);
						core.getLogger()
								.info("set system time " + BaseUtil.formatDate("yyyy-MM-dd HH:mm:ss", downloadDate));
					}

				} else if (data instanceof IPCfgData) {// ip
					if (Env.isLinuxEnvironment()) {
						Env.configIpAddress(((IPCfgData) data).getDeviceIp());
					}

				} else if (data instanceof ChnSelectData) {// 选中
					core.getCalibrateService().cfgChnSelect((ChnSelectData) data);

				} else if (data instanceof TestModeData) {// 选中并计量/停止通道
					core.getCalibrateService().cfgTestMode((TestModeData) data);

				} else if (data instanceof BaseCfgData) {// 基础配置
					core.cfgBaseCfg((BaseCfgData) data);

				} else if (data instanceof BaseInfoConfigData) {// 基础配置
					core.cfgBaseInfoConfig((BaseInfoConfigData) data);

				} else if (data instanceof CalibratePlanData) {// 校准点
					core.getCalCfg().cfgCalibratePlan((CalibratePlanData) data);

					// } else if (data instanceof DelayData) {// 延时
					// core.getCalCfg().cfgDelay((DelayData) data);

				} else if (data instanceof SteadyCfgData) {// adc ready
					core.getCalCfg().cfgSteadyCfg((SteadyCfgData) data);

				} else if (data instanceof RangeCurrentPrecisionData) {// 精度
					core.getCalCfg().cfgRangeCurrentPrecision((RangeCurrentPrecisionData) data);

				} else if (data instanceof CalculatePlanData) {// 计量方案
					core.getCalCfg().cfgCalculatePlan((CalculatePlanData) data);

				} else if (data instanceof ModeSwitchData) {// 进入/退出校准模式
					core.getCalibrateService().cfgModeSwitch(((ModeSwitchData) data).getMode());

				} else if (data instanceof MeterConnectData) {// 切万用表
					core.getCalibrateService().cfgMeterConnect((MeterConnectData) data);

				} else if (data instanceof ConnectDeviceData) {

					core.getLogger().info(data);
					core.getCalibrateService().connectDevice((ConnectDeviceData) data);

				} else if (data instanceof ModuleSwitchData) {// 膜片开关
					core.getCalibrateService().cfgModuleSwitch((ModuleSwitchData) data);

				}else if(data instanceof RelaySwitchDebugData){
					RelaySwitchDebugData debugData=(RelaySwitchDebugData) data;
					core.getRelayBoards().get(0).cfgCalRelaySwitch(debugData.getDriverIndex(), debugData.getChannel());
				} else if(data instanceof RelayControlExDebugData){
					RelayControlExDebugData debugData=(RelayControlExDebugData) data;
					System.out.println(core.getCalBoardMap().get(debugData.getDriverIndex()));
					core.getCalBoardMap().get(debugData.getDriverIndex()).cfgRelayControl2(debugData.getRelayIndex(), debugData.isConnected());
				} else if (data instanceof LogicFlashWriteData) {// 逻辑板flash
					core.getCalibrateService().cfgLogicFlashWrite((LogicFlashWriteData) data);

				} else if (data instanceof CheckFlashWriteData) {// 回检板flash
					// core.getCalibrateService().cfgCheckFlashWrite((CheckFlashWriteData) data);

				} else if (data instanceof HeartbeatData) {// 心跳
					// core.getLogger().info("heart beat ...");

				} else if (data instanceof CheckCalibrateDebugData) {// 校准调试
					// core.getCalibrateService().cfgCheckCalibrateDebug((CheckCalibrateDebugData)
					// data);

				} else if (data instanceof CheckCalculateDebugData) {// 计量调试
					// core.getCalibrateService().cfgCheckCalculateDebug((CheckCalculateDebugData)
					// data);

				} else if (data instanceof DeviceConnectData) {// 连接
					if (((DeviceConnectData) data).isConnected()) {
						core.getDeviceCore().connect();
					} else {
						core.getDeviceCore().disConnect();
					}

				} else if (data instanceof CalibrateTerminalData) {// 校准终端
					// CalibrateCore.getBaseCfg().base.calibrateTerminal = ((CalibrateTerminalData)
					// data)
					// .getCalibrateTerminal();
					// CalibrateCore.getBaseCfg().flush();

				} else if (data instanceof BindCalBoardData) {// 绑定校准板

					if (core.getChnMapService().isEnable()) {

						throw new Exception("已启用映射模式，无需绑定!");
					}
					core.getCalibrateService().cfgBindCalBoard((BindCalBoardData) data);

				} else if (data instanceof CalBoardUpdateModeData) {// 校准板升级模式
					CalBoardUpdateModeData tempData = (CalBoardUpdateModeData) data;
					if (core.getCalibrateService().getCalibrateCoreWorkMode() != CalibrateCoreWorkMode.NONE) {
						throw new Exception(I18N.getVal(I18N.EnterCalUpdateModeError, tempData.getDriverIndex() + 1));
					}
					core.getCalBoardMap().get(tempData.getDriverIndex()).cfgUpdateMode(tempData.isUpdateMode());

				} else if (data instanceof CalBoardUpdateFileData) {// 校准板升级数据
					CalBoardUpdateFileData tempData = (CalBoardUpdateFileData) data;
					core.getCalBoardMap().get(tempData.getDriverIndex()).cfgUpdateFile(tempData.getFileSize(),
							tempData.getPackCount(), tempData.getPackIndex(), tempData.getPackContent());

				} else if (data instanceof WorkformUpdateData) {// 校准主控升级

					WorkformUpdateData tempData = (WorkformUpdateData) data;
					if (tempData.isUpdate()) {// 升级主控

						if (Env.isLinuxEnvironment()) {

							if (core.getCalibrateService().getCalibrateCoreWorkMode() != CalibrateCoreWorkMode.NONE) {
								throw new Exception(I18N.getVal(I18N.MainUpdateError));
							}
							core.getNetworkService().pushLog(I18N.getVal(I18N.Reboot), false);
							// String fileName = "/var/ftp/pub/workform.jar";
							// File file = new File(fileName);
							// if (!file.exists()) {
							// throw new Exception(I18N.getVal(I18N.UpdateFileNotExist));
							// }
							// String[] cmdCp = new String[] { "cp", fileName, "~" };// 复制文件
							// String result = Env.executeSysCmd(cmdCp);
							// if (!result.isEmpty()) {
							// throw new Exception(result);
							// }

							new Thread(() -> {// 重启
								try {
									Thread.sleep(1000);
									String[] cmdReboot = new String[] { "reboot" };
									Env.executeSysCmd(cmdReboot);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}).start();

						}
					}

				} else if (data instanceof PCSelfCheckData) {
					core.getCalibrateService().cfgPCSelfCheck((PCSelfCheckData) data);

				} else if (data instanceof PCSelfTestInfoData) {
					// core.getCalibrateService().cfgPCSelfTestInfo((PCSelfTestInfoData) data);

				} else if (data instanceof CalCalibrate2DebugData) {
					core.getCalibrateService().cfgCalibrate2Debug((CalCalibrate2DebugData) data);

				} else if (data instanceof CalCalculate2DebugData) {

					core.getCalibrateService().cfgCalculate2Debug((CalCalculate2DebugData) data);

				} else if (data instanceof CalRelayControlDebugData) {

					// 获取校准板
					Meter meter = core.getCalBoardMap().get(data.getDriverIndex()).getMeter();
					CalRelayControlDebugData relay = (CalRelayControlDebugData) data;
					if (relay.isConnected()) {
						synchronized (meter) {

							// 断开另外一个校准板
							if (core.getMeterParamMap().get(meter).lastCalIndex != data.getDriverIndex()
									&& core.getMeterParamMap().get(meter).lastCalIndex != -1) {

								core.getCalBoardMap().get(core.getMeterParamMap().get(meter).lastCalIndex)
										.cfgRelayControl(false);
							}

							core.getCalibrateService().cfgRelayControlDebug((CalRelayControlDebugData) data);
							// 表连接标志
							core.getMeterParamMap().get(meter).lastCalIndex = data.getDriverIndex();
						}
					} else {
						
						core.getCalibrateService().cfgRelayControlDebug((CalRelayControlDebugData) data);
						// 表断开连接标志
						core.getMeterParamMap().get(meter).lastCalIndex = -1;
						
					}

				} else if (data instanceof LogicCalibrate2DebugData) {

					core.getCalibrateService().cfgLogicCalibrateDebug((LogicCalibrate2DebugData) data);

				} else if (data instanceof LogicCalculate2DebugData) {
					System.out.println("=======LogicCaliculate2DebugData=======");
					core.getCalibrateService().cfgLogicCalculateDebug((LogicCalculate2DebugData) data);

				} else if (data instanceof CalTempControlDebugData) {

					core.getCalibrateService().cfgTempControl(data.getDriverIndex(),
							((CalTempControlDebugData) data).getTemperature(),
							((CalTempControlDebugData) data).isOpen());

				} else if (data instanceof CalResistanceDebugData) {

					core.getCalibrateService().cfgResistanceDebug((CalResistanceDebugData) data);

				}else if (data instanceof ResistanceModeRelayDebugData) {
					core.getCalibrateService().cfgRelayResistanceDebug((ResistanceModeRelayDebugData) data);


				} else if (data instanceof LogicFlashWrite2DebugData) {
                  
					core.getCalibrateService().cfgLogicFlashWriteDebug((LogicFlashWrite2DebugData)data);
					
				} else if(data instanceof DriverModeSwitchData) {

					core.getCalibrateService().cfgDriverModeSwitch((DriverModeSwitchData)data);
					
			    } else if(data instanceof CalBoardTestModeData) {
				   
			    	core.getCalibrateService().cfgCalboardTestMode((CalBoardTestModeData)data);
			    	
			    } else if(data instanceof SwitchMeterData) { 
			       
			    	core.getCalibrateService().cfgCalboardMeterSwitch((SwitchMeterData) data);
			    	
			    } else {
					core.getLogger().error("Unprocessed config protocol:" + data.getClass().getSimpleName());
				}

				return response;

			}
			if (decorator instanceof QueryDecorator) {
				response = new ResponseDecorator(data, true);

				if (data instanceof TimeData) {// 时间
					((TimeData) data).setTime(new Date());

				} else if (data instanceof BaseCfgData) {// 基础配置
					core.qryBaseCfg((BaseCfgData) data);

				} else if (data instanceof CalibratePlanData) {// 校准点
					core.getCalCfg().qryCalibratePlan((CalibratePlanData) data);

				} else if (data instanceof SteadyCfgData) {// adc ready
					core.getCalCfg().qrySteadyCfg((SteadyCfgData) data);

				} else if (data instanceof RangeCurrentPrecisionData) {// 精度
					core.getCalCfg().qryRangeCurrentPrecision((RangeCurrentPrecisionData) data);

				} else if (data instanceof CalculatePlanData) {// 计量方案
					core.getCalCfg().qryCalculatePlan((CalculatePlanData) data);

				} else if (data instanceof ModeSwitchData) {// 进入/退出校准模式
					core.getCalibrateService().qryModeSwitch((ModeSwitchData) data);

				} else if (data instanceof MeterConnectData) {// 切万用表
					core.getCalibrateService().qryMeterConnect((MeterConnectData) data);

				} else if (data instanceof ModuleSwitchData) {// 膜片开关
					core.getCalibrateService().qryModuleSwitch((ModuleSwitchData) data);

				} else if (data instanceof LogicCalibrate2DebugData) {// 校准调试
					core.getCalibrateService().qryLogicCalibrateDebug((LogicCalibrate2DebugData) data);

				} else if (data instanceof LogicCalculate2DebugData) {// 计量调试
					core.getCalibrateService().qryLogicCalculateDebug((LogicCalculate2DebugData) data);

				} else if (data instanceof LogicFlashWriteData) {// 逻辑板flash
					core.getCalibrateService().qryLogicFlashWrite((LogicFlashWriteData) data);

				} else if (data instanceof LogicFlashWrite2DebugData) { // 上位机查询逻辑板flash
					core.getCalibrateService().qryLogicFlashWriteDebug((LogicFlashWrite2DebugData) data);

				} else if (data instanceof CheckFlashWriteData) {// 回检板flash
					// core.getCalibrateService().qryCheckFlashWrite((CheckFlashWriteData) data);

				} else if (data instanceof RequestCalculateData) {// 获取数据
					core.getCalibrateService().qryRequestCalculate((RequestCalculateData) data);

				} else if (data instanceof CheckCalibrateDebugData) {// 校准调试
					// core.getCalibrateService().qryCheckCalibrateDebug((CheckCalibrateDebugData)
					// data);

				} else if (data instanceof CheckCalculateDebugData) {// 计量调试
					// core.getCalibrateService().qryCheckCalculateDebug((CheckCalculateDebugData)
					// data);

				} else if (data instanceof CalibrateTerminalData) {// 校准方式，PC/液晶屏
					// ((CalibrateTerminalData) data)
					// .setCalibrateTerminal(CalibrateCore.getBaseCfg().base.calibrateTerminal);

				} else if (data instanceof BaseInfoQueryData) {// 信息查询
					core.qryBaseInfoQuery((BaseInfoQueryData) data);
					System.out.println(data);

				} else if (data instanceof CalBoardUpdateModeData) {// 查询升级状态
					CalBoardUpdateModeData tempData = (CalBoardUpdateModeData) data;
					CalUpdateModeData revData = core.getCalBoardMap().get(tempData.getDriverIndex()).qryUpdateMode();
					tempData.setUpdateMode(revData.isUpdateMode());

				} else if (data instanceof PCSelfCheckData) {
					// core.getCalibrateService().qryPCSelfCheck((PCSelfCheckData) data);

				} else if (data instanceof PCSelfTestInfoData) {
					core.getCalibrateService().qryPCSelfTestInfo((PCSelfTestInfoData) data);

				} else if (data instanceof CalRelayControlDebugData) {
					core.getCalibrateService().qryRelayControlDebug((CalRelayControlDebugData) data);

				} else if (data instanceof ReadMeterData) {

					core.getCalibrateService().qryMeterRead((ReadMeterData)data);

				} else if (data instanceof CalResistanceDebugData) {

					System.out.println(data);
					core.getCalibrateService().qryResistanceDebug((CalResistanceDebugData) data);
				} else if(data instanceof ResistanceModeRelayDebugData) {
					QueryDecorator queryDecorator=(QueryDecorator)decorator;
					byte relayIndex=queryDecorator.getDestData().getEncodeData().get(1);
					WorkPattern workPattern=WorkPattern.values()[queryDecorator.getDestData().getEncodeData().get(2)];
					int range=queryDecorator.getDestData().getEncodeData().get(3);
					double resistance=1;
//					if(workPattern!=WorkPattern.CV) {
//						
						resistance=core.getCalibrateService().getResistanceEx2(data.getDriverIndex(), relayIndex,
								workPattern,range);
//					}
//					
//					core.getMeters().get(0).ReadSingleClearBuffer();
					
					
					((ResistanceModeRelayDebugData) data).setResistance(resistance);
					((ResistanceModeRelayDebugData) data).setRelayIndex(relayIndex);
					((ResistanceModeRelayDebugData) data).setRange(range);
					((ResistanceModeRelayDebugData) data).setWorkPattern(workPattern);
					
					System.out.println(data.toString());
				} else if (data instanceof CalTempQueryDebugData) {

					core.getCalibrateService().qryTemperatureDebug((CalTempQueryDebugData) data);

				} else if(data instanceof DeviceSelfCheckData) { 
				    
					core.getCalibrateService().qryDeviceSelfCheck((DeviceSelfCheckData)data);
				} else {
					core.getLogger().error("Unprocessed query protocol:" + data.getClass().getSimpleName());
				}
				return response;
			}

		} catch (Exception e) {

			core.getLogger().error(e.getMessage(), e);
			if (response.getResult().getCode() == Result.SUCCESS) {
				response.setResult(DefaultResult.FAIL);
			}
			response.setInfo(e.getMessage() == null ? "" : e.getMessage());
		}

		return response;
	}

	@Override
	public ResponseDecorator sendCommand(Decorator decorator, int timeOut) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
