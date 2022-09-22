package com.nltecklib.protocol.fuel.control;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * EF 容量上限配置
 * 
 * @author guofang_ma
 *
 */
public class CboardCapacityUpperLimitData extends Data implements Queryable, Configable, Responsable {

	/**
	 * EF流量值
	 */
	private double maxLiquidCalacity;

	public double getMaxLiquidCalacity() {
		return maxLiquidCalacity;
	}

	public void setMaxLiquidCalacity(double maxLiquidCalacity) {
		this.maxLiquidCalacity = maxLiquidCalacity;
	}

	@Override
	public void encode() {
		data.addAll(Arrays.asList(ProtocolUtil.split((int) (maxLiquidCalacity * 10), 1, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		maxLiquidCalacity = ProtocolUtil.compose(data.subList(index, index + 1).toArray(new Byte[0]), true) / 10.0;
	}

	@Override
	public Code getCode() {
		return ControlCode.EF_CAPACITY_UPPER_LIMIT_CODE;
	}

}
