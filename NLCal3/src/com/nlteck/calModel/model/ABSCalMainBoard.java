package com.nlteck.calModel.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nlteck.calModel.base.BaseCfgManager;
import com.nlteck.calModel.base.BaseCfgManager.AdcAdjust;
import com.nlteck.calModel.base.DelayConfig.DetailConfig;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestDot.TestResult;
import com.nlteck.service.CalboxService;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

/**
 * 
 * @author wang_xingguo
 * @Description: 抽象校准板对象
 * @date 2022年5月24日
 *
 */
public abstract class ABSCalMainBoard {
	public TestDot dot;
	public int calBoardIndex;
	public List<CalBoardChannel> calBoardChannels=new ArrayList<>();
	public CalboxService calboxService;
	public ABSLogicMianBoard absLogicBoard;
	public BaseCfgManager baseCfgManager=new BaseCfgManager();
	public abstract void changeModel(List<CalBox> calboxs,CalibrateCoreWorkMode mode) throws Exception;//进入校准模式
	public abstract void switchModule(List<CalBox> calboxs,int channelIndex, boolean isModeChange) throws Exception;//膜片开关
	public abstract void sendCalibrateDot(TestDot dot,boolean open) throws Exception;//发送校准板校准点
	public abstract void sendCalMeasure(TestDot dot) throws Exception;//发送校准板计量点
	public abstract void sendLogicDot(TestDot dot) throws Exception;//发送逻辑板校准点
	public abstract void sendLogicMeasure(TestDot dot) throws Exception;//发送 逻辑板计量点
	public abstract void gatherMeter(TestDot dot, DetailConfig detailConfig) throws IOException, InterruptedException, Exception;//读取万用表值 
	
	
	public void TestSleep(int delay) {
		if (delay == 0) {
			return;
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取计量失败的点
	 * 
	 * @author wavy_zheng 2022年4月29日
	 * @param channel 
	 * @return
	 */
	public List<TestDot> fetchAllFailDot(ChannelDO channel) {

		List<TestDot> list = new ArrayList<>();
		for (TestDot dot : channel.getMeasureDotList()) {

			if (dot.testResult == TestResult.Fail) {

				list.add(dot);
			}

		}

		return list;

	}
	
	/**
	 * 调整错误计量点的B值,暂时定为主模片
	 * 
	 * @author wavy_zheng 2022年4月29日
	 * @param failDots
	 * @throws Exception
	 */
	public void adjustBValues(ChannelDO channel, List<TestDot> failDots) throws Exception {

		List<TestDot> dots = channel.getCalDotList().stream().filter(x -> x.moduleIndex == 0).collect(Collectors.toList());
		Map<TestDot, TestDot> map = new HashMap<>();
		for (int n = 0; n < failDots.size(); n++) {

			TestDot fail = failDots.get(n);
			List<TestDot> fetchs = new ArrayList<>();
			for (TestDot dot : dots) {

				if (dot.getMode() == fail.getMode() && dot.getPrecision()== fail.getPrecision()) {

					fetchs.add(dot);
				}

			}

			if (!fetchs.isEmpty()) {
				
				boolean fetchVal = false;
				for (int i = 0 ; i< fetchs.size(); i++) {

					TestDot secDot = fetchs.get(i);

					if (fail.getMeterVal()<= secDot.getMeterVal()) {
  
						TestDot adjustDot = map.get(secDot);
						fetchVal = true;
						// 同一段偏差更大的计量点获得B值调整机会
						if (adjustDot == null || Math.abs(fail.getMeterVal() - fail.getProgramVal()) > Math
								.abs(adjustDot.getMeterVal()- adjustDot.getProgramVal())) {
							map.put(secDot, fail);

						}
						break;

					}

				}
				if(!fetchVal) {
					
					//取第最后1个值
					map.put(fetchs.get(fetchs.size() - 1), fail);
				}
				
				
			}

		}

		// 开始调整B值
		for (Iterator<TestDot> it = map.keySet().iterator(); it.hasNext();) {

			TestDot key = it.next();
			TestDot val = map.get(key);
			double offset = val.getMeterVal() - val.getProgramVal();
//			triggerDebugLog(channel,
//					"计量点:" + val.programVal + ",模式:" + val.mode + ",实际值:" + val.meterVal + ",偏差:" + offset);
			double B = key.getProgramb() - key.getProgramk()* offset;
//			triggerDebugLog(channel,
//					"开始调整" + key.meterVal + "段B值:" + key.programB + " - " + key.programK + " * " + offset + " = " + B);
//			triggerDebugLog(channel, key.meterVal + "段B值:" + key.programB + " -> " + B);
			key.setProgramb(B);

		}

		if (!map.isEmpty()) {

//			triggerDebugLog(channel, "开始重新写入调整后的KB校准系数,请稍后...");
//			absLogicBoard.writeFlash(channel, 0);

		}

	}
	
	public void adjustAdcOffset(boolean logic, TestDot dot, boolean backCheck2) {
		// TODO 修正ADC偏差

		BaseCfgManager baseCfgManager = new BaseCfgManager();
		if (!baseCfgManager.adjustParam.use) {
			return;
		}

		double offset = (logic ? dot.getAdc() : (backCheck2 ? dot.checkAdc2 : dot.getCheckAdc())) - dot.getProgramVal();

		// triggerDebugLog(dot.channel,
		// String.format("%s%s adc调整前=%f", logic ? "逻辑板" : "回检板", dot.mode, (logic ?
		// dot.adc : dot.checkAdc)));

		AdcAdjust adcAdjust = findAdjust(logic, dot.getMode(), dot.getPole(), dot.getPrecision());
		if (adcAdjust != null) {

			if (Math.abs(offset) > adcAdjust.threshold) {

				offset = offset / adcAdjust.div;
				if (logic) {

					dot.setAdc(dot.getProgramVal() + offset); // 求出校正后的ADC
				} else {

					if (!backCheck2) {
						dot.setCheckAdc(dot.getProgramVal() + offset); // 求出校正后的ADC
					} else {

						dot.checkAdc2 = dot.getProgramVal() + offset; // 求出校正后的ADC
					}
				}
			}

		}

	}

	private AdcAdjust findAdjust(boolean logic, CalMode mode, Pole pole, int precision) {

		Optional<AdcAdjust> adjust = baseCfgManager.adjustParam.adcAdjusts.stream()
				.filter(x -> x.logic == logic && x.mode == mode && x.pole == pole && x.level == precision).findAny();

		if (!adjust.equals(Optional.empty())) {
			return adjust.get();
		}
		return null;

	}
	
	/**
	 * 根据实际电流值计算值精度范围（档位）
	 * @author  wavy_zheng
	 * 2022年5月4日
	 * @param meterVal
	 * @return
	 * @exception xingguo_wang
	 */
	public int getRangeForMeterVal(double meterVal) {
		
		Optional<RangeCurrentPrecision> a = WorkBench.calCfgManager.rangeCurrentPrecisionData
				.getRanges().stream().filter(x ->meterVal > x.min && meterVal <= x.max).findAny();

		if (a.equals(Optional.empty())) {
			
			return 0;
		}

		// 临界档位往高精度
		List<RangeCurrentPrecision> ranges = WorkBench.calCfgManager.rangeCurrentPrecisionData
				.getRanges();

		int precision = ranges.get(ranges.size() - 1).level;// 默认最高精度

		for (RangeCurrentPrecision range : ranges) {
			if (meterVal > range.min) {
				precision = range.level;
				break;
			}
		}
		
		return precision;
		
		
		
	}
	public long  getDAFromMeter(ChannelDO channel , int moduleIndex , DriverEnvironment.CalMode mode , int range , double meterVal) {
		
		List<TestDot> list = channel.getCalDotList().stream().filter(x->x.moduleIndex == moduleIndex 
				&& x.getMode() == mode && x.getPrecision() == range).collect(Collectors.toList());
		
		if(list.isEmpty()) {
			
			return 0;
		}
		
		for(int n = 0; n < list.size() ; n++) {
			
			TestDot dot = list.get(n);
		    if(meterVal <= dot.getMeterVal()) {
		    	
		    	if(dot.getProgramk() == 0) {
		    		
		    		continue;
		    	}
				
				return (long) (meterVal * dot.getProgramk() + dot.getProgramb());	
				
			}
		}
		
		TestDot dot = list.get(list.size() - 1);
		
		return (long) (meterVal * dot.getProgramk() + dot.getProgramb());
		
	}

}
