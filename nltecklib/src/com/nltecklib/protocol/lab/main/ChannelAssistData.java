package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ChannelAssistData extends Data implements Responsable,Alertable {
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	//通道数据包
	private List<AssistPack> assistDatas = new ArrayList<AssistPack>();
	
	@Override
	public void encode() {
		
		data.add((byte)assistDatas.size());
		
		//各通道辅助数据包
		for (int i = 0; i < assistDatas.size(); i++) {
			
			AssistPack assistData = assistDatas.get(i);
			//通道序号
			data.add((byte) assistData.getChnIndex());
			//功率电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(assistData.getPowerVoltage() * 10), 2,true)));
			//备份电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(assistData.getBackupVoltage() * 10), 2,true)));
			//通道温度
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long)(assistData.getChnTemp() * 10), 2,true)));
		}	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		int chnCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (chnCount > Data.maxCoreChnCount) { 
			//通道个数大于单主控最多支持通道个数

			throw new RuntimeException("error channel count : " + chnCount);
		}
		assistDatas.clear();
		for (int i = 0; i < chnCount; i++) {
			
			AssistPack assistData = new AssistPack();
			//通道序号
			assistData.setChnIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
			//功率电压
			assistData.setPowerVoltage((double)(ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10));
			index += 2;
			//备份电压
			assistData.setBackupVoltage((double)(ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10));
			index += 2;
			//通道温度
			assistData.setChnTemp((double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+2).toArray(new Byte[0]), true) / 10);
			index += 2;
			
			assistDatas.add(assistData);
		}
	}
	
	@Override
	public Code getCode() {
		return MainCode.AssistCode;
	}

	

	public List<AssistPack> getAssistDatas() {
		return assistDatas;
	}

	public void setAssistDatas(List<AssistPack> assistDatas) {
		this.assistDatas = assistDatas;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public String toString() {
		return "ChannelAssistData [assistDatas=" + assistDatas + "]";
	}
	


}
