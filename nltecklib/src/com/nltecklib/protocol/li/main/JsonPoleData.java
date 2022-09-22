package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年7月11日 下午2:46:38
* 类说明
*/
public class JsonPoleData extends Data implements Configable, Queryable, Responsable {
    
	private List<PoleData> poles = new ArrayList<PoleData>();
	
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
		
		Gson gson = new GsonBuilder().create();
		String jsonStr = gson.toJson(this);
		System.out.println(jsonStr);
		
		try {
			byte[] arr = jsonStr.getBytes("utf-8");
			data.addAll(Arrays.asList(ProtocolUtil.split((long)arr.length, 2, true)));
			for (int n = 0; n < arr.length; n++) {

				data.add(arr[n]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("极性json编码错误:" + e.getMessage());
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		
		data = encodeData;
		int index = 0;

		int infoBytes = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		byte[] procBytes = new byte[infoBytes];
		for (int i = index; i < index + infoBytes; i++) {

			procBytes[i - index] = data.get(i);

		}
		index += infoBytes;
		try {
			String jsonStr = new String(procBytes, "utf-8");
			Gson gson = new GsonBuilder().create();
			JsonPoleData jp = gson.fromJson(jsonStr, JsonPoleData.class);
            this.setPoles(jp.getPoles());
			

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("极性json解码错误:" + e.getMessage());
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.JsonPoleCode;
	}

	public List<PoleData> getPoles() {
		return poles;
	}

	public void setPoles(List<PoleData> poles) {
		this.poles = poles;
	}
	
	public void clearPoles() {
		
		this.poles.clear();
	}
	
	public void appendPole(PoleData pole) {
		
		this.poles.add(pole);
	}

	@Override
	public String toString() {
		return "JsonPoleData [poles=" + poles + "]";
	}
	
	

}
