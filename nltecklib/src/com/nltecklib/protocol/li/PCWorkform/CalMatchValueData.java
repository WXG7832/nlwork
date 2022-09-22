package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.MatchState;
import com.nltecklib.protocol.li.logic2.Logic2CalMatchData.AdcData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * Âß¼­°åµçÆ½Æ¥Åä
 * 
 * @author Administrator
 *
 */
public class CalMatchValueData extends Data implements Alertable, Responsable {

	private List<AdcData> adcList = new ArrayList<AdcData>();

	public CalMatchValueData() {

	}

	@Override
	public boolean supportUnit() {

		return true;
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

		data.add((byte) unitIndex);
		data.add((byte) adcList.size());
		for (AdcData ad : adcList) {
			data.add((byte) ad.chnIndex);
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (ad.adc * 100), 3, true)));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		adcList.clear();
		for (int n = 0; n < count; n++) {

			AdcData ad = new AdcData();
			ad.chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			ad.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			adcList.add(ad);

		}
	}

	@Override
	public Code getCode() {

		return PCWorkformCode.CalMathValueCode;
	}

	public void setAdcList(List<AdcData> adcList) {
		this.adcList = adcList;
	}

	public List<AdcData> getAdcList() {
		return adcList;
	}

	public void addAdcData(AdcData ad) {

		adcList.add(ad);
	}

	@Override
	public String toString() {
		return "CalMatchValueData [adcList=" + adcList + "]";
	}

}
