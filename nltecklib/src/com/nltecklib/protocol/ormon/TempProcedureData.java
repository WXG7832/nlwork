package com.nltecklib.protocol.ormon;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.util.CRC16;
import com.nltecklib.protocol.util.ProtocolUtil;


public class TempProcedureData {
	
	public static List<byte[]> decode(String message, int index) {
		
		List<byte[]> sendData = new ArrayList<>();
		try {
			message = message.replace("\r", "");
			message = message.replace("\n", "");
			message = message.replace("\t", "");
			message = message.replace(" ", "");
			String[] datas = message.split(";");
			for (int i = 0; i < datas.length; i++) {
				
				String[] data = datas[i].split("=");
				TempProcedure tempProcedure = new TempProcedure();
				tempProcedure.index = index;
				tempProcedure.addr = Integer.parseInt(data[0], 16);
				tempProcedure.data = Integer.parseInt(data[1], 16);
				sendData.add(decode(tempProcedure));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new RuntimeException("数据转换错误！请检查数据格式！");
		}
		return sendData;
	}
	
	public static byte[] decode(TempProcedure data) {
		
		Byte[] addrs = ProtocolUtil.split(data.addr, 2, true);
		Byte[] datas = ProtocolUtil.split(data.data, 2, true);
		Byte[] sendData = new Byte[]{(byte) data.index, 0x06, addrs[0], addrs[1], datas[0], datas[1]};
		int crc = CRC16.calcCrc16(sendData);
		Byte[] crcData = ProtocolUtil.split(crc, 2, false);
		return new byte[]{sendData[0], sendData[1], sendData[2], sendData[3], sendData[4],
				sendData[5], crcData[0], crcData[1]};
	}
	
	public static class TempProcedure {
		
		private int index;	// 表下标
		private int addr;	// 地址
		private int data;	// 数据
		
		@Override
		public String toString() {
			return "TempProcedure [index=" + index + ", addr=" + addr + ", data=" + data + "]";
		}
	}
}
