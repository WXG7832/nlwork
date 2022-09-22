package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
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
* @version 创建时间：2022年4月6日 下午6:11:40
* 单元连接状态及操作
*/
public class UnitConnectStateData extends Data implements Configable, Queryable, Responsable {
    
	/**
	 * 单元状态
	 * @author wavy_zheng
	 * 2022年4月6日
	 *
	 */
	public static class UnitState {
		
		
		public int      driverIndex; //驱动板号，也是单元号
		public int      pickIndex; //驱动板中的采集单元序号
		public String   ip; //ip地址
		public boolean  connected;
		public int      pickChnCount; //一个采集单元包含的通道数
		public boolean  enabled; //是否启用?
		@Override
		public String toString() {
			return "UnitState [driverIndex=" + driverIndex + ", pickIndex=" + pickIndex + ", ip=" + ip + ", connected="
					+ connected + ", pickChnCount=" + pickChnCount + ", enabled=" + enabled + "]";
		}
		
		
		
	}
	
	private List<UnitState> unitStates = new ArrayList<>();
	
	
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
		
		data.add((byte) unitStates.size());
		for(int n = 0 ; n < unitStates.size(); n++) {
			
			UnitState us = unitStates.get(n);
			data.add((byte) us.driverIndex);
			data.add((byte) us.pickIndex);
			data.addAll(ProtocolUtil.encodeIp(us.ip));
			data.add((byte) us.pickChnCount);
			data.add((byte) (us.connected ? 0x01 : 0x00));
			data.add((byte) (us.enabled ? 0x01 : 0x00));
		}
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			UnitState us = new UnitState();
			us.driverIndex = data.get(index++);
			us.pickIndex = data.get(index++);
			us.ip = ProtocolUtil.decodeIp(data.subList(index, index+4));
			index += 4;
			us.pickChnCount = data.get(index++);
			us.connected = data.get(index++) == 1;
			us.enabled   = data.get(index++) == 1;
			
			unitStates.add(us);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.UnitStateCode;
	}
	
	
	public void appendUnit(UnitState us) {
		
		unitStates.add(us);
	}
	
	public List<UnitState> getUnitBy(int driverIndex) {
		
		List<UnitState> list = new ArrayList<>();
		for(UnitState us : unitStates) {
			
			  if(us.driverIndex == driverIndex) {
				  
				  list.add(us);
			  }
		}
		
		return list;
	}
	

	public List<UnitState> getUnitStates() {
		return unitStates;
	}

	@Override
	public String toString() {
		return "UnitConnectStateData [unitStates=" + unitStates + "]";
	}
	
	
	
	

}
