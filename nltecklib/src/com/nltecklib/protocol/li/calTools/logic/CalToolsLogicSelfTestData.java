package com.nltecklib.protocol.li.calTools.logic;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicEnvironment.CalToolsLogicCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
 * Âß¼­°å¹ÊÕÏ¼ì²â
 * @author Administrator
 *
 */
public class CalToolsLogicSelfTestData extends Data implements Queryable, Responsable {
    
	private boolean SRAM_OK;
	private boolean AD1_OK;
	private boolean AD2_OK;
	private boolean AD3_OK;
	private boolean FLASH_OK;
	
	@Override
	public boolean supportUnit() {
		
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		//SRAM
		data.add((byte) (SRAM_OK ? 0x00 : 0x01));
		//AD1
		data.add((byte) (AD1_OK ? 0x00 : 0x01));
		//AD2
		data.add((byte) (AD2_OK ? 0x00 : 0x01));
		//AD3
		data.add((byte) (AD3_OK ? 0x00 : 0x01));
		//FLASH
		data.add((byte) (FLASH_OK ? 0x00 : 0x01));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data= encodeData;
		//SRAM
		SRAM_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//AD1
		AD1_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//AD2
		AD2_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//AD3
		AD3_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//FLASH
		FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;

	}

	@Override
	public Code getCode() {
		
		return CalToolsLogicCode.SerialTestCode;
	}

	public boolean isSRAM_OK() {
		return SRAM_OK;
	}

	public void setSRAM_OK(boolean sRAM_OK) {
		SRAM_OK = sRAM_OK;
	}

	public boolean isAD1_OK() {
		return AD1_OK;
	}

	public void setAD1_OK(boolean aD1_OK) {
		AD1_OK = aD1_OK;
	}

	public boolean isAD2_OK() {
		return AD2_OK;
	}

	public void setAD2_OK(boolean aD2_OK) {
		AD2_OK = aD2_OK;
	}

	public boolean isFLASH_OK() {
		return FLASH_OK;
	}

	public void setFLASH_OK(boolean fLASH_OK) {
		FLASH_OK = fLASH_OK;
	}

	public boolean isAD3_OK() {
		return AD3_OK;
	}

	public void setAD3_OK(boolean aD3_OK) {
		AD3_OK = aD3_OK;
	}

	@Override
	public String toString() {
		return "CalToolsLogicSelfTestData [SRAM_OK=" + SRAM_OK + ", AD1_OK=" + AD1_OK + ", AD2_OK=" + AD2_OK
				+ ", AD3_OK=" + AD3_OK + ", FLASH_OK=" + FLASH_OK + "]";
	}

}
