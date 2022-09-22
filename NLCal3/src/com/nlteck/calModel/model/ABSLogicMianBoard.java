package com.nlteck.calModel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nlteck.calModel.base.BaseCfgManager;
import com.nlteck.calModel.base.CalCfgManager;
import com.nlteck.calModel.base.I18N;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestDot;
import com.nlteck.service.CalboxService;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWrite2DebugData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData.CalParamData;

public abstract class ABSLogicMianBoard {
	CalboxService calboxService = new CalboxService();
	CalCfgManager calCfgManager = new CalCfgManager();
	BaseCfgManager baseCfgManager = new BaseCfgManager();
	private int uintIndex; //分区序号
	public abstract TestDot gatherCalibrateADC(TestDot dot) throws Exception ;// 获取校准ADC
	public abstract TestDot gatherCalculateADC(TestDot dot) throws Exception;// 获取计量ADC
	// 单膜片获取计量adc
	public abstract TestDot gatherCalculateADCmoudle(TestDot dot) throws Exception ;
	public void writeFlash(ChannelDO channelDO,int mouduleIndex) {

		// 驱动板flash
		Map<DriverEnvironment.CalMode, List<CalParamData>> dotMap = new HashMap<>();
		List<CalParamData> checkList = new ArrayList<>();
		List<CalParamData> check2List = new ArrayList<>();
		for (TestDot calDot : channelDO.getCalDotList()) {
			if (calDot.getAdck() != 0 && calDot.moduleIndex == mouduleIndex) {
				CalParamData cd = new CalParamData();
				cd.calMode = calDot.getMode();
				cd.pole = calDot.getPole();
				cd.meter = calDot.getMeterVal();
				cd.adc = calDot.getAdc();
				cd.adcK = calDot.getAdck();
				cd.adcB = calDot.getAdcb();
				System.out.println("adcK:" + calDot.getAdck() + "\tadcB:" + calDot.getAdcb());
				cd.da = (int) calDot.getProgramVal();
				cd.programK = calDot.getProgramk();
				cd.programB = calDot.getProgramb();
				cd.range = calDot.getPrecision();

				if (dotMap.containsKey(cd.calMode)) {

					dotMap.get(cd.calMode).add(cd);

				} else {

					List<CalParamData> list = new ArrayList<>();
					list.add(cd);
					dotMap.put(cd.calMode, list);
				}

				if (calDot.getMode() == DriverEnvironment.CalMode.CV) {

					if (calDot.getCheckAdc() != 0) {
						//
						CalParamData back1 = new CalParamData();
						back1.calMode = calDot.getMode();
						back1.pole = calDot.getPole();
						back1.meter = calDot.getMeterVal();
						back1.adc = calDot.getCheckAdc();
						back1.adcK = calDot.getCheckAdck();
						back1.adcB = calDot.getCheckAdcb();
						back1.da = (int) calDot.getProgramVal();
						back1.programK = calDot.getProgramk();
						back1.programB = calDot.getProgramb();
						back1.range = calDot.getPrecision();
						checkList.add(back1);
					}

					if (calDot.checkAdc2 != 0) {

						CalParamData back2 = new CalParamData();
						back2.calMode = calDot.getMode();
						back2.pole = calDot.getPole();
						back2.meter = calDot.getMeterVal();
						back2.adc = calDot.checkAdc2;
						back2.adcK = calDot.checkAdcK2;
						back2.adcB = calDot.checkAdcB2;
						back2.da = (int) calDot.getProgramVal();
						back2.programK = calDot.getProgramk();
						back2.programB = calDot.getProgramb();
						back2.range = calDot.getPrecision();
						check2List.add(back2);

					}

				}
			}
		}

		List<CalParamData> cvParams = dotMap.get(DriverEnvironment.CalMode.CV);
		List<CalParamData> dcParams = dotMap.get(DriverEnvironment.CalMode.DC);
		List<CalParamData> ccParams = dotMap.get(DriverEnvironment.CalMode.CC);
		if (cvParams == null) {

			cvParams = new ArrayList<>();
		}
		if (ccParams == null) {

			ccParams = new ArrayList<>();
		}
		if (dcParams == null) {

			dcParams = new ArrayList<>();
		}

		if (!checkList.isEmpty()) {

			cvParams.addAll(checkList);
		}
		if (!check2List.isEmpty()) {

			cvParams.addAll(check2List);
		}
		int cvTotalCount = cvParams.size();

		System.out.println("共写入cc" + ccParams.size() + ",cv " + (cvTotalCount - checkList.size() - check2List.size())
				+ ",dc" + dcParams.size() + ",cv1 " + checkList.size() + ",cv2 " + check2List.size());

		LogicFlashWrite2DebugData logicFlashWriter2DebugData = new LogicFlashWrite2DebugData();
		logicFlashWriter2DebugData.setUnitIndex(0);
		logicFlashWriter2DebugData.setDriverIndex(channelDO.getDriverIndex());
		logicFlashWriter2DebugData.setChnIndex(channelDO.getChnIndex());
		logicFlashWriter2DebugData.setModuleIndex(mouduleIndex);
		logicFlashWriter2DebugData.setKb_dotMap(dotMap);

		if (!ccParams.isEmpty()) {
			System.out.println("first cc adc :" + ccParams.get(0).adc);
		}

		// flash.setCv1DotCount(checkList.size());
		// flash.setCv2DotCount(check2List.size());
		logicFlashWriter2DebugData.setCv1DotCount(checkList.size());
		logicFlashWriter2DebugData.setCv2DotCount(check2List.size());
		// TODO xingguo_wang
//		for (CalBox calBox : channelDO.getDevice().getCalBoxList()) {
//			calboxService.cfgFlash(calBox, logicFlashWriter2DebugData);
//		}

	
	}
	
	public double calculateStable(TestDot dot, List<Double> adcs) throws Exception {

		if (adcs.size() - calCfgManager.steadyCfgData.getTrailCount() < 2) {
			throw new Exception(I18N.getVal(I18N.AdcCountNotEnough, adcs.size()));
		}

		adcs.sort(null);

		int cut = calCfgManager.steadyCfgData.getTrailCount() / 2;
		List<Double> tempAdcs = adcs.subList(cut, adcs.size() - cut);

		// 总和
		double sum = 0;
		for (double val : tempAdcs) {
			sum += val;
		}
		// 平均数
		double avg = sum / tempAdcs.size();

		double sum2 = 0;
		for (double val : tempAdcs) {
			sum2 += Math.pow(val - avg, 2);
		}
		// 样本方差
		double sigma2 = sum2 / (tempAdcs.size() - 1);
		// 样本标准差
		double sigma = sigma2 >= 0 ? Math.sqrt(sigma2) : 0;

		if (avg <= 0) {
			throw new Exception(I18N.getVal(I18N.AdcIsZero));
		}

		if (sigma < calCfgManager.steadyCfgData.getMaxSigma()) {
			return avg;
		}

		throw new Exception(I18N.getVal(I18N.AdcNotStable));
	
	}
	
	public int getUintIndex() {
		return uintIndex;
	}
	public void setUintIndex(int uintIndex) {
		this.uintIndex = uintIndex;
	}


}
