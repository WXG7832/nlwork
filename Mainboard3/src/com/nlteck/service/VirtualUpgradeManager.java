package com.nlteck.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.CheckBoard;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.FileUtil;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.logic2.Logic2UpgradeData;
import com.nltecklib.protocol.li.main.UpgradeProgressData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.BaseUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年8月18日 下午4:23:00
* 类说明
*/
public class VirtualUpgradeManager extends AbsUpgradeManager {


	/*@Override
	public void upgradeLogicProgram(LogicBoard lb, String fileName) throws AlertException {
		
		if (lb.getControlUnit().getState() == State.FORMATION) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeLogicFail));
		}
		logger.info("start upgrade logic " + lb.getLogicIndex() + " program");
		File file = new File(FTP_PATH + "/" + fileName);
		if (!file.exists()) {

			logger.error(file.getName() + " do not exist!");
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeLogicMissing));
		}
		byte[] content = null;
		try {
			content = FileUtil.getBytes(file);
		} catch (IOException e) {

			e.printStackTrace();
			logger.error(BaseUtil.getThrowableException(e));
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeLogicFail) + ":" + e.getMessage());
		}

		long fileSize = content.length;
		int packCount = (int) (fileSize / PACK_SIZE + (fileSize % PACK_SIZE == 0 ? 0 : 1));
		logger.info("split " + packCount + " packs");
          
		logger.info("sleep 2 s");
		CommonUtil.sleep(2000);
		

		for (int n = 0; n < packCount; n++) {

			Logic2UpgradeData lud = new Logic2UpgradeData();

			lud.setUnitIndex(lb.getLogicIndex());
			lud.setFileSize((int) fileSize);
			lud.setPackIndex(n + 1);
			lud.setPackCount(packCount);
			byte[] pack = Arrays.copyOfRange(content, n * PACK_SIZE,
					n == packCount - 1 ? (int) fileSize : (n + 1) * PACK_SIZE);
			lud.setPackContent(ProtocolUtil.convertArrayToList(pack));
			Context.getLogicboardService().writeUpgradeData(lud , 8000);
			logger.info("upgrade logic " + lb.getLogicIndex() + " progress: " + (n + 1) + "/" + packCount);

			UpgradeProgressData upd = new UpgradeProgressData();
			upd.setUnitIndex(lb.getLogicIndex());
			upd.setUpgradeType(UpgradeType.Logic);
			upd.setRange(packCount);
			upd.setPos(n + 1);
			Context.getPcNetworkService().pushSendQueue(new AlertDecorator(upd));
			
			CommonUtil.sleep(500);

		}


	}*/

	@Override
	public void upgradeDriverProgram(DriverBoard lb, String fileName, UpgradeType type) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void upgradeCoreCfgFile(String fileName) throws AlertException {
		// TODO Auto-generated method stub

	}

	@Override
	public void upgradeCoreFile(String fileName) throws AlertException {
		// TODO Auto-generated method stub

	}

}
