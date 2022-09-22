package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 液晶屏控制状态显示
 * @Desc：   
 * @author：LLC   
 * @Date：2021年10月19日 上午1:16:35   
 * @version
 */
public class ScreenControlStateData  extends Data implements Configable, Responsable {
	   
    	private static final int IP_ADDR_BYTE_LENGTH = 4;
	
	    public byte[]  ipAddress = new byte[IP_ADDR_BYTE_LENGTH];
	
		//液晶屏地址
		private int screenIndex;
		
		private boolean connect;
		
		
		@Override
		public boolean supportMain() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean supportChannel() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void encode() {
			
			data.add((byte) screenIndex);
			
			data.add((byte) (connect ? 0x01 : 0x00));

			for(int n = 0 ; n < IP_ADDR_BYTE_LENGTH ; n++) {
				data.add(ipAddress[n]);
			}
			
		}

		@Override
		public void decode(List<Byte> encodeData) {
			
		    data = encodeData;
		    int index = 0;
		    screenIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		    
		    connect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
			
			for(int n = 0 ; n < IP_ADDR_BYTE_LENGTH ; n++) {
				ipAddress[n] = data.get(index++);
			}
		}

		@Override
		public Code getCode() {
			// TODO Auto-generated method stub
			return AccessoryCode.ScreenControlStateCode;
		}


		public int getScreenIndex() {
			return screenIndex;
		}

		public void setScreenIndex(int screenIndex) {
			this.screenIndex = screenIndex;
		}

		public boolean isConnect() {
			return connect;
		}

		public void setConnect(boolean connect) {
			this.connect = connect;
		}

		public void setIpAddress(String ip) {
			
			String[] secs = ip.split("\\.");
			for(int n = 0 ; n < ipAddress.length ; n++) {
				
				ipAddress[n] = (byte) Integer.parseInt(secs[n]);
			}
			
		}
		
		
		public String getIpAddress() {
			
			StringBuffer info = new StringBuffer();
	         for(int n = 0 ; n < ipAddress.length ; n++) {
				
				 info.append(ProtocolUtil.getUnsignedByte(ipAddress[n]));
				 if(n < ipAddress.length - 1) {
					 
					 info.append(".");
				 }
			}
	         
	         return info.toString();
			
		}

	}
