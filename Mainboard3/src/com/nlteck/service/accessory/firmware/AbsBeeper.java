package com.nlteck.service.accessory.firmware;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午11:22:53
* 蜂鸣报警器
*/
public abstract class AbsBeeper {
    
	protected   int    index;
	protected   boolean use;
	
	protected AbsBeeper(int index) {
		
		this.index = index;
		use = MainBoard.startupCfg.getBeepAlertManagerInfo().beepInfos.get(index).isUseable();
	}
	
	/**
	 * 蜂鸣报警
	 * @author  wavy_zheng
	 * 2020年12月25日
	 * @param seconds
	 * @param twinkle
	 * @throws AlertException
	 */
	public abstract void  beep(int seconds , boolean twinkle) throws AlertException;
	
	/**
	 * 关闭蜂鸣器
	 * @author  wavy_zheng
	 * 2020年12月25日
	 * @throws AlertException
	 */
	public abstract void close() throws AlertException;

	public int getIndex() {
		return index;
	}

	public boolean isUse() {
		return use;
	}
	
	
	
	
	
	
}
