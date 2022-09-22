package com.nlteck.service.accessory.controller;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.StateLightInfo;
import com.nlteck.service.accessory.firmware.AbsStateLight;
import com.nlteck.service.accessory.firmware.real.SerialStateLight;
import com.nlteck.service.accessory.firmware.virtual.VirtualStateLight;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * ×´Ì¬µÆ¿ØÖÆÆ÷
 * 
 * @author Administrator
 *
 */
public class StateLightController {

	private boolean use;
	private MainBoard mainboard;
	private List<AbsStateLight> lights = new ArrayList<>();
	


	public StateLightController(MainBoard mainboard , boolean virtual) {

		this.mainboard = mainboard;
		this.use = MainBoard.startupCfg.getStateLightControllerInfo().use;
		for (StateLightInfo info : MainBoard.startupCfg.getStateLightControllerInfo().stateLights) {

			lights.add(virtual ? new VirtualStateLight(info.index)
					: new SerialStateLight(info.index));
		}
	}

	/**
	 * µãµÆ
	 * 
	 * @author wavy_zheng 2020Äê12ÔÂ24ÈÕ
	 * @param index
	 * @param light
	 * @param twinkle
	 *            true ºôÎüµÆ
	 * @throws AlertException
	 */
	public void light(int unitIndex, LightColor light, boolean twinkle) throws AlertException {

		boolean allLight = true;
		
		if (mainboard.getState() == State.CAL) {

			allLight = true;
		}

		if (allLight) {

			for (int index = 0; index < lights.size(); index++) {
				
				if (index > lights.size() - 1) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.OperationError));
				}
				if (!lights.get(index).isUse()) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.FirmIsDisabled));
				}
				lights.get(index).light(light, twinkle);
			}
		} else {
			
			if (unitIndex > lights.size() - 1) {

				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.OperationError));
			}
			if (!lights.get(unitIndex).isUse()) {

				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.FirmIsDisabled));
			}
			lights.get(unitIndex).light(light, twinkle);
		}

	}

	
	
	

}
