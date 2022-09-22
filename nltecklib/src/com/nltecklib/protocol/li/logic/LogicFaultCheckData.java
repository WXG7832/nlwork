package com.nltecklib.protocol.li.logic;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
 * Âß¼­°å¹ÊÕÏ¼ì²â
 * @author Administrator
 *
 */
public class LogicFaultCheckData extends Data implements Queryable, Responsable {
    
	private boolean SRAM_OK;
	private boolean AD1_OK;
	private boolean AD2_OK;
	private boolean FLASH_OK;
	private boolean CAL_OK; //ÊÇ·ñÒÑÐ£×¼
	
	@Override
	public boolean supportUnit() {
		
		return true;
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
		
		data.add((byte) unitIndex);
		//SRAM
		data.add((byte) (SRAM_OK ? 0x00 : 0x01));
		//AD1
		data.add((byte) (AD1_OK ? 0x00 : 0x01));
		//AD2
		data.add((byte) (AD2_OK ? 0x00 : 0x01));
		//FLASH
		data.add((byte) (FLASH_OK ? 0x00 : 0x01));
		//CAL
		data.add((byte) (CAL_OK ? 0x00 : 0x01));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data= encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//SRAM
		SRAM_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//AD1
		AD1_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//AD2
		AD2_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//FLASH
		FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		//CAL
		CAL_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;

	}

	@Override
	public Code getCode() {
		
		return LogicCode.FaultCheckCode;
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

	public boolean isCAL_OK() {
		return CAL_OK;
	}

	public void setCAL_OK(boolean cAL_OK) {
		CAL_OK = cAL_OK;
	}

	
	
	

}
