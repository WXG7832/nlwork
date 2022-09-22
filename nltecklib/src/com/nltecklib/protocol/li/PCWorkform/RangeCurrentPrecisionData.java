package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月29日 下午6:08:56
* 类说明
*/
public class RangeCurrentPrecisionData extends Data implements Configable, Queryable, Responsable {
   
	
	private List<RangeCurrentPrecision>  ranges = new ArrayList<>();
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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
	
	

	public void setRanges(List<RangeCurrentPrecision> ranges) {
		this.ranges = ranges;
	}

	@Override
	public void encode() {
		
		ranges.sort(null);//排序
		
		data.add((byte) ranges.size());
		for(RangeCurrentPrecision precision : ranges) {
			
			data.add((byte) precision.level);//精度档位
			//该量程的理论精度偏差
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (precision.precide * 100), 2, true)));
			//量程下限
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (precision.min * 100), 3, true)));
			//量程上限
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (precision.max * 100), 3, true)));
			//最大ADC允许偏差范围
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (precision.maxAdcOffset * 100), 2, true)));
			//最大万用表允许偏差范围
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (precision.maxMeterOffset * 100), 2, true)));
		}
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			RangeCurrentPrecision range = new RangeCurrentPrecision();
			range.level = ProtocolUtil.getUnsignedByte(data.get(index++));
			range.precide =  ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 100;
			index += 2;
			range.min = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			range.max = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			range.maxAdcOffset = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 100;
			index += 2;
			range.maxMeterOffset = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 100;
			index += 2;

			appendRange(range);
			
		}

		ranges.sort(null);//排序
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.RangeCurrentPrecisionCode;
	}
	
	
	public void appendRange(RangeCurrentPrecision range) {
		
		ranges.add(range);
	}

	public List<RangeCurrentPrecision> getRanges() {
		return ranges;
	}

	@Override
	public String toString() {
		return "RangeCurrentPrecisionData [ranges=" + ranges + "]";
	}
	
	

}
