package com.nlteck.listener;

import com.nlteck.firmware.Channel;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
/**
 * 分区通道事件监听器
 * @author Administrator
 *
 */
public interface ChannelListener {
	
	/**
	 * 通道状态发生变更
	 * @param chn
	 * @param oldState
	 * @param newState
	 */
	void stateChanged(Channel chn , ChnState oldState, ChnState newState);
	
	/**
	 * 通道模式发生变更
	 * @param chn
	 * @param oldMode
	 * @param newMode
	 */
	void modeChanged(Channel chn , WorkMode oldMode , WorkMode newMode);
	


}
