package com.nlteck.service.accessory.firmware;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午10:14:17
* 类说明
*/
public abstract class AbsTripleColorLight {
    
	protected int        index;
	protected boolean    use;
	
	protected AbsTripleColorLight(int index) {
		super();
		this.index = index;
		use = MainBoard.startupCfg.getColorLightManagerInfo().colorLights.get(index).use;
	}
	
	
	
	
	public int getIndex() {
		return index;
	}




	public boolean isUse() {
		return use;
	}




	/**
	 * 点亮三色灯
	 * @author  wavy_zheng
	 * 2020年12月25日
	 * @param color
	 * @param twinkle
	 * @throws AlertException
	 */
	public abstract void light(LightColor  color , boolean twinkle) throws AlertException;
	
	
	
	
	
	
}
