package com.nltecklib.protocol.fuel.voltage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.CalDot;
import com.nltecklib.protocol.fuel.ChnSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电压采集板校准写入协议数据(04)
 * 
 * @author caichao_tang
 *
 */
public class VBoardCalWriteData extends Data implements BoardNoSupportable, ChnSupportable, Responsable, Queryable, Configable {
    /**
     * 校准点
     */
    public static final int CAL_DOT_COUNT = 30;
    /**
     * 每个校准点字节数
     */
    public static final int CAL_DOT_BYTES = 32;

    /**
     * 校准点补0字节数
     */
    public static final int MODE_LEFT_BYTES = 21;
    /**
     * ADC 采样值小数位数
     */
    public static final int ADC_BIT_COUNT = 2;
    /**
     * ADC K 值小数位数
     */
    public static final int K_BIT_COUNT = 7;
    /**
     * ADC B 值小数位数
     */
    public static final int B_BIT_COUNT = 7;

    private List<CalDot> calDots = new ArrayList<>(CAL_DOT_COUNT);

    @Override
    public void encode() {
	// 30个校准点
	for (int i = 0; i < CAL_DOT_COUNT; i++) {

	    CalDot dot = new CalDot();
	    if (i < calDots.size()) {
		dot = calDots.get(i);
	    }
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.getAdc() * Math.pow(10, ADC_BIT_COUNT)), 3, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.getAdck() * Math.pow(10, K_BIT_COUNT)), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.getAdcb() * Math.pow(10, B_BIT_COUNT)), 4, true)));

	    // 补21个0
	    for (int j = 0; j < 21; j++) {
		data.add((byte) 0);
	    }
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	for (int i = 0; i < CAL_DOT_COUNT; i++) {

	    CalDot dot = new CalDot();

	    dot.setAdc((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true) / Math.pow(10, ADC_BIT_COUNT));
	    index += 3;
	    dot.setAdck((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, K_BIT_COUNT));
	    index += 4;
	    dot.setAdcb((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, B_BIT_COUNT));
	    index += 4;

	    calDots.add(dot);

	    index += MODE_LEFT_BYTES;
	}
    }

    @Override
    public Code getCode() {
	return VolCode.CAL_DATA_CODE;
    }

    public List<CalDot> getCalDots() {
	return calDots;
    }

    public void setCalDots(List<CalDot> calDots) {
	this.calDots = calDots;
    }

    @Override
    public String toString() {
	return "VBoardCalWriteData [boardNum=" + boardNum + ", channel=" + chnNum + ", calDots=" + calDots + "]";
    }

}
