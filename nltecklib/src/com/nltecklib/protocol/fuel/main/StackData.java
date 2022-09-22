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
 * 主控电推协议数据
 * 
 * @author caichao_tang
 *
 */
public class StackData extends Data implements Configable, Responsable, Queryable {
    private double current;
    private int number;
    private int area;

    public double getCurrent() {
	return current;
    }

    public void setCurrent(double current) {
	this.current = current;
    }

    public int getNumber() {
	return number;
    }

    public void setNumber(int number) {
	this.number = number;
    }

    public int getArea() {
	return area;
    }

    public void setArea(int area) {
	this.area = area;
    }

    @Override
    public void encode() {
	data.add((byte) number);
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (area), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (current * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	number = ProtocolUtil.getUnsignedByte(data.get(index++));
	area = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	current = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
    }

    @Override
    public Code getCode() {
	return MainCode.STACK_CODE;
    }

    @Override
    public String toString() {
	return "StackData [current=" + current + ", number=" + number + ", area=" + area + ", result=" + result + ", orient=" + orient + "]";
    }

}
