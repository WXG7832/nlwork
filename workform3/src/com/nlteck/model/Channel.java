package com.nlteck.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.nlteck.fireware.CalibrateCore;
import com.nlteck.fireware.DeviceCore;
import com.nlteck.fireware.DeviceDriverboard;
import com.nlteck.model.TestDot.TestResult;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCheckFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculateDotData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculatePlanMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

/**
 * 通道校准模型
 * 
 * @author guofang_ma
 *
 */
public class Channel {

	private int driverIndex;//驱动板序号
	private int chnIndex;// 分区内通道号

	private double[] matchVolt;

	private DeviceCore deviceCore;

	private CalBoardChannel bindingCalBoardChannel;

	private TestDot lastTestDot;// 上一个测试点
	private CalState chnState = CalState.NONE;// 通道状态
	private String info;// 通道信息，一般是错误信息
	private boolean ready;// 准备状态
	private boolean selected;// 选中状态

	private Date startDate;
	private Date endDate;
	
	private int  calculateIndex; //计量次数
	
	private DeviceDriverboard   driverboard;

	private VirtualChannelData virtualChannelData = new VirtualChannelData();// 通道虚拟参数

	public static class VirtualChannelData {
		public double virtualVolt;// 虚拟对接电压
		public boolean open;// 虚拟膜片开关
		public CalMode calMode;// 模式
		public Pole pole;
		public double calculateDot;
		public long programV;
		public long programI;
		public int precision;
		public int adcSize;
		public double meter;
		public MbFlashParamData flash;
	}

	public VirtualChannelData getVirtualChannelData() {
		return virtualChannelData;
	}

	private CalculateDotData calculateDotData;// 最近保存的校准/计量点
    
	
	
	public Channel(DeviceCore deviceCore, int driverIndex, int chnIndexInDriver) {

		this.deviceCore = deviceCore;
		this.driverIndex = driverIndex;
		this.chnIndex = chnIndexInDriver;
		matchVolt = new double[CalibrateCore.getBaseCfg().match.time];
	}

	public CalBoardChannel getBindingCalBoardChannel() {
		return bindingCalBoardChannel;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public double[] getMatchVolt() {
		return matchVolt;
	}

	public void setMatchVolt(double[] matchVolt) {
		this.matchVolt = matchVolt;
	}

	/**
	 * 绑定
	 * 
	 * @param calBoardChannel
	 */
	public void setBindingCalBoardChannel(CalBoardChannel calBoardChannel) {

		if (this.bindingCalBoardChannel == calBoardChannel) {
			return;
		}

		if (calBoardChannel == null) {// 解绑
			if (this.bindingCalBoardChannel != null) {
				this.bindingCalBoardChannel.setBindingChannel(null);
				this.bindingCalBoardChannel = null;
			}

		} else {
			if (calBoardChannel.getBindingChannel() != null) {
				calBoardChannel.getBindingChannel().bindingCalBoardChannel = null;
			}
			if (this.bindingCalBoardChannel != null) {
				this.bindingCalBoardChannel.setBindingChannel(null);
			}
			this.bindingCalBoardChannel = calBoardChannel;
			calBoardChannel.setBindingChannel(this);
		}
	}

	public int getDriverIndex() {
		return driverIndex;
	}


	/**
	 * 分区通道
	 * 
	 * @return
	 */
	public int getChnIndex() {
		return chnIndex;
	}

	private List<TestDot> calDots = new ArrayList<TestDot>();
	private List<TestDot> measureDots = new ArrayList<TestDot>();

	public CalState getChnState() {
		return chnState;
	}

	public void setChnState(CalState chnState) {
		this.chnState = chnState;
	}

	public TestDot getLastTestDot() {
		return lastTestDot;
	}

	public void setLastTestDot(TestDot lastTestDot) {
		this.lastTestDot = lastTestDot;
	}

	public List<TestDot> getCalDots() {
		return calDots;
	}

	public void setCalDots(List<TestDot> calDots) {
		this.calDots = calDots;
	}

	public List<TestDot> getMeasureDots() {
		return measureDots;
	}

	public void setMeasureDots(List<TestDot> measureDots) {
		this.measureDots = measureDots;
	}

	public CalculateDotData getCalculateDotData() {
		return calculateDotData;
	}

	public void setCalculateDotData(CalculateDotData calculateDotData) {
		this.calculateDotData = calculateDotData;
	}

	/**
	 * 设备通道号
	 * 
	 * @return
	 */
	public Integer getDeviceChnIndex() {
		return driverIndex * CalibrateCore.getBaseCfg().base.driverChnCount + chnIndex;
	}

	/**
	 * 板内通道
	 * 
	 * @return
	 */
	public int getDriverChnIndex() {
		return chnIndex;
	}

	public DeviceCore getDeviceCore() {
		return deviceCore;
	}

	//初始化计量点
	public void initCalculate() {
		measureDots.clear();
		for (CalculatePlanMode mode : deviceCore.getCore().getCalCfg().calculatePlanData.getModes()) {
			if (mode.disabled) {
				continue;
			}

			for (double checkDot : mode.dots) {
				TestDot testDot = new TestDot();
				testDot.channel = this;
				testDot.testType = TestType.Measure;
				testDot.mode = CalMode.values()[mode.mode.ordinal()];// 模式
				testDot.pole = Pole.values()[mode.pole.ordinal()];// 极性
				testDot.voltMode = null;
				testDot.programVal = checkDot;
				testDot.calculatePrecision();// 精度

				measureDots.add(testDot);
			}
		}
		/*if (CalibrateCore.getBaseCfg().base.calCV2) {
			measureDots.addAll(measureDots.stream().filter(x -> x.mode == CalMode.CV).map(x -> {
				try {
					TestDot cv2Dot = x.clone();
					cv2Dot.mode = CalMode.CV;
					cv2Dot.voltMode = null;
					return cv2Dot;
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				return x;
			}).collect(Collectors.toList()));
		}*/
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	// 初始化校准点
	public void initCalibrate() {
		calDots.clear();
		for (CalibratePlanMode mode : deviceCore.getCore().getCalCfg().calibratePlanData.getModes()) {

			if (CalibrateCore.getBaseCfg().base.calCheckOnly) {
				if (mode.mode != Logic2Environment.CalMode.CV) {
					continue;
				}
			}

			for (CalibratePlanDot planDot : mode.dots) {
				TestDot testDot = new TestDot();
				testDot.channel = this;
				testDot.testType = TestType.Cal;
				testDot.mode = CalMode.values()[mode.mode.ordinal()];// 模式
				testDot.pole = Pole.values()[mode.pole.ordinal()];// 极性
				testDot.voltMode =  null;
				testDot.precision = mode.level;// 精度
				testDot.programVal = planDot.da;
				testDot.moudleIndex = mode.moduleIndex;

				// 放入比较范围
				testDot.minAdc = planDot.adcMin;
				testDot.maxAdc = planDot.adcMax;
				testDot.minMeter = planDot.meterMin;
				testDot.maxMeter = planDot.meterMax;

				testDot.minAdcK = mode.adcKMin;
				testDot.maxAdcK = mode.adcKMax;
				testDot.minAdcB = mode.adcBMin;
				testDot.maxAdcB = mode.adcBMax;
				testDot.minProgramK = mode.pKMin;
				testDot.maxProgramK = mode.pKMax;
				testDot.minProgramB = mode.pBMin;
				testDot.maxProgramB = mode.pBMax;

				testDot.minCheckAdcK = mode.checkAdcKMin;
				testDot.maxCheckAdcK = mode.checkAdcKMax;
				testDot.minCheckAdcB = mode.checkAdcBmin;
				testDot.maxCheckAdcB = mode.checkAdcBmax;
				
				testDot.combine      = mode.combine;
				testDot.mainMeter    = mode.mainMeter;

				calDots.add(testDot);
			}
		}
		
	}

	public static UploadTestDot testDotToUploadDot(TestDot dot) {
		UploadTestDot ud = new UploadTestDot();
		ud.testType = dot.testType;
		ud.unitIndex = 0;
		ud.moduleIndex = dot.moudleIndex;
		ud.chnIndex = dot.channel.getDeviceChnIndex();
		ud.mode =  Logic2Environment.CalMode.values()[dot.mode.ordinal()];
		ud.voltMode = dot.voltMode;
		ud.pole = PoleData.Pole.values()[dot.pole.ordinal()];
		ud.precision = dot.precision;
		ud.meterVal = dot.meterVal;

		if (dot.channel.getStartDate() != null) {
			ud.seconds = (int) (((dot.channel.getEndDate() == null ? new Date() : dot.channel.getEndDate()).getTime()
					- dot.channel.getStartDate().getTime()) / 1000);
		}

		List<TestDot> modeDots = null;
		switch (dot.testType) {
		case Cal:
			modeDots = dot.channel.getCalDots().stream()
					.filter(x -> x.mode == dot.mode && x.pole == dot.pole && x.precision == x.precision)
					.collect(Collectors.toList());
			break;
		case Measure:
			modeDots = dot.channel.getMeasureDots().stream().filter(x -> x.mode == dot.mode && x.pole == dot.pole)
					.collect(Collectors.toList());
			break;
		default:
			break;
		}
		ud.range = modeDots.size();
		ud.pos = modeDots.indexOf(dot) + 1;

		ud.programVal = dot.programVal;
		ud.programK = dot.programK;
		ud.programB = dot.programB;

		ud.adc = dot.adc;
		ud.adcK = dot.adcK;
		ud.adcB = dot.adcB;

		ud.checkAdc = dot.checkAdc;
		ud.checkAdcK = dot.checkAdcK;
		ud.checkAdcB = dot.checkAdcB;
		
		ud.adc2   = dot.checkAdc2;
		ud.adcK2  = dot.checkAdcK2;
		ud.adcB2  = dot.checkAdcB2;

		ud.success = TestResult.Success.equals(dot.testResult);
		ud.info = dot.info;
		return ud;
	}

	public DeviceDriverboard getDriverboard() {
		return driverboard;
	}

	public void setDriverboard(DeviceDriverboard driverboard) {
		this.driverboard = driverboard;
	}
	
	/**
	 * 将通过真实表值通过KB系数换算成DA值
	 * @author  wavy_zheng
	 * 2022年4月11日
	 * @param meterVal
	 * @return
	 */
	public long  getDAFromMeter(int moduleIndex , CalMode mode , int range , double meterVal) {
		
		List<TestDot> list = getCalDots().stream().filter(x->x.moudleIndex == moduleIndex && x.mode == mode && x.precision == range).collect(Collectors.toList());
		
		//triggerDebugLog(this, "主模片mode = " + mode + ",range=" + range + ",共找到" + list.size() + "个校准点");
		
		if(list.isEmpty()) {
			
			return 0;
		}
		
		for(int n = 0; n < list.size() ; n++) {
			
			TestDot dot = getCalDots().get(n);
		    if(meterVal <= dot.meterVal) {
					
				return (long) (meterVal * dot.programK + dot.programB);	
					//return (long) dot.programVal;
			}
		}
		
		TestDot dot = list.get(list.size() - 1);
		
		return (long) (meterVal * dot.programK + dot.programB);
		
	}

	public int getCalculateIndex() {
		return calculateIndex;
	}

	public void setCalculateIndex(int calculateIndex) {
		this.calculateIndex = calculateIndex;
	}
	
	
	
	
	

//	public void test(TestDot dot) {
//
//		boolean isFirstDot = lastTestDot == null;
//		boolean isModeChange = !dot.sameMode(lastTestDot);
//
//		try {
//
//			if (isModeChange) {
//
//				// 关闭膜片
//				switchDiap(dot, false);
//
//			}
//
//			// 下发逻辑板程控
//			setLogicProgram(dot);
//
//			// 下发回检板
//			setCheckProgram(dot);
//
//			if (isFirstDot || isModeChange) {
//
//				// 打开膜片
//				switchDiap(dot, true);
//			}
//
//			// 读取adc
//			getAdc(dot);
//
//			// 读取万用表
//			getMeter(dot);
//
//			// 比较
//			checkDot(dot);
//
//		} finally {
//			lastTestDot = dot;
//		}
//
//	}

//	private void setLogicProgram(TestDot dot) {
//		// TODO Auto-generated method stub
//
//	}
//
//	private void setCheckProgram(TestDot dot) {
//		// TODO Auto-generated method stub
//
//	}
//
//	private void switchDiap(TestDot dot, boolean b) {
//		// TODO Auto-generated method stub
//		
//		mainboard.getDeviceController().sendCommand(decorator, 3000);
//	}
//
//	private void getAdc(TestDot dot) {
//		// TODO Auto-generated method stub
//
//	}
//
//	private void getMeter(TestDot dot) {
//		// TODO Auto-generated method stub
//
//	}
//
//	private void checkDot(TestDot dot) {
//		// TODO Auto-generated method stub
//
//	}

}
