package com.nltecklib.protocol.camera;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.camera.Environment.Code;
/**
 * 协议数据内容格式
 * 
 * @author Administrator
 *
 */
public abstract class Data implements NlteckIOPackage {

	protected  List<Byte> data = new ArrayList<Byte>();

	public Data() {

	}

	public abstract Code getCode(); // 获取协议码

	public int getLength() {
		// 数据区长度
		return data.size();
	}

	public List<Byte> getEncodeData() {

		return data;
	}
	
	public void clear() {

		this.data.clear();
	}

	public List<Byte> getData() {
		return data;
	}

	public void setData(List<Byte> data) {
		this.data = data;
	}
}
