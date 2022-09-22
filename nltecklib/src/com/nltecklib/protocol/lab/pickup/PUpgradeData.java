package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 采集芯片在线升级协议
 * 
 * @author Administrator
 *
 */
public class PUpgradeData extends Data implements Configable, Responsable {

	private int packCount; // 升级包总数
	private int packIndex; // 升级包序号
	private long totalLength; // 升级文件总字节数
	private List<Byte> content = new ArrayList<Byte>();
	private int  year;
	private int  month;
	private int  day;

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		//年，月，日
		data.add((byte) (year - 2000));
		data.add((byte) month);
		data.add((byte) day);
		// 文件大小
		data.addAll(Arrays.asList(ProtocolUtil.split(totalLength, 3, true)));
		// 包数
		data.addAll(Arrays.asList(ProtocolUtil.split(packCount, 2, true)));
		// 当前包序号,0开始
		data.addAll(Arrays.asList(ProtocolUtil.split(packIndex, 2, true)));
		// 当前包大小
		data.addAll(Arrays.asList(ProtocolUtil.split(content.size(), 2, true)));
		// 包内容
		data.addAll(content);

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		//年月日
		year = 2000 + ProtocolUtil.getUnsignedByte(data.get(index++));
		month = ProtocolUtil.getUnsignedByte(data.get(index++));
		day = ProtocolUtil.getUnsignedByte(data.get(index++));
				
		// 文件大小
		totalLength = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;
		// 包数
		packCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// 包序号
		packIndex = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		int packLength  = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		//加载包内容
		content.clear();
		content.addAll(data.subList(index, index + packLength));
		index += packLength;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.UpgradeCode;
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

	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	public List<Byte> getContent() {
		return content;
	}

	public void setContent(List<Byte> content) {
		this.content = content;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "UpgradeData [packCount=" + packCount + ", packIndex=" + packIndex + ", totalLength=" + totalLength
				+ ", content=" + content + "]";
	}
	
	

}
