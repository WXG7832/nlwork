package com.nlteck.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.CheckBoard;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.FileUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.check2.Check2Environment.CheckWorkMode;
import com.nltecklib.protocol.li.check2.Check2ProgramStateData;
import com.nltecklib.protocol.li.check2.Check2StartupData;
import com.nltecklib.protocol.li.check2.Check2UpgradeData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.LogicState;
import com.nltecklib.protocol.li.logic2.Logic2ProgramStateData;
import com.nltecklib.protocol.li.logic2.Logic2StateData;
import com.nltecklib.protocol.li.logic2.Logic2UpgradeData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.li.main.UpgradeProgressData;
import com.nltecklib.protocol.power.driver.DriverUpgradeData;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.BaseUtil;

/**
 * 主控升级程序管理
 * 
 * @author Administrator
 *
 */
public class UpgradeManager extends AbsUpgradeManager{

	private int packIndex; // 当前升级包数
	private int packCount; // 包总数

	private final static int PACK_SIZE = 1024;

	

	public UpgradeManager() {

		

	}
	

	/**
	 * 升级驱动板程序
	 * 
	 * @author wavy_zheng 2021年1月2日
	 * @param lb
	 *            驱动板对象
	 * @param driverIndexFlag
	 *            ; 需要升级的驱动板序号集合 0xffff,升级逻辑板下所有的驱动板
	 * @throws AlertException
	 */
	public void upgradeDriverProgram(DriverBoard db, String fileName , UpgradeType type) throws AlertException {

		if (db.getControlUnit().getState() == State.FORMATION) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeFileStateErr,fileName));
		}
		logger.info("start upgrade  driver " + (db.getDriverIndex() + 1) + " " + type.name() + " program");
		File file = new File(FTP_PATH + "/" + fileName);
		if (!file.exists()) {

			logger.error(file.getName() + " do not exist!");
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeFileMissing,file.getName()));
		}
		byte[] content = null;
		try {
			content = FileUtil.getBytes(file);
		} catch (IOException e) {

			e.printStackTrace();
			logger.error(BaseUtil.getThrowableException(e));
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeFileFail, e.getMessage()));
		}

		long fileSize = content.length;
		int packCount = (int) (fileSize / PACK_SIZE + (fileSize % PACK_SIZE == 0 ? 0 : 1));
		logger.info("split " + packCount + " packs");
		
		logger.info("sleep 4 s");
		CommonUtil.sleep(4000);


		logger.info("start to upgrade  driver " + (db.getDriverIndex() + 1) + " program!");

		for (int n = 0; n < packCount; n++) {

			DriverUpgradeData  lud = new DriverUpgradeData();
			lud.setDriverIndex(db.getDriverIndex());
			lud.setFileSize((int) fileSize);
			lud.setPackIndex(n + 1);
			lud.setPackCount(packCount);
			byte[] pack = Arrays.copyOfRange(content, n * PACK_SIZE,
					n == packCount - 1 ? (int) fileSize : (n + 1) * PACK_SIZE);
			lud.setPackContent(ProtocolUtil.convertArrayToList(pack));
			Context.getDriverboardService().writeUpgrade(lud);
			logger.info("upgrade driver " + (db.getDriverIndex() + 1) +
					" ,progress: " + (n + 1) + "/" + packCount);

			UpgradeProgressData upd = new UpgradeProgressData();
			upd.setUnitIndex(0);
			upd.setDriverIndex(db.getDriverIndex());
			upd.setUpgradeType(type);
			upd.setRange(packCount);
			upd.setPos(n + 1);
			Context.getPcNetworkService().pushSendQueue(new AlertDecorator(upd));
			CommonUtil.sleep(500);

		}
		
	}

	/**
	 * 升级配置文件
	 * 
	 * @author wavy_zheng 2021年1月3日
	 * @throws AlertException
	 */
	public void upgradeCoreCfgFile(String fileName) throws AlertException {

		logger.info("start upgrade core cfg ");
		File file = new File(FTP_PATH + "/" + fileName);

		if (!file.exists()) {

			logger.error(file.getName() + " do not exist!");
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeCoreCfgMissing));
		}

		try {
			FileUtil.moveFile(file, new File("/home/root/config/" + CFG_FILENAME));
		} catch (IOException e) {

			logger.error(BaseUtil.getThrowableException(e));
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeCoreCfgFail));
		}

		UpgradeProgressData upd = new UpgradeProgressData();
		upd.setUnitIndex(0);
		upd.setUpgradeType(UpgradeType.CoreCfg);
		upd.setRange(100);
		upd.setPos(100);
		Context.getPcNetworkService().pushSendQueue(new AlertDecorator(upd));
		BaseUtil.sleep(500);

	}

	/**
	 * 升级主控
	 * 
	 * @param packIndex
	 * @param packCount
	 * @param byteCount
	 * @param content
	 * @throws AlertException
	 */
	public void upgradeCoreFile(String fileName) throws AlertException {

		logger.info("start upgrade core board program");
		File file = new File(FTP_PATH + "/" + fileName);
		
		UpgradeProgressData upd = new UpgradeProgressData();
		upd.setUnitIndex(0);
		upd.setUpgradeType(UpgradeType.Core);
		upd.setRange(100);

		if (!file.exists()) {

			logger.error(file.getName() + " do not exist!");
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeCoreMissing));
		}

		/*try {
			FileUtil.moveFile(file, new File("/home/root/" + CORE_FILENAME));
		} catch (IOException e) {

			logger.error(BaseUtil.getThrowableException(e));
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeCoreFail));
		}
		logger.info("reboot core board");*/
		
		
		upd.setPos(100);
		Context.getPcNetworkService().pushSendQueue(new AlertDecorator(upd));
		/*
		BaseUtil.sleep(2000);

		// 重启
		try {
			Environment.reboot();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeCoreFail));
		}
		*/

	}

	

	public String getLogicDriverFileName() {
		return logicDriverFileName;
	}

	public void setLogicDriverFileName(String logicDriverFileName) {
		this.logicDriverFileName = logicDriverFileName;
	}

	public String getCheckDriverFileName() {
		return checkDriverFileName;
	}

	public void setCheckDriverFileName(String checkDriverFileName) {
		this.checkDriverFileName = checkDriverFileName;
	}

	public String getCoreFileName() {
		return coreFileName;
	}

	public void setCoreFileName(String coreFileName) {
		this.coreFileName = coreFileName;
	}

	public String getCoreCfgFileName() {
		return coreCfgFileName;
	}

	public void setCoreCfgFileName(String coreCfgFileName) {
		this.coreCfgFileName = coreCfgFileName;
	}
	
	

}
