package com.nltecklib.protocol.fuel;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.Orient;
import com.nltecklib.protocol.fuel.Environment.Result;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * 此类用于表示协议数据区
 * 
 * @author caichao_tang
 *
 */
public abstract class Data implements NlteckIOPackage {

	protected List<Byte> data = new ArrayList<Byte>();

	protected int boardNum;
	protected int chnNum;
	protected Component component = Component.NONE;

	protected Result result = Result.SUCCESS;
	protected Orient orient;	

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public int getChnNum() {
		return chnNum;
	}

	public void setChnNum(int chnNum) {
		this.chnNum = chnNum;
	}

	public int getBoardNum() {
		return boardNum;
	}

	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}

	public Data() {

	}

	/**
	 * 编码
	 */
	public abstract void encode();

	/**
	 * 解码
	 * 
	 * @param encodeData
	 */
	public abstract void decode(List<Byte> encodeData);

	/**
	 * 获得功能码
	 * 
	 * @return
	 */
	public abstract Code getCode();

	public int getLength() {
		// 数据区长度
		return data.size();
	}

	public List<Byte> getEncodeData() {

		return data;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Orient getOrient() {
		return orient;
	}

	public void setOrient(Orient orient) {
		this.orient = orient;
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
