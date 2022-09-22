package com.nlteck.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.DriverInfo;
import com.nltecklib.io.mina.NetworkConnector;
import com.nltecklib.protocol.lab.ConfigDecorator;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Decorator;
import com.nltecklib.protocol.lab.Entity;
import com.nltecklib.protocol.lab.QueryDecorator;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Result;
import com.nltecklib.protocol.lab.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.lab.pickup.PHardErrInfoData;
import com.nltecklib.protocol.lab.pickup.PInfoExData;
import com.nltecklib.protocol.lab.pickup.POptData;
import com.nltecklib.protocol.lab.pickup.PPickupExData;
import com.nltecklib.protocol.lab.pickup.PPickupExData.DataPackEx;
import com.nltecklib.protocol.lab.pickup.PProtectionData;
import com.nltecklib.protocol.lab.pickup.PStepData;
import com.nltecklib.protocol.lab.pickup.PVoltBoundaryExData;
import com.nltecklib.protocol.lab.pickup.PVoltBoundaryExData.PoleEx;
import com.nltecklib.protocol.lab.pickup.PWorkEnvData;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ADCheck;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalibrateCheck;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.Opt;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.WorkEnv;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalculateData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverChannelTemperData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CheckResult;
import com.nltecklib.protocol.power.driver.DriverEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.power.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.power.driver.DriverHeartbeatData;
import com.nltecklib.protocol.power.driver.DriverInfoData;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.nltecklib.protocol.power.driver.DriverModuleSwitchData;
import com.nltecklib.protocol.power.driver.DriverOperateData;
import com.nltecklib.protocol.power.driver.DriverPickupData;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;
import com.nltecklib.protocol.power.driver.DriverPoleData;
import com.nltecklib.protocol.power.driver.DriverProtectData;
import com.nltecklib.protocol.power.driver.DriverResumeData;
import com.nltecklib.protocol.power.driver.DriverResumeData.ResumeUnit;
import com.nltecklib.protocol.power.driver.DriverStepData;
import com.nltecklib.protocol.power.driver.DriverUpgradeData;
import com.nltecklib.utils.BaseUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年7月29日 下午11:00:15 实验室驱动板采集组件
 */
public class LabDriverBoardService extends AbsDriverBoardService {

	private static final int OPT_TIMEOUT = 2000;
	private static final int CONNECT_TIMEOUT = 1000 * 7; // 默认网络连接超时时间
	private Map<ResponseTicket, Data> poolMap = new ConcurrentHashMap<>();
	private LinkedBlockingQueue<Decorator> sendQueue = new LinkedBlockingQueue<Decorator>();

	/**
	 * 协议标记
	 * 
	 * @author wavy_zheng 2022年3月17日
	 *
	 */
	public static class ResponseTicket {

		public Code code;
		public int chnIndex;

		public ResponseTicket(Code code, int chnIndex) {

			this.code = code;
			this.chnIndex = chnIndex;

		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof ResponseTicket) {

				ResponseTicket ticket = (ResponseTicket) obj;

				return ticket.code == this.code
						&& (ticket.chnIndex == this.chnIndex || ticket.chnIndex == 0xff || this.chnIndex == 0xff);

			}

			return false;
		}

		@Override
		public int hashCode() {

			return code.hashCode() * 100 + 0;

		}

	}

	public LabDriverBoardService(MainBoard mainboard) {
		super(mainboard);

	}

	private ChnDataPack convertFromDataPackEx(DataPackEx dpx, int chnIndex) {

		ChnDataPack pack = new ChnDataPack();
		pack.setChnIndex(chnIndex);
		pack.setVoltage(dpx.voltage);
		pack.setBackupVoltage(dpx.backVoltage);
		pack.setPowerVoltage(dpx.powerVoltage);
		pack.setCapacity(dpx.capacity);
		pack.setCurrent(dpx.current);
		pack.setStepIndex(dpx.stepIndex);
		pack.setLoopIndex(dpx.loopIndex);
		pack.setStepElapsedTime(dpx.miliseconds);
		switch (dpx.workState) {

		case NONE:
			pack.setState(ChnState.NONE);
			break;
		case EXCEPT:
			pack.setState(ChnState.EXCEPT);
			break;
		case RUN:
			pack.setState(ChnState.RUNNING);
			break;
		case STOP:
			pack.setState(ChnState.STOP);
			break;
		case UDT:
			pack.setState(ChnState.UDT);
			break;
		case COMPLETE:
			pack.setState(ChnState.COMPLETE);
			break;

		}

		switch (dpx.workMode) {

		case CCC:
			pack.setWorkMode(WorkMode.CC);
			break;
		case CCCV:
			pack.setWorkMode(WorkMode.CC_CV);
			break;
		case CDC:
			pack.setWorkMode(WorkMode.DC);
			break;
		case SLEEP:
			pack.setWorkMode(WorkMode.SLEEP);
			break;
		case CVC:
			pack.setWorkMode(WorkMode.CV);
			break;

		}

		if (dpx.alertInfo != null) {
			// 报警信息
			pack.setAlertVolt(dpx.alertInfo.alertVoltage);
			pack.setAlertCurrent(dpx.alertInfo.alertCurrent);
			pack.setAlertCapacity(dpx.alertInfo.alertCapacity);

			DriverEnvironment.AlertCode ac = DriverEnvironment.AlertCode.NORMAL;
			switch (dpx.alertInfo.alertType) {
			case VOLT_UPPER:
				ac = DriverEnvironment.AlertCode.OVER_VOLT;
				break;
			case CURR_UPPER:
				ac = DriverEnvironment.AlertCode.OVER_CURR;
				break;
			case POLE_REVERSE:
				ac = DriverEnvironment.AlertCode.POLE_REVERSE;
				break;
			case MAIN_COMM:
			case MOUDLE_COMM:
			case AD_COMM:
			case MODULE_FIRM:
				ac = DriverEnvironment.AlertCode.HARDERR;
				break;
			case BACK_VOLT_UPPER:
				ac = DriverEnvironment.AlertCode.DEVICE_VOLT_UPPER;
				break;
			case NORMAL:
				ac = DriverEnvironment.AlertCode.NORMAL;
				break;
			default:
				ac = DriverEnvironment.AlertCode.HARDERR;
				break;

			}

			pack.setAlertCode(ac);

		}

		return pack;
	}

	@Override
	public DriverPickupData pickupDriver(int driverIndex) throws AlertException {

		DriverBoard db = mainboard.getDriverByIndex(driverIndex);
		DriverInfo di = MainBoard.startupCfg.getDriverInfo(driverIndex);

		DriverPickupData dpd = new DriverPickupData();
		dpd.setDriverIndex(driverIndex);

		for (int n = 0; n < db.getChannelCount(); n++) {

			PPickupExData pickup = new PPickupExData();
			pickup.setChnIndex(n);
			try {
				PPickupExData recvData = (PPickupExData) db.sendAndRecvData(n, new QueryDecorator(pickup),
						(int) di.communication, 1);
				for (DataPackEx dpx : recvData.getPacks()) {
					
					dpd.getPacks().add(convertFromDataPackEx(dpx, n));
				}

			} catch (Exception e) {

				throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC,
						e.getMessage());
			}

		}

		return dpd;
	}

	@Override
	public DriverChannelTemperData pickupTemperature(int driverIndex) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writePole(DriverPoleData data) throws AlertException {

		DriverBoard db = mainboard.getDriverByIndex(data.getDriverIndex());
		// 转化为单板配置极性
		for (int n = 0; n < db.getChannelCount(); n++) {

			PVoltBoundaryExData bed = new PVoltBoundaryExData();
			bed.setBoundary(data.getVoltageBound());
			bed.setChnIndex(n);
			bed.setPole(data.getPole() == Pole.POSITIVE ? PoleEx.NORMAL : PoleEx.REVERSE);
			try {
				db.sendAndRecvData(n, new ConfigDecorator(bed), OPT_TIMEOUT, 1);
			} catch (Exception e) {
				throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC,
						e.getMessage());
			}
		}

	}

	@Override
	public void writeHeartbeat(DriverHeartbeatData data) throws AlertException {

		// 实验室暂无心跳

	}

	@Override
	public DriverInfoData readDriverInfo(int driverIndex, int chnIndexInDriver) throws AlertException {

		return null;
	}

	@Override
	public void writeBaseProtect(DriverProtectData data) throws AlertException {

		DriverBoard db = mainboard.getDriverByIndex(data.getDriverIndex());
		// 转化为单板配置极性
		for (int n = 0; n < db.getChannelCount(); n++) {

			PProtectionData ppd = new PProtectionData();
			ppd.setChnIndex(n);
			ppd.setMainIndex(0);
			ppd.setVoltUpper(data.getChnVoltUpper());
			ppd.setCurrUpper(data.getChnCurrUpper());

			try {
				db.sendAndRecvData(n, new ConfigDecorator(ppd), OPT_TIMEOUT, 1);
			} catch (Exception e) {
				throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC,
						e.getMessage());
			}
		}

	}

	@Override
	public void writeOperate(DriverOperateData data) throws AlertException {

		DriverBoard db = mainboard.getDriverByIndex(data.getDriverIndex());
		// 转化为单板配置极性
		for (int n = 0; n < db.getChannelCount(); n++) {

			POptData opt = new POptData();
			opt.setMainIndex(0);
			opt.setChnIndex(n);
			if ((data.getOptFlag() & 0x01 << n) > 0) {

				opt.setOpt(data.isOpen() ? Opt.STARTUP : Opt.STOP);

			} else {

				continue;
			}

			System.out.println("write operate driver " + data.getDriverIndex() + ",chnIndex = " + n);
			try {
				db.sendAndRecvData(n, new ConfigDecorator(opt), OPT_TIMEOUT, 1);
			} catch (Exception e) {
				throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC,
						e.getMessage());
			}
		}

	}

	private com.nltecklib.protocol.lab.pickup.PStepData.Step convertToLabStep(Step step , long miliseconds , double startCapacity) {

		com.nltecklib.protocol.lab.pickup.PStepData.Step pstep = new com.nltecklib.protocol.lab.pickup.PStepData.Step();
		pstep.stepIndex = step.stepIndex;
		pstep.loopIndex = step.loopIndex == 0 ? 1 : step.loopIndex;
		pstep.startCapacity = startCapacity;
		pstep.endCapacity = step.overCapacity;
		pstep.miliseconds = miliseconds > 0 ? miliseconds : step.overTime * 1000;

		pstep.workMode = convertWorkMode(step.workMode);
		switch (step.workMode) {

		case CCC:
			pstep.voltage = step.specialVoltage;
			pstep.current = step.specialCurrent;
			pstep.endVoltage = step.overThreshold;
			break;
		case CC_CV:
			pstep.voltage = step.specialVoltage;
			pstep.current = step.specialCurrent;
			pstep.endCurrent = step.overThreshold;
			break;
		case CVC:
			pstep.voltage = step.specialVoltage;
			pstep.current = step.specialCurrent;
			pstep.endCurrent = step.overThreshold;
			break;
		case CCD:
			//注意实验室的程控电压需要比截止电压低
			pstep.voltage = step.overThreshold == 0 ? 2000 :  step.overThreshold - 50;
			pstep.current = step.specialCurrent;
			pstep.endVoltage = step.overThreshold;
			break;
		case DW:
			// 恒功率
			pstep.voltage = step.specialCurrent;
			pstep.endVoltage = step.specialVoltage;
			pstep.endCurrent = step.overThreshold;
			break;

		}

		return pstep;
	}

	@Override
	public void writeSteps(DriverStepData data) throws AlertException {

		DriverBoard db = mainboard.getDriverByIndex(data.getDriverIndex());
		// 转化为单板配置极性
		for (int n = 0; n < db.getChannelCount(); n++) {

			PStepData psd = new PStepData();
			psd.setMainIndex(0);
			psd.setChnIndex(n);

			for (Step step : data.getSteps()) {

				com.nltecklib.protocol.lab.pickup.PStepData.Step pstep = convertToLabStep(step , 0 , 0);
				psd.getSteps().add(pstep);

			}
			// 下发流程步次
			try {
				// 先清除以前遗留的步次
				POptData opt = new POptData();
				opt.setMainIndex(0);
				opt.setChnIndex(n);
				opt.setOpt(Opt.STOP);
				db.sendAndRecvData(n, new ConfigDecorator(opt), OPT_TIMEOUT, 1);

				db.sendAndRecvData(n, new ConfigDecorator(psd), OPT_TIMEOUT, 1);
			} catch (Exception e) {
				throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC,
						e.getMessage());
			}
		}

	}

	private com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode convertWorkMode(MainEnvironment.WorkMode wm) {

		switch (wm) {

		case CCC:
			return com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode.CCC;
		case CC_CV:
			return com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode.CCCV;
		case CVC:
			return com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode.CVC;
		case CCD:
			return com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode.CDC;
		case SLEEP:
			return com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode.SLEEP;
		case DW:
			return com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode.CDP;

		}

		return null;
	}

	@Override
	public void writeResume(DriverResumeData data) throws AlertException {
		DriverBoard db = mainboard.getDriverByIndex(data.getDriverIndex());

		for (int n = 0; n < data.getUnits().size(); n++) {

			ResumeUnit ru = data.getUnits().get(n);
			Channel chn = db.getChannel(ru.chnIndex);
			ProcedureData procedure = chn.getCurrentProcedure();
			if (procedure != null) {

				PStepData psd = new PStepData();
				psd.setMainIndex(0);
				psd.setChnIndex(ru.chnIndex);

				for (int i = ru.stepIndex; i <= procedure.getStepCount(); i++) {

					Step step = procedure.getStep(i - 1);

					if (i == ru.stepIndex) {
						db.log("resume step " + i);
						db.log("resume time = " + (step.overTime * 1000) + " - " + ru.miliseconds + " = " + (step.overTime * 1000 - ru.miliseconds));
						com.nltecklib.protocol.lab.pickup.PStepData.Step pstep = convertToLabStep(step , step.overTime * 1000 - ru.miliseconds , ru.capacity);
						psd.getSteps().add(pstep);
					} else {

						com.nltecklib.protocol.lab.pickup.PStepData.Step pstep = convertToLabStep(step , 0 , 0 );
						psd.getSteps().add(pstep);
					}

				}
				// 下发流程步次
				try {
					// 先清除以前遗留的步次
					POptData opt = new POptData();
					opt.setMainIndex(0);
					opt.setChnIndex(ru.chnIndex);
					opt.setOpt(Opt.STOP);
					db.sendAndRecvData(ru.chnIndex, new ConfigDecorator(opt), OPT_TIMEOUT, 1);
					db.log("opt close chn first,ok!!!");
					db.log("config resume steps: " + psd);
					// 下发步次
					db.sendAndRecvData(ru.chnIndex, new ConfigDecorator(psd), OPT_TIMEOUT, 1);
				} catch (Exception e) {
					throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC,
							e.getMessage());
				}

			}

		}

	}

	@Override
	public void writeUpgrade(DriverUpgradeData upgrade) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeWorkMode(DriverModeData data) throws AlertException {

		DriverBoard db = mainboard.getDriverByIndex(data.getDriverIndex());
		// 转化为单板配置极性
		for (int n = 0; n < db.getChannelCount(); n++) {

			PWorkEnvData we = new PWorkEnvData();
			we.setChnIndex(n);

			switch (data.getMode()) {
			case WORK:
				we.setWorkEnv(WorkEnv.WORK);
				break;
			case CAL:
				we.setWorkEnv(WorkEnv.CAL);
				break;
			case DRIVER_UPGRADE:
				we.setWorkEnv(WorkEnv.UPGRADE);
				break;
			case CHECK_UPGRADE:
				we.setWorkEnv(WorkEnv.MODULE_UPGRADE);
				break;
			case PICK_UPGRADE:
				we.setWorkEnv(WorkEnv.AD_UPGRADE);
				break;

			}

			try {
				db.sendAndRecvData(n, new ConfigDecorator(we), OPT_TIMEOUT, 1);
			} catch (Exception e) {
				throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC,
						e.getMessage());
			}
		}

	}

	@Override
	public void writeModuleSwitch(DriverModuleSwitchData data) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCalibrate(DriverCalibrateData data) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public DriverCalibrateData readCalibrate(int driverIndex, int chnIndexInDriver) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeCalculate(DriverCalculateData data) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeFlash(DriverCalParamSaveData flash) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public DriverCalculateData readCalculate(int driverIndex, int chnIndexInDriver) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DriverCalParamSaveData readFlash(int driverIndex, int chnIndexInDriver,int moduleIndex) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DriverMatchAdcData readMatchAdcs(int driverIndex) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DriverCheckData readDriverSelfCheckInfo(int driverIndex) throws AlertException {

		DriverBoard db = mainboard.getDriverByIndex(driverIndex);
		DriverCheckData dcd = new DriverCheckData();
		dcd.setDriverIndex(driverIndex);
		PHardErrInfoData ped = new PHardErrInfoData();
		ped.setMainIndex(0);
		ped.setChnIndex(0);
		try {
			PHardErrInfoData recvData = (PHardErrInfoData) db.sendAndRecvData(0, new QueryDecorator(ped), OPT_TIMEOUT,
					1);
			dcd.setAdPick(recvData.getAdCheck() == ADCheck.OK ? CheckResult.NORMAL : CheckResult.MISS);
			dcd.setCalParam(recvData.getCalCheck() == CalibrateCheck.OK ? CheckResult.NORMAL : CheckResult.CAL_ERR);
			dcd.setCheckboard(CheckResult.NORMAL);
			dcd.setDriverFlash(CheckResult.NORMAL);
			dcd.setDriverSram(CheckResult.NORMAL);
			dcd.setPowerOk(true);
			dcd.setTempPick(CheckResult.NORMAL);

		} catch (Exception e) {
			throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC, e.getMessage());
		}

		return dcd;
	}

	@Override
	public DriverInfoData readDriverSoftInfo(int driverIndex) throws AlertException {

		DriverInfoData did = new DriverInfoData();
		did.setDriverIndex(driverIndex);
		DriverBoard db = mainboard.getDriverByIndex(driverIndex);
		PInfoExData ped = new PInfoExData();
		ped.setMainIndex(0);
		ped.setChnIndex(0);
		try {
			PInfoExData recvData = (PInfoExData) db.sendAndRecvData(0, new QueryDecorator(ped), OPT_TIMEOUT, 1);
			did.setDriverSoftVersion(recvData.getDriverVersion());
			did.setPickSoftVersion(recvData.getAdVersion());
			did.setCheckSoftVersion(recvData.getModuleVersions().isEmpty() ? "" : recvData.getModuleVersions().get(0));
			did.setMaxCurrent(MainBoard.startupCfg.getMaxDeviceCurrent());

		} catch (Exception e) {
			throw new AlertException(com.nltecklib.protocol.li.main.MainEnvironment.AlertCode.LOGIC, e.getMessage());
		}

		return did;
	}

}
