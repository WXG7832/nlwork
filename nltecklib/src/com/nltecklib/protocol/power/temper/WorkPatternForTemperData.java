/**
 * 
 */
package com.nltecklib.protocol.power.temper;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode.WorkPattern;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 新增工作模式 0x05 
 * @version: v1.0.0
 * @author: Admin
 * @date: 2021年12月30日 下午5:38:56
 *
 */
public class WorkPatternForTemperData extends Data implements Configable, Queryable, Responsable {

	private WorkPattern workPattern;

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) workPattern.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > WorkPattern.values().length - 1) {

			throw new RuntimeException("error workPattern code : " + code);
		}
		workPattern = WorkPattern.values()[code];
	}

	@Override
	public Code getCode() {
		return TemperCode.WorkPattern;
	}

	public WorkPattern getWorkPattern() {
		return workPattern;
	}

	public void setWorkPattern(WorkPattern workPattern) {
		this.workPattern = workPattern;
	}

	@Override
	public String toString() {
		return "WorkPatternForTemperData [workPattern=" + workPattern + "]";
	}

}
