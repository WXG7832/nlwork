package com.nltecklib.protocol.lab.backup;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BProtectionData extends Data implements Configable, Queryable, Responsable {
    
	private double  backupVoltUpper;


	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		//备份电压上限值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(backupVoltUpper * 1000), 3, true)));
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		//备份电压上限值
		backupVoltUpper = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 1000;
		index += 3;
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return BackupCode.BatterProtectCode;
	}



	public double getBackupVoltUpper() {
		return backupVoltUpper;
	}

	public void setBackupVoltUpper(double backupVoltUpper) {
		this.backupVoltUpper = backupVoltUpper;
	}

	

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "ProtectionData [backupVoltUpper=" + backupVoltUpper + "]";
	}
	
	

}
