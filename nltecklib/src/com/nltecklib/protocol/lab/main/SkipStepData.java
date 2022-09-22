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
* @author  wavy_zheng
* @version 创建时间：2020年6月21日 下午9:35:55
* 跳转步次
*/
public class SkipStepData extends Data implements Responsable, Configable {
   
	private long  executeStepIndex; //步次号
	private long  executeLoopIndex; //执行循环累计次数
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		 data.addAll(Arrays.asList(ProtocolUtil.split(executeStepIndex, 4, true)));
		 data.addAll(Arrays.asList(ProtocolUtil.split(executeLoopIndex, 4, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		executeStepIndex = ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true);
		index += 4;
		executeLoopIndex = ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true);
		index += 4;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.SkipStepCode;
	}

	public long getExecuteStepIndex() {
		return executeStepIndex;
	}

	public void setExecuteStepIndex(long executeStepIndex) {
		this.executeStepIndex = executeStepIndex;
	}

	public long getExecuteLoopIndex() {
		return executeLoopIndex;
	}

	public void setExecuteLoopIndex(long executeLoopIndex) {
		this.executeLoopIndex = executeLoopIndex;
	}
	
	
	

}
