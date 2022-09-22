package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 分选结果
 * @Desc：   
 * @author：LLC   
 * @Date：2021年11月26日 下午1:08:15   
 * @version
 */
public class TestResultData  extends Data implements Configable,Responsable {

	
	/**
	 * 用bit从低到高分别表示选中分选的通道,1表示选中，0表示未选中
	 */
	private short chnFlag;
	
	/**
	 * 用bit从低到高分别表示分选结果，1表示分选PASS , 0表示分选失败
	 */
	private short resultFlag;
	
	/**
	 * 清除标记
	 */
	private boolean  clear; //是否重置分选灯
	
	
	@Override
	public boolean supportMain() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split(chnFlag, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(resultFlag, 2, true)));
		data.add((byte) (clear ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		chnFlag = (short) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		resultFlag = (short) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		clear = data.get(index++) == 0x01;
	
	}

	@Override
	public Code getCode() {
		return MainCode.TestResultCode;
	}

	/**
	 * 
	 * @author  wavy_zheng
	 * 2021年12月30日
	 * @param flag
	 */
	public void setChnFlag(int chnIndexInUnit, boolean resultOk) {
		
		chnFlag |=  0x01 << chnIndexInUnit;
			
		if(resultOk) {
			
			resultFlag = (short)(resultFlag | 0x01 <<chnIndexInUnit);
		} else {
			
			resultFlag = (short)(resultFlag & ~(0x01 <<chnIndexInUnit));
		}

		
		
	}

	public short getChnFlag() {
		return chnFlag;
	}

	public short getResultFlag() {
		return resultFlag;
	}

	public boolean isClear() {
		return clear;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}

	@Override
	public String toString() {
		return "TestResultData [chnFlag=" + chnFlag + ", resultFlag=" + resultFlag + ", clear=" + clear + "]";
	}

	
	
	
	

}
