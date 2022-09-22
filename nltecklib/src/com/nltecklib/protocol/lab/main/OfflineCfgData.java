package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年7月7日 上午9:41:26
* 类说明
*/
public class OfflineCfgData extends Data implements Configable, Queryable, Responsable {
    
	private  int   offlineMinute; //离线运行时间,0为不启用离线功能
	
	
	
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
		
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(offlineMinute, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		offlineMinute = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.OfflineCfgCode;
	}

	public int getOfflineMinute() {
		return offlineMinute;
	}

	public void setOfflineMinute(int offlineMinute) {
		this.offlineMinute = offlineMinute;
	}
	
	

}
