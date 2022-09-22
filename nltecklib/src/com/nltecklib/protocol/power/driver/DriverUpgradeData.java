package com.nltecklib.protocol.power.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年1月18日 下午3:11:30
* 驱动板升级协议
*/
public class DriverUpgradeData extends Data implements Configable, Responsable {
    
	private int   fileSize; //文件总字节数
	private int   packCount; //总包数
	private int   packIndex; //当前包序号，从1开始
	private List<Byte>  packContent = new ArrayList<>();
	
	
	@Override
	public boolean supportDriver() {
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
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)fileSize, 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)packCount, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)packIndex, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)packContent.size(), 2, true)));
		data.addAll(packContent);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		fileSize = (int) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;
		packCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		packIndex = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		int packSize = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		packContent = data.subList(index, index + packSize);
		index += packSize;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.UpgradeCode;
	}

	@Override
	public String toString() {
		return "DriverUpgradeData [fileSize=" + fileSize + ", packCount=" + packCount + ", packIndex=" + packIndex
				+ ", packContent=" + packContent + "]";
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
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

	public List<Byte> getPackContent() {
		return packContent;
	}

	public void setPackContent(List<Byte> packContent) {
		this.packContent = packContent;
	}
	
	

}
