package com.nlteck.fireware;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nlteck.model.CalBoardChannel;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.PCWorkform.CalibrateTerminalData.CalibrateTerminal;
import com.nltecklib.protocol.li.cal.CalEnvironment;
import com.nltecklib.protocol.li.cal.CalEnvironment.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.CalUpdateModeData;
import com.nltecklib.protocol.li.cal.OverTempAlertData;
import com.nltecklib.protocol.li.cal.ResistanceModeData;
import com.nltecklib.protocol.li.cal.ResistanceModeRelayData;
import com.nltecklib.protocol.li.cal.TempControlData;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * 校准板抽象类
 * 
 * @author guofang_ma
 *
 */
public abstract class CalBoard {

	protected Map<Integer, Double> resistivitys = new HashMap<Integer, Double>();// 电阻系数
	protected List<CalBoardChannel> calBoardChannels = new ArrayList<CalBoardChannel>();
	protected CalibrateCore core;
	protected int index;
	protected boolean disabled;
	protected Meter meter;

	public CalBoard(CalibrateCore core, int index) {
		this.core = core;
		this.index = index;

		if (core.getChnMapService().isEnable()) {
              
			
			List<Integer> chnIndexList = core.getChnMapService().findAllCalboardChannels(index);
			for(Integer chnIndex : chnIndexList) {
				
				CalBoardChannel chn = new CalBoardChannel(index, chnIndex);
				
				//默认绑定
				//core.getDeviceCore().getChannelMap().get(chnIndex).setBindingCalBoardChannel(chn);
				
//				chn.setBindingChannel(core.getDeviceCore().getChannelMap().get(chnIndex));
				calBoardChannels.add(chn);
				
			}

			System.out.println("map chn index list:" + chnIndexList);
			
		} else {

			for (int i = 0; i < CalibrateCore.getBaseCfg().calChnCount; i++) {
				calBoardChannels.add(new CalBoardChannel(index, i));
			}
		}

	}
	
	public CalBoardChannel findCalboardChnByIndex(int deviceChnIndex) {
		
		for(CalBoardChannel cb : calBoardChannels) {
			
			if(cb.getChnIndex() == deviceChnIndex) {
				
				return cb;
			}
			
		}
		
		return null;
		
	}
	

	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public List<CalBoardChannel> getCalBoardChannels() {
		return calBoardChannels;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isWork() {
		return work;
	}

	// public boolean isAlive() {
	// return workThread != null && workThread.isAlive();
	// }

	public void setWork(boolean work) {
		this.work = work;
	}

	public int getIndex() {
		return index;
	}

	public void clearResistivitys() {
		resistivitys.clear();
	}

	public void clearResistivity() {
		resistivitys.clear();
	}

	public double getResistivity(int precision) {
		if (!resistivitys.containsKey(precision)) {
			double resistivity = qryExCalResisterFactor(precision);
			core.getLogger().info("read precision " + precision + ", resistivity " + resistivity);
			resistivitys.put(precision, resistivity);
		}
		return resistivitys.get(precision);
	}

	protected Thread workThread;
	protected boolean work;

	public void startCalculate() {

		if (disabled) {
			return;
		}

		if (work) {
			return;
		}

		workThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				work = true;
				init();
				resistivitys.clear();
				try {
					calculate();
				} catch (Exception e) {
					core.getLogger().error("calibrate error:" + e.getMessage(), e);
				} finally {
					if (CalibrateCore.getBaseCfg().base.stopMode == 0) {
						closeMeterRelay();
					}
					work = false;
				}
			}
		});
		workThread.setDaemon(true);
		workThread.start();

	}

	public void startCalibrate() {

		if (disabled) {
			return;
		}

		if (work) {
			return;
		}

		workThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				work = true;
				init();
				try {
					calibrate();
				} catch (Exception e) {
					core.getLogger().error("calibrate error:" + e.getMessage(), e);
				} finally {
					if (CalibrateCore.getBaseCfg().base.stopMode == 0) {
						closeMeterRelay();
					}
					work = false;
				}
			}
		});
		workThread.setDaemon(true);
		workThread.start();

	}
	
	

	/**
	 * 
	 * 按照设备顺序返回需要校准的通道
	 * 
	 * @return
	 */
	private List<CalBoardChannel> getDeviceOrderChannels() {
		return calBoardChannels.stream().filter(x -> x.getBindingChannel() != null && x.getBindingChannel().isReady())
				.sorted(new Comparator<CalBoardChannel>() {

					@Override
					public int compare(CalBoardChannel o1, CalBoardChannel o2) {

						return o1.getBindingChannel().getDeviceChnIndex() - o2.getBindingChannel().getDeviceChnIndex();
					}
				}).collect(Collectors.toList());
	}

	/**
	 * 校准绑定通道
	 * 
	 * @throws Exception
	 */
	public void calibrate() throws Exception {

		for (CalBoardChannel calBoardChannel : getDeviceOrderChannels()) {
			try {
				if (calBoardChannel.getBindingChannel() != null && calBoardChannel.getBindingChannel().isReady()) {
					
					//驱动板进入校准
					synchronized(calBoardChannel.getBindingChannel().getDriverboard()) {
					
					   core.getCalibrateService().calibrate(calBoardChannel.getBindingChannel() , false);
					
					}
					
					
				}
			} catch (Exception e) {

				if (CalibrateCore.getBaseCfg().base.stopMode == 1) {
					throw e;
				}

				if (!work) {
					throw e;
				}
				// if (CalibrateCore.getBaseCfg().base.calibrateTerminal == CalibrateTerminal.PC
				// && !core.isPcConnected()) {
				// throw e;
				// }
			}
		}
	}

	/**
	 * 计量绑定通道
	 * 
	 * @throws Exception
	 */
	public void calculate() throws Exception {

		for (CalBoardChannel calBoardChannel : getDeviceOrderChannels()) {
			try {
				
				if (calBoardChannel.getBindingChannel() != null && calBoardChannel.getBindingChannel().isReady()) {
					
					//驱动板进入校准
					synchronized(calBoardChannel.getBindingChannel().getDriverboard()) {
					      core.getCalibrateService().calculate(calBoardChannel.getBindingChannel());
					}
					
				}
			} catch (Exception e) {

				if (CalibrateCore.getBaseCfg().base.stopMode == 1) {
					throw e;
				}

				if (!work) {
					throw e;
				}
				// if (CalibrateCore.getBaseCfg().base.calibrateTerminal == CalibrateTerminal.PC
				// && !core.isPcConnected()) {
				// throw e;
				// }
			}
		}
	}

	public abstract void cfgVoltBase(int chnIndex, WorkState workState, double voltBase);

	public abstract void cfgCalibrate2(int chnIndex, WorkState workState, WorkMode workMode, int precision, Pole pole,
			int programV, int programI);

	public abstract void cfgRelayControl( boolean connected);

	public abstract void cfgRelayControl2(int relayIndex, boolean connected);
	public abstract boolean qryRelayControl();

	/**
	 * 查询新版电阻系数
	 * 
	 * @author wavy_zheng 2022年2月8日
	 * @param driverIndex
	 * @param wp
	 * @param range
	 * @return
	 */
	public abstract ResistanceModeData qryResistanceModeData(WorkPattern wp, int range);
	
	/**
	 * 查询新版电阻系数2
	 * 
	 * @author xinggguo_w 2022年8月24日
	 * @param driverIndex
	 * @param wp
	 * @param range
	 * @return
	 */
	public abstract ResistanceModeRelayData qryResistanceModeData(int resistanceIndex,WorkPattern wp, int range);

	/**
	 * 配置新版电阻系数
	 * 
	 * @author wavy_zheng 2022年2月9日
	 * @param data
	 */
	public abstract void cfgResistanceModeData(ResistanceModeData data);
	/**
	 * 配置新版450A电阻系数
	 * 
	 * @author xingguo_w 2022年9月6日
	 * @param data
	 */
	public abstract void cfgRelayResistanceData(ResistanceModeRelayData data);

	/**
	 * 配置恒温开关
	 * 
	 * @author wavy_zheng 2022年2月9日
	 * @param tcd
	 */
	public abstract void cfgTempControlData(double temp, boolean open);

	public abstract double qryTemperature();

	public abstract OverTempAlertData qryOverTempatureData();

	public abstract double qryExCalResisterFactor(int level);

	public abstract void cfgCalculate2(int chnIndex, WorkState workState, WorkMode workMode, CalEnvironment.Pole pole,
			double calculateDot, int precision);

	public abstract void cfgUpdateMode(boolean updateMode);

	public abstract CalUpdateModeData qryUpdateMode();

	public abstract void cfgUpdateFile(int fileSize, int packCount, int packIndex, List<Byte> packContent)
			throws Exception;

	public abstract void closeMeterRelay();
	
	public abstract void cfgTestMode(int chnIndex , TestType type) ;

	public abstract void init();


}
