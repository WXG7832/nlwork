package com.nlteck;

import com.nlteck.alert.AlertProcessor;
import com.nlteck.controller.Controller;
import com.nlteck.controller.PCController;
import com.nlteck.controller.WorkformController;
import com.nlteck.firmware.GPIO;
import com.nlteck.firmware.GpioPowerController;
import com.nlteck.firmware.MainBoard;
import com.nlteck.firmware.PowerController;
import com.nlteck.firmware.PowerController.PowerListener;
import com.nlteck.firmware.VirtualPowerController;
import com.nlteck.service.AbsCheckBoardService;
import com.nlteck.service.AbsDriverBoardService;
import com.nlteck.service.AbsLogicBoardService;
import com.nlteck.service.AbsNetworkService;
import com.nlteck.service.ChannelIndexService;
import com.nlteck.service.ChnMapService;
import com.nlteck.service.CoreService;
import com.nlteck.service.FileSaveService;
import com.nlteck.service.LabDriverBoardService;
import com.nlteck.service.PCNetworkService;
import com.nlteck.service.SerialDriverBoardService;
import com.nlteck.service.SyncStepControlService;
import com.nlteck.service.UpgradeManager;
import com.nlteck.service.VirtualDriverBoardService;
import com.nlteck.service.WorkformNetworkService;
import com.nlteck.service.StartupCfgManager.DriverboardType;
import com.nlteck.service.accessory.AccessoriesService;
import com.nlteck.service.accessory.PortManager;
import com.nlteck.service.accessory.manager.AlertManager;
import com.nlteck.service.data.DataProviderService;
import com.nlteck.service.data.OfflineDataProviderService;
import com.nlteck.service.data.OnlineDataProviderService;
import com.nltecklib.protocol.li.main.AllowStepSkipData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月14日 上午11:16:24 环境上下文,保管各种服务组件
 */
public class Context {

	// 主控核心服务
	private static CoreService coreService;
   
	private static AbsNetworkService pcNetworkService; // 上位机
	private static WorkformNetworkService workformNetworkService; // 校准主控通信服务组件
	private static AbsDriverBoardService driverboardService; //驱动板核心服务

	private static DataProviderService dataProvider; // 数据实时在线提供服务
	private static SyncStepControlService  syncStepControlService; //同步服务
	
	private static ChannelIndexService   channelIndexService; //通道序号定义服务

	private static FileSaveService fileSaveService; // 文件保存服务
	
	private static ChnMapService   chnMapService; // 通道映射服务

	private static Controller pcController; // 上位机控制器
	private static WorkformController workformController; // 校准控制器

	private static RunningLamp runningLamp; // 主控运行灯
	private static PowerController powerProvider; // 供电组件

	private static PortManager portManager; // 串口管理器
	
	private static AccessoriesService  accessoriesService; //配件服务
	private static AlertManager        alertManager;  //设备报警管理
	
	private static UpgradeManager      upgradeManager; //升级管理器

	/**
	 * 创建所有服务组件
	 * 
	 * @author wavy_zheng 2020年11月14日
	 * @param mb
	 *            主控对象
	 * @param virtual
	 *            是否是虚拟组件
	 * @throws AlertException
	 */
	public static void initServices(MainBoard mb, boolean virtual) throws AlertException {

		

		coreService = new CoreService(mb);
		
		channelIndexService = new ChannelIndexService(mb);

		pcNetworkService = new PCNetworkService(mb);

		workformNetworkService = new WorkformNetworkService(mb);
       
		if(virtual) {
			
			driverboardService = new VirtualDriverBoardService(mb);
		} else {
			
			if(MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
				
				driverboardService = new SerialDriverBoardService(mb);
			} else if(MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {
				
				driverboardService = new LabDriverBoardService(mb); //使用实验室驱动板服务
			}
		}
		//driverboardService = virtual ? new VirtualDriverBoardService(mb) : new SerialDriverBoardService(mb);
        
		pcController = new PCController(mb);

		workformController = new WorkformController(mb);

		dataProvider = new OfflineDataProviderService(mb);

		fileSaveService = new FileSaveService(mb);
		
		chnMapService   = new ChnMapService();
		
		accessoriesService = new AccessoriesService(mb);
         
		portManager = new PortManager();
		
		alertManager = new AlertManager(mb);
		
		syncStepControlService = new SyncStepControlService(mb);
		
		upgradeManager = new UpgradeManager();
		
		/**
		 * 已经移植到初始化模块
		 */
		/*powerProvider = virtual ? new VirtualPowerController() :  new GpioPowerController(new GPIO(GpioPowerController.POWER_CONTROL_PIN),
				new GPIO(GpioPowerController.POWER_CHCEK_PIN));
		
		powerProvider.addPowerListener(new PowerListener() {
			
			@Override
			public void powerOff() {
				
				Context.getCoreService().powerOff();
				
			}
		});*/
		
		
		if (!virtual) {
			
			portManager.init(virtual); //初始化串口

			runningLamp = new RunningLamp(new GPIO(GpioPowerController.LAMP_CONTROL_PIN));

			
            
			
			

		}

	}
	
	
	

	public static RunningLamp getRunningLamp() {
		return runningLamp;
	}




	public static PowerController getPowerProvider() {
		return powerProvider;
	}




	public static CoreService getCoreService() {
		return coreService;
	}

	public static AbsNetworkService getPcNetworkService() {
		return pcNetworkService;
	}

	public static WorkformNetworkService getWorkformNetworkService() {
		return workformNetworkService;
	}

	

	public static Controller getPcController() {
		return pcController;
	}

	public static WorkformController getWorkformController() {
		return workformController;
	}
    
	/**
	 * 根据工作环境切换数据提供器
	 * @author  wavy_zheng
	 * 2020年12月15日
	 * @return
	 */
	public static DataProviderService getDataProvider() {
		return dataProvider;
	}

	public static void setDataProvider(DataProviderService dataProvider) {
		Context.dataProvider = dataProvider;
	}

	public static FileSaveService getFileSaveService() {
		return fileSaveService;
	}

	public static PortManager getPortManager() {
		return portManager;
	}

	public static AccessoriesService getAccessoriesService() {
		return accessoriesService;
	}

	public static AlertManager getAlertManager() {
		return alertManager;
	}

	public static SyncStepControlService getSyncStepControlService() {
		return syncStepControlService;
	}

	public static UpgradeManager getUpgradeManager() {
		return upgradeManager;
	}

	public static ChannelIndexService getChannelIndexService() {
		return channelIndexService;
	}




	public static AbsDriverBoardService getDriverboardService() {
		return driverboardService;
	}




	public static void setPowerProvider(PowerController powerProvider) {
		Context.powerProvider = powerProvider;
	}

	

}
