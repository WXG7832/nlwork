package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 设备操作按钮
 * @Desc：   
 * @author：LLC   
 * @Date：2021年10月19日 上午1:19:26   
 * @version
 */
public class DeviceOperateBtnData  extends Data implements Configable, Responsable {
	   
	
		private boolean press;
		
		
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
			
			data.add((byte) mainIndex);
			
			data.add((byte) (press ? 0x01 : 0x00));

		}

		@Override
		public void decode(List<Byte> encodeData) {
			
		    data = encodeData;
		    int index = 0;
		    mainIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		    
		    press = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
			
		}

		@Override
		public Code getCode() {
			// TODO Auto-generated method stub
			return AccessoryCode.DeviceOptBtnCode;
		}



		public boolean isPress() {
			return press;
		}

		public void setPress(boolean press) {
			this.press = press;
		}


	}
