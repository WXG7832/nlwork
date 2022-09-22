package com.nltecklib.protocol.fins;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


public class FinsProtocolCodecFactory implements ProtocolCodecFactory {
    
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	private boolean client;   //是否用于客户端协议解析
	private FinsMessageReceiver   receiver;
	
	public FinsProtocolCodecFactory(boolean client , FinsMessageReceiver receiver){
		
		this.client = client;
		this.receiver = receiver;
		
			
		encoder = new FinsProtocolEncoder(client,receiver);
		decoder = new FinsProtocolDecoder(client,receiver);
		
	}
	
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		
		return  decoder;
	}

}
