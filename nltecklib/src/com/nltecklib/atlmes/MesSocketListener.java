package com.nltecklib.atlmes;

import com.nltecklib.protocol.atlmes.Root;

public interface MesSocketListener {
	/**
	 * 监听网口数据
	 * @param data
	 */
	public void revData(Root data);
	/**
	 * 监听传输日志
	 * @param jsonStr
	 */
	void sendMesResult(Root root,boolean send);
	

}
