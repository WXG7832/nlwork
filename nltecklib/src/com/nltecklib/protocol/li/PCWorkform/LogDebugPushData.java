package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月30日 下午7:48:17 类说明
 */
public class LogDebugPushData extends Data implements Alertable, Responsable {

	private boolean error;
	private Date date;
	private String log;

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
		return true;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) chnIndex);
		data.add((byte) (error ? 1 : 0));
		data.addAll(ProtocolUtil.encodeDate(date, true));
		List<Byte> logList = ProtocolUtil.encodeString(log, "utf-8", 0);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) logList.size(), 2, true)));
		data.addAll(logList);

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		error = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		date=ProtocolUtil.decodeDate(data.subList(index, index+6), true);
		index+=6;
		int len = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		log = ProtocolUtil.decodeString(data, index, len, "utf-8");
		index += len;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.LogDebugCode;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "LogDebugPushData [error=" + error + ", date=" + date + ", log=" + log + "]";
	}

}
