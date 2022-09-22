package com.nltecklib.protocol.camera.data;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
/**
 * 获取一帧温度
 * @author Administrator
 *
 */
public class TempData extends Data implements Encode{
	private int index = 1;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void encode() {
		data.clear();
		data.add((byte) index);
	}

	@Override
	public Code getCode() {
		return CameraCode.TEMP;
	}

	@Override
	public String toString() {
		return "TempData [index=" + index + "]";
	}
	
}
