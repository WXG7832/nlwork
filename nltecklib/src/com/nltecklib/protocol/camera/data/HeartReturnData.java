package com.nltecklib.protocol.camera.data;

import java.util.List;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Decode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;

public class HeartReturnData extends Data implements Decode{

	@Override
	public void decode(List<Byte> encodeData) {
		
	}

	@Override
	public Code getCode() {
		data.clear();
		return CameraCode.HEART_RETURN;
	}

	@Override
	public String toString() {
		return "HeartReturnData []";
	}
}
