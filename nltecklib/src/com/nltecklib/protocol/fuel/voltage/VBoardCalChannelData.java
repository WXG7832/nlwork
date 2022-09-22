package com.nltecklib.protocol.fuel.voltage;

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
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电压采集板校准通道协议数据(03)
 * 
 * @author caichao_tang
 *
 */
public class VBoardCalChannelData extends Data implements BoardNoSupportable, ChnSupportable, Responsable, Queryable, Configable {
    private int pickupNum;
    private ArrayList<Double> adcValueArrayList = new ArrayList<Double>();

    public int getPickupNum() {
	return pickupNum;
    }

    public void setPickupNum(int pickupNum) {
	this.pickupNum = pickupNum;
    }

    public ArrayList<Double> getAdcValueArrayList() {
	return adcValueArrayList;
    }

    public void setAdcValueArrayList(ArrayList<Double> adcValueArrayList) {
	this.adcValueArrayList = adcValueArrayList;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split(pickupNum, 1, true)));
	for (double adc : adcValueArrayList) {
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 100), 3, true)));
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	adcValueArrayList = new ArrayList<Double>();
	pickupNum = (int) ProtocolUtil.compose(new Byte[] { encodeData.get(0) }, true);
	List<Byte> recivedAdcArrayList = encodeData.subList(1, pickupNum * 3 + 1);
	for (int i = 0; i < recivedAdcArrayList.size() - 2; i = i + 3) {
	    Byte[] anAdcValueByte = new Byte[3];
	    anAdcValueByte[0] = recivedAdcArrayList.get(i);
	    anAdcValueByte[1] = recivedAdcArrayList.get(i + 1);
	    anAdcValueByte[2] = recivedAdcArrayList.get(i + 2);
	    adcValueArrayList.add(ProtocolUtil.composeSpecialMinus(anAdcValueByte, true) / 100.0);
	}
    }

    @Override
    public Code getCode() {
	return VolCode.CAL_CHANNEL_CODE;
    }

}
