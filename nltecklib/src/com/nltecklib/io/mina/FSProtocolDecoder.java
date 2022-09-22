package com.nltecklib.io.mina;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.utils.LogUtil;




public class FSProtocolDecoder extends CumulativeProtocolDecoder {
    
	private boolean client;
	private NlteckPackageFactory  factory;
	private DebugDataListener     listener;
	
	public FSProtocolDecoder(boolean client, NlteckPackageFactory  factory){
		
		this.client = client;
		this.factory = factory;
		
	}
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		
		int minLen = factory.getMinDecodeLen();
		if(in.remaining() > minLen){
			
			in.mark();
			byte[] headArray = new byte[minLen];
			in.get(headArray);
			
			int packLen = factory.getPackLen(headArray);
			
			String localIp = ((InetSocketAddress)session.getLocalAddress()).getAddress().getHostAddress();
			String remoteIp = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();		
			
			if(in.remaining() >= packLen - minLen ){  //因前面取走了6字节,这里remain长度发生了变化
				 
				 in.reset(); //重置到快照标记
				 byte[] buff = new byte[packLen];
				 in.get(buff, 0, packLen);
				
				 NlteckIOPackage obj = null;
				 try {
					 obj = factory.decode(buff);
				 }catch(Exception e) {
					 
					//Logger logger =  LogUtil.getLogger(remoteIp + "_protocol_err");
					 Logger logger =  LogUtil.getLogger("info");
					
					logger.error(e.getMessage());
					logger.error(Arrays.toString(e.getStackTrace()));
					
					//发送消息
					 if(this.listener != null) {
						 
						 listener.onDecode(buff);
					 }
					
					throw new Exception("解析错误:" + LogUtil.printArray(buff));
				 }
				 out.write(obj);
				 
				 String log = "  <--" + (client ? "server(":"client(") + remoteIp + "):" +  LogUtil.printArray(buff);
				//TODO 添加一个消息
				 
				 
				 //发送消息
				 if(this.listener != null) {
					 
					 listener.onDecode(buff);
				 }
				 /**
					 * 禁止打印，会影响性能
					 */
				// Logger logger =  LogUtil.getLogger(remoteIp + "_protocol");
			     //Logger logger = Entity.getLogger(remoteIp + "/" + (obj.getCode() == MainEnvironment.MainCode.PickupCode || obj.getCode() == MainEnvironment.MainCode.DeviceStateCode ? remoteIp + "(pickup)" : remoteIp + "(config)"));
			    // logger.debug(log);
				 
				 
				 //System.out.println(log);
				 
			    
				 if(in.remaining() > 0){  //有粘包继续下一轮解析
				    
					//System.out.println("in.remaining() = " + in.remaining());
				    return true;
				 }
			}else{
				
				
				
				 //内容不够则进入下一轮，重置标记等待拼凑成完整的协议包再解析
				in.reset();
				return false;
				
				
			}
			

		}

		return false; //处理成功,接收下一个包
	}
	
	public void setListener(DebugDataListener listener) {
		this.listener = listener;
	}
	
	
	

}
