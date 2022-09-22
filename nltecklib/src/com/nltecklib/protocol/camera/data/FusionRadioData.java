package com.nltecklib.protocol.camera.data;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.camera.Environment.FusionRadio;
/**
 * 柔和比协议
 * @author Administrator
 *
 */
public class FusionRadioData extends Data implements Encode{
	private FusionRadio radio;
	
	public FusionRadio getRadio() {
		return radio;
	}

	public void setRadio(FusionRadio radio) {
		this.radio = radio;
	}

	@Override
	public void encode() {
		data.clear();
		data.add((byte) radio.getCode());
	}

	@Override
	public Code getCode() {
		return CameraCode.FUSION_RADIO;
	}

	@Override
	public String toString() {
		return "FusionRadioData [radio=" + radio + "]";
	}

}
