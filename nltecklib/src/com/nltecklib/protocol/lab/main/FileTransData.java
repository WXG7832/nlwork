package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年5月23日 下午6:16:55
* 文件传输协议
* 用于主控与上位机之间的文件传输
*/
public class FileTransData extends Data implements Configable, Responsable {
    
	private UpgradeType  upgradeType;
	private List<Byte> transData = new ArrayList<>();
	private int   packCount; //文件传输包数
	private int   packIndex;  //传输序号
	
	private final static int PACK_BYTE = 1024; //1包字节大小
	
	
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
		
		data.add((byte) upgradeType.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long)packCount, 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)packIndex, 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)transData.size(), 2,true)));
		data.addAll(transData);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > UpgradeType.values().length - 1) {
			
			throw new RuntimeException("error upgrade type code:" + code);
		}
		upgradeType = UpgradeType.values()[code];
		
		packCount = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		packIndex = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		int packByte = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		transData.addAll(data.subList(index, index + packByte));
		index += packByte;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.FileTransCode;
	}

	public List<Byte> getTransData() {
		return transData;
	}

	public void setTransData(List<Byte> transData) {
		this.transData = transData;
	}

	public int getPackCount() {
		return packCount;
	}

	public void setPackCount(int packCount) {
		this.packCount = packCount;
	}

	public int getPackIndex() {
		return packIndex;
	}

	public void setPackIndex(int packIndex) {
		this.packIndex = packIndex;
	}

	public UpgradeType getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(UpgradeType upgradeType) {
		this.upgradeType = upgradeType;
	}
	
	

}
