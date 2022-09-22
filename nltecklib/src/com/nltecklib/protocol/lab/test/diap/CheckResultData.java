/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.CheckResultMode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 10.膜片检测结果功能码0x0A支持配置和查询
* @version: v1.0.0
* @author: Admin_shaw
* @date: 2022年1月20日 上午10:45:29 
*
*/
public class CheckResultData extends Data implements Configable, Queryable, Responsable {

	private CheckResultMode checkResult;
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
		data.add((byte) checkResult.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		
		int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (mode > CheckResultMode.values().length - 1) {

			throw new RuntimeException("error CheckResultMode mode code : " + mode);
		}
		checkResult = CheckResultMode.values()[mode];
		
	}

	@Override
	public Code getCode() {
		return DiapTestCode.CheckResult;
	}

	public CheckResultMode getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(CheckResultMode checkResult) {
		this.checkResult = checkResult;
	}

	
}
