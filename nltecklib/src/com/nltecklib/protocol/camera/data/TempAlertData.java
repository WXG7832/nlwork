package com.nltecklib.protocol.camera.data;

import java.util.Arrays;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * 温度阈值协议
 * @author Administrator
 *
 */
public class TempAlertData extends Data implements Encode{
	private int temp;
	
	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	@Override
	public void encode() {
		data.clear();
		data.addAll(Arrays.asList(ProtocolUtil.split((long)temp, 2, true)));
	}

	@Override
	public Code getCode() {
		return CameraCode.TEMP_ALERT;
	}

	@Override
	public String toString() {
		return "TempAlertData [temp=" + temp + "]";
	}

}
