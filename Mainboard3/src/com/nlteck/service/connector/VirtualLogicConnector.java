package com.nlteck.service.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nlteck.AlertException;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.test.RandomDriverDataProducer;
import com.nltecklib.protocol.li.logic.LogicCalMatchData;
import com.nltecklib.protocol.li.logic.LogicCalProcessData;
import com.nltecklib.protocol.li.logic.LogicCalculateData;
import com.nltecklib.protocol.li.logic.LogicChnStartData;
import com.nltecklib.protocol.li.logic.LogicChnStopData;
import com.nltecklib.protocol.li.logic.LogicChnSwitchData;
import com.nltecklib.protocol.li.logic.LogicDeviceProtectData;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.MatchState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.li.logic.LogicFaultCheckData;
import com.nltecklib.protocol.li.logic.LogicFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKCalculateData;
import com.nltecklib.protocol.li.logic.LogicHKCalibrateData;
import com.nltecklib.protocol.li.logic.LogicHKFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKOperationData;
import com.nltecklib.protocol.li.logic.LogicHKOperationData.SwitchState;
import com.nltecklib.protocol.li.logic.LogicLabPoleData;
import com.nltecklib.protocol.li.logic.LogicLabProtectData;
import com.nltecklib.protocol.li.logic.LogicPickupData;
import com.nltecklib.protocol.li.logic.LogicPoleData;
import com.nltecklib.protocol.li.logic.LogicStateData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * 虚拟逻辑板数据连接器
 * 
 * @author Administrator
 *
 */
public class VirtualLogicConnector implements LogicConnector {

	private LogicBoard logicBoard;
	private LogicPoleData logicPole = new LogicPoleData();
	private LogicDeviceProtectData deviceProtect = new LogicDeviceProtectData();
	private LogicState state = LogicState.UDT; // 待测模式
	// private LogicProcedureData procedureStepData = new LogicProcedureData(); //
	// 流程步次信息
	private Map<Integer, Boolean> chnSwitchStates = new HashMap<Integer, Boolean>(); // true打开

	private int pickupCount = 0; // 采集次数

	private WorkMode workMode;
	private double specialVoltage;
	private double specialCurrent;
	private double overThreshold;

	private List<RandomDriverDataProducer> producers = new ArrayList<RandomDriverDataProducer>();

	public VirtualLogicConnector(LogicBoard logicBoard) {

		this.logicBoard = logicBoard;
		// 默认通道全部打开
		for (int i = 0; i < logicBoard.getDrivers().size(); i++) {

			for (int j = 0; j < MainBoard.startupCfg.getDriverChnCount(); j++) {

				int chnIndex = i * MainBoard.startupCfg.getDriverChnCount() + j;
				chnSwitchStates.put(chnIndex, true);
			}
			producers.add(new RandomDriverDataProducer(logicBoard.getLogicIndex(), i));
		}

	}

	@Override
	public boolean configPole(LogicPoleData lpd, StringBuffer err) {

		this.logicPole = lpd;
		return true;
	}

	@Override
	public boolean configDeviceProtect(LogicDeviceProtectData ldpd, StringBuffer err) {

		this.deviceProtect = ldpd;
		return true;
	}

	@Override
	public boolean configChnSwitchState(LogicChnSwitchData lcsd, StringBuffer err) {

		List<Byte> states = lcsd.getChannelStates();

		// System.out.println(lcsd.getChannelStates());

		int chnCountInDriver = MainBoard.startupCfg.getDriverChnCount();
		for (int n = 0; n < states.size(); n++) {

			// 每个字节代表8个通道
			for (int m = 0; m < 8; m++) {

				int chnIndexInUnit = m + n * 8;

				if ((states.get(n) & (0x01 << m)) == 0) {

					producers.get(chnIndexInUnit / chnCountInDriver).switchChnsState(chnIndexInUnit % chnCountInDriver,
							true);

				} else {

					producers.get(chnIndexInUnit / chnCountInDriver).switchChnsState(chnIndexInUnit % chnCountInDriver,
							false);

				}
			}

		}
		return true;
	}

	@Override
	public boolean configStartup(LogicStateData lsd, StringBuffer err) {

		state = lsd.getStartupState();
		for (RandomDriverDataProducer p : producers) {

			p.changeState(lsd);
		}
		return true;
	}

	/**
	 * 从逻辑板获取单板数据
	 */
	@Override
	public LogicPickupData pickup(int driverIndex) {

		LogicPickupData lpd = producers.get(driverIndex).pickup();
		return lpd;
	}

	@Override
	public boolean stopChn(LogicChnStopData lcsd, StringBuffer err) {

		producers.get(lcsd.getDriverIndex()).stopChn(lcsd.getSelectChns());
		return true;
	}

	@Override
	public LogicCalProcessData getLogicCalProcess(LogicCalProcessData lcpd, StringBuffer err) {

		return null;
	}

	@Override
	public boolean setLogicCalProcess(LogicCalProcessData lcpd, StringBuffer err) {

		return false;
	}

	@Override
	public boolean writeCalFlash(LogicFlashWriteData lfwd, StringBuffer err) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearBuff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRecvTimeout(int timeout) {
		// TODO Auto-generated method stub

	}

	@Override
	public LogicFlashWriteData readCalFlash(int logicIndex, int chnIndexInLogic, StringBuffer err) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogicCalMatchData readBaseVoltage(int logincIndex, int chnIndexInLogic) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean configBaseVoltage(int logicIndex, int chnIndexInLogic, Pole pole, MatchState ms) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LogicCalculateData readCalculateData(int logicIndex, int chnIndex) throws IOException {

		return null;
	}

	@Override
	public boolean startChn(LogicChnStartData lcsd) throws AlertException {

		producers.get(lcsd.getDriverIndex()).resumeChn(lcsd.getChnFlag(), lcsd.getWorkMode(), lcsd.getProgramVoltage(),
				lcsd.getProgramCurrent(), lcsd.getEndThreshold());

		return true;
	}

	@Override
	public void startAllChns(int logicIndex, WorkMode workMode, double voltage, double current, double threshold)
			throws AlertException {

		for (RandomDriverDataProducer producer : producers) {

			producer.operate(LogicState.WORK, workMode, voltage, current, threshold);
		}

	}

	@Override
	public void stopAllChns(int logicIndex) throws AlertException {

		for (RandomDriverDataProducer producer : producers) {

			producer.operate(LogicState.UDT, WorkMode.UDT, 0, 0, 0);
		}

	}

	@Override
	public boolean writeCalculateData(LogicCalculateData lcd) throws AlertException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateAllDriverTemp(double temp1, double temp2) {

		for (RandomDriverDataProducer rddp : producers) {

			rddp.setMainTemp(temp1);
			rddp.setSubTemp(temp2);
		}
	}

	@Override
	public LogicFaultCheckData readFaultCheckData(int logicIndex) throws AlertException {

		return new LogicFaultCheckData();
	}

	@Override
	public void enableModule(int chnIndexInLogic, boolean open) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean configSinglePole(LogicLabPoleData llpd) throws AlertException {

		return true;
	}

	@Override
	public boolean configSingleProtect(LogicLabProtectData llpd) throws AlertException {

		return true;
	}

	@Override
	public boolean configHKLogicCalibration(LogicHKCalibrateData data, StringBuffer err) throws AlertException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LogicHKCalibrateData readLogicHKCalibration(LogicHKCalibrateData data, StringBuffer err)
			throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean writeHKCalculation(LogicHKCalculateData lcd) throws AlertException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LogicHKCalculateData readHKCalculation(int logicIndex, int chnIndex) throws AlertException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean writeHKCalFlash(LogicHKFlashWriteData lfwd, StringBuffer err) throws AlertException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LogicHKFlashWriteData readHKCalFlash(int logicIndex, int chnIndexInLogic, StringBuffer err) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean operateHkChns(LogicHKOperationData lod) throws AlertException {

		if (workMode == null && lod.getSwitchState() == SwitchState.ON) {

			throw new AlertException(AlertCode.LOGIC, "没有配置工作模式，无法启动测试!");
		}
		if (lod.getSwitchState() == SwitchState.ON) {

			producers.get(lod.getDriverIndex()).resumeChn(lod.getSelectFlag(), workMode, specialVoltage, specialCurrent,
					overThreshold);
		} else {

			producers.get(lod.getDriverIndex()).stopChn(lod.getSelectFlag());
		}

		return true;
	}

	@Override
	public boolean configHKProcedureStep(WorkMode workMode, double specialVoltage, double specialCurrent,
			double threshold) throws AlertException {
        
//		System.out.println("config hk procedure:" + workMode + ",special voltage = " 
//		+specialVoltage + ",special current = " +specialCurrent );
		this.workMode = workMode;
		this.specialVoltage = specialVoltage;
		this.specialCurrent = specialCurrent;
		this.overThreshold = threshold;
		return true;
	}

}
