package com.nltecklib.protocol.camera.data;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
/**
 * 发射率协议
 * @author Administrator
 *
 */
public class EmissivityData extends Data implements Encode{
	private double emissivity;
	
	public double getEmissivity() {
		return emissivity;
	}

	public void setEmissivity(double emissivity) {
		this.emissivity = emissivity;
	}

	@Override
	public void encode() {
		data.clear();
		data.add((byte) (emissivity * 100));
	}

	@Override
	public Code getCode() {
		return CameraCode.EMISSIVITY;
	}

	@Override
	public String toString() {
		return "EmissivityData [emissivity=" + emissivity + "]";
	}

}
