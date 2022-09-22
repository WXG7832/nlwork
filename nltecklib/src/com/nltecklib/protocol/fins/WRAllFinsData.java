package com.nltecklib.protocol.fins;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;

public class WRAllFinsData extends Data{
	private List<Boolean> results = new ArrayList<Boolean>();

	public List<Boolean> getResults() {
		return results;
	}

	public void setResults(List<Boolean> results) {
		this.results = results;
	}

	@Override
	public void encode() {
		data.clear();
		datalength = 1;
		area = Area.WR;
		if (orient == Orient.WRITE) {
			byte b = 0;
			byte b1 = 0;
			for (int i = 0; i < results.size(); i++) {
				if (i < 8) {
	    			if(results.get(i)){
	    				int nn=(1<<(i));
	    				b1 += nn;
	    			}
				}else {
					if(results.get(i)){
						int nn=(1<<(i - 8));
						b += nn;
					}
					
				}
			}
			data.add(b);
			data.add(b1);
		}
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		results.clear();
		data = encodeData;
		byte b = data.get(1);
		for (int i = 7; i >= 0; i--) { //对于byte的每bit进行判定
            results.add((b & 1) == 1);   //判定byte的最后一位是否为1，若为1，则是true；否则是false
            b = (byte) (b >> 1);       //将byte右移一位
        }
		b = data.get(0);
		for (int i = 7; i >= 0; i--) { //对于byte的每bit进行判定
            results.add((b & 1) == 1);   //判定byte的最后一位是否为1，若为1，则是true；否则是false
            b = (byte) (b >> 1);       //将byte右移一位
        }
	}

}
