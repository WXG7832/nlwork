/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 烧录状态
* @author: JenHoard_Shaw
* @date: 创建时间：2022年7月13日 下午5:32:07 
*
*/
public class DiapBurnStateData extends Data implements Configable, Responsable {

	private boolean burnStart;
	
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

		data.add((byte) (burnStart ? 0x01 : 0x00));
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		burnStart = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	}

	@Override
	public Code getCode() {
		return DiapTestCode.BurnState;
	}

	public boolean isBurnStart() {
		return burnStart;
	}

	public void setBurnStart(boolean burnStart) {
		this.burnStart = burnStart;
	}
	
}
