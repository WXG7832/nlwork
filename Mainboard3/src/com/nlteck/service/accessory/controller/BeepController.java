package com.nlteck.service.accessory.controller;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.BeepAlertInfo;
import com.nlteck.service.StartupCfgManager.StateLightInfo;
import com.nlteck.service.accessory.firmware.AbsBeeper;
import com.nlteck.service.accessory.firmware.real.SerialBeeper;
import com.nlteck.service.accessory.firmware.real.SerialStateLight;
import com.nlteck.service.accessory.firmware.virtual.VirtualBeeper;
import com.nlteck.service.accessory.firmware.virtual.VirtualStateLight;
import com.nltecklib.protocol.li.main.VoiceAlertData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月27日 上午11:46:38 蜂鸣管理器
 */
public class BeepController {

	protected VoiceAlertData beep = new VoiceAlertData();
	private List<AbsBeeper> beepers = new ArrayList<>();

	public BeepController(boolean virtual) {

		try {
			beep = Context.getFileSaveService().readBeepAlertFile();
		} catch (AlertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (BeepAlertInfo info : MainBoard.startupCfg.getBeepAlertManagerInfo().beepInfos) {

			beepers.add(virtual ? new VirtualBeeper(info.index) : new SerialBeeper(info.index));
		}
	}

	/**
	 * 执行蜂鸣
	 * 
	 * @author wavy_zheng 2020年11月27日
	 */
	public void beep(int index) throws AlertException {

		if (index > beepers.size() - 1) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.OperationError));
		}
		if (!beepers.get(index).isUse()) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.FirmIsDisabled));
		}
		if (beep.isAudioAlertOpen()) {

			beepers.get(index).beep(beep.getTime(), true);
		}
	}
	
	public void close(int index) throws AlertException {
		
		if (index > beepers.size() - 1) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.OperationError));
		}
		if (!beepers.get(index).isUse()) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.FirmIsDisabled));
		}
		if (beep.isAudioAlertOpen()) {

			beepers.get(index).close();
		}
		
	}
	

	/**
	 * 蜂鸣是否禁用?
	 * 
	 * @author wavy_zheng 2020年11月27日
	 * @return
	 */
	public boolean isBeepOn() {

		return beep.isAudioAlertOpen();
	}

	public VoiceAlertData getBeep() {
		return beep;
	}

	public void setBeep(VoiceAlertData beep) {
		this.beep = beep;
	}
	
	
	

}
