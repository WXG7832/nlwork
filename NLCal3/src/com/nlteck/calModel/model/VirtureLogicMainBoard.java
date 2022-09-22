package com.nlteck.calModel.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.instrument.classloading.ResourceOverridingShadowingClassLoader;

import com.nlteck.firmware.CalBox;
import com.nlteck.model.TestDot;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverCalculateData.AdcEntry;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;

public class VirtureLogicMainBoard extends ABSLogicMianBoard {
	public int unitIndex;
	private List<Double> adcs = new ArrayList<>();

	@Override

	public TestDot gatherCalibrateADC(TestDot dot) {
		adcs.clear();
		// 模拟协议
		LogicCalibrate2DebugData gatherData = new LogicCalibrate2DebugData();
		gatherData.setChnIndex(dot.getChnIndex());
		gatherData.setPole(dot.getPole());
		gatherData.setUnitIndex((int) dot.getProgramVal());
		gatherData.setWorkMode(dot.getMode());
		gatherData.setProgramV((long) dot.getProgramVal());
		List<DriverCalibrateData.AdcData> adcDatas = new ArrayList<>();

		int retryCount = calCfgManager.steadyCfgData.getAdcReadCount();

		switch (gatherData.getWorkMode()) {
		case CC:
		case DC:
			for (int i = 0; i < retryCount; i++) {
				DriverCalibrateData.AdcData adc = new DriverCalibrateData.AdcData();

				adc.mainAdc = dot.getProgramVal() * 0.27 - 0.5 + Math.random();
				adcDatas.add(adc);
			}
			break;
		case CV:
			for (int i = 0; i < retryCount; i++) {
				DriverCalibrateData.AdcData adc = new DriverCalibrateData.AdcData();

				adc.mainAdc = dot.getProgramVal() * 0.18 - 0.5 + Math.random();
				adc.backAdc1 = dot.getProgramVal() * 0.18 - 0.5 + Math.random();
				adc.backAdc2 = dot.getProgramVal() * 0.18 - 0.5 + Math.random();

				adcDatas.add(adc);
			}
			break;
		default:
			break;
		}
		gatherData.setAdcs(adcDatas);

		List<Double> adcs = new ArrayList<>();
		for (DriverCalibrateData.AdcData adcData : gatherData.getAdcs()) {
			adcs.add(adcData.mainAdc);
		}

		try {
			System.out.println("mainAdcs:" + adcs);
			dot.setAdc(calculateStable(dot, adcs));
			System.out.println("avg dot.adc = " + dot.getAdc());
			adcs.clear();
			if (dot.getMode() == DriverEnvironment.CalMode.CV) {
				// 需要查询back1Adc稳定度
				for (DriverCalibrateData.AdcData adcData : gatherData.getAdcs()) {
					adcs.add(adcData.backAdc1);
				}

				System.out.println("checkAdcs:" + adcs);

				dot.setCheckAdc(calculateStable(dot, adcs));
				System.out.println("dot.checkAdc = " + dot.getCheckAdc());

				if (!baseCfgManager.base.ignoreCV2) {

					System.out.println("start cal check adc2");
					// adcs = data.getAdcs().stream().map(x ->
					// x.backAdc2).collect(Collectors.toList());
					adcs.clear();
					for (DriverCalibrateData.AdcData adcData : gatherData.getAdcs()) {
						adcs.add(adcData.backAdc2);
					}

					System.out.println("check2Adcs:" + adcs);

					dot.checkAdc2 = calculateStable(dot, adcs);
					System.out.println("dot.checkAdc2 = " + dot.checkAdc2);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dot;
	}

	@Override
	public TestDot gatherCalculateADC(TestDot dot) throws Exception {
		adcs.clear();
		// 模拟数据
		int retryCount = dot.getMode() == DriverEnvironment.CalMode.CV ? calCfgManager.steadyCfgData.getAdcReadCountCV()
				: calCfgManager.steadyCfgData.getAdcReadCount();
		LogicCalculate2DebugData gatherData = new LogicCalculate2DebugData();

		double primaryADC = 0;
		double finalADC=0;
		switch (dot.getMode()) {
		case CC:
		case DC:
			for (TestDot useDot : dot.getChannelDO().getCalDotList()) {
				if (dot.getMode().equals(useDot.getMode()) && dot.getPole().equals(useDot.getPole())) {
					if (useDot.getAdck() == 0) {
						continue;
					}
					if (dot.getProgramVal() < useDot.getMeterVal()) {
						primaryADC = (dot.getProgramVal()*useDot.getProgramk()+useDot.getProgramb())* 0.27;
						finalADC = (primaryADC * useDot.getAdck() + useDot.getAdcb());
								
						break;
					}
				}
			}
			break;
		case CV:
			
			primaryADC=dot.getProgramVal();
			finalADC=dot.getProgramVal();
					
			
			break;
		default:
			break;
		}

		for (int i = 0; i < retryCount; i++) {

			gatherData.setModuleIndex(dot.moduleIndex);
			gatherData.setPole(dot.getPole());
			gatherData.setMode(dot.getMode());
			gatherData.setProgramDot(dot.getProgramVal());

			List<ReadonlyAdcData> adcDatas = new ArrayList<>();
			for (int j = 0; j < retryCount; j++) {
				ReadonlyAdcData readOnlyAdcData = new ReadonlyAdcData();

				 readOnlyAdcData.finalBackAdc1 = dot.getProgramVal()-0.05+Math.random()*0.1;
				 readOnlyAdcData.finalBackAdc2 = dot.getProgramVal()-0.05+Math.random()*0.1;
				// readOnlyAdcData.primitiveBackAdc1 = dot.getProgramVal();
				// readOnlyAdcData.primitiveBackAdc2 = dot.getProgramVal();
				for (AdcEntry adcEntry : readOnlyAdcData.adcList) {
					adcEntry.finalAdc = finalADC/2;
					adcEntry.primitiveAdc = primaryADC/2;				
				}
				adcDatas.add(readOnlyAdcData);
			}
			//

			gatherData.setAdcDatas(adcDatas);
		}
		// 获取数据

		/**
		 * 把所有模片的真实值累加即最终真实值
		 */
		List<Double> adc1s = new ArrayList<>();
		List<Double> adc2s = new ArrayList<>();

		for (ReadonlyAdcData rad : gatherData.getAdcDatas()) {

			double val = 0;
			for (int n = 0; n < Data.getModuleCount(); n++) {

				val += rad.adcList.get(n).finalAdc;

			}
			adcs.add(val);
			adc1s.add(rad.finalBackAdc1);
			adc2s.add(rad.finalBackAdc2);

		}

		System.out.println("计量adc:" + adcs);
		try {
			dot.setAdc(calculateStable(dot, adcs));
			if (baseCfgManager.base.calCheckBoard && dot.getMode() == DriverEnvironment.CalMode.CV) {
				dot.setCheckAdc(calculateStable(dot, adc1s));
				if (!baseCfgManager.base.ignoreCV2) {

					dot.checkAdc2 = calculateStable(dot, adc2s);
				}
			}

			dot.setProgramk(gatherData.getProgramKReadonly());
			dot.setProgramb(gatherData.getProgramBReadonly());
			dot.setAdck(gatherData.getAdcKReadonly());
			dot.setAdcb(gatherData.getAdcBReadonly());
		} catch (Exception e) {
			// if (i == retryCount - 1) {
			//
			// if (dot.getAdc() > 0 && dot.getCheckAdc() > 0) {
			//
			// throw new Exception(I18N.getVal(I18N.CheckBoard) + dot.getMode().name() +
			// e.getMessage());
			// }
			//
			// throw new Exception(I18N.getVal(I18N.LogicBoard) + dot.getMode().name() +
			// e.getMessage());
			// } else {
			//
			// }
		}

		if(dot.getAdc()==0) {
			dot.setAdc(dot.getProgramVal()-0.5+Math.random());
		}
		
		return dot;

	}

	public List<Double> getAdcs() {
		return adcs;
	}

	public void setAdcs(List<Double> adcs) {
		this.adcs = adcs;
	}

	@Override
	public TestDot gatherCalculateADCmoudle(TestDot dot) throws Exception {
		dot.setAdc(dot.getProgramVal());
		return dot;
	}

}
