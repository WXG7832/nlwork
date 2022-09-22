package com.nltecklib.protocol.fins;

import java.util.Map;

import com.nltecklib.protocol.util.ProtocolUtil;

public abstract class FinsData {
	
	protected String area;				// 区域
	//protected AddressCode address;		// 地址
	protected boolean isBit;			// 按位读写
	protected boolean isRead = true;	// 读还是写
	protected Map<Integer,Integer> datas;		// 准备写入的数据 或 读取到的数据
	protected int dataLength = 1;		// 长度  或  位
	protected int address;              //地址
	
	public abstract byte[] encode();
	
	public abstract void decode(byte[] data);
	
	/**
	 * 将int转换成byte[],低位在前,如1234 转换成	{-46,4,0,0}
	 * @param number
	 * @return
	 */
	public static byte[] intToByteArray(int number) {   
		return new byte[] {   
				(byte) (number & 0xFF),   
				(byte) ((number >> 8) & 0xFF),      
				(byte) ((number >> 16) & 0xFF),      
				(byte) ((number >> 24) & 0xFF),   
		};   
	}

	/**
	 * 将float转换成byte[],低位在前,如1.5 转换成
	 * @param number
	 * @return
	 */
	public static byte[] floatToByteArray(float number) {   
		int value = Float.floatToIntBits(number);
		return new byte[] {   
				(byte) (value & 0xFF),   
				(byte) ((value >> 8) & 0xFF),      
				(byte) ((value >> 16) & 0xFF),      
				(byte) ((value >> 24) & 0xFF),   
		};   
	}

	/**
	 * 内存格式转换
	 *	 区域.地址.长度 如:DM.1.1
	 * @param memory
	 * @param isBit
	 * @return
	 */
	private static byte[] addsToByte(String memory, boolean isBit) {
		String[] addrParts = memory.split("\\.");
		byte[] ch = intToByteArray(Integer.parseInt(addrParts[1]));//地址
		byte[] count = intToByteArray(Integer.parseInt(addrParts[2]));//读写长度
		byte[] addrs = new byte[6];
		addrs[1] = ch[1];
		addrs[2] = ch[0];

		if (!isBit){   //字处理            
			switch (addrParts[0]) {
			case "CIO":
				addrs[0] = (byte) 0xB0;
				break;
			case "WR":
				addrs[0] = (byte) 0xB1;
				break;
			case "DM":
				addrs[0] = (byte) 0x82;
				break;
			case "HR":
				addrs[0] = (byte) 0xB2;
				break;
			case "TIM":
				addrs[0] = (byte) 0x89;
				break;
			case "AR":
				addrs[0] = (byte) 0xB3;
				break;
			case "CNT":
				addrs[0] = (byte) 0x89;
				break;
			default:
				addrs[0] = 0x00;
				break;
			}
			addrs[3] = 0x00;
			addrs[4] = count[1];
			addrs[5] = count[0];//读写字的长度
		}
		else { //位处理            
			switch (addrParts[0]) {
			case "CIO":
				addrs[0] = 0x30;
				break;
			case "WR":
				addrs[0] = 0x31;
				break;
			case "DM":
				addrs[0] = 0x02;
				break;
			case "HR":
				addrs[0] = 0x32;
				break;
			case "TIM":
				addrs[0] = 0x09;
				break;
			case "AR":
				addrs[0] = 0x33;
				break;
			case "CNT":
				addrs[0] = 0x09;
				break;
			default:
				addrs[0] = 0x00;
				break;
			}
			addrs[3] = count[0];
			addrs[4] = 0x00;
			addrs[5] = 0x01;//每次读写一位
		}
		return addrs;
	}
	
	/**
	 * 报文处理
	 * 
	 * @param memory	要读写的内存地址(格式:区域.地址.长度,例如:DM.1.1)
	 * @param isBit		位还是字
	 * @param isRead	读还是写
	 * @param datas		写入的数据
	 * @return
	 */
	public static byte[] encode(String memory, boolean isBit, boolean isRead, byte[] datas){
		int dataLength;
		if (!isRead && datas != null)
			dataLength = datas.length + 26;
		else
			dataLength = 26;
		byte[] sendByte = new byte[dataLength + 8];
		sendByte[0] = 0x46;		//F
		sendByte[1] = 0x49;		//I
		sendByte[2] = 0x4e;		//N
		sendByte[3] = 0x53;		//S
		
		byte[] bs = intToByteArray(dataLength);
		sendByte[4] = bs[3];		//cmd length
		sendByte[5] = bs[2];
		sendByte[6] = bs[1];
		sendByte[7] = bs[0];
		sendByte[8] = 0;		//frame command
		sendByte[9] = 0;
		sendByte[10] = 0;
		sendByte[11] = 0x02;
		sendByte[12] = 0;		//err
		sendByte[13] = 0;
		sendByte[14] = 0;
		sendByte[15] = 0;
		//command frame header
		sendByte[16] = (byte) 0x80;	//ICF	80标识要求有回复，81标识不要求有回复
		sendByte[17] = 0x00;		//RSV	默认为00
		sendByte[18] = 0x02;		//GCT, 	表示穿过的网络层数量，0层为02,1层为01，2层为00；
		sendByte[19] = 0x00;		//DNA, local network
		sendByte[20] = FinsTcpConnector.server[3];	//DA1	目的节点地址，默认是目的PLC的ip地址的最后位
		sendByte[21] = 0x00;		//DA2, CPU unit
		sendByte[22] = 0x00;		//SNA, local network
		sendByte[23] = FinsTcpConnector.client[3];	//SA1	源节点地址，即上位机ip地址的最后位
		sendByte[24] = 0x00;		//SA2, CPU unit
		sendByte[25] = (byte) 0xff;	//SID

		sendByte[26] = 0x01;
		if (isRead)
			sendByte[27] = 0x01;
		else
			sendByte[27] = 0x02;	//读写具体命令，0101表示读，0102表示写；

		byte[] head = addsToByte(memory, isBit);
		sendByte[28] = head[0];		//区
		sendByte[29] = head[1];
		sendByte[30] = head[2];		//地址
		sendByte[31] = head[3];		//位
		sendByte[32] = head[4];		
		sendByte[33] = head[5];		//读写长度2个字节

		if (!isRead && datas != null) {
			System.arraycopy(datas, 0, sendByte, 34, datas.length);
		}

		return sendByte;
	}

	/**
	 * 打印字节数组
	 * @param bytes
	 * @return
	 */
	public static String printList(byte[] bytes) {

		StringBuilder buff = new StringBuilder();
		for (Byte b : bytes) {

			String str = Integer.toHexString(ProtocolUtil.getUnsignedByte(b));
			if (str.length() < 2)
				str = "0" + str;
			else
				str = str.substring(str.length() - 2);
			buff.append(str + " ");
		}

		return buff.toString().toUpperCase();

	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}

	public int getAddress() {
		return address;
	}

	
	public void setAddress(int address) {
		this.address = address;
	}
	
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public boolean isBit() {
		return isBit;
	}
	
	public void setBit(boolean isBit) {
		this.isBit = isBit;
	}
	
	public int getDataLength() {
		return dataLength;
	}
	
	public void setLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public Map<Integer, Integer> getDatas() {
		return datas;
	}

	public void setDatas(Map<Integer, Integer> datas) {
		this.datas = datas;
	}

	
}
