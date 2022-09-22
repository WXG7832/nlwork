package com.nltecklib.protocol.camera.data;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.camera.Environment.UploadMode;
/**
 * 上传数据协议
 * @author Administrator
 *
 */
public class UploadData extends Data implements Encode{
	private UploadMode mode;
	
	public UploadMode getMode() {
		return mode;
	}

	public void setMode(UploadMode mode) {
		this.mode = mode;
	}

	@Override
	public void encode() {
		data.clear();
		data.add((byte) mode.ordinal());
	}

	@Override
	public Code getCode() {
		return CameraCode.UPLOAD;
	}

	@Override
	public String toString() {
		return "UploadData [mode=" + mode + "]";
	}

}
