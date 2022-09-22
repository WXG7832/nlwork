package com.nltecklib.protocol.lab.screen;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ChnState;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月2日 下午4:45:43
* 类说明
*/
public class ChnStateData extends Data implements Configable, Queryable, Responsable {
    
	private List<ChnState>  chnStates = new ArrayList<>();;
	
	
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
		
		data.add((byte) chnStates.size());
		for(int n = 0 ; n < chnStates.size() ; n++) {
			
			data.add((byte) (chnStates.get(n).ordinal() + 1));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
	    data = encodeData;
	    int index = 0;
	    int count = ProtocolUtil.getUnsignedByte(data.get(index++));
	    for(int n = 0 ; n < count ; n++) {
	    	
	    	int code = ProtocolUtil.getUnsignedByte(data.get(index++));
	    	if(code > ChnState.values().length - 1) {
	    		
	    		throw new RuntimeException("error chn state code :" + code);
	    	}
	    	ChnState cs = ChnState.values()[code];
	    	chnStates.add(cs);
	    }

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.ChnStateCode;
	}

	public List<ChnState> getChnStates() {
		return chnStates;
	}

	public void setChnStates(List<ChnState> chnStates) {
		this.chnStates = chnStates;
	}

	@Override
	public String toString() {
		return "ChnStateData [chnStates=" + chnStates + "]";
	}
	
	

}
