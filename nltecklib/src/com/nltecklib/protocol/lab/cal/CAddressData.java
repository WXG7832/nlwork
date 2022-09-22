/**
 * 
 */
package com.nltecklib.protocol.lab.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;

/**   
* 
* @Description: 校准板地址配置与查询 0x0B
* @author: JenHoard_Shaw
* @date: 创建时间：2022年8月9日 上午11:45:27 
*
*/
public class CAddressData extends Data implements Configable, Queryable, Responsable {

	private byte address;
	
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
		
		data.add(address);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		address = data.get(0);

	}

	@Override
	public Code getCode() {
		return CalCode.ADDRESS;
	}

	public byte getAddress() {
		return address;
	}

	public void setAddress(byte address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "CAddressData [address=" + address + "]";
	}

}
