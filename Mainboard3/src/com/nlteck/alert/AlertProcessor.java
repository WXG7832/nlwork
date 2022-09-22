package com.nlteck.alert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.Customer;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.AlertData;
import com.nltecklib.protocol.li.main.CCProtectData;
import com.nltecklib.protocol.li.main.CVProtectData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.DCProtectData;
import com.nltecklib.protocol.li.main.FirstCCProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.OfflinePickupData;
//import com.nltecklib.protocol.li.main.OverChargeProtectData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.li.main.SlpProtectData;
import com.nltecklib.protocol.li.main.StartEndCheckData;


/**
 * 报警管理器
 * 
 * @author Administrator
 *
 */
public abstract class AlertProcessor {

	protected AlertCode code; // 报警代码
	protected String message; // 报警详细信息
	public static final double MIN_TOUCH_CURRENT = 50; // 最小接触电阻计算电流阀值

	public AlertCode getCode() {
		return code;
	}

	public void setCode(AlertCode code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 分析报警事件，如产生报警则产生报警异常
	 * 
	 * @param chn
	 * @throws AlertException
	 */
	public abstract AlertData processAlert(Channel chn) throws AlertException;


	

	/**
	 * 回检板压差或阻值差报警
	 * 
	 * @param chn
	 * @param chnData
	 * @param protect
	 * @return
	 * @throws AlertException
	 */
	public static AlertData processCheckOffsetAlert(Channel chn, ChannelData chnData, ChnData rawData,
			CheckVoltProtectData protect, List<ChannelData> caches) throws AlertException {

		AlertData alert = new AlertData();
		if (chn.getState() != ChnState.ALERT) {

			if (protect.getResisterOffset() > 0 && chnData.getCurrent() >= MIN_TOUCH_CURRENT) {

				// 接触电阻
				double r = Math
						.abs((MainBoard.startupCfg.getProtocol().useLogicPowerVoltage ? chnData.getVoltage()
								: chnData.getDeviceVoltage()) - chnData.getPowerVoltage())
						/ (chnData.getCurrent() / 1000);
				if (r > protect.getResisterOffset() && chn.isResisterOffsetAlert()) {

					alert.setAlertCode(AlertCode.TOUCH);
					alert.setChnIndex(chn.getDeviceChnIndex());
					alert.setDate(new Date());
					alert.setUnitIndex(0);
					alert.setAlertInfo("回检保护[接触电阻：" + CommonUtil.formatNumber(r, 1) + "mΩ,已超过设定值:"
							+ protect.getResisterOffset() + "mΩ]");
					return alert;
				} else if (r > protect.getResisterOffset()) {

					chn.setResisterOffsetAlert(true); // 第一次电阻值偏差不产生报警，但记录
				} else {

					chn.setResisterOffsetAlert(false); // 消除上次的电阻偏差报警
				}
			}

		}

		if (chnData.getState() == ChnState.RUN && (chnData.getWorkMode() == WorkMode.CCC
				|| chnData.getWorkMode() == WorkMode.CVC || chnData.getWorkMode() == WorkMode.CCD)) {

			if (chnData.getCurrent() <= MainBoard.startupCfg.getMinRunningCurrent()  /*&& rawData.getState() != LogicEnvironment.ChnState.COMPLETE */) {

				alert.setAlertCode(AlertCode.CUR_LOWER);
				alert.setChnIndex(chn.getDeviceChnIndex());
				alert.setDate(new Date());
				alert.setUnitIndex(0);
				alert.setAlertInfo(
						"电流过低保护[检测到运行电流：" + CommonUtil.formatNumber(rawData.getCurrent(), 1) + "mA,已低于或等于设定值:"
								+ MainBoard.startupCfg.getMinRunningCurrent() + "mA,状态=" + rawData.getState() + "]");
				return alert;
			}
			Step step = chn.getProcedureStep(chn.getStepIndex());

			if (step != null && step.timeProtect && step.overTime > 0 && chnData.getTimeStepSpend() > step.overTime) {

				alert.setAlertCode(AlertCode.TIME_OVER);
				alert.setChnIndex(chn.getDeviceChnIndex());
				alert.setDate(new Date());
				alert.setUnitIndex(0);
				alert.setAlertInfo("步次时间保护[检测到步次" + chn.getStepIndex() + "已运行" + chn.getStepElapseMiliseconds() / 1000
						+ "s,超过时间保护值" + step.overTime + "s");
				return alert;

			}

		}

		return null;
	}

	/**
	 * 流程首尾电压容量检测
	 * 
	 * @param chn
	 * @param chnData
	 * @param secd
	 * @return
	 */
	public static AlertData processStartEndCheckAlert(Channel chn, ChannelData chnData, StartEndCheckData secd) {

		if (chn.getState() == ChnState.ALERT) {

			return null;
		}
		if (chn.getStepIndex() == 1 && chn.getLoopIndex() == 1) {

			if (chn.getPreState() != ChnState.RUN) {

				if (secd.getStartVoltageLower() > 0 && chnData.getVoltage() < secd.getStartVoltageLower()) {

					AlertData alert = new AlertData();
					alert.setAlertCode(AlertCode.VOLT_LOWER);
					alert.setChnIndex(chn.getDeviceChnIndex());
					alert.setDate(new Date());
					alert.setUnitIndex(chn.getControlUnitIndex());
					alert.setAlertInfo("流程首尾保护[初始电压：" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,低于设定下限值("
							+ secd.getStartVoltageLower() + "mV)]");
					return alert;

				} else if (secd.getStartVoltageUpper() > 0 && chnData.getVoltage() > secd.getStartVoltageUpper()) {

					AlertData alert = new AlertData();
					alert.setAlertCode(AlertCode.VOLT_UPPER);
					alert.setChnIndex(chn.getDeviceChnIndex());
					alert.setDate(new Date());
					alert.setUnitIndex(chn.getControlUnitIndex());
					alert.setAlertInfo("流程首尾保护[初始电压：" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,高于设定上限值("
							+ secd.getStartVoltageUpper() + "mV)]");
					return alert;
				}
			}
		}

		// 结束电压判断
		if (chn.getState() == ChnState.COMPLETE && chn.getPreState() == ChnState.RUN) {

			if (secd.getEndVoltageLower() > 0 && chnData.getVoltage() < secd.getEndVoltageLower()) {

				AlertData alert = new AlertData();
				alert.setAlertCode(AlertCode.VOLT_LOWER);
				alert.setChnIndex(chn.getDeviceChnIndex());
				alert.setDate(new Date());
				alert.setUnitIndex(chn.getControlUnitIndex());
				alert.setAlertInfo("流程首尾保护[结束电压：" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,低于设定下限值("
						+ secd.getEndVoltageLower() + "mV)]");
				return alert;

			} else if (secd.getEndVoltageUpper() > 0 && chnData.getVoltage() > secd.getEndVoltageUpper()) {

				AlertData alert = new AlertData();
				alert.setAlertCode(AlertCode.VOLT_UPPER);
				alert.setChnIndex(chn.getDeviceChnIndex());
				alert.setDate(new Date());
				alert.setUnitIndex(chn.getControlUnitIndex());
				alert.setAlertInfo("流程首尾保护[结束电压：" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,高于设定上限值("
						+ secd.getEndVoltageUpper() + "mV)]");
				return alert;
			}

			// 结束容量判断
			if (secd.getEndCapacityLower() > 0 && chnData.getAccumulateCapacity() < secd.getEndCapacityLower()) {

				AlertData alert = new AlertData();
				alert.setAlertCode(AlertCode.CAPACITY_UPPER);
				alert.setChnIndex(chn.getDeviceChnIndex());
				alert.setDate(new Date());
				alert.setUnitIndex(chn.getControlUnitIndex());
				alert.setAlertInfo("流程首尾保护[结束容量："
						+ CommonUtil.formatNumber(chnData.getAccumulateCapacity(),
								MainBoard.startupCfg.getProtocol().capacityResolution)
						+ "mAh,低于设定下限值(" + secd.getEndCapacityLower() + "mAh)]");
				return alert;

			} else if (secd.getEndCapacityUpper() > 0 && chnData.getAccumulateCapacity() > secd.getEndCapacityUpper()) {

				AlertData alert = new AlertData();
				alert.setAlertCode(AlertCode.CAPACITY_UPPER);
				alert.setChnIndex(chn.getDeviceChnIndex());
				alert.setDate(new Date());
				alert.setUnitIndex(chn.getControlUnitIndex());
				alert.setAlertInfo("流程首尾保护[结束容量："
						+ CommonUtil.formatNumber(chnData.getAccumulateCapacity(),
								MainBoard.startupCfg.getProtocol().capacityResolution)
						+ "mAh,高于设定上限值(" + secd.getEndCapacityUpper() + "mAh)]");
				return alert;

			}

		}

		return null;
	}
	
	/**
	 * 获取电流精度
	 * @return
	 */
	private static double  getCurrentPrecision(double current) {
		
		double precision = MainBoard.startupCfg.getCurrentPrecide();
		if(MainBoard.startupCfg.getDoublePrecision().use) {
			
			if(current <= MainBoard.startupCfg.getDoublePrecision().threshold) {
				
				precision = MainBoard.startupCfg.getDoublePrecision().precision;
			}
		}
		return precision;
		
	}

	/**
	 * 恒定偏差报警
	 * 
	 * @param chn
	 * @param chnData
	 * @return
	 */
	public static AlertData processConstantOffsetAlert(Channel chn, ChannelData chnData ) {

		if (chn.getState() == ChnState.RUN) {
			
			if(chn.getLeadStepCount() > 0 || MainBoard.startupCfg.getCustomer() == Customer.HK) {
				
				return null; //在启动或恢复时暂不做任何保护
			}
			Step step = chn.getProcedureStep(chnData.getStepIndex());
			if(step == null) {
				
				return null;
			}
			AlertData alert = new AlertData();
			switch (step.workMode) {
            
			case CC_CV:
				
				
				if(chn.isCvInCcCvMode(chnData)) {
					
					if (Math.abs(step.specialVoltage - chnData.getVoltage()) > MainBoard.startupCfg.getVoltagePreCide()
							* 3) {

						alert.setAlertCode(AlertCode.VOLT_WAVE);
						alert.setChnIndex(chn.getDeviceChnIndex());
						alert.setDate(new Date());
						alert.setUnitIndex(chn.getControlUnitIndex());
						alert.setAlertInfo("恒压保护[当前电压 " + CommonUtil.formatNumber(chnData.getVoltage(), 1)
								+ "mV未稳定在恒压值(" + CommonUtil.formatNumber(step.specialVoltage, 1) + "mV)范围内]");
						return alert;
					}
					
				} else {
					
					if (Math.abs(step.specialCurrent - chnData.getCurrent()) > getCurrentPrecision(chnData.getCurrent())
							* 3) {

						alert.setAlertCode(AlertCode.CURR_WAVE);
						alert.setChnIndex(chn.getDeviceChnIndex());
						alert.setDate(new Date());
						alert.setUnitIndex(chn.getControlUnitIndex());
						alert.setAlertInfo("恒流保护[当前电流 " + CommonUtil.formatNumber(chnData.getCurrent(), 1) + "mA未稳定在恒流值("
								+ CommonUtil.formatNumber(step.specialCurrent, 1) + "mA)范围内]");

						return alert;
					}
				}
				break;
			case CCC:
			case CCD:
				if (Math.abs(step.specialCurrent - chnData.getCurrent()) > getCurrentPrecision(chnData.getCurrent())
						* 3) {

					alert.setAlertCode(AlertCode.CURR_WAVE);
					alert.setChnIndex(chn.getDeviceChnIndex());
					alert.setDate(new Date());
					alert.setUnitIndex(chn.getControlUnitIndex());
					alert.setAlertInfo("恒流保护[当前电流 " + CommonUtil.formatNumber(chnData.getCurrent(), 1) + "mA未稳定在恒流值("
							+ CommonUtil.formatNumber(step.specialCurrent, 1) + "mA)范围内]");

					return alert;
				}
				break;
			case CVC:
				// 当cv从暂停中恢复时不触发保护
				if (chn.isReadyVoltageInCvModeResume(chnData.getVoltage())) {
					if (Math.abs(step.specialVoltage - chnData.getVoltage()) > MainBoard.startupCfg.getVoltagePreCide()
							* 3) {

						alert.setAlertCode(AlertCode.VOLT_WAVE);
						alert.setChnIndex(chn.getDeviceChnIndex());
						alert.setDate(new Date());
						alert.setUnitIndex(chn.getControlUnitIndex());
						alert.setAlertInfo("恒压保护[当前电压 " + CommonUtil.formatNumber(chnData.getVoltage(), 1)
								+ "mV未稳定在恒压值(" + CommonUtil.formatNumber(step.specialVoltage, 1) + "mV)范围内]");
						return alert;
					}
				}
				break;
			

			}
		}

		return null;
	}

	/**
	 * 首步次结束电压范围检测
	 * 
	 * @param tpd
	 * @return
	 */
	public static AlertData processFirstStepAlert(Channel chn, ChannelData chnData, FirstCCProtectData tpd) {

		// System.out.println("chnData.getTimeStepSpend() = " +
		// chnData.getTimeStepSpend() + "," + tpd);
		if (chn.getState() != ChnState.ALERT && tpd.isNeedCheck()
				&& (chnData.getVoltage() < tpd.getVoltLower() || chnData.getVoltage() > tpd.getVoltUpper())
				&& chnData.getTimeStepSpend() > tpd.getTimeOut()) {

			AlertData alert = new AlertData();
			alert.setAlertCode(AlertCode.TOUCH);
			alert.setChnIndex(chn.getDeviceChnIndex());
			alert.setDate(new Date());
			alert.setUnitIndex(chn.getControlUnitIndex());
			alert.setAlertInfo(
					"首步次CC保护[步次运行时间" + tpd.getTimeOut() + "s后检测电压：" + CommonUtil.formatNumber(chnData.getVoltage(), 1)
							+ "mV,不在设定范围(" + tpd.getVoltLower() + "-" + tpd.getVoltUpper() + ")]");
			return alert;
		}

		return null;
	}
	/**
	 * 处理cc和cv工作模式
	 * @author  wavy_zheng
	 * 2020年3月11日
	 * @param chn
	 * @param ccProtect
	 * @param cvProtect
	 * @param caches
	 * @param special
	 * @return
	 */
	public static AlertData processCcCvProtectAlert(Channel chn , CCProtectData ccProtect , CVProtectData cvProtect ,List<ChannelData> caches,boolean special) {
		
		List<ChannelData> ccList = new ArrayList<ChannelData>();
		List<ChannelData> cvList = new ArrayList<ChannelData>();
		for(ChannelData cd : caches) {
			
			if(!chn.isCvInCcCvMode(cd)) {
			   ccList.add(cd);
			} else {
				
				cvList.add(cd);
			}
		}
		if(!ccList.isEmpty()) {
			
			AlertData alert = processCCProtectAlert(chn, ccProtect, ccList, special);
			if(alert != null) {
				
				return alert;
			}
		}
		if(!cvList.isEmpty()) {
			
			AlertData alert = processCVProtectAlert(chn, cvProtect, cvList);
            if(alert != null) {
				
				return alert;
			}
		}
		
		
		return null;
	}

	/**
	 * 处理CC报警保护
	 * 
	 * @param chn
	 * @param protect
	 * @param overProtect
	 * @param caches
	 * @param offcaches
	 * @param special
	 *            true表示只检查上下限
	 * @return
	 */
	public static AlertData processCCProtectAlert(Channel chn, CCProtectData protect, List<ChannelData> caches,
			boolean special) {

		if (caches.size() == 0) {
			return null;
		}

		ChannelData chnData = caches.get(caches.size() - 1);

		AlertData alert = new AlertData();
		alert.setAlertCode(AlertCode.NORMAL);
		alert.setChnIndex(chn.getDeviceChnIndex());
		alert.setUnitIndex(chn.getControlUnitIndex());

		// 检查电压上限保护值
		if (protect.getVoltUpper() > 0 && chnData.getVoltage() > protect.getVoltUpper()) {

			alert.setAlertCode(AlertCode.VOLT_UPPER);
			alert.setAlertInfo("CC保护[检测电压" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV超过CC电压保护上限值"
					+ protect.getVoltUpper() + "mV]");
			return alert;
		}
		// 检查电压下限保护值
		if (protect.getVoltLower() > 0 && chnData.getVoltage() < protect.getVoltLower()) {

			alert.setAlertCode(AlertCode.VOLT_LOWER);
			alert.setAlertInfo("CC保护[检测电压" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV低于CC电压保护下限值"
					+ protect.getVoltLower() + "mV]");
			return alert;
		}

		if (special) {

			return null; // cc-cv转序忽略报警
		}

		if (caches.size() > 1 ) {
			// 电流超差保护
			if (protect.getCurrOffsetVal() > 0 || protect.getCurrOffsetPercent() > 0) {

				double offset = protect.getCurrOffsetPercent() * 0.01 * chnData.getCurrent() >= protect
						.getCurrOffsetVal() ? protect.getCurrOffsetPercent() * 0.01 * chnData.getCurrent()
								: protect.getCurrOffsetVal();

				if (Math.abs(chnData.getCurrent() - caches.get(caches.size() - 2).getCurrent()) > offset) {

					alert.setAlertCode(AlertCode.CURR_WAVE);
					alert.setAlertInfo(
							"CC保护[电流超差保护," + CommonUtil.formatNumber(caches.get(caches.size() - 2).getCurrent(), 1)
									+ "mA -->" + CommonUtil.formatNumber(chnData.getCurrent(), 1) + "mA,超设定值:"
									+ CommonUtil.formatNumber(offset, 1) + "mA]");
					// 先将报警前的最后一个数据上传给PC，这里使用离线数据上传
					// OfflinePickupData opd = new OfflinePickupData();
					// try {
					// // 复制一个数据;注意不可重用一个数据，因为接下去这个数据就要被修改为报警数据
					// ChannelData alertChnData = (ChannelData) chnData.clone();
					// opd.appendOfflineData(alertChnData);
					// } catch (CloneNotSupportedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// opd.setUnitIndex(chn.getLogicIndex());
					// opd.setChnIndex(chn.getLogicChnIndex());
					// chn.getDriverBoard().getLogicBoard().getMainBoard().pushSendQueue(new
					// ResponseDecorator(opd, true));

					return alert;
				}
			}
			// 电压超差保护2000mAh以下
			if (chnData.getAccumulateCapacity() < 2000
					&& (protect.getVoltWaveValUnder2000() > 0 || protect.getVoltWaveValUnder2000() > 0)) {

				double offset = protect.getVoltWaveValUnder2000() >= protect.getVoltWavePercentUnder2000() * 0.01
						* chnData.getVoltage() ? protect.getVoltWaveValUnder2000()
								: protect.getVoltWavePercentUnder2000() * 0.01 * chnData.getVoltage();
				// System.out.println("caches.get(caches.size() - 2).getVoltage() =" +
				// caches.get(caches.size() - 2).getVoltage() + "->" + chnData.getVoltage() +
				// ",offset = " + offset);
				if (Math.abs(chnData.getVoltage() - caches.get(caches.size() - 2).getVoltage()) > offset) {

					alert.setAlertCode(AlertCode.VOLT_WAVE);
					alert.setAlertInfo("CC保护[电压超差保护2000mAh以下,"
							+ CommonUtil.formatNumber(caches.get(caches.size() - 2).getVoltage(), 1) + "mV -->"
							+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,超设定值:" + offset + "mV]");
					return alert;
				}
			} else if (chnData.getAccumulateCapacity() >= 2000
					&& (protect.getVoltWaveValAbove2000() > 0 || protect.getVoltWaveValAbove2000() > 0)) {

				double offset = protect.getVoltWaveValAbove2000() >= protect.getVoltWavePercentAbove2000() * 0.01
						* chnData.getVoltage() ? protect.getVoltWaveValAbove2000()
								: protect.getVoltWavePercentAbove2000() * 0.01 * chnData.getVoltage();
				if (Math.abs(chnData.getVoltage() - caches.get(caches.size() - 2).getVoltage()) > offset) {

					alert.setAlertCode(AlertCode.VOLT_WAVE);
					alert.setAlertInfo("CC保护[电压超差保护2000mAh以上,"
							+ CommonUtil.formatNumber(caches.get(caches.size() - 2).getVoltage(), 1) + "mV -->"
							+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,超设定值:" + offset + "mV]");
					return alert;
				}
			}
		}

		// 电压上升速率保护
		if (protect.getVoltAscUnitSeconds() > 0 && caches.size() > 1) {

			for (int i = caches.size() - 2; i >= 0; i--) {

				if (chnData.getDate().getTime() - caches.get(i).getDate().getTime() >= protect.getVoltAscUnitSeconds()
						* 1000) {

					if (protect.getVoltAscValUpper() > 0
							&& chnData.getVoltage() - caches.get(i).getVoltage() > protect.getVoltAscValUpper()) {

						alert.setAlertCode(AlertCode.VOLT_WAVE);
						alert.setAlertInfo("CC保护[电压上升斜率保护," + protect.getVoltAscUnitSeconds() + "s内"
								+ CommonUtil.formatNumber(caches.get(i).getVoltage(), 1) + "mV -> "
								+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV超设定最大上升电压幅度"
								+ CommonUtil.formatNumber(protect.getVoltAscValUpper(), 1) + "mV]");
						return alert;
					}
					if (protect.getVoltAscValLower() > 0
							&& chnData.getVoltage() - caches.get(i).getVoltage() < protect.getVoltAscValLower()) {

						alert.setAlertCode(AlertCode.VOLT_WAVE);
						alert.setAlertInfo("CC保护[电压上升斜率保护," + protect.getVoltAscUnitSeconds() + "s内"
								+ CommonUtil.formatNumber(caches.get(i).getVoltage(), 1) + "mV -> "
								+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV低于设定最小上升电压幅度"
								+ CommonUtil.formatNumber(protect.getVoltAscValLower(), 1) + "mV]");
						return alert;
					}

					break;
				}
			}

		}

		// 容量保护
		if (protect.getCapacityUpper() > 0 && chnData.getCapacity() >= protect.getCapacityUpper()) {

			alert.setAlertCode(AlertCode.CAPACITY_UPPER);
			alert.setAlertInfo("CC保护[容量超限保护,当前步次累计容量" + CommonUtil.formatNumber(chnData.getCapacity(), 1) + "mAh,超过设定值"
					+ protect.getCapacityUpper() + "mAh]");
			return alert;

		}
		// 时间保护
		if (protect.getMinuteUpper() > 0 && chnData.getTimeStepSpend() > protect.getMinuteUpper() * 60) {

			alert.setAlertCode(AlertCode.TIME_OVER);
			alert.setAlertInfo("CC保护[时间超限保护,当前步次累计时间" + CommonUtil.formatTime(chnData.getTimeStepSpend()) + ",超过设定值"
					+ CommonUtil.formatTime(protect.getMinuteUpper() * 60) + "]");
			return alert;
		}

		// 检查连续电压下降个数
		if (protect.getVoltDescCount() > 0 && protect.getVoltDescCount() + 1 <= caches.size()) {

			boolean continueDescend = true;
			double base = chnData.getVoltage();
			StringBuffer info = new StringBuffer("CC保护[电压发生连续下降");
			for (int i = caches.size() - 1 - protect.getVoltDescCount(); i < caches.size(); i++) {

				if (i < caches.size() - 1) {
					if (caches.get(i).getVoltage() - caches.get(i + 1).getVoltage() < protect.getVoltDescVal()) {

						continueDescend = false;
						break;
					}
					info.append(CommonUtil.formatNumber(caches.get(i).getVoltage(), 1) + "mV->");
				} else {

					info.append(CommonUtil.formatNumber(caches.get(i).getVoltage(), 1) + "mV");
				}

			}
			if (continueDescend) {

				alert.setAlertCode(AlertCode.VOLT_WAVE);
				alert.setAlertInfo(info.append(",超过设定次数" + protect.getVoltDescCount() + "]").toString());
				return alert;
			}

		}

		return null;
	}

	/**
	 * 处理DC报警
	 * 
	 * @param chn
	 * @param protect
	 * @param overProtect
	 * @param caches
	 * @param offcaches
	 * @return
	 */
	public static AlertData processDCProtectAlert(Channel chn, DCProtectData protect, List<ChannelData> caches) {

		if (caches.size() == 0) {
			return null;
		}
		AlertData alert = new AlertData();
		alert.setAlertCode(AlertCode.NORMAL);
		alert.setChnIndex(chn.getDeviceChnIndex());
		alert.setUnitIndex(chn.getControlUnitIndex());

		ChannelData chnData = caches.get(caches.size() - 1); // 最新缓存数据

		// 检查电压上限保护值
		if (protect.getVoltUpper() > 0 && chnData.getVoltage() > protect.getVoltUpper()) {

			alert.setAlertCode(AlertCode.VOLT_UPPER);
			alert.setAlertInfo("DC保护[检测电压" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV超过DC电压保护上限值"
					+ protect.getVoltUpper() + "mV]");
			return alert;
		}
		// 检查电压下限保护值
		if (protect.getVoltLower() > 0 && chnData.getVoltage() < protect.getVoltLower()) {

			alert.setAlertCode(AlertCode.VOLT_LOWER);
			alert.setAlertInfo("DC保护[检测电压" + CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV低于DC电压保护下限值"
					+ protect.getVoltLower() + "mV]");
			return alert;
		}

		if (caches.size() > 1) {
			// 电流超差保护
			if (protect.getCurrOffsetVal() > 0 || protect.getCurrOffsetPercent() > 0) {

				double offset = protect.getCurrOffsetPercent() * 0.01 * chnData.getCurrent() >= protect
						.getCurrOffsetVal() ? protect.getCurrOffsetPercent() * 0.01 * chnData.getCurrent()
								: protect.getCurrOffsetVal();
				// System.out.println("caches.get(caches.size() - 1).getCurrent() = " +
				// caches.get(caches.size() - 1).getCurrent() + "->" + caches.get(caches.size()
				// - 2).getCurrent() + ",offset = " + offset);
				if (Math.abs(caches.get(caches.size() - 1).getCurrent()
						- caches.get(caches.size() - 2).getCurrent()) > offset) {

					alert.setAlertCode(AlertCode.CURR_WAVE);
					alert.setAlertInfo(
							"DC保护[电流超差保护," + CommonUtil.formatNumber(caches.get(caches.size() - 2).getCurrent(), 1)
									+ "mA -->" + CommonUtil.formatNumber(caches.get(caches.size() - 1).getCurrent(), 1)
									+ "mA,超设定值:" + offset + "mA]");
					// OfflinePickupData opd = new OfflinePickupData();
					// try {
					// // 复制一个数据;注意不可重用一个数据，因为接下去这个数据就要被修改为报警数据
					// ChannelData alertChnData = (ChannelData) chnData.clone();
					// opd.appendOfflineData(alertChnData);
					// } catch (CloneNotSupportedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// opd.setUnitIndex(chn.getLogicIndex());
					// opd.setChnIndex(chn.getLogicChnIndex());
					// chn.getDriverBoard().getLogicBoard().getMainBoard().pushSendQueue(new
					// ResponseDecorator(opd, true));

					return alert;
				}
			}
			// 电压超差保护2000mAh以下
			if (chnData.getAccumulateCapacity() < 2000
					&& (protect.getVoltWaveValUnder2000() > 0 || protect.getVoltWaveValUnder2000() > 0)) {

				double offset = protect.getVoltWaveValUnder2000() >= protect.getVoltWavePercentUnder2000() * 0.01
						* chnData.getVoltage() ? protect.getVoltWaveValUnder2000()
								: protect.getVoltWavePercentUnder2000() * 0.01 * chnData.getVoltage();
				if (Math.abs(chnData.getVoltage() - caches.get(caches.size() - 2).getVoltage()) > offset) {

					alert.setAlertCode(AlertCode.VOLT_WAVE);
					alert.setAlertInfo("DC保护[电压超差保护2000mAh以下,"
							+ CommonUtil.formatNumber(caches.get(caches.size() - 2).getVoltage(), 1) + "mV -->"
							+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,超设定值:" + offset + "mV]");
					return alert;
				}
			} else if (chnData.getAccumulateCapacity() >= 2000
					&& (protect.getVoltWaveValAbove2000() > 0 || protect.getVoltWaveValAbove2000() > 0)) {

				double offset = protect.getVoltWaveValAbove2000() >= protect.getVoltWavePercentAbove2000() * 0.01
						* chnData.getVoltage() ? protect.getVoltWaveValAbove2000()
								: protect.getVoltWavePercentAbove2000() * 0.01 * chnData.getVoltage();
				if (Math.abs(chnData.getVoltage() - caches.get(caches.size() - 2).getVoltage()) > offset) {

					alert.setAlertCode(AlertCode.VOLT_WAVE);
					alert.setAlertInfo("DC保护[电压超差保护2000mAh以上,"
							+ CommonUtil.formatNumber(caches.get(caches.size() - 2).getVoltage(), 1) + "mV -->"
							+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,超设定值:" + offset + "mV]");
					return alert;
				}
			}
		}

		// 电压下降速率保护
		if (protect.getVoltDescUnitSeconds() > 0 && caches.size() > 1) {

			for (int i = caches.size() - 2; i >= 0; i--) {

				// 时间跨度
				if (chnData.getDate().getTime() - caches.get(i).getDate().getTime() >= protect.getVoltDescUnitSeconds()
						* 1000) {

					if (protect.getVoltDescValUpper() > 0
							&& caches.get(i).getVoltage() - chnData.getVoltage() > protect.getVoltDescValUpper()) {

						alert.setAlertCode(AlertCode.VOLT_WAVE);
						alert.setAlertInfo("DC保护[电压下降速率保护," + protect.getVoltDescUnitSeconds() + "s内"
								+ caches.get(i).getVoltage() + "mV->" + chnData.getVoltage() + "mV,超设定最大下降电压幅度"
								+ protect.getVoltDescValUpper() + "mV]");
						return alert;
					}
					if (protect.getVoltDescValLower() > 0
							&& caches.get(i).getVoltage() - chnData.getVoltage() < protect.getVoltDescValLower()) {

						alert.setAlertCode(AlertCode.VOLT_WAVE);
						alert.setAlertInfo("DC保护[电压下降速率保护," + protect.getVoltDescUnitSeconds() + "s内"
								+ caches.get(i).getVoltage() + "mV->" + chnData.getVoltage() + "mV,低于设定最小下降电压幅度"
								+ protect.getVoltDescValLower() + "mV]");
						return alert;
					}

					break;
				}
			}

		}
		// 容量保护
		if (protect.getCapacityUpper() > 0 && chnData.getCapacity() >= protect.getCapacityUpper()) {

			alert.setAlertCode(AlertCode.CAPACITY_UPPER);
			alert.setAlertInfo("DC保护[容量超限保护,当前步次累计容量" + CommonUtil.formatNumber(chnData.getCapacity(), 1) + "mAh,超过设定值"
					+ protect.getCapacityUpper() + "mAh]");
			return alert;

		}
		// 时间保护
		if (protect.getMinuteUpper() > 0 && chnData.getTimeStepSpend() > protect.getMinuteUpper() * 60) {

			alert.setAlertCode(AlertCode.TIME_OVER);
			alert.setAlertInfo("DC保护[时间超限保护,当前步次累计时间" + CommonUtil.formatTime(chnData.getTimeStepSpend()) + ",超过设定值"
					+ CommonUtil.formatTime(protect.getMinuteUpper() * 60) + "]");
			return alert;
		}

		// 检查连续电压上升个数
		if (protect.getVoltAscCount() > 0 && protect.getVoltAscCount() + 1 <= caches.size()) {

			boolean continueAscend = true;
			double base = chnData.getVoltage();
			StringBuffer info = new StringBuffer("DC保护[电压发生连续上升");
			for (int i = caches.size() - 1 - protect.getVoltAscCount(); i < caches.size(); i++) {

				if (i < caches.size() - 1) {

					if (caches.get(i + 1).getVoltage() - caches.get(i).getVoltage() < protect.getVoltAscVal()) {

						continueAscend = false;
						break;
					}
					info.append(CommonUtil.formatNumber(caches.get(i).getVoltage(), 1) + "mV->");
				} else {

					info.append(CommonUtil.formatNumber(caches.get(i).getVoltage(), 1) + "mV");
				}

			}
			if (continueAscend) {

				alert.setAlertCode(AlertCode.VOLT_WAVE);
				alert.setAlertInfo(info.append(",超过设定次数" + protect.getVoltAscCount() + "]").toString());
				return alert;
			}

		}

		return null;
	}

	/**
	 * 处理CV报警模式
	 * 
	 * @param chn
	 * @param protect
	 * @param overProtect
	 * @param caches
	 * @param offcaches
	 * @return
	 */
	public static AlertData processCVProtectAlert(Channel chn, CVProtectData protect, List<ChannelData> caches) {

		if (caches.size() == 0) {
			return null;
		}

		ChannelData chnData = caches.get(caches.size() - 1);

		AlertData alert = new AlertData();
		alert.setAlertCode(AlertCode.NORMAL);
		alert.setChnIndex(chn.getDeviceChnIndex());
		alert.setUnitIndex(chn.getControlUnitIndex());

		if (protect.getCurrUpper() > 0 && chnData.getCurrent() > protect.getCurrUpper()) {

			alert.setAlertCode(AlertCode.CUR_UPPER);
			alert.setAlertInfo("CV保护[检测电流" + CommonUtil.formatNumber(chnData.getCurrent(), 1) + "mA超过电流保护上限值"
					+ protect.getCurrUpper() + "mA]");
			// OfflinePickupData opd = new OfflinePickupData();
			// try {
			// // 复制一个数据;注意不可重用一个数据，因为接下去这个数据就要被修改为报警数据
			// ChannelData alertChnData = (ChannelData) chnData.clone();
			// opd.appendOfflineData(alertChnData);
			// } catch (CloneNotSupportedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// opd.setUnitIndex(chn.getLogicIndex());
			// opd.setChnIndex(chn.getLogicChnIndex());
			// chn.getDriverBoard().getLogicBoard().getMainBoard().pushSendQueue(new
			// ResponseDecorator(opd, true));

			return alert;
		}
		// 检查电流下限保护值
		if (protect.getCurrLower() > 0 && chnData.getCurrent() < protect.getCurrLower()) {

			System.out.println(caches);
			alert.setAlertCode(AlertCode.CUR_LOWER);
			alert.setAlertInfo("CV保护[检测电流" + CommonUtil.formatNumber(chnData.getCurrent(), 1) + "mA低于电流保护下限值"
					+ protect.getCurrLower() + "mA]");
			// OfflinePickupData opd = new OfflinePickupData();
			// try {
			// // 复制一个数据;注意不可重用一个数据，因为接下去这个数据就要被修改为报警数据
			// ChannelData alertChnData = (ChannelData) chnData.clone();
			// opd.appendOfflineData(alertChnData);
			// } catch (CloneNotSupportedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// opd.setUnitIndex(chn.getLogicIndex());
			// opd.setChnIndex(chn.getLogicChnIndex());
			// chn.getDriverBoard().getLogicBoard().getMainBoard().pushSendQueue(new
			// ResponseDecorator(opd, true));
			return alert;
		}

		if (chn.isReadyVoltageInCvModeResume(chnData.getVoltage())) {
			// 电压超差波动保护
			if (caches.size() > 1 && (protect.getVoltOffsetVal() > 0 || protect.getVoltOffsetPercent() > 0)) {

				double offsetVoltage = protect.getVoltOffsetPercent() * 0.01 * chnData.getVoltage();
				double offset = offsetVoltage >= protect.getVoltOffsetVal() ? offsetVoltage
						: protect.getVoltOffsetVal();

				if (Math.abs(chnData.getVoltage() - caches.get(caches.size() - 2).getVoltage()) > offset) {

					alert.setAlertCode(AlertCode.VOLT_WAVE);
					alert.setAlertInfo("CV保护[电压超差保护,"
							+ CommonUtil.formatNumber(caches.get(caches.size() - 2).getVoltage(), 1) + "mV -->"
							+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + "mV,超设定值:" + offset + "mV]");
					return alert;
				}
			}
		}
		// 容量保护
		if (protect.getCapacityUpper() > 0 && chnData.getCapacity() >= protect.getCapacityUpper()) {

			alert.setAlertCode(AlertCode.CAPACITY_UPPER);
			alert.setAlertInfo("CV保护[容量超限保护,当前步次累计容量" + CommonUtil.formatNumber(chnData.getCapacity(), 1) + "mAh,超过设定值"
					+ protect.getCapacityUpper() + "mAh]");
			return alert;

		}
		// 时间保护
		if (protect.getMinuteUpper() > 0 && chnData.getTimeStepSpend() > protect.getMinuteUpper() * 60) {

			alert.setAlertCode(AlertCode.TIME_OVER);
			alert.setAlertInfo("CV保护[时间超限保护,当前步次累计时间" + CommonUtil.formatTime(chnData.getTimeStepSpend()) + ",超过设定值"
					+ CommonUtil.formatTime(protect.getMinuteUpper() * 60) + "]");
			return alert;
		}

		// 检查连续电流上升个数
		if (protect.getCurrAscCount() > 0 && protect.getCurrAscCount() + 1 <= caches.size()) {

			boolean continueAsc = true;
			double base = chnData.getCurrent();
			StringBuffer info = new StringBuffer("CV保护[电流发生连续上升");
			for (int i = caches.size() - 1 - protect.getCurrAscCount(); i < caches.size(); i++) {

				// 两次电流上升阀值
				if (i < caches.size() - 1) {
					if (caches.get(i + 1).getCurrent() - caches.get(i).getCurrent() < protect.getCurrAscVal()) {

						continueAsc = false;
						break;
					}
					info.append(CommonUtil.formatNumber(caches.get(i).getCurrent(), 1) + "mA->");
				} else {

					info.append(CommonUtil.formatNumber(caches.get(i).getCurrent(), 1) + "mA");
				}

			}
			if (continueAsc) {

				alert.setAlertCode(AlertCode.CURR_WAVE);
				alert.setAlertInfo(info.append(",超过设定次数" + protect.getCurrAscCount() + "]").toString());
				OfflinePickupData opd = new OfflinePickupData();
				try {
					// 复制一个数据;注意不可重用一个数据，因为接下去这个数据就要被修改为报警数据
					ChannelData alertChnData = (ChannelData) chnData.clone();
					opd.getChnDataList().add(alertChnData);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				opd.setUnitIndex(0);
				opd.setChnIndex(chn.getDeviceChnIndex());
				Context.getPcNetworkService().pushSendQueue(new ResponseDecorator(opd, true));

				return alert;
			}

		}

		return null;
	}

	/**
	 * 处理休眠报警
	 * 
	 * @param chn
	 * @param caches
	 * @param spd
	 * @return
	 */
	public static AlertData processSleepProtectAlert(Channel chn, List<ChannelData> caches, SlpProtectData spd) {

		if (spd.getVoltOffset() > 0 && caches.size() > 1) {

			if (Math.abs(caches.get(caches.size() - 1).getVoltage() - caches.get(caches.size() - 2).getVoltage()) > spd
					.getVoltOffset()) {

				AlertData alert = new AlertData();
				alert.setAlertCode(AlertCode.VOLT_WAVE);
				alert.setChnIndex(chn.getDeviceChnIndex());
				alert.setUnitIndex(chn.getControlUnitIndex());
				alert.setAlertInfo("休眠保护[检测电压发生波动,"
						+ CommonUtil.formatNumber(caches.get(caches.size() - 2).getVoltage(), 1) + "mV ->"
						+ CommonUtil.formatNumber(caches.get(caches.size() - 1).getVoltage(), 1) + "mV]");
				return alert;
			}

		}
		return null;
	}

}
