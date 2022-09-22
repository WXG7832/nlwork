/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 在线升级模块 功能码 0x09
 * @version: v1.0.0
 * @date: 2021年12月29日 上午10:18:31
 *
 */
public class UpgradeData extends Data implements Configable, Queryable, Responsable {

	private String date;
	private int fileSize; // 文件总字节数
	private int packCount; // 总包数
	private int packIndex; // 包序号
	private List<Byte> packContent = new ArrayList<>();

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) Integer.parseInt(date.substring(0, 2)));//年
		data.add((byte) Integer.parseInt(date.substring(2, 4)));//月
		data.add((byte) Integer.parseInt(date.substring(4, 6)));//日
		data.addAll(Arrays.asList(ProtocolUtil.split((long) fileSize, 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) packCount, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) packIndex, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) packContent.size(), 2, true)));
		data.addAll(packContent);
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		date = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) +"";
		index += 3;
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
		return CheckCode.UpgradeCode;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
