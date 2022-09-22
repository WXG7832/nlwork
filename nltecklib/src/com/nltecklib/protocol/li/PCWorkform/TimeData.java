package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.MeterCfg;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 上午11:36:44 类说明
 */
public class TimeData extends Data implements Configable, Responsable {

	private Date time = new Date();

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public void encode() {

		data.addAll(ProtocolUtil.encodeDate(time, true));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		time = ProtocolUtil.decodeDate(data.subList(index, index + 6), true);
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.TimeCode;
	}

	@Override
	public String toString() {
		return "TimeData [time=" + time + "]";
	}

}
