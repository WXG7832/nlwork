package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.ChnSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class TestData extends Data implements Configable, Responsable, Queryable, BoardNoSupportable, ChnSupportable {
	// 内部功能码采用通道号
	// 启用板号
	private List<Boolean> switchOns = new ArrayList<>();
	private List<Double> params = new ArrayList<Double>();
	private String info = "";

	public int getFuncCode() {
		return chnNum;
	}

	public void setFuncCode(int funcCode) {
		this.chnNum = funcCode;
	}

	public List<Double> getParams() {
		return params;
	}

	public void setParams(List<Double> params) {
		this.params = params;
	}

	public List<Boolean> getSwitchOns() {
		return switchOns;
	}

	public void setSwitchOns(List<Boolean> switchOns) {
		this.switchOns = switchOns;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public void encode() {
		data.add((byte) switchOns.size());
		for (boolean on : switchOns) {
			data.add((byte) (on ? 1 : 0));
		}
		data.add((byte) params.size());
		for (double param : params) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (param * 100), 3, true)));
		}
		List<Byte> infoList = ProtocolUtil.encodeString(info, "utf-8", 0);
		data.add((byte) infoList.size());
		data.addAll(infoList);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			switchOns.add(data.get(index) == 1);
			index++;
		}
		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			params.add(
					(double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true)
							/ 100);
			index += 3;
		}
		int len = ProtocolUtil.getUnsignedByte(data.get(index++));
		info = ProtocolUtil.decodeString(encodeData, index, len, "utf-8");
		index+=len;
	}



	@Override
	public Code getCode() {
		return MainCode.TEST_CODE;
	}

	@Override
	public String toString() {
		return "TestData [switchOns=" + switchOns + ", params=" + params + ", info=" + info + "]";
	}

}
