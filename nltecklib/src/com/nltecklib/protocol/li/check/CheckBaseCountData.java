package com.nltecklib.protocol.li.check;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 回检板基础数据检验协议
 * @author Administrator
 *
 */
public class CheckBaseCountData extends Data implements Queryable, Responsable {
    
	private int count;
	private int version;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
        data.addAll(Arrays.asList(ProtocolUtil.split((long)count, 2, true)));
        data.add((byte) version);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		//分区通道数
		count = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		//软件版本号
		version = ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.BaseCountCode;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	

}
