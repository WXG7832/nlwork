package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月30日 上午11:17:06
* 主控开始按校准方案校准通道
* xingguo_wang short字节不够，使用int兼容
*/
public class ChnSelectData extends Data implements Configable, Responsable {
    
//	private short     chnFlag;
	private int		chnFlag;
//	private boolean   calibrate;
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		if (Data.isUseHugeDriverChnCount()) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long)chnFlag, 4,true)));
		} else {
			data.addAll(Arrays.asList(ProtocolUtil.split((long)chnFlag, 2,true)));
		}
//        data.add((byte) (calibrate ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (Data.isUseHugeDriverChnCount()) {
			chnFlag = (int) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
		} else {

			chnFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
		}
// 	    calibrate = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
 	    

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.ChnSelectCode;
	}

	public int getChnFlag() {
		return chnFlag;
	}

	public void setChnFlag(int chnFlag) {
		this.chnFlag = chnFlag;
	}

	@Override
	public String toString() {
		return "CalibrateData [chnFlag=" + chnFlag + "]";
	}

//	public boolean isCalibrate() {
//		return calibrate;
//	}
//
//	public void setCalibrate(boolean calibrate) {
//		this.calibrate = calibrate;
//	}
//	
	
	

}
