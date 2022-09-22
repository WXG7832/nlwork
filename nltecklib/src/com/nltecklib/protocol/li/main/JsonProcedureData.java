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
* @version 创建时间：2021年7月8日 下午1:30:39
* 使用json格式将多个分区的流程一并下发或读取，提高效率
*/
public class JsonProcedureData extends Data implements Configable, Queryable, Responsable {
    
	
	//流程集合，序号为分区序号，从0开始
	private List<ProcedureData>  procedures = new ArrayList<>();
	
	
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
			throw new RuntimeException("流程编码错误:" + e.getMessage());
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
			JsonProcedureData jp = gson.fromJson(jsonStr, JsonProcedureData.class);
            this.setProcedures(jp.getProcedures());
			

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("流程解码错误:" + e.getMessage());
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.JsonProcedureCode;
	}
	
	
	public void appendProcedure(ProcedureData procedure) {
		
		procedures.add(procedure);
	}
	
	public void updateProcedure(int index , ProcedureData procedure) {
		
		procedures.set(index, procedure);
	}
	
	
	public void clearProcedure() {
		
		procedures.clear();
	}

	public List<ProcedureData> getProcedures() {
		return procedures;
	}

	public void setProcedures(List<ProcedureData> procedures) {
		this.procedures = procedures;
	}

	@Override
	public String toString() {
		return "JsonProcedureData [procedures=" + procedures + "]";
	}
	
	

}
