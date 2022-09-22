package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 液晶屏单元状态显示
 * @Desc：   
 * @author：LLC   
 * @Date：2021年10月19日 上午1:11:23   
 * @version
 */
public class ScreenUnitStateData   extends Data implements Configable, Responsable {
	   
		
		//液晶屏地址
		private int screenIndex;
		
		private boolean connect;
		
		
		@Override
		public boolean supportMain() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean supportChannel() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void encode() {
			
			data.add((byte) screenIndex);
			data.add((byte) mainIndex);
			
			data.add((byte) (connect ? 0x01 : 0x00));

		}

		@Override
		public void decode(List<Byte> encodeData) {
			
		    data = encodeData;
		    int index = 0;
		    screenIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		    mainIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		    
		    connect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
			
		}

		@Override
		public Code getCode() {
			// TODO Auto-generated method stub
			return AccessoryCode.ScreenUnitStateCode;
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


	}
