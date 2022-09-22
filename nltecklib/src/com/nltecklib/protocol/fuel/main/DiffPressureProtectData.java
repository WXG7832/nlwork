package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控压差保护协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class DiffPressureProtectData extends Data implements Responsable, Configable, Queryable {

    private int alert;
    private int stop;

    public int getAlert() {
	return alert;
    }

    public void setAlert(int alert) {
	this.alert = alert;
    }

    public int getStop() {
	return stop;
    }

    public void setStop(int stop) {
	this.stop = stop;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (alert), 2, true))); // 编码
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stop), 2, true))); // 编码
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	alert = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	stop = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
    }

    @Override
    public Code getCode() {
	return MainCode.DIFFPRESSURE_PROTECT_CODE;
    }

    @Override
    public String toString() {
	return "DiffPressureProtectData [alert=" + alert + ", stop=" + stop + "]";
    }

}
