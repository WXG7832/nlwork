package com.nltecklib.atlmes;

import com.nltecklib.protocol.atlmes.RequestRoot;
import com.nltecklib.protocol.atlmes.Root;

public interface MesListener {
	/**
	 * º‡Ã˝MES«Î«Û
	 * 
	 * @param data
	 */
	void receiveRequestData(RequestRoot data);
	
	/**
	 * 
	 */
	void sendMesResult(Root root,boolean send);
}
