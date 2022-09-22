package com.nlteck.component;

public interface ScreenEvent {

	public enum EventType {

		/**
		 * 修改IP地址事件,修改密码事件
		 */
		CHANGE_IP_ADDRESS, CHANGE_PASSWORD
	}

	/**
	 * 用户操作事件监视
	 * 
	 * @author Administrator
	 *
	 */

	public void onClick(EventType eventType);
	

}
