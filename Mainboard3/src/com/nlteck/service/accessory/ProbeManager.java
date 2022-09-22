package com.nlteck.service.accessory;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.ProbeInfo;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ProbeType;
import com.nltecklib.protocol.li.accessory.TempProbeData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.power.temper.TempSwPickData;
import com.nltecklib.protocol.power.temper.TempSwPickData.Temper;
import com.nltecklib.utils.LogUtil;
import com.nltecklib.protocol.power.temper.TemperAdjustData;

/**
 * 温度探头采集管理器
 * 
 * @author Administrator
 *
 */
public abstract class ProbeManager {

	/**
	 * 温度探头集合
	 */
	protected List<TempSwPickData> tempDataList = new ArrayList();
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	protected MainBoard mainBoard;
	protected boolean commErr; // 通信故障?
	private final int PEEK_TIME = 5; // 轮询秒数
	protected List<ProbeInfo>   probeInfos  = new ArrayList<>();
	
	private static final int FRAME_TEMPER_COUNT = 4;//料框温度个数

	//private Logger logger = LogUtil.getLogger("controlTempTest");
	
	public ProbeManager(MainBoard mainBoard) throws AlertException{

		this.mainBoard = mainBoard;
		
		for(int n = 0 ; n < MainBoard.startupCfg.getProbeManagerInfo().probeInfos.size() ; n++) {
			
			if(MainBoard.startupCfg.getProbeManagerInfo().probeInfos.get(n).type == ProbeType.Temperature) {
				
			   probeInfos.add(MainBoard.startupCfg.getProbeManagerInfo().probeInfos.get(n));
			   tempDataList.add(new TempSwPickData());
			}
		}
		
//		int index = MainBoard.startupCfg.getProbeManagerInfo().probeInfos.indexOf(new ProbeInfo(ProbeType.Temperature, null));
//		if (index != -1) {
//
//			probeInfos.add(MainBoard.startupCfg.getProbeManagerInfo().probeInfos.get(index));
//			// 初始化
//			for (int n = 0; n < probeInfos.size(); n++) {
//
//				tempDataList.add(new TempSwPickData());
//			}
//
//		} else {
//
//			throw new AlertException(AlertCode.INIT, "初始化探头管理器失败:没有启用温度探头");
//		}


		executor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {

				// 温控监视线程
				try {

					if (mainBoard.isPoweroff() || !mainBoard.isInitOk()) {
						return;
					}
					for (int n = 0; n < probeInfos.size(); n++) {
						TempSwPickData data = readTempList(n);
						tempDataList.set(n, data);

					}
					
					
					commErr = false;

				} catch (AlertException e) {

					if (!commErr) {
						if (Context.getAlertManager() != null) {
							try {
								Context.getAlertManager().handle(e.getAlertCode(), e.getMessage(), false);
							} catch (AlertException e1) {

								e1.printStackTrace();
							}
						}
						commErr = true;
					}

					e.printStackTrace();
				} catch (Throwable ex) {

					Environment.errLogger.info(CommonUtil.getThrowableException(ex));
				}

			}

		}, 1, PEEK_TIME, TimeUnit.SECONDS);

	}

	/**
	 * 获取温度列表
	 * 
	 * @return
	 * @throws AlertException
	 */
	public abstract TempSwPickData readTempList(int index) throws Exception;
	
	/**
	 * 通过温控表，操控阀，控制恒温
	 * @return
	 * @throws Exception
	 */
	public abstract void setConstantTemp(int driverIndex, double temp) throws Exception;

	
	public void setConstantTemp(double temp) throws Exception {
		
		try {

			if (mainBoard.isPoweroff() || !mainBoard.isInitOk()) {
				return;
			}
		
			for (int n = 0; n < probeInfos.size(); n++) {
				
				if(probeInfos.get(n).controlTemp) {
					//温控温度设置
					setConstantTemp(n, temp);
				}
				
			}
			
			commErr = false;

		} catch (AlertException e) {

			if (!commErr) {
				if (Context.getAlertManager() != null) {
					try {
						Context.getAlertManager().handle(e.getAlertCode(), e.getMessage(), false);
					} catch (AlertException e1) {

						e1.printStackTrace();
					}
				}
				commErr = true;
			}

			e.printStackTrace();
		} catch (Throwable ex) {

			Environment.errLogger.info(CommonUtil.getThrowableException(ex));
		}
	}
	
	
	/**
	 * 获取料框温度
	 * @return
	 */
	public List<Double> getFrameTemps() {
	
		List<Double> frames = new ArrayList<Double>();
		
		for(int n = 0 ; n < probeInfos.size() ; n++) {
			
			if(probeInfos.get(n).controlTemp) {
				int temperSize = tempDataList.get(n).getTempers().size();
				if(temperSize >= FRAME_TEMPER_COUNT) {
					
					frames.add(tempDataList.get(n).getTempers().get(temperSize - 4).getTemper());
					frames.add(tempDataList.get(n).getTempers().get(temperSize - 3).getTemper());
					frames.add(tempDataList.get(n).getTempers().get(temperSize - 2).getTemper());
					frames.add(tempDataList.get(n).getTempers().get(temperSize - 1).getTemper());
					
					//logger.info(String.format("=======温度板2，料框1温度：%.1f,料框2温度：%.1f,料框3温度：%.1f,料框4温度：%.1f,", t1,t2,t3,t4));
				}
			}
		}
		
		int offset = FRAME_TEMPER_COUNT - frames.size(); 
		if(offset > 0) {
			for(int n = 0; offset > n;n++) {
				frames.add(0d);
			}
		}
		
		return frames;
	}
	
	/**
	 * 获取指定通道的检测值
	 * @author  wavy_zheng
	 * 2022年2月22日
	 * @param deviceChnIndex
	 * @return
	 */
	public double  getProbeValue(int deviceChnIndex) {
		
		int maxChnIndex = 0;
		for(int n = 0 ; n < tempDataList.size() ; n++) {
			
			maxChnIndex += tempDataList.get(n).getTempers().size();
			
		}
		if(deviceChnIndex >= maxChnIndex) {
			return -1;
		}
		int chnIndex = 0;
		for(int n = 0 ; n < tempDataList.size() ; n++) {
			
			if(deviceChnIndex < chnIndex + tempDataList.get(n).getTempers().size()) {
				return tempDataList.get(n).getTempers().get(deviceChnIndex - chnIndex).getTemper();
			}
			chnIndex += tempDataList.get(n).getTempers().size();
		}
		return -1;
		
	}
	
}
