package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月30日 下午2:11:27
* 模片开关
*/
public class ModuleSwitchData extends Data implements Configable, Queryable, Responsable {
   
	
	public boolean open;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
          data.add((byte) unitIndex);
          data.add((byte)chnIndex);
          data.add((byte) (open ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
         data = encodeData;
         int index = 0;
         unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
 		 chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
 		 open = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.ModuleSwitchCode;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	

}
