package com.nltecklib.protocol.camera.data;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.camera.Environment.Pallet;
/**
 * 调色板协议
 * @author Administrator
 *
 */
public class PalletData extends Data implements Encode{
	private Pallet pallet;
	
	public Pallet getPallet() {
		return pallet;
	}

	public void setPallet(Pallet pallet) {
		this.pallet = pallet;
	}

	@Override
	public void encode() {
		data.clear();
		data.add((byte) pallet.getCode());
	}

	@Override
	public Code getCode() {
		return CameraCode.PALLET;
	}

	@Override
	public String toString() {
		return "PalletData [pallet=" + pallet + "]";
	}

}
