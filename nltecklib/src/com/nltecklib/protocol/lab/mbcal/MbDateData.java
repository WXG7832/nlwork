package com.nltecklib.protocol.lab.mbcal;

import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月30日 上午9:59:17
* 类说明
*/
public class MbDateData extends Data implements Configable, Queryable, Responsable {
    
	private Date  date;
	
	@Override
	public boolean supportMain() {
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
		
		data.addAll(ProtocolUtil.encodeDate(date, true));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		date = ProtocolUtil.decodeDate(encodeData, true);

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MbCalCode.DATE;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "MbDateData [date=" + date + "]";
	}
	
	

}
