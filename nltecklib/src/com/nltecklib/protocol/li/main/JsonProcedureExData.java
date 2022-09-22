package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.util.ProcedureDataAdapter;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 
 * @ClassName: JsonProcedureExData
 * @Description: TODO(新版本的流程数据)
 * @author hao_dong
 * @version 2022年7月7日
 *
 */
public class JsonProcedureExData extends Data implements Configable, Queryable, Responsable{

	
	/* 旧版的流程对象 */
	private ProcedureData pd;
	
	/* 流程超压保护 */
	private DeviceProtectData device;
	
	/* 流程极性保护 */
	private PoleData pole;
	
	/* 流程首尾保护 */
	private StartEndCheckData secd;
	
	/* 流程首尾保护 */
	private CheckVoltProtectData check;
	
	
	
	
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
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new ProcedureDataAdapter()).create();
		
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
			Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new ProcedureDataAdapter()).create();
			JsonProcedureExData jp = gson.fromJson(jsonStr, JsonProcedureExData.class);
			this.pd = jp.pd;
			this.device = jp.device;
			this.pole = jp.pole;
			this.secd = jp.secd;
			this.check = jp.check;
			
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("流程解码错误:" + e.getMessage());
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.JsonProcedureEx;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		JsonProcedureExData another = (JsonProcedureExData) super.clone();
		another.setProcedureData((ProcedureData) this.getProcedureData().clone());
		return another;
	}
	

    
	
	
	
	@Override
	public String toString() {
		return "JsonProcedureExData [pd=" + pd + ", device=" + device + ", pole=" + pole + ", secd=" + secd + ", check="
				+ check + "]";
	}

	public DeviceProtectData getDeviceProtect() {
		return device;
	}
	
	public void setDeviceProtect(DeviceProtectData device) {
		this.device = device;
	}
	
	public ProcedureData getProcedureData() {
		return pd;

	}
	
	public void setProcedureData(ProcedureData pd) {
		this.pd = pd;
	}
	
	public PoleData getPoleProtect() {
		return pole;
	}
	
	public void setPoleProtect(PoleData pole) {
		this.pole = pole;
	}
	
	public CheckVoltProtectData getCheckProtect() {
		return check;
	}
	
	public void setCheckProtect(CheckVoltProtectData check) {
		this.check = check;
	}
	
	public StartEndCheckData getFirstEndProtect() {
		return secd;
	}
	
	public void setFirstEndProtect(StartEndCheckData secd) {
		this.secd = secd;
	}
	
	
	

}
