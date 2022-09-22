package com.nlteck.service.accessory.controller;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.ColorLightInfo;
import com.nlteck.service.StartupCfgManager.StateLightInfo;
import com.nlteck.service.accessory.firmware.AbsTripleColorLight;
import com.nlteck.service.accessory.firmware.real.SerialStateLight;
import com.nlteck.service.accessory.firmware.real.SerialTripleColorLight;
import com.nlteck.service.accessory.firmware.virtual.VirtualStateLight;
import com.nlteck.service.accessory.firmware.virtual.VirtualTripleColorLight;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午9:59:42
* 三色灯控制器
*/
public  class TripleColorLightController {
   
	private boolean use;
	private List<AbsTripleColorLight>  lightors = new ArrayList<>();
	
	public TripleColorLightController(boolean virtual) {
		
		use = MainBoard.startupCfg.getColorLightManagerInfo().use;
		for (ColorLightInfo info : MainBoard.startupCfg.getColorLightManagerInfo().colorLights) {

			lightors.add(virtual ? new VirtualTripleColorLight(info.index)
					: new SerialTripleColorLight(info.index));
		}
	}
	
	
	public void light(int index , LightColor color , boolean twinkle) throws AlertException {
		
		if (index > lightors.size() - 1) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.OperationError));
		}
		if (!lightors.get(index).isUse()) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.FirmIsDisabled));
		}
		
		lightors.get(index).light(color, twinkle);
	}


	public boolean isUse() {
		return use;
	}
	
	
	
	
}
