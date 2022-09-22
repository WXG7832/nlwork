package com.nltecklib.protocol.li.PCWorkform;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 上午11:36:44 类说明
 */
public class UploadTestDotData extends Data implements Alertable, Responsable {

	private UploadTestDot dot;

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

	public UploadTestDot getDot() {
		return dot;
	}

	public void setDot(UploadTestDot dot) {
		this.dot = dot;
	}

	@Override
	public void encode() {

		
		data.add((byte)unitIndex);
		data.add((byte)chnIndex);
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss.SSS").create();
		String content = gson.toJson(dot);
		try {
			byte[] bytes = content.getBytes("utf-8");
			data.addAll(ProtocolUtil.convertArrayToList(bytes));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("convert content to bytes error:" + e.getMessage());
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		
		unitIndex=ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex=ProtocolUtil.getUnsignedByte(data.get(index++));

		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss.SSS").create();
		// 转成字符串
		String content;
		try {
			content = new String(ProtocolUtil.convertListToArray(data.subList(index, data.size())), "utf-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("convert byte to json string error:" + e.getMessage());
		}
		dot = gson.fromJson(content, UploadTestDot.class);

	}

	@Override
	public String toString() {
		return "UploadTestDotData [dot=" + dot + "]";
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.UploadTestDotCode;
	}

}
