package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.ErrCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.pickup.PCalibrateExData.AdcData;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年4月13日 下午4:34:01 适用于2代校准ADC查询协议
 */
public class CalQueryChnADCExData extends Data implements Queryable, Responsable {

	private byte moduleIndex;
	private ErrCode errCode = ErrCode.NORMAL;
	private List<AdcData> adcList = new ArrayList<>();

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		if (Data.isUseModuleCal()) {
			data.add((byte) moduleIndex);
		}
		// errCode
		data.add((byte) errCode.ordinal());
		data.add((byte) adcList.size());
		for (AdcData adc : adcList) {

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.primitiveAdc * 1000), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.primitiveBackAdc * 1000), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.primitivePowerAdc * 1000), 4, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		if (Data.isUseModuleCal()) {
			moduleIndex = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		}
		// 故障码
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ErrCode.values().length - 1) {

			throw new RuntimeException("error errCode mode code :" + code);
		}
		errCode = ErrCode.values()[code];

		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			AdcData adc = new AdcData();

			double val = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 1000;
			index += 4;
			adc.primitiveAdc = val;

			val = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;
			adc.primitiveBackAdc = val;

			val = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;
			adc.primitivePowerAdc = val;

			adcList.add(adc);

		}

	}

	public byte getModuleIndex() {
		return moduleIndex;
	}

	public void setModuleIndex(byte moduleIndex) {
		this.moduleIndex = moduleIndex;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.CalQueryChnADCExCode;
	}

	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
	}

	public List<AdcData> getAdcList() {
		return adcList;
	}

	public void setAdcList(List<AdcData> adcList) {
		this.adcList = adcList;
	}

	@Override
	public String toString() {
		return "CalQueryChnADCExData [errCode=" + errCode + ", adcList=" + adcList + "]";
	}

}
