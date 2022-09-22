/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.check.CheckEnvironment.WorkPattern;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 工作环境功能码：0x01支持配置，查询
 * @version: v1.0.0
 * @author: Admin
 * @date: 2021年12月29日 上午10:09:05
 *
 */
public class WorkEnvData extends Data implements Configable, Queryable, Responsable {

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
		return CheckCode.WorkEnvCode;
	}

	public WorkPattern getWorkPattern() {
		return workPattern;
	}

	public void setWorkPattern(WorkPattern workPattern) {
		this.workPattern = workPattern;
	}

	@Override
	public String toString() {
		return "WorkEnvData [workPattern=" + workPattern + "]";
	}

}
