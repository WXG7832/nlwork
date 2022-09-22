package com.nlteck.service;

import com.nlteck.base.I18N;
import com.nlteck.fireware.CalibrateCore;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月26日 下午1:34:32 消息上报服务组件
 */
public class MessageService {

	private CalibrateCore core;
	private I18N i18n;

	public MessageService(CalibrateCore core, I18N i18n) {

		this.core = core;
		this.i18n = i18n;
	}

}
