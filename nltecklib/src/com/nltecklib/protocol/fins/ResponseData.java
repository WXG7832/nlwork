package com.nltecklib.protocol.fins;

import java.util.List;

/**
 * 
* @ClassName: ResponseData  
* @Description: fins协议统一回复类 
* @author zhang_longyong  
* @date 2019年12月20日
 */
public class ResponseData extends Data {

	@Override
	public void encode() {
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
	}
	
}
