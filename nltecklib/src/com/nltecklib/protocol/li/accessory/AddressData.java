package com.nltecklib.protocol.li.accessory;

import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;

/**
 * 0x30µÿ÷∑≤È—Ø≈‰÷√
 * 
 * @author caichao_tang
 *
 */
public class AddressData extends Data implements Configable, Queryable, Responsable {
	private byte boardAddress;

	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add(boardAddress);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		boardAddress = data.get(0);
	}

	@Override
	public Code getCode() {
		return AccessoryCode.ADDRESS;
	}

	public byte getBoardAddress() {
		return boardAddress;
	}

	public void setBoardAddress(byte boardAddress) {
		this.boardAddress = boardAddress;
	}

}
