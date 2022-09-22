package com.nltecklib.protocol.li.check2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.check2.Check2Environment.DriverFaultInfo;
import com.nltecklib.protocol.util.ProtocolUtil;

public class Check2FaultCheckData extends Data implements Queryable, Responsable {

	private boolean AD_OK;
	private boolean FLASH_OK;
	private boolean CAL_OK; // 是否已校准
	
	/**
	 * adc校准系数
	 */
	private boolean ADC_FLASH_OK;
	private List<DriverFaultInfo> driverFaultInfos = new ArrayList<DriverFaultInfo>();
	

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
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
		// AD
		data.add((byte) (AD_OK ? 0x00 : 0x01));
		// FLASH
		data.add((byte) (FLASH_OK ? 0x00 : 0x01));
		// CAL
		data.add((byte) (CAL_OK ? 0x00 : 0x01));

		//adc校准系数
		data.add((byte) (ADC_FLASH_OK ? 0x01 : 0x00));
		data.add((byte) driverFaultInfos.size());
		for(int n = 0 ; n < driverFaultInfos.size() ; n++) {
			
			DriverFaultInfo dfi = driverFaultInfos.get(n);
			data.add((byte) (dfi.stateOk ? 0x00 : 0x01));
			data.add((byte) (dfi.baseVoltOk ? 0x00 : 0x01));
			
			short chnFlag = dfi.chnsFlag;
			if (isReverseDriverChnIndex()) {

				if (Data.getDriverChnCount() <= 8) {

					chnFlag = ProtocolUtil.reverseByteBit((byte) dfi.chnsFlag, Data.getDriverChnCount());
				} else if (Data.getDriverChnCount() <= 16) {
					chnFlag = ProtocolUtil.reverseShortBit(dfi.chnsFlag, Data.getDriverChnCount());
				}
			}
			
			Arrays.asList(ProtocolUtil.split((long) chnFlag, 2, true));
			
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// AD1
		AD_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		// FLASH
		FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		// CAL
		CAL_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		ADC_FLASH_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

		int count = ProtocolUtil.getUnsignedByte(data.get(index++));

		for(int n = 0 ; n < count ; n++) {
			
			DriverFaultInfo dfi = new DriverFaultInfo();
			dfi.stateOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
			dfi.baseVoltOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
            short chnFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
            index += 2;
            if (isReverseDriverChnIndex()) {
                
    			if (Data.getDriverChnCount() <= 8) {
                    
    				chnFlag = ProtocolUtil.reverseByteBit((byte)chnFlag, Data.getDriverChnCount());
    			} else if (Data.getDriverChnCount() <= 16) {
    				chnFlag = ProtocolUtil.reverseShortBit(chnFlag, Data.getDriverChnCount());
    			} 
    		}
            dfi.chnsFlag = chnFlag;
            driverFaultInfos.add(dfi);
           
            
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.FaultCheckCode;
	}

	public boolean isAD_OK() {
		return AD_OK;
	}

	public void setAD_OK(boolean aD_OK) {
		AD_OK = aD_OK;
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

	public boolean isADC_FLASH_OK() {
		return ADC_FLASH_OK;
	}

	public void setADC_FLASH_OK(boolean aDC_FLASH_OK) {
		ADC_FLASH_OK = aDC_FLASH_OK;
	}

	public List<DriverFaultInfo> getDriverFaultInfos() {
		return driverFaultInfos;
	}

	public void setDriverFaultInfos(List<DriverFaultInfo> driverFaultInfos) {
		this.driverFaultInfos = driverFaultInfos;
	}

	@Override
	public String toString() {
		return "Check2FaultCheckData [AD_OK=" + AD_OK + ", FLASH_OK=" + FLASH_OK + ", CAL_OK=" + CAL_OK
				+ ", ADC_FLASH_OK=" + ADC_FLASH_OK + ", driverFaultInfos=" + driverFaultInfos + "]";
	}

	
	

}
