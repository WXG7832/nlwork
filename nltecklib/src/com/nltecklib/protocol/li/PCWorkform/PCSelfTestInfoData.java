package com.nltecklib.protocol.li.PCWorkform;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBSelfTestInfo;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PCSelfTestInfoData extends Data implements Queryable, Responsable {

	private MBSelfTestInfo selfTestInfo = new MBSelfTestInfo();

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

	@Override
	public void encode() {
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss.SSS").create();
		String content = gson.toJson(selfTestInfo);
		try {
			byte[] bytes = content.getBytes("utf-8");
			data.addAll(ProtocolUtil.convertArrayToList(bytes));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("convert content to bytes error:" + e.getMessage());
		}
	}

	public MBSelfTestInfo getSelfTestInfo() {
		return selfTestInfo;
	}

	public void setSelfTestInfo(MBSelfTestInfo selfTestInfo) {
		this.selfTestInfo = selfTestInfo;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss.SSS").create();
		// ×ª³É×Ö·û´®
		String content;
		try {
			content = new String(ProtocolUtil.convertListToArray(data.subList(index, data.size())), "utf-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("convert byte to json string error:" + e.getMessage());
		}
		selfTestInfo = gson.fromJson(content, MBSelfTestInfo.class);
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.SelfTestInfoCode;
	}

	@Override
	public String toString() {
		return "PCSelfTestInfoData [selfTestInfo=" + selfTestInfo + "]";
	}

}
