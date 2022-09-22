/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 9.新增正负电流查询功能码    0x09 支持查询
* @version: v1.0.0
* @author: admin
* @date: 2021年12月7日 上午11:34:43 
*
*/
public class CurrQueryData extends Data implements Queryable, Responsable {

	private long positiveCurr;// 正向电流
	private long reverseCurr;// 反向电流
	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) positiveCurr, 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) reverseCurr, 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		positiveCurr = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true);
		index += 4;
		
		reverseCurr = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true);
		index += 4;
		
	}

	@Override
	public Code getCode() {
		return DiapTestCode.CurrQuery;
	}

	public long getPositiveCurr() {
		return positiveCurr;
	}

	public void setPositiveCurr(long positiveCurr) {
		this.positiveCurr = positiveCurr;
	}

	public long getReverseCurr() {
		return reverseCurr;
	}

	public void setReverseCurr(long reverseCurr) {
		this.reverseCurr = reverseCurr;
	}
	
	

}
