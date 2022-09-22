package com.nltecklib.protocol.fuel.temp;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.temp.TempEnviroment.TempCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 温控板PID协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TBoardPIDData extends Data implements BoardNoSupportable, ComponentSupportable, Configable, Responsable, Queryable {
    private double p;
    private double i;
    private double d;

    public double getP() {
	return p;
    }

    public void setP(double p) {
	this.p = p;
    }

    public double getI() {
	return i;
    }

    public void setI(double i) {
	this.i = i;
    }

    public double getD() {
	return d;
    }

    public void setD(double d) {
	this.d = d;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (p * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (i * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (d * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	double pRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	p = pRecv / 10;
	index += 2;
	double iRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	i = iRecv / 10;
	index += 2;
	double dRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	d = dRecv / 10;
    }

    @Override
    public Code getCode() {
	return TempCode.PID_CODE;
    }

    @Override
    public String toString() {
	return "TBoardPIDData [p=" + p + ", i=" + i + ", d=" + d + ", boardNum=" + boardNum + ", componentCode=" + component + "]";
    }

}
