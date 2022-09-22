package com.nltecklib.protocol.lab.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalBackupMode;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * 校准协议
 * @author Administrator
 *
 */
public class BCalibrateData extends Data implements Configable, Queryable, Responsable {
    
	private CalBackupMode   workMode = CalBackupMode.SLEEP;
	
	private List<Double> primitiveAdcs=new ArrayList<Double>();

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		//工作方式
		data.add((byte) workMode.ordinal());
		//ready
		
		//长度
		data.add((byte)primitiveAdcs.size());
		
		for (double val : primitiveAdcs) {
			//原始adc，只读
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(val * 1000), 3, true)));
		}
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		//工作方式
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > CalBackupMode.values().length - 1) {
			
			throw new RuntimeException("error work mode code :" + code);
		}
		workMode = CalBackupMode.values()[code];
		
		int lenth=ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int i=0;i<lenth;i++) {
			double val = (double) ProtocolUtil.compose(data.subList(index, index+ 3).toArray(new Byte[0]), true) / 1000 ;
			index += 3;
			primitiveAdcs.add(val);
		}
		
		//原始ADC
	

	}
	

	public List<Double> getPrimitiveAdcs() {
		return primitiveAdcs;
	}

	public void setPrimitiveAdcs(List<Double> primitiveAdcs) {
		this.primitiveAdcs = primitiveAdcs;
	}

	@Override
	public Code getCode() {

		return BackupCode.CalChnCode;
	}

	

	public CalBackupMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalBackupMode workMode) {
		this.workMode = workMode;
	}


	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "CalibrateData [workMode=" + workMode + ", primitiveAdcs=" + primitiveAdcs + "]";
	}
	
	

}
