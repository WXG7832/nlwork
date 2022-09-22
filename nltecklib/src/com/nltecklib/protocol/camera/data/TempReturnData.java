package com.nltecklib.protocol.camera.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Decode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 获取一帧温度返回值
 * @author Administrator
 *
 */
public class TempReturnData extends Data implements Decode{
	private static final int LENGTH = 1024;
	private static final int VALUE = 2731;
	private Map<Integer, Double> temps = new HashMap<Integer, Double>();
	
	public Map<Integer, Double> getTemps() {
		return temps;
	}

	public void setTemps(Map<Integer, Double> temps) {
		this.temps = temps;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data.clear();
		data = encodeData;
		int index = 0;
		for (int i = 0; i < LENGTH; i++) {
			double temp = (ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) - VALUE)/10.0;
			temps.put(i, temp);
			index += 2;
		}
	}

	@Override
	public Code getCode() {
		return CameraCode.TEMP_RETURN;
	}

	@Override
	public String toString() {
		return "TempReturnData [temps=" + temps + "]";
	}

}
