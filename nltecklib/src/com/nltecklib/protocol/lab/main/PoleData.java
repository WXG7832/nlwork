package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PoleData extends Data implements Configable,Queryable,Responsable,Cloneable {
	
	private Pole pole = Pole.NORMAL;
	private double  poleDefine; //界定电压
	
	@Override
	public void encode() {
		
		Generation gen = Data.getGeneration();
		
		data.add((byte)pole.ordinal());
		data.add((byte)(poleDefine < 0 ? 0 : 1)); //界定电压符号(正/负) 0:负数  1:正数
		data.addAll(Arrays.asList(ProtocolUtil.split(Math.abs((int)(poleDefine * 100)),  Data.getGeneration() == Generation.ND2 ? 4 : 2, true)));	
	}
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		if (encodeData.get(index) > Pole.values().length - 1) {
			//强行配置为正极性
			pole = Pole.NORMAL;
		} else {
			pole = Pole.values()[encodeData.get(index++)];
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
		
		return MainCode.PoleCode;
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
	
	

	@Override
	public String toString() {
		return "PoleData [pole=" + pole + ", poleDefine=" + poleDefine + "]";
	}

	@Override
	public boolean supportChannel() {
		return true;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	

}
