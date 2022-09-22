/**
 * 
 */
package com.nltecklib.protocol.power.temper;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 版本信息 功能码 0x04
* @version: v1.0.0
* @date: 2021年12月29日 下午7:58:31 
*
*/
public class VersionForTemperData extends Data implements Queryable, Responsable {

	private String version;

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
		data.addAll(ProtocolUtil.encodeString(version, "US-ASCII", 30));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;

		// 软件版本号
		version = ProtocolUtil.decodeString(data, index, 30, "US-ASCII");
		index += 30;
	}

	@Override
	public Code getCode() {
		return TemperCode.VersionCode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
