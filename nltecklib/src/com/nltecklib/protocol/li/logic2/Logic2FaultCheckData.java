package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.DriverFaultInfo;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * Âß¼­°å¹ÊÕÏ¼ì²â
 * 
 * @author Administrator
 *
 */
public class Logic2FaultCheckData extends Data implements Queryable, Responsable {

	private boolean SRAM_OK;

	private boolean AD1_CHECK_OK;
	private boolean AD2_CHECK_OK;
	private boolean AD3_CHECK_OK;
	private boolean AD1_FLASH_OK;
	private boolean AD2_FLASH_OK;
	private boolean AD3_FLASH_OK;

	private boolean LOGIC_FLASH_OK;

	private List<DriverFaultInfo> driverFaultList = new ArrayList<>();

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
		// SRAM
		data.add((byte) (SRAM_OK ? 0x00 : 0x01));
		// AD1
		data.add((byte) (AD1_CHECK_OK ? 0x00 : 0x01));
		// AD2
		data.add((byte) (AD2_CHECK_OK ? 0x00 : 0x01));
		// AD3
		data.add((byte) (AD3_CHECK_OK ? 0x00 : 0x01));
		//Âß¼­°åFLASH ok
		data.add((byte) (LOGIC_FLASH_OK ? 0x00 : 0x01));
		// AD1
		data.add((byte) (AD1_FLASH_OK ? 0x00 : 0x01));
		// AD2
		data.add((byte) (AD2_FLASH_OK ? 0x00 : 0x01));
		// AD3
		data.add((byte) (AD3_FLASH_OK ? 0x00 : 0x01));
        
		//
		data.add((byte) driverFaultList.size());
		
		for(DriverFaultInfo dfi : driverFaultList) {
			
			
			data.add((byte) (dfi.driverFlashOk ? 0x00 : 0x01));
			data.add((byte) dfi.chnFlashOkList.size());
			for(int n = 0 ; n < dfi.chnFlashOkList.size() ; n++) {
				
				data.add((byte) (dfi.chnFlashOkList.get(n) ? 0x00 : 0x01));
			}
			
			
			
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// SRAM
		SRAM_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		// AD1
		AD1_CHECK_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		// AD2
		AD2_CHECK_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		// FLASH
		AD3_CHECK_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
	
		LOGIC_FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		AD1_FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		// AD2
		AD2_FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		// FLASH
		AD3_FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		driverFaultList.clear();
		for(int n = 0 ; n < count ; n++) {
			
			DriverFaultInfo dfi = new DriverFaultInfo();
			dfi.driverFlashOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
			int size = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			for(int i = 0 ; i < size ; i++) {
				
				dfi.chnFlashOkList.add(ProtocolUtil.getUnsignedByte(data.get(index++)) == 0);
			}
			driverFaultList.add(dfi);
			
		}
		
		

	}

	@Override
	public Code getCode() {

		return Logic2Code.FaultCheckCode;
	}

	public boolean isSRAM_OK() {
		return SRAM_OK;
	}

	public void setSRAM_OK(boolean sRAM_OK) {
		SRAM_OK = sRAM_OK;
	}

	public boolean isAD1_CHECK_OK() {
		return AD1_CHECK_OK;
	}

	public void setAD1_CHECK_OK(boolean aD1_CHECK_OK) {
		AD1_CHECK_OK = aD1_CHECK_OK;
	}

	public boolean isAD2_CHECK_OK() {
		return AD2_CHECK_OK;
	}

	public void setAD2_CHECK_OK(boolean aD2_CHECK_OK) {
		AD2_CHECK_OK = aD2_CHECK_OK;
	}

	public boolean isAD3_CHECK_OK() {
		return AD3_CHECK_OK;
	}

	public void setAD3_CHECK_OK(boolean aD3_CHECK_OK) {
		AD3_CHECK_OK = aD3_CHECK_OK;
	}

	public boolean isAD1_FLASH_OK() {
		return AD1_FLASH_OK;
	}

	public void setAD1_FLASH_OK(boolean aD1_FLASH_OK) {
		AD1_FLASH_OK = aD1_FLASH_OK;
	}

	public boolean isAD2_FLASH_OK() {
		return AD2_FLASH_OK;
	}

	public void setAD2_FLASH_OK(boolean aD2_FLASH_OK) {
		AD2_FLASH_OK = aD2_FLASH_OK;
	}

	public boolean isAD3_FLASH_OK() {
		return AD3_FLASH_OK;
	}

	public void setAD3_FLASH_OK(boolean aD3_FLASH_OK) {
		AD3_FLASH_OK = aD3_FLASH_OK;
	}

	public boolean isLOGIC_FLASH_OK() {
		return LOGIC_FLASH_OK;
	}

	public void setLOGIC_FLASH_OK(boolean lOGIC_FLASH_OK) {
		LOGIC_FLASH_OK = lOGIC_FLASH_OK;
	}

	public List<DriverFaultInfo> getDriverFaultList() {
		return driverFaultList;
	}

	public void setDriverFaultList(List<DriverFaultInfo> driverFaultList) {
		this.driverFaultList = driverFaultList;
	}

}
