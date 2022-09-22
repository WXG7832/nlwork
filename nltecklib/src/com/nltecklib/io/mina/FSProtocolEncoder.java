package com.nltecklib.io.mina;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.utils.LogUtil;



/**
 * 电池化成分选formation separation 协议编码转换
 * @author Administrator
 *
 */
public class FSProtocolEncoder implements ProtocolEncoder {
     
	private boolean client;
	private NlteckPackageFactory  factory;
	private DebugDataListener     listener;
	
	public FSProtocolEncoder(boolean client , NlteckPackageFactory  factory){
		this.client = client;
		this.factory = factory;
		
	}
	@Override
	public void dispose(IoSession session) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {
		
		NlteckIOPackage pack = (NlteckIOPackage)message;
		String remoteIp = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
		
		byte[] encodeData = factory.encode(pack);
		
		String log = "-->" + (client ? "server(":"client(") + remoteIp + "):"  +  LogUtil.printArray(encodeData);
		
		/**
		 * 禁止打印，会影响性能
		 */
//		Logger logger = LogUtil.getLogger( remoteIp + "_protocol");
//		logger.debug(log);
		
		if(this.listener != null) {
			
			this.listener.onEncode(encodeData);
		}
		
		
		IoBuffer buff = IoBuffer.allocate(encodeData.length, false);
		buff.put(encodeData);
		buff.flip();
		out.write(buff);
		

	}
	
	public void setListener(DebugDataListener listener) {
		this.listener = listener;
	}
	
	

}
