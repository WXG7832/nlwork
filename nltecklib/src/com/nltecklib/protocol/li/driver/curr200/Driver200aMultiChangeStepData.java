package com.nltecklib.protocol.li.driver.curr200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
* Copyright: Copyright (c) 2021 NiuLian
* 
* @Description: 通道流程控制指令 0x2B 支持配置 支持查询
* @version: v1.0.0
* @author: Administrator
* @date: 2021年11月10日 上午10:35:45 
*
 */

public class Driver200aMultiChangeStepData extends Data implements Configable,Queryable,Responsable {
    
	
	private short  chnSelect; //通道选择标志位
	
	
	public static class Step200aControl {
		
		public int chnIndex;
		public int loopIndex;//循环号
		public int stepIndex;//当前步次
		public int rangeLevel;//档位
		public int miliseconds; //流逝时间ms
		public double current;//通道额定电流
		public int diapIndex;//膜片编号
		public List<Double> brangeCurrentLst = new ArrayList<>();//分流n号膜片集合额定/恒定电流 (只读)
	}
	
	private List<Step200aControl>  stepControls = new ArrayList<>();

	
	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)chnSelect, 2, true)));
		
		for(int n = 0 ; n < stepControls.size() ; n++) {
			data.add((byte) stepControls.get(n).loopIndex);
			data.add((byte) stepControls.get(n).stepIndex);
			data.add((byte) stepControls.get(n).rangeLevel);
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (stepControls.get(n).current * 10), 3, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnSelect = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		for(int n = 0 ; n < stepControls.size() ; n++) {
			Step200aControl sc = new Step200aControl();
			sc.loopIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.rangeLevel = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.current = (double) (ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10);
			index += 3;
			int diapIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.diapIndex = diapIndex;
			
			for(int i = 0 ; i < bitcount(diapIndex) ; i++) {
				
				sc.brangeCurrentLst.add((double) (ProtocolUtil.compose(
						data.subList(index, index + 3).toArray(new Byte[0]), true) / 10));
				index +=3;
			}
			
		}

	}

	@Override
	public Code getCode() {
		return DriverCode.MultiTransferProcessCode200a;
	}

	public short getChnSelect() {
		return chnSelect;
	}

	public void setChnSelect(short chnSelect) {
		this.chnSelect = chnSelect;
	}

	public List<Step200aControl> getStepControls() {
		return stepControls;
	}

	public void setStepControls(List<Step200aControl> stepControls) {
		this.stepControls = stepControls;
	}

	public int bitcount(int n)
    {
        int count=0 ;
        while (n!=0) {
            count++ ;
            n &= (n - 1) ;
        }
        return count ;
    }
	
	

}
