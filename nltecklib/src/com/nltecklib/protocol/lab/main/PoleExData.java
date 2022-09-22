package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Data.Generation;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年7月29日 上午9:23:10
* 批量保存极性保护
*/
public class PoleExData extends Data implements Configable, Responsable {
   
	//通道选择标记集合
	private List<Byte>  chnFlag = new ArrayList<>(64);
	
	private Pole pole = Pole.NORMAL;
	private double  poleDefine; //界定电压
	
	
	public PoleExData() {
		
		for(int n = 0 ; n < 64 ; n++) {
			
			chnFlag.add((byte) 0);
		}
	}
	
	@Override
	public boolean supportMain() {
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
		
		data.addAll(chnFlag);
		data.add((byte)pole.ordinal());
		data.add((byte)(poleDefine < 0 ? 0 : 1)); //界定电压符号(正/负) 0:负数  1:正数
		data.addAll(Arrays.asList(ProtocolUtil.split(Math.abs((int)(poleDefine * 100)),  Data.getGeneration() == Generation.ND2 ? 4 : 2, true)));	

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		chnFlag = data.subList(index, index + 64);
		index += 64;
		if (data.get(index) > Pole.values().length - 1) {
			//强行配置为正极性
			pole = Pole.NORMAL;
		} else {
			pole = Pole.values()[data.get(index++)];
		}
		int len = Data.getGeneration() == Generation.ND2 ? 4 : 2;
		boolean minus = encodeData.get(index++) == 0;
		poleDefine = (double)ProtocolUtil.compose(data.subList(index, index + len).toArray(new Byte[0]), true) / 100;
		if (minus) {
			poleDefine = -poleDefine;
		}
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.PoleExCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public double getPoleDefine() {
		return poleDefine;
	}

	public void setPoleDefine(double poleDefine) {
		this.poleDefine = poleDefine;
	}
	
	public void appendChnIndex(int chnIndex) {
		
		int index = chnIndex / 8;
		int pos   = chnIndex % 8;
		
		if(index > 63) {
			
			throw new RuntimeException("error byte index :" + index);
		}
		
		byte b = (byte) (chnFlag.get(index) | 0x01 << pos);
		chnFlag.set(index, b);
	}
	
	public boolean isChnSelected(int chnIndex) {
		
		int index = chnIndex / 8;
		int pos   = chnIndex % 8;
        if(index > 63) {
			
			return false;
		}
       return  (chnFlag.get(index) & 0x01 << pos) > 0 ;
	}

}
