package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * Âß¼­°åµçÆ½Æ¥Åä
 * 
 * @author Administrator
 *
 */
public class Logic2CalMatchData extends Data implements Queryable, Responsable {

	public static class AdcData {

		public int chnIndex;
		public double adc;

		@Override
		public String toString() {
			return "AdcData [chnIndex=" + chnIndex + ", adc=" + adc + "]";
		}

	}

	private List<AdcData> adcList = new ArrayList<AdcData>();

	public Logic2CalMatchData() {

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
		for (int n = 0; n < adcList.size(); n++) {

			AdcData ad = adcList.get(n);
			int chnIndex = ad.chnIndex;
			if (Data.isReverseDriverChnIndex()) {

				chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());

			}
			data.add((byte) chnIndex);
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
			int chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (Data.isReverseDriverChnIndex()) {

				chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());

			}
			double adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			ad.chnIndex = chnIndex;
			ad.adc = adc;
			adcList.add(ad);

		}
	}

	@Override
	public Code getCode() {

		return Logic2Code.MatchCalCode;
	}

	public List<AdcData> getAdcList() {
		return adcList;
	}

	public void addAdcData(AdcData ad) {

		adcList.add(ad);
	}

}
