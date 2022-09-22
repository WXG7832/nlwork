package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;

/**
 * 0x13通道灯批量控制
 * 
 * @author caichao_tang
 *
 */
public class ChnLightBatchControlData extends Data implements Configable, Responsable {
    /**
     * Byte中从低位到高位依次表示：红，绿，蓝，闪烁（1开启，0关闭）
     */
    private List<Byte> channelLightList = new ArrayList<>();

    public List<Byte> getChannelLightList() {
	return channelLightList;
    }

    public void setChannelLightList(List<Byte> channelLightList) {
	this.channelLightList = channelLightList;
    }

    @Override
    public boolean supportMain() {
	return false;
    }

    @Override
    public boolean supportChannel() {
	return false;
    }

    @Override
    public void encode() {
	data.add((byte) channelLightList.size());
	for (int i = 0; i < channelLightList.size(); i++) {
	    data.add((byte) i);
	    data.add(channelLightList.get(i));
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	int chnLightNum = data.get(index++);
	for (int i = 0; i < chnLightNum; i++) {
	    // 通道灯序号以list index表示
	    index++;
	    channelLightList.add(data.get(index++));
	}
    }

    @Override
    public Code getCode() {
	return AccessoryCode.CHN_LIGHT_BATCH_CONTROL;
    }

    @Override
    public String toString() {
	return "ChnLightBatchControlData [channelLightList=" + channelLightList + "]";
    }

}
