package com.nltecklib.protocol.fins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.fins.Environment.Command;
import com.nltecklib.protocol.fins.Environment.DataUnit;
import com.nltecklib.protocol.fins.Environment.Error;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.fins.Environment.Result;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.BaseUtil;

public class Entity implements NlteckPackageFactory{
	private static final Byte[] HEAD = new Byte[] {0x46,0x49,0x4E,0x53};
	private static final String FINS = "FINS";				// 协议头
	private static final int HEAD_LENGTH = 4;				// 协议头长度
	private static final int DATA_LENGTH_LENGTH = 4;			// 数据长度的长度
	private static final int MIN_LENGTH = 24;				// fins协议最小长度
	private static final int MIN_DATA_LENGTH = 6;			// fins协议数据区最小长度
	private static final int HANDSHAKE_DATA_LENGTH = 0x10;	// fins协议握手包数据长度
	private static final int WRITE_SUCCESS_LENGTH = 0x16;	// 写入成功时数据区长度

	private byte[] client = new byte[4]; // 本机数据
	private byte[] server = new byte[4]; // PLC数据 

	public Entity(String localIp, String remoteIp) {
		if (BaseUtil.checkIpAddress(localIp)) {

			

			String[] arr = localIp.split("\\.");
			if (arr.length == 4) {
				for (int i = 0; i < arr.length; i++) {

					client[i] = (byte) Integer.parseInt(arr[i]);
				}
			}
		}

		if (BaseUtil.checkIpAddress(remoteIp)) {

			

			String[] arr = remoteIp.split("\\.");
			if (arr.length == 4) {
				for (int i = 0; i < arr.length; i++) {

					server[i] = (byte) Integer.parseInt(arr[i]);
				}
			}
		}
	}
	
	public static String printList(List<Byte> list) {

		StringBuilder buff = new StringBuilder();
		for (Byte b : list) {

			String str = Integer.toHexString(ProtocolUtil.getUnsignedByte(b));
			if (str.length() < 2)
				str = "0" + str;
			else
				str = str.substring(str.length() - 2);
			buff.append(str + " ");
		}

		return buff.toString().toUpperCase();

	}

	public NlteckIOPackage decode(byte[] data) {
		
		
		//System.out.println("recv: " + printList(ProtocolUtil.convertArrayToList(data)));
		
		FinsProtocol responseData = new InputFinsProtocol();
		if(!FINS.equals(new String(data, 0, 4))){
			throw new RuntimeException("the head is error");
		}

		int errorCode = (int) ProtocolUtil.compose(new byte[] {data[12],data[13],
				data[14],data[15]}, true);
		Error error = Error.getCode(errorCode);
		responseData.setError(error);
		if (data.length == HEAD_LENGTH + DATA_LENGTH_LENGTH + HANDSHAKE_DATA_LENGTH) {//握手协议
			// 本机IP节点
			client[0] = data[16];
			client[1] = data[17];
			client[2] = data[18];
			client[3] = data[19];
			// 服务端IP节点
			server[0] = data[20];
			server[1] = data[21];
			server[2] = data[22];
			server[3] = data[23];
		}else {			
			int resultCode = (int) ProtocolUtil.compose(new byte[] {data[28],data[29]}, true);
			responseData.setResult(Result.getCode(resultCode));
			if (data.length > HEAD_LENGTH + DATA_LENGTH_LENGTH + WRITE_SUCCESS_LENGTH) {
				List<Byte> datas = ProtocolUtil.convertArrayToList(data);
				responseData.setData(datas.subList(MIN_LENGTH + MIN_DATA_LENGTH, datas.size()));
			}	
		}

		return responseData;
	}

	@Override
	public byte[] encode(NlteckIOPackage pack) {
		if (client.length < 4 || server.length < 4) {

		}
		FinsProtocol data = (FinsProtocol) pack;
		List<Byte> buff = new ArrayList<Byte>();
		//头
		buff.addAll(Arrays.asList(HEAD));
		//数据长度
		int dataLength = 26;
		if (data.getOrient() == Orient.WRITE)
			dataLength = data.getData().size() + 26;
		if (data.getCommand() == Command.HANDSHAKE) {
			dataLength = 12;
		}
		Byte[] lenData = ProtocolUtil.split(dataLength, 4, true);
		buff.addAll(Arrays.asList(lenData));
		//frame command
		Byte[] commandData = ProtocolUtil.split(data.getCommand().getCode(), 4, true);
		buff.addAll(Arrays.asList(commandData));
		//err
		Byte[] errData = ProtocolUtil.split(data.getError().getCode(), 4, true);
		buff.addAll(Arrays.asList(errData));

		if (data.getCommand() == Command.HANDSHAKE) {
			buff.add((byte) 0);
			buff.add((byte) 0);
			buff.add((byte) 0);
			buff.add(client[3]);
		}else {
			//command frame header
			buff.add((byte) 0x80);	//ICF	80标识要求有回复，81标识不要求有回复
			buff.add((byte) 0x00);		//RSV	默认为00
			buff.add((byte) 0x02);		//GCT, 	表示穿过的网络层数量，0层为02,1层为01，2层为00；
			buff.add((byte) 0x00);		//DNA, local network
			buff.add(server[3]);	//DA1	目的节点地址，默认是目的PLC的ip地址的最后位
			buff.add((byte) 0x00);		//DA2, CPU unit
			buff.add((byte) 0x00);		//SNA, local network
			buff.add(client[3]);	//SA1	源节点地址，即上位机ip地址的最后位
			buff.add((byte) 0x00);		//SA2, CPU unit
			buff.add((byte) 0xff);	//SID

			//func
			Byte[] funcData = ProtocolUtil.split(data.getOrient().getCode(), 2, true);
			buff.addAll(Arrays.asList(funcData));

			//区域
			buff.add(data.getArea().getCode(data.getDataUnit() == DataUnit.BIT));
			//地址
			Byte[] addrData = ProtocolUtil.split(data.getAddress(), 2, true);
			buff.addAll(Arrays.asList(addrData));
			//偏移
			buff.add((byte) data.getOffset());
			
			if(data.getDataUnit() == DataUnit.BYTE) {
				//数据长度
				buff.addAll(Arrays.asList(ProtocolUtil.split(data.getData().size() / 2 ,2,true)));
			} else {
				
				buff.addAll(Arrays.asList(ProtocolUtil.split(data.getData().size() ,2,true)));
			}
			//长度
//			if (data.isBit()) {
//				buff.add((byte) data.getDatalength());
//				buff.add((byte) 0x00);
//				buff.add((byte) 0x01);//每次读写一位
//			}else {
//				buff.add((byte) 0x00);
//				Byte[] dLenData = ProtocolUtil.split(data.getDatalength(), 2, true);
//				buff.addAll(Arrays.asList(dLenData));
//			}

			if (data.getData().size() > 0 && data.getOrient() == Orient.WRITE) {
				buff.addAll(data.getData());
			}

		}
		//System.out.println("send: " + printList(buff));
		return ProtocolUtil.convertListToArray(buff);
	}

	@Override
	public int getMinDecodeLen() {
		return MIN_LENGTH - 1;
	}

	@Override
	public int getPackLen(byte[] data) {
		int index = findHeadPos(data);
		if (data.length - index < getMinDecodeLen()) {

			throw new RuntimeException("can not decode data with short of data length");
		}
		if (!FINS.equals(new String(data, index,index + 4))) {

			throw new RuntimeException("error protocol head :" + data[index]);
		}
		int dataLen = (int) ProtocolUtil.compose(new byte[] {data[index +4],data[ index + 5],
				data[ index + 6],data[ index + 7]}, true);
		return HEAD_LENGTH + DATA_LENGTH_LENGTH + dataLen;
	}

	@Override
	public int findHeadPos(byte[] data) {
		int start = -1;
		for (int i = 0; i < data.length - 4; i++) {
			if (FINS.equals(new String(data, i, i + 4))) { // 找协议头
				start = i;
				break;
			}
		}
		return start;
	}

	public void setClient(String localIp) {
		if (BaseUtil.checkIpAddress(localIp)){

			System.out.println(localIp);

			String[] arr = localIp.split("\\.");
			if (arr.length == 4) {
				for (int i = 0; i < arr.length; i++) {

					client[i] = (byte) Integer.parseInt(arr[i]);
				}
			}
		}
	}

	public void setServer(String remoteIp) {
		if (BaseUtil.checkIpAddress(remoteIp)) {

			System.out.println(remoteIp);

			String[] arr = remoteIp.split("\\.");
			if (arr.length == 4) {
				for (int i = 0; i < arr.length; i++) {

					server[i] = (byte) Integer.parseInt(arr[i]);
				}
			}
		}
	}
	
	/**
	 * 获取本机节点
	 * @return
	 */
	public String getLocalIp() {
		
		String str = "";
		for(int n = 0 ; n < client.length ; n++) {
			
			str += client[n] + (n == client.length - 1 ? "" : ".");
		}
		return str;
	}
	
	/**
	 * 获取远程节点
	 * @return
	 */
	public String getRemoteIp() {
		
		String str = "";
		for(int n = 0 ; n < server.length ; n++) {
			
			str += server[n] + (n == server.length - 1 ? "" : ".");
		}
		return str;
	}
	

}
