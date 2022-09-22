package com.nltecklib.protocol.power.calBox.calBox_device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData.AdcData;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * ¶Ô½ÓADC
 * 
 * @author Administrator
 *
 */
public class MbMatchAdcData extends Data implements Queryable, Responsable {

	private List<AdcData> adcList = new ArrayList<AdcData>();

	public MbMatchAdcData() {

	}

	@Override
	public boolean supportDriver() {

		return false;
	}

	@Override
	public boolean supportChannel() {

		return false;
	}

	@Override
	public void encode() {

		data.add((byte) adcList.size());
		for (int n = 0; n < adcList.size(); n++) {

			AdcData ad = adcList.get(n);
			int chnIndex = ad.chnIndex;

			data.add((byte) chnIndex);
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (ad.adc * 100), 3, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		adcList.clear();
		for (int n = 0; n < count; n++) {

			AdcData ad = new AdcData();
			int chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

			double adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			ad.chnIndex = chnIndex;
			ad.adc = adc;
			adcList.add(ad);

		}
	}

	@Override
	public Code getCode() {

		return CalBoxDeviceCode.MatchAdcCode;
	}

	public List<AdcData> getAdcList() {
		return adcList;
	}

	public void addAdcData(AdcData ad) {

		adcList.add(ad);
	}

	public void setAdcList(List<AdcData> adcList) {
		this.adcList = adcList;
	}

	@Override
	public String toString() {
		return "MatchAdcData [adcList=" + adcList + "]";
	}

}
