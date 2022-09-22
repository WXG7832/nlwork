package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculateDotData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 下午5:36:10 类说明
 */
public class RequestCalculateData extends Data implements Queryable, Responsable {

	private List<CalculateDotData> datas = new ArrayList<>();

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
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) datas.size());
		Calendar c = Calendar.getInstance();
		
		for (int n = 0; n < datas.size(); n++) {
			
			CalculateDotData dot = datas.get(n);
			data.add((byte) dot.chnIndexInLogic);
			data.add((byte) dot.state.ordinal());
			//计量点
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(dot.value * 100), 3,true)));
			//adc
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(dot.adc * 100), 3,true)));
			//万用表
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(dot.meter * 100), 3,true)));
			//用时
			data.addAll(Arrays.asList(ProtocolUtil.split((long)dot.seconds, 2,true)));
			//日期
			c.setTime(dot.date);
			data.add((byte) c.get(Calendar.YEAR - 2000));
			data.add((byte) (c.get(Calendar.MONTH) + 1));
			data.add((byte) c.get(Calendar.DATE));
			data.add((byte) c.get(Calendar.HOUR_OF_DAY));
			data.add((byte) c.get(Calendar.MINUTE));
			data.add((byte) c.get(Calendar.SECOND));
			
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		Calendar c = Calendar.getInstance();
		for(int n = 0 ; n < count ; n++) {
			
			CalculateDotData dot = new CalculateDotData();
			int chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			dot.chnIndexInLogic = chnIndex;
			
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(code > CalState.values().length - 1 ) {
				
				throw new RuntimeException("invalid cal state code:" + code );
			}
			dot.state = CalState.values()[code];
			dot.value =  (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			//adc
			dot.adc =  (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			//meter
			dot.meter =  (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			//seconds
			dot.seconds = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			//date
			int year = 2000 + ProtocolUtil.getUnsignedByte(data.get(index++));
			int month = ProtocolUtil.getUnsignedByte(data.get(index++)) - 1;
			int date = ProtocolUtil.getUnsignedByte(data.get(index++));
			int hour = ProtocolUtil.getUnsignedByte(data.get(index++));
			int minute = ProtocolUtil.getUnsignedByte(data.get(index++));
			int second = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DATE, date);
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			c.set(Calendar.SECOND, second);
			c.set(Calendar.MILLISECOND, 0);
			dot.date = c.getTime();

			appendDotData(dot);
			
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.RequireDataCode;
	}

	public void appendDotData(CalculateDotData data) {

		datas.add(data);
	}

	public List<CalculateDotData> getDatas() {
		return datas;
	}

}
