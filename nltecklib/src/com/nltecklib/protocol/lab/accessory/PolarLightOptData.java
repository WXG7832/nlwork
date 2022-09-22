/**
 * 
 */
package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 极性灯操作 0x1A 支持配置与查询
* @author: JenHoard_Shaw
* @date: 2022年5月16日 下午4:06:19 
*
*/
public class PolarLightOptData extends Data implements Configable, Queryable, Responsable {

	private PolarMode polarMode;// 极性
	private byte flickFlag;// 闪烁标志位
	
	/**
	* @Description: 灯极性
	 */
	public enum PolarMode{
		
		POSITIVE,REVERSE
	}
	
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
		
		data.add((byte) polarMode.ordinal());
		data.add(flickFlag);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > PolarMode.values().length - 1) {

			throw new RuntimeException("error polarMode code :" + code);
		}
		polarMode = PolarMode.values()[code];
		
		flickFlag = data.get(index++);

	}

	@Override
	public Code getCode() {
		return AccessoryCode.PolarLightCode;
	}

	
	public PolarMode getPolarMode() {
		return polarMode;
	}

	public void setPolarMode(PolarMode polarMode) {
		this.polarMode = polarMode;
	}

	public byte getFlickFlag() {
		return flickFlag;
	}

	public void setFlickFlag(byte flickFlag) {
		this.flickFlag = flickFlag;
	}

}
