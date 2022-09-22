package com.nltecklib.protocol.fins;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Command;

/**
 * 
* @ClassName: HandshakeData  
* @Description: 握手协议
* @author zhang_longyong  
* @date 2019年12月20日
 */
public class HandshakeData extends Data {
	
	public HandshakeData() {
		command = Command.HANDSHAKE;
	}

	@Override
	public void encode() {
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
	}
	
}
