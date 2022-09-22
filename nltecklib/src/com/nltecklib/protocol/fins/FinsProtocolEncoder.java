package com.nltecklib.protocol.fins;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 电池化成分选formation separation 协议编码转换
 * @author Administrator
 *
 */
public class FinsProtocolEncoder implements ProtocolEncoder {
     
	
	private static Logger log = Logger.getLogger(FinsProtocolEncoder.class);
	
	private boolean client;
	private FinsMessageReceiver  receiver;
	
	public FinsProtocolEncoder(boolean client , FinsMessageReceiver  receiver){
		this.client = client;
		this.receiver = receiver;
	}
	@Override
	public void dispose(IoSession session) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {
		
		byte[] data = ((PlcData) message).encode();
		
		System.out.println("发送数据：(" + System.currentTimeMillis() + ") " + PlcData.printList(data));

		IoBuffer buff = IoBuffer.allocate(data.length, false);
		buff.put(data);
		buff.flip();
		out.write(buff);
	}
	
}
