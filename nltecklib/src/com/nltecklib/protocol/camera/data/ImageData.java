package com.nltecklib.protocol.camera.data;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Decode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.camera.Pixel;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 图像数据返回协议
 * @author Administrator
 *
 */
public class ImageData extends Data implements Decode{
	public static final int SUM = 230;
	private int frameNum;
	private List<Pixel> pixels = new ArrayList<Pixel>();
	public int getFrameNum() {
		return frameNum;
	}

	public void setFrameNum(int frameNum) {
		this.frameNum = frameNum;
	}

	public List<Pixel> getPixels() {
		return pixels;
	}

	public void setPixels(List<Pixel> pixels) {
		this.pixels = pixels;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data.clear();
		data = encodeData;
		int index = 0;
		frameNum = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < SUM; i++) {
			Pixel pixel = new Pixel();
			pixel.r = ProtocolUtil.getUnsignedByte(data.get(index++));
			pixel.g = ProtocolUtil.getUnsignedByte(data.get(index++));
			pixel.b = ProtocolUtil.getUnsignedByte(data.get(index++));
			pixels.add(pixel);
		}
	}

	@Override
	public Code getCode() {
		return CameraCode.IMAGE;
	}

	@Override
	public String toString() {
		return "ImageData [frameNum=" + frameNum + ", pixels=" + pixels + "]";
	}
	
}
