package com.nltecklib.protocol.fuel.heatConduct;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 导热罐控制板转速配置查询协议数据
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardRotateSpeedData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    /**
     * 转速值
     */
    private double rotate;

    public double getRotate() {
	return rotate;
    }

    public void setRotate(double rotate) {
	this.rotate = rotate;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (rotate * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	rotate = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return HeatConductBoardFunctionCode.ROTATE_SPEED;
    }

}
