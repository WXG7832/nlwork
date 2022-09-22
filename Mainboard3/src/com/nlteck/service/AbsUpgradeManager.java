package com.nlteck.service;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.firmware.CheckBoard;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;

/**
* @author  wavy_zheng
* @version 创建时间：2021年8月18日 下午2:52:57
* 抽象升级管理器
*/
public abstract class AbsUpgradeManager {
   
	protected Logger logger;
	protected  final static int PACK_SIZE = 1024;
	
	public final static String CORE_FILENAME = "mainboard.jar";
	public final static String CFG_FILENAME = "cfg.xml";
	public final static String LOGIC_DRIVER_FILENAME = "logicDriver.bin";
	public final static String CHECK_DRIVER_FILENAME = "checkDriver.bin";
	public final static String PICKUP_DRIVER_FILENAME = "pickupDriver.bin";
	public final static String TEMP_DRIVER_FILENAME = "tempDriver.bin";
	
	
	protected String logicDriverFileName = LOGIC_DRIVER_FILENAME;
	protected String checkDriverFileName = CHECK_DRIVER_FILENAME;
	protected String coreFileName = CFG_FILENAME;
	protected String coreCfgFileName = CFG_FILENAME;
	protected String pickupDriverFileName = PICKUP_DRIVER_FILENAME;
	protected String tempDriverFileName = TEMP_DRIVER_FILENAME;
	
	protected final static String FTP_PATH = "/var/ftp/pub";
	
	
	protected AbsUpgradeManager() {
		
		try {
			logger = LogUtil.createLog("log/upgradeManager.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 升级核心板配置文件
	 * @author  wavy_zheng
	 * 2021年8月18日
	 * @param fileName
	 * @throws AlertException
	 */
	public abstract void upgradeCoreCfgFile(String fileName) throws AlertException;
	
	
	/**
	 * 升级主控
	 * @author  wavy_zheng
	 * 2021年8月18日
	 * @param fileName
	 * @throws AlertException
	 */
	public abstract void upgradeCoreFile(String fileName) throws AlertException;
	
	/**
	 * 升级驱动板及子板程序
	 * @author  wavy_zheng
	 * 2022年1月22日
	 * @param db
	 * @param fileName
	 * @param type
	 * @throws AlertException
	 */
	public abstract void upgradeDriverProgram(DriverBoard db, String fileName , UpgradeType type) throws AlertException ;


	public String getPickupDriverFileName() {
		return pickupDriverFileName;
	}


	public String getTempDriverFileName() {
		return tempDriverFileName;
	}
	
	
	
}
