package com.nlteck;

import com.nlteck.firmware.Channel;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

/**
 * 主控触发报警异常
 * 
 * @author Administrator
 *
 */
public class AlertException extends Exception {

	private AlertCode alertCode;
	private Channel    channel;
	
	public AlertException(AlertCode alertCode, String message) {
		this(null,alertCode,message);

	}
	/**
	 * 通道报警
	 * @param chn
	 * @param alertCode
	 * @param message
	 */
	public AlertException(Channel chn , AlertCode alertCode, String message) {
		super(message);
		this.alertCode = alertCode;
		this.channel = chn;

	}

	public AlertCode getAlertCode() {
		return alertCode;
	}
	public Channel getChannel() {
		return channel;
	}
	
	

}
