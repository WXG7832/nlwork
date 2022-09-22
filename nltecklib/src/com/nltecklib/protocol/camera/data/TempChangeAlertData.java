package com.nltecklib.protocol.camera.data;

import java.util.Arrays;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * 温度突变阈值协议
 * @author Administrator
 *
 */
public class TempChangeAlertData extends Data implements Encode{
	private int time;
	private int temp;
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	@Override
	public void encode() {
		data.clear();
		data.addAll(Arrays.asList(ProtocolUtil.split((long)time, 2, true)));
		data.add((byte) temp);
	}

	@Override
	public Code getCode() {
		return CameraCode.TEMP_CHANGE_ALERT;
	}

	@Override
	public String toString() {
		return "TempChangeAlertData [time=" + time + ", temp=" + temp + "]";
	}

}
