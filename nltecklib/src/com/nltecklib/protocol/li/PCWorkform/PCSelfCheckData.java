package com.nltecklib.protocol.li.PCWorkform;

import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.SelfCheckState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月18日 上午11:18:14 类说明
 */
@Deprecated
public class PCSelfCheckData extends Data implements Configable, Queryable, Responsable {

	private SelfCheckState state = SelfCheckState.NONE;

	private Date date=new Date();

	@Override
	public boolean supportUnit() {

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

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) state.ordinal());
		if(date==null) {
			date=new Date();
		}
		data.addAll(ProtocolUtil.encodeDate(date, true));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > SelfCheckState.values().length - 1) {

			throw new RuntimeException("error self check state:" + code);

		}
		state = SelfCheckState.values()[code];
		date = ProtocolUtil.decodeDate(data.subList(index, index + 6), true);

	}

	@Override
	public Code getCode() {
		
		return PCWorkformCode.SelfCheckCode;
	}

	public SelfCheckState getState() {
		return state;
	}

	public void setState(SelfCheckState state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "PCSelfCheckData [state=" + state + ", date=" + date + "]";
	}
	
	

}
