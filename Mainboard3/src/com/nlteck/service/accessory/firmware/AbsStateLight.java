package com.nlteck.service.accessory.firmware;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.StateLightInfo;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月24日 下午5:44:44
* 极性灯
*/
public abstract class AbsStateLight {
    
	protected int   index;
	protected boolean use;

	public AbsStateLight(int index) {
		super();
		this.index = index;
		
		StateLightInfo slf = MainBoard.startupCfg.getStateLightControllerInfo().stateLights.get(index);
		this.use = slf.use;
		
	}
	/**
	 * 点亮极性灯
	 * @author  wavy_zheng
	 * 2020年12月24日
	 * @param color
	 * @param twinkle  true 启用呼吸灯
	 */
	public abstract void light(LightColor  color , boolean twinkle) throws AlertException;
	
	
	public boolean isUse() {
		return use;
	}
	
	
	
	
	
	
	
	
	
}
