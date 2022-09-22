package com.nlteck.firmware;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nlteck.calModel.base.BaseCfgManager;
import com.nlteck.calModel.base.CalCfgManager;
import com.nlteck.model.BaseCfg;
import com.nlteck.model.StablePlan;
import com.nlteck.model.TestData;
import com.nlteck.model.TestLog;
import com.nlteck.service.CalboxService;
import com.nlteck.service.CoreService;
import com.nlteck.service.Database2Manager;
import com.nlteck.service.DatabaseManager;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoQueryData;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.ChnSelectData;
import com.nltecklib.protocol.li.PCWorkform.DelayData;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.protocol.li.PCWorkform.TestModeData;
import com.nltecklib.utils.LogUtil;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.TestMode;
import nlcal.NlteckCalEnvrionment;

/**
 * 全局类
 * 
 * @author caichao_tang
 *
 */
public class WorkBench {
	
	
    private static Logger logger;
	
	public enum CalType {
		
		CAL , MEASURE
	}
	
	public enum DeviceType {
		
		POWERBOX , CAPACITY;
		
		@Override
		public String toString() {
		    
			switch(this) {
			case POWERBOX:
				return "电源柜";
			case CAPACITY:
				return "恒温箱";
			
			}
			return "";
		}
		
	}
	
	
	/**
	 * 设备集合
	 */
	public static List<Device> deviceList = new ArrayList<>();
	/**
	 * 未进行绑定的校准箱
	 */
	public static List<CalBox> calBoxList = new ArrayList<>();
	
	
	public static Database2Manager   dataManager =  null;
	
	
	public static CalboxService      boxService = null;
	
	/**
	 * 核心测试类
	 */
	public static CoreService        coreService = null;
	
	//基础配置
	public static BaseCfg           baseCfg = null;
	// 校准配置
	public static CalCfgManager		calCfgManager=null;
	public static BaseCfgManager 	baseCfgManager=null;
	public static CalboxService 	calboxService=null;
	/**
	 * 当前测试信息
	 */
	public static TestData currenTestData;
	/**
	 * springContext对象
	 */
	private static ApplicationContext ac;
	/**
	 * 0x02校准方案协议数据
	 */
	public static CalibratePlanData calibratePlanData = new CalibratePlanData();
	/**
	 * 0x03延时配置
	 */
	public static DelayData delayData = new DelayData();
	/**
	 * 0x04 ADC稳定度检测配置
	 */
	public static SteadyCfgData steadyCfgData = new SteadyCfgData();
	/**
	 * 0x06 精度档位
	 */
	public static RangeCurrentPrecisionData rangeCurrentPrecisionData = new RangeCurrentPrecisionData();
	/**
	 * 0x07计量方案
	 */
	public static CalculatePlanData calculatePlanData = new CalculatePlanData();
	
	/**
	 * 稳定度测试方案
	 */
	public static StablePlan    stablePlan   = new StablePlan();
	

	/**
	 * 软件初始化
	 */
	static {
		
		
		try {
		    
			logger = LogUtil.getLogger("database");
			
			dataManager = Database2Manager.getInstance();
			System.out.println("loading device list");
			deviceList = dataManager.listAllDevices();
			calBoxList = dataManager.listCalBox(null);
			boxService = new CalboxService();
			coreService = new CoreService();
			
			baseCfg = BaseCfg.loadCfgFile();
			System.out.println(baseCfg);
			/**
			 * xingguo_wang 
			 */
			calboxService = new CalboxService();
			calCfgManager = new CalCfgManager();
			baseCfgManager = new BaseCfgManager();
			
			//设置协议
//			Data.setModuleCount(baseCfg.protocol.moduleCount);
//			com.nltecklib.protocol.power.Data.setModuleCount(baseCfg.protocol.moduleCount);

			
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error(CommonUtil.getThrowableException(e));
		}
		
		
//		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
//		try {
//			Thread.currentThread().setContextClassLoader(WorkBench.class.getClassLoader());
//			System.out.println("开始载入spring配置文件");
//			ac = new ClassPathXmlApplicationContext("/springConfig.xml");
//			System.out.println("载入配置文件成功！");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			Thread.currentThread().setContextClassLoader(oldLoader);
//		}
		// 加载配置文件
//		try {
//			NlteckCalEnvrionment.loadXML();
//			WorkBench.deviceList = NlteckCalEnvrionment.deviceList;
//			WorkBench.calBoxList = NlteckCalEnvrionment.calBoxList;
//			// 设置默认日志存储对象
//			String testName = "defaultTest";
//			List<TestData> testDataList = WorkBench.getDatabaseManager().getTestDataDao().select(testName);
//			// 如果有重复的测试名
//			if (testDataList != null && testDataList.size() > 0) {
//				WorkBench.currenTestData = testDataList.get(0);
//			} else {
//				// 保存到数据库
//				TestData testData = new TestData();
//				testData.setTestName(testName);
//				testData.setStartTime(new Date());
//				WorkBench.getDatabaseManager().getTestDataDao().insert(testData);
//				// 设置到上位机
//				WorkBench.currenTestData = testData;
//			}
//			// 记录操作日志
//			WorkBench.getDatabaseManager().getTestLogDao()
//					.insert(new TestLog(WorkBench.currenTestData.getId(), "INFO", "软件已启动", new Date()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static Database2Manager getDatabaseManager() {
		return  dataManager;
	}

	

	/**
	 * 通过设备MAC寻找设备
	 * 
	 * @param calBoxMac
	 * @return
	 */
	public static Device getDeviceByMac(String deviceMac) {
		for (Device device : deviceList) {
			if (device.getMac().equals(deviceMac))
				return device;
		}
		return null;
	}

	/**
	 * 通过校准箱MAC寻找校准箱
	 * 
	 * @param calBoxMac
	 * @return
	 */
	public static CalBox getCalBoxByMac(String calBoxMac) {
		for (CalBox calBox : calBoxList) {
			if (calBox.getMac().equals(calBoxMac))
				return calBox;
		}
		return null;
	}

	/**
	 * 根据校准箱名称寻找校准箱
	 * 
	 * @param name
	 * @return
	 */
	public static CalBox getCalBoxByCalBoxName(String name) {
		for (CalBox calBox : calBoxList) {
			if (calBox.getName().equals(name))
				return calBox;
		}
		return null;
	}

	/**
	 * 通过设备名寻找设备
	 * 
	 * @param name
	 * @return
	 */
	public static Device getDeviceByName(String name) {
		for (Device device : deviceList) {
			if (device.getName().equals(name)) {
				return device;
			}
		}
		return null;
	}

	/**
	 * 返回设备通道中所有已对接的校准箱
	 * 
	 * @param channelList
	 *            设备通道List
	 * @return
	 */
	// public static List<CalBox> getCalBoxListByChannel(List<Channel> channelList)
	// {
	// List<CalBox> calBoxList = new ArrayList<>();
	// for (Channel channel : channelList) {
	// if (channel.getMatchedChannel() != null)
	// calBoxList.add(channel.getMatchedChannel().getCalBoard().getCalBox());
	// }
	// return calBoxList;
	// }
	
	/**
	 * 从数据库中加载所有设备
	 * @author  wavy_zheng
	 * 2021年1月17日
	 */
	public static void loadAllDevices() {
		
		
		
		
		
	}
	

	/**
	 * 发送配置命令
	 * 
	 * @param mainBoard
	 * @param sendData
	 * @param timeout
	 * @param info
	 * @return
	 */
	public static boolean configCommand(CalBox calBox, Data sendData, int timeout, StringBuffer info) {
		if (calBox == null || !calBox.getConnector().isConnected()) {
			info.append("网络连接异常！");
			return false;
		}
		calBox.getReceiveBufferMap().remove(sendData.hashCode());
		Decorator configDecorator = new ConfigDecorator(sendData);
		calBox.getConnector().send(configDecorator);
		Decorator responseDecorator = calBox.findResponseBy(sendData.getCodeKey(), timeout);
		try {
			if (responseDecorator == null) {
				throw new Exception("操作通信超时");
			} else if (responseDecorator.getDestData().getResult().getCode() != Result.SUCCESS) {
				throw new Exception("Result=" + responseDecorator.getDestData().getResult() + ", info="
						+ ((ResponseDecorator) responseDecorator).getInfo());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			info.append(e.getMessage());
			return false;
		}
	}



	public static CalboxService getBoxService() {
		return boxService;
	}

	
	
}
