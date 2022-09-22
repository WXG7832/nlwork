package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.OptMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 单通道启动指令
 * 用于单板单个或多个通道的测试启动恢复命令
 * @author Administrator
 *
 */
public class Logic2ChnOptData extends Data implements Configable, Responsable {

	private SwitchState switchState = SwitchState.OFF; //通道开还是关?
	private OptMode    optMode = OptMode.SYNC;
	private List<Byte> chnIndexList = new ArrayList<>();
	
	

	public Logic2ChnOptData() {

		
	}

	@Override
	public boolean supportUnit() {

		return true;
	}

	@Override
	public boolean supportDriver() {

		return false;
	}

	@Override
	public boolean supportChannel() {

		return false;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) optMode.ordinal());
		// 操作
		data.add((byte) switchState.ordinal());
        data.add((byte) chnIndexList.size());
       
        if(Data.isReverseDriverChnIndex()) {
        	
        	 List<Byte> list = new ArrayList<>();
        	for(Byte chnIndex : chnIndexList) {
        		
        		int index = ProtocolUtil.getUnsignedByte(chnIndex);
        		list.add((byte) ProtocolUtil.reverseChnIndexInLogic(index, Data.getDriverChnCount()));
        	}
        	chnIndexList = list;
        }
        data.addAll(chnIndexList);
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		
		int val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		if (val > OptMode.values().length - 1) {

			throw new RuntimeException("error opt mode index:" + val );
		}
		optMode = OptMode.values()[val];
	
		val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		if (val > SwitchState.values().length - 1) {

			throw new RuntimeException("error switch state index:" + val );
		}
		switchState = SwitchState.values()[val];
		
		int count = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		
		chnIndexList = encodeData.subList(index, index + count);
		
        if(Data.isReverseDriverChnIndex()) {
        	
        	List<Byte> list = new ArrayList<>();
        	for(Byte chnIndex : chnIndexList) {
        		
        		int unIndex = ProtocolUtil.getUnsignedByte(chnIndex);
        		list.add((byte) ProtocolUtil.reverseChnIndexInLogic(unIndex, Data.getDriverChnCount()));
        	}
        	chnIndexList = list;
        }
	}

	@Override
	public Code getCode() {
		return Logic2Code.ChnStartCode;
	}

    
	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

	public OptMode getOptMode() {
		return optMode;
	}

	public void setOptMode(OptMode optMode) {
		this.optMode = optMode;
	}

	public List<Byte> getChnIndexList() {
		return chnIndexList;
	}

	public void setChnIndexList(List<Byte> chnIndexList) {
		this.chnIndexList = chnIndexList;
	}

	@Override
	public String toString() {
		return "Logic2ChnOptData [switchState=" + switchState + ", optMode=" + optMode + ", chnIndexList="
				+ chnIndexList + "]";
	}

	

	
	
	
}
