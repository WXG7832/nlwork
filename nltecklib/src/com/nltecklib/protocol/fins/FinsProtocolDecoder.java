package com.nltecklib.protocol.fins;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.nltecklib.protocol.plc.PlcData;


public class FinsProtocolDecoder extends CumulativeProtocolDecoder {

	private boolean client;
	private FinsMessageReceiver  receiver;
	
	private static final String FINS = "FINS";				// 协议头
	private static final int HEAD_LENGTH = 4;				// 协议头长度
	private static final int ERROR_CODE_LENGTH = 4;			// 错误码长度
	private static final int MIN_LENGTH = 24;				// fins协议最小长度
	private static final int MIN_DATA_LENGTH = 6;			// fins协议数据区最小长度
	private static final int ERROR_INDEX = 15;  			// fins协议错误码所在下标
	private static final int HANDSHAKE_DATA_LENGTH = 0x10;	// fins协议握手包数据长度
	private static final int WRITE_SUCCESS_LENGTH = 0x16;	// 写入成功时数据区长度

	public FinsProtocolDecoder(boolean client , FinsMessageReceiver  receiver){

		this.client = client;
		this.receiver = receiver;
	}
	
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

		if(in.remaining() >= MIN_LENGTH){

			in.mark();
			byte[] buffer = new byte[MIN_LENGTH];
			in.get(buffer);
			
			System.out.println("接收数据：(" + System.currentTimeMillis() + ") " + PlcData.printList(buffer));
			
			// 检查协议头
			checkHead(buffer);

			// 检查错误码
			checkErrorCode(buffer[ERROR_INDEX]);
			
			// 数据长度
			int len = getDataLength(buffer);
			
			// 判断是不是握手包返回数据
			if(len == HANDSHAKE_DATA_LENGTH){

				if(buffer[11] != 1) throw new RuntimeException("The command is error");
				
				// 本机IP节点
				FinsTcpConnector.client[0] = buffer[16];
				FinsTcpConnector.client[1] = buffer[17];
				FinsTcpConnector.client[2] = buffer[18];
				FinsTcpConnector.client[3] = buffer[19];
				// 服务端IP节点
				FinsTcpConnector.server[0] = buffer[20];
				FinsTcpConnector.server[1] = buffer[21];
				FinsTcpConnector.server[2] = buffer[22];
				FinsTcpConnector.server[3] = buffer[23];
				
				ResponseData responseData = new ResponseData();
				out.write(responseData);
				return false;
				
			} else if(in.remaining() < MIN_DATA_LENGTH){		//前面已经取走了24位
				
				//内容不够则进入下一轮，重置标记等待拼凑成完整的协议包再解析
				in.reset();
				return false;
			}
			
			in.reset();
			buffer = new byte[MIN_LENGTH + MIN_DATA_LENGTH];
			in.get(buffer);
			
			// 检查读取/写入结果
			checkResultData(buffer);
			
			// 写入成功
			if (len == WRITE_SUCCESS_LENGTH){
				
				ResponseData responseData = new ResponseData();
				out.write(responseData);
				return false;
				
			} else {
				
				// 没有读到完整数据
				if (in.remaining() < len - MIN_LENGTH - MIN_DATA_LENGTH){
					
					in.reset();
					return false;
				}

				// 去掉协议头、错误码、节点数据等等 真正读取到的数据的长度
				int readDataLength = len - (MIN_LENGTH + MIN_DATA_LENGTH - HEAD_LENGTH - ERROR_CODE_LENGTH);
				byte[] data = new byte[readDataLength];
				in.get(data, 0, readDataLength);
				System.out.println("数据区内容:  "+ PlcData.printList(data));
				ResponseData responseData = new ResponseData();
				//responseData.setData(data);
				out.write(responseData);

				if(in.remaining() > 0){  //有粘包继续下一轮解析
					
					return true;
				}
			}
		}

		return false; //处理成功,接收下一个包
	}
	
	
	/**
	 * 检查协议头
	 * 
	 * @param buffer 1-4位
	 */
	private void checkHead(byte[] buffer){
		
		if(!FINS.equals(new String(buffer, 0, 4))){
			
			throw new RuntimeException("the head is error");
		}
	}
	
	/**
	 * 数据长度
	 * 
	 * @param buffer 5-8位
	 * @return
	 */
	private int getDataLength(byte[] buffer){
		
		return ((buffer[4] & 0x0ff) << 24) + 
				((buffer[5] & 0x0ff) << 16) + 
				((buffer[6] & 0x0ff) << 8) + 
				(buffer[7] & 0x0ff);
	}
	
	/**
	 * 检查错误码
	 * 
	 * @param data
	 */
	private void checkErrorCode(byte data){
		
		switch (data) {
		case 0x01:
			throw new RuntimeException("The header is not FINS");
		case 0x02:
			throw new RuntimeException("The data length is too long");
		case 0x03:
			throw new RuntimeException("The command is not supported");
		case 0x20:
			throw new RuntimeException("All connections ars is use");
		case 0x21:
			throw new RuntimeException("The specified node is already connected");
		case 0x22:
			throw new RuntimeException("Attempt to access a protected node from an"
					+ "unspecified IP address");
		case 0x23:
			throw new RuntimeException("The client FINS node address is out of range");
		case 0x24:
			throw new RuntimeException("The same FINS node address is being used by"
					+ "the client and server");
		case 0x25:
			throw new RuntimeException("All the node addresses available for allocation"
					+ "have been userd");
		}
	}
	
	/**
	 * 检查结果
	 * 
	 * @param buffer 28-29位 
	 */
	private void checkResultData(byte[] buffer){
		
		//end code err
		if (buffer[28] != 0 && buffer[29] != 0) {

			switch (buffer[28]) {
			case 0x00:
				if (buffer[29] == 0x01) 
					throw new RuntimeException("service canceled"); 
			case 0x01:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("local node not in network"); 
				case 0x02:
					throw new RuntimeException("token timeout"); 
				case 0x03: 
					throw new RuntimeException("retries failed"); 
				case 0x04: 
					throw new RuntimeException("too many send frames");
				case 0x05: 
					throw new RuntimeException("node address range error");
				case 0x06: 
					throw new RuntimeException("node address duplication"); 
				}
				break;
			case 0x02:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("destination node not in network"); 
				case 0x02: 
					throw new RuntimeException("unit missing"); 
				case 0x03:
					throw new RuntimeException("third node missing"); 
				case 0x04: 
					throw new RuntimeException("destination node busy"); 
				case 0x05: 
					throw new RuntimeException("response timeout"); 
				}
				break;
			case 0x03:
				switch (buffer[29]) {
				case 0x01:
					throw new RuntimeException("communications controller error"); 
				case 0x02:
					throw new RuntimeException("CPU unit error"); 
				case 0x03:
					throw new RuntimeException("controller error"); 
				case 0x04:
					throw new RuntimeException("unit number error"); 
				}
				break;
			case 0x04:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("undefined command"); 
				case 0x02:
					throw new RuntimeException("not supported by model/version"); 
				}
				break;
			case 0x05:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("destination address setting error"); 
				case 0x02:
					throw new RuntimeException("no routing tables"); 
				case 0x03: 
					throw new RuntimeException("routing table error"); 
				case 0x04:
					throw new RuntimeException("too many relays"); 
				}
				break;
			case 0x10:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("command too long"); 
				case 0x02:
					throw new RuntimeException("command too short"); 
				case 0x03: 
					throw new RuntimeException("elements/data don't match"); 
				case 0x04: 
					throw new RuntimeException("command format error"); 
				case 0x05: 
					throw new RuntimeException("header error"); 
				}
				break;
			case 0x11:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("area classification missing"); 
				case 0x02:
					throw new RuntimeException("access size error"); 
				case 0x03:
					throw new RuntimeException("address range error"); 
				case 0x04: 
					throw new RuntimeException("address range exceeded"); 
				case 0x06: 
					throw new RuntimeException("program missing"); 
				case 0x09: 
					throw new RuntimeException("relational error"); 
				case 0x0a: 
					throw new RuntimeException("duplicate data access"); 
				case 0x0b:
					throw new RuntimeException("response too long"); 
				case 0x0c:
					throw new RuntimeException("parameter error"); 
				}
				break;
			case 0x20:
				switch (buffer[29]) {
				case 0x02:
					throw new RuntimeException("protected"); 
				case 0x03: 
					throw new RuntimeException("table missing"); 
				case 0x04: 
					throw new RuntimeException("data missing"); 
				case 0x05:
					throw new RuntimeException("program missing"); 
				case 0x06: 
					throw new RuntimeException("file missing"); 
				case 0x07:
					throw new RuntimeException("data mismatch"); 
				}
				break;
			case 0x21:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("read-only"); 
				case 0x02: 
					throw new RuntimeException("protected , cannot write data link table"); 
				case 0x03: 
					throw new RuntimeException("cannot register"); 
				case 0x05: 
					throw new RuntimeException("program missing"); 
				case 0x06: 
					throw new RuntimeException("file missing"); 
				case 0x07: 
					throw new RuntimeException("file name already exists"); 
				case 0x08: 
					throw new RuntimeException("cannot change"); 
				}
				break;
			case 0x22:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("not possible during execution"); 
				case 0x02:
					throw new RuntimeException("not possible while running"); 
				case 0x03: 
					throw new RuntimeException("wrong PLC mode"); 
				case 0x04: 
					throw new RuntimeException("wrong PLC mode"); 
				case 0x05: 
					throw new RuntimeException("wrong PLC mode"); 
				case 0x06: 
					throw new RuntimeException("wrong PLC mode"); 
				case 0x07: 
					throw new RuntimeException("specified node not polling node"); 
				case 0x08:
					throw new RuntimeException("step cannot be executed"); 
				}
				break;
			case 0x23:
				switch (buffer[29]) {
				case 0x01:
					throw new RuntimeException("file device missing"); 
				case 0x02:
					throw new RuntimeException("memory missing"); 
				case 0x03: 
					throw new RuntimeException("clock missing"); 
				}
				break;
			case 0x24:
				if (buffer[29] == 0x01) 
					throw new RuntimeException("table missing"); 
			case 0x25:
				switch (buffer[29]) {
				case 0x02: 
					throw new RuntimeException("memory error"); 
				case 0x03: 
					throw new RuntimeException("I/O setting error"); 
				case 0x04:
					throw new RuntimeException("too many I/O points"); 
				case 0x05: 
					throw new RuntimeException("CPU bus error"); 
				case 0x06:
					throw new RuntimeException("I/O duplication"); 
				case 0x07: 
					throw new RuntimeException("CPU bus error"); 
				case 0x09: 
					throw new RuntimeException("SYSMAC BUS/2 error"); 
				case 0x0a: 
					throw new RuntimeException("CPU bus unit error"); 
				case 0x0d: 
					throw new RuntimeException("SYSMAC BUS No. duplication"); 
				case 0x0f: 
					throw new RuntimeException("memory error"); 
				case 0x10:
					throw new RuntimeException("SYSMAC BUS terminator missing"); 
				}
				break;
			case 0x26:
				switch (buffer[29]) {
				case 0x01: 
					throw new RuntimeException("no protection"); 
				case 0x02: 
					throw new RuntimeException("incorrect password"); 
				case 0x04: 
					throw new RuntimeException("protected"); 
				case 0x05: 
					throw new RuntimeException("service already executing"); 
				case 0x06:
					throw new RuntimeException("service stopped"); 
				case 0x07:
					throw new RuntimeException("no execution right"); 
				case 0x08:
					throw new RuntimeException("settings required before execution"); 
				case 0x09: 
					throw new RuntimeException("necessary items not set"); 
				case 0x0a: 
					throw new RuntimeException("number already defined"); 
				case 0x0b: 
					throw new RuntimeException("error will not clear"); 
				}
				break;
			case 0x30:
				if (buffer[29] == 0x01) 
					throw new RuntimeException("no access right"); 
			case 0x40:
				if (buffer[29] == 0x01) 
					throw new RuntimeException("service aborted"); 
			}
		}
	}

}
