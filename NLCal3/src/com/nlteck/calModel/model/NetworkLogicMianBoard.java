package com.nlteck.calModel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nlteck.calModel.base.CalCfgManager;
import com.nlteck.calModel.base.I18N;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.TestDot;
import com.nlteck.service.CalboxService;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverCalculateData.AdcEntry;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;

public class NetworkLogicMianBoard extends ABSLogicMianBoard{
	public CalboxService calboxService=new CalboxService();
	CalCfgManager calCfgManager = new CalCfgManager();
	@Override
	public TestDot gatherCalibrateADC(TestDot dot) throws Exception {
//		int retryCount = calCfgManager.steadyCfgData.getAdcReadCount();
//
//		for (int i = 0; i < retryCount; i++) {
//			LogicCalibrate2DebugData data = null;
//			for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
////				System.out.println(dot.getChannelDO().getDeviceChnIndex());
//				data=calboxService.readCalibrate(calBox, dot.getChannelDO().getDeviceChnIndex());
//			}
//			System.out.println("读取校准点adc"+data);
//			List<Double> adcs = data.getAdcs().stream().map(x -> x.mainAdc).collect(Collectors.toList());
//			try {
//				System.out.println("mainAdcs:" + adcs);
//				dot.setAdc(calculateStable(dot, adcs));
//				System.out.println("avg dot.adc = " + dot.getAdc());
//				if (dot.getMode() == DriverEnvironment.CalMode.CV) {
//
//					// 需要查询back1Adc稳定度
//					adcs = data.getAdcs().stream().map(x -> x.backAdc1).collect(Collectors.toList());
//					System.out.println("checkAdcs:" + adcs);
//					try {
//						dot.setCheckAdc(calculateStable(dot, adcs));
//						System.out.println("dot.checkAdc = " + dot.getCheckAdc());
//
//					} catch (Exception e) {
//						if (i == retryCount - 1) {
//							throw new Exception("回检ADC1 " + dot.getMode().name() + e.getMessage());
//						} else {
//							sleep(dot, calCfgManager.steadyCfgData.getAdcRetryDelay());
//						}
//					}
//					if(!baseCfgManager.base.ignoreCV2) {
//						
//						System.out.println("start cal check adc2");
//						adcs = data.getAdcs().stream().map(x -> x.backAdc2).collect(Collectors.toList());
//						System.out.println("check2Adcs:" + adcs);
//						
//						try {
//							dot.checkAdc2 = calculateStable(dot, adcs);
//							System.out.println("dot.checkAdc2 = " + dot.checkAdc2);
//
//						} catch (Exception e) {
//							if (i == retryCount - 1) {
//								throw new Exception("回检2ADC " + dot.getMode().name() + e.getMessage());
//							} else {
//								sleep(dot, calCfgManager.steadyCfgData.getAdcRetryDelay());
//							}
//						}
//						
//					}
//					
//				}
//				break;
//
//			} catch (Exception e) {
//				if (i == retryCount - 1) {
//					throw new Exception("adc " + dot.getMode().name() + e.getMessage());
//				} else {
//					sleep(dot, calCfgManager.steadyCfgData.getAdcRetryDelay());
//				}
//			}
//
//		}
		LogicCalibrate2DebugData data = WorkBench.getBoxService().readCalibrate(dot.getChannelDO().getDevice().getCalBoxList().get(0),
				dot.getChannelDO().getDeviceChnIndex());
		System.out.println(dot.getChannelDO().getDeviceChnIndex()+"通道"+data);
		List<Double> adcs = data.getAdcs().stream().map(x -> x.mainAdc).collect(Collectors.toList());
		try {
			System.out.println("mainAdcs:" + adcs);
			dot.setAdc(calculateStable(dot, adcs));
			System.out.println("avg dot.adc = " + dot.getAdc());
			if (dot.getMode() == DriverEnvironment.CalMode.CV) {

				// 需要查询back1Adc稳定度
				adcs = data.getAdcs().stream().map(x -> x.backAdc1).collect(Collectors.toList());
				System.out.println("checkAdcs:" + adcs);
				try {
					dot.setCheckAdc(calculateStable(dot, adcs));
					System.out.println("dot.checkAdc = " + dot.getCheckAdc());

				} catch (Exception e) {
					e.printStackTrace();
				}
				if(!baseCfgManager.base.ignoreCV2) {
					
					System.out.println("start cal check adc2");
					adcs = data.getAdcs().stream().map(x -> x.backAdc2).collect(Collectors.toList());
					System.out.println("check2Adcs:" + adcs);
					
					try {
						dot.checkAdc2 = calculateStable(dot, adcs);
						System.out.println("dot.checkAdc2 = " + dot.checkAdc2);

					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return dot;
		
	}

	@Override
	public TestDot gatherCalculateADC(TestDot dot) throws Exception {

		int retryCount = dot.getMode() == DriverEnvironment.CalMode.CV ? calCfgManager.steadyCfgData.getAdcReadCountCV() : calCfgManager.steadyCfgData.getAdcReadCount();
		
		LogicCalculate2DebugData gatherData = new LogicCalculate2DebugData();
		for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
			gatherData=calboxService.readCalculate(calBox, dot.getChannelDO().getChnIndex());
		}
		
		
		for (int i = 0; i < retryCount; i++) {
			
			List<Double> adcs = new ArrayList<>();
			
			/**
			 * 把所有模片的真实值累加即最终真实值
			 */
			List<Double> adc1s = new ArrayList<>();
			List<Double> adc2s = new ArrayList<>();
			
			for(ReadonlyAdcData rad : gatherData.getAdcDatas()) {
				
				double val = 0;
				for(int n = 0 ; n < Data.getModuleCount() ; n++) {
	             
					val += rad.adcList.get(n).finalAdc;

				}
				adcs.add(val);
				adc1s.add(rad.finalBackAdc1);
				adc2s.add(rad.finalBackAdc2);
				
			}
			
			try {
				
				dot.setAdc(calculateStable(dot, adcs));
				if(baseCfgManager.base.calCheckBoard && dot.getMode() == DriverEnvironment.CalMode.CV) {
				    dot.setCheckAdc(calculateStable(dot , adc1s));
				    if(!baseCfgManager.base.ignoreCV2) {
				    	
				    	dot.checkAdc2 = calculateStable(dot , adc2s);
				    }
				}
				
				dot.setProgramk(gatherData.getProgramKReadonly());
				dot.setProgramb(gatherData.getProgramBReadonly());
				dot.setAdck(gatherData.getAdcKReadonly());
				dot.setAdcb(gatherData.getAdcBReadonly());
				break;
			} catch (Exception e) {
				if (i == retryCount - 1) {
					
					if(dot.getAdc() > 0 && dot.getCheckAdc() > 0 ) {
						
						throw new Exception(I18N.getVal(I18N.CheckBoard) + dot.getMode().name() + e.getMessage());
					}
					
					throw new Exception(I18N.getVal(I18N.LogicBoard) + dot.getMode().name() + e.getMessage());
				} else {
					sleep(dot, dot.getMode() == DriverEnvironment.CalMode.CV  ? calCfgManager.steadyCfgData.getAdcRetryDelayCV() : calCfgManager.steadyCfgData.getAdcRetryDelay());
				}
			}
		}

	
		return dot;
		
	}

	@Override
	// 单个膜片计量
	public TestDot gatherCalculateADCmoudle(TestDot dot) throws Exception {
		
		int retryCount = dot.getMode() == DriverEnvironment.CalMode.CV ? calCfgManager.steadyCfgData.getAdcReadCountCV() : calCfgManager.steadyCfgData.getAdcReadCount();
		
		for(int i=0;i<retryCount;i++) {
			
			LogicCalculate2DebugData gatherData = null;
			for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
				gatherData=calboxService.readCalculate(calBox, dot.getChnIndex());
			}
			System.out.println(gatherData);
			
			List<Double> adcs = new ArrayList<>();
			List<Double> adc1s = new ArrayList<>();
			List<Double> adc2s = new ArrayList<>();
			
			for(ReadonlyAdcData rad : gatherData.getAdcDatas()) {
				
				double val = 0;
				// 
				val = rad.adcList.get(0).finalAdc;
				adcs.add(val);
				adc1s.add(rad.finalBackAdc1);
				adc2s.add(rad.finalBackAdc2);
			}
			System.out.println("获取的最终ADC："+adcs);
			if(dot.getMode().equals(CalMode.CV)) {
				System.out.println("获取的最终ADC1："+adc1s);
				System.out.println("获取的最终ADC2："+adc2s);
			}
			
			try {
				dot.setAdc(calculateStable(dot, adcs));
				if(baseCfgManager.base.calCheckBoard && dot.getMode() == DriverEnvironment.CalMode.CV) {
					dot.setCheckAdc(calculateStable(dot , adc1s));
					if(!baseCfgManager.base.ignoreCV2) {
						
						dot.checkAdc2 = calculateStable(dot , adc2s);
					}
				}
				
				dot.setProgramk(gatherData.getProgramKReadonly());
				dot.setProgramb(gatherData.getProgramBReadonly());
				dot.setAdck(gatherData.getAdcKReadonly());
				dot.setAdcb(gatherData.getAdcBReadonly());
				break;
			}catch (Exception e) {
				if(i==retryCount) {
					if(dot.getAdc() > 0 && dot.getCheckAdc() > 0 ) {
						throw new Exception(I18N.getVal(I18N.CheckBoard) + dot.getMode().name() + e.getMessage());
					}
					throw new Exception(I18N.getVal(I18N.LogicBoard) + dot.getMode().name() + e.getMessage());
				}else {
					sleep(dot, dot.getMode() == DriverEnvironment.CalMode.CV  ? calCfgManager.steadyCfgData.getAdcRetryDelayCV() : calCfgManager.steadyCfgData.getAdcRetryDelay());
				}
			}
		}
			
		
		
//		int retryCount = dot.getMode() == DriverEnvironment.CalMode.CV ? calCfgManager.steadyCfgData.getAdcReadCountCV() : calCfgManager.steadyCfgData.getAdcReadCount();
//		
//		
//		List<Double> adcs = new ArrayList<>();
//		List<Double> adc1s = new ArrayList<>();
//		List<Double> adc2s = new ArrayList<>();
//		for(int i=0;i<retryCount;i++) {
//			adcs.clear();
//			adc1s.clear();
//			adc2s.clear();
//			LogicCalculate2DebugData gatherData = null;
//			for(CalBox calBox:dot.getChannelDO().getDevice().getCalBoxList()) {
//				gatherData=calboxService.readCalculate(calBox, dot.getChnIndex());
//			}
//			for(ReadonlyAdcData rad : gatherData.getAdcDatas()) {
//				
//				double val = 0;
//				// 
//				val = rad.adcList.get(dot.moduleIndex).finalAdc;
////				System.out.print("原始ADC："+rad.adcList.get(dot.moduleIndex).primitiveAdc+"\t");
//				adcs.add(val);
//				adc1s.add(rad.finalBackAdc1);
//				adc2s.add(rad.finalBackAdc2);
//			}
//			System.out.println("获取的最终ADC："+adcs);
//			if(dot.getMode().equals(CalMode.CV)) {
//				System.out.println("获取的最终ADC1："+adc1s);
//				System.out.println("获取的最终ADC2："+adc2s);
//			}
//			
//			try {
//				
//				dot.setAdc(calculateStable(dot, adcs));
//				if(baseCfgManager.base.calCheckBoard && dot.getMode() == DriverEnvironment.CalMode.CV) {
//					dot.setCheckAdc(calculateStable(dot , adc1s));
//					if(!baseCfgManager.base.ignoreCV2) {
//						
//						dot.checkAdc2 = calculateStable(dot , adc2s);
//					}
//				}
//				
//				dot.setProgramk(gatherData.getProgramKReadonly());
//				dot.setProgramb(gatherData.getProgramBReadonly());
//				dot.setAdck(gatherData.getAdcKReadonly());
//				dot.setAdcb(gatherData.getAdcBReadonly());
//				break;
//			} catch (Exception e) {
//				
//				if (i == retryCount - 1) {
//					
//					if(dot.getAdc() > 0 && dot.getCheckAdc() > 0 ) {
//						
//						throw new Exception(I18N.getVal(I18N.CheckBoard) + dot.getMode().name() + e.getMessage());
//					}
//					
//					throw new Exception(I18N.getVal(I18N.LogicBoard) + dot.getMode().name() + e.getMessage());
//				} else {
//					sleep(dot, dot.getMode() == DriverEnvironment.CalMode.CV  ? calCfgManager.steadyCfgData.getAdcRetryDelayCV() : calCfgManager.steadyCfgData.getAdcRetryDelay());
//				}
//			}
//		}
		return dot;	
	}
	
	private void sleep(TestDot dot, int delay) {
		if (delay == 0) {
			return;
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
