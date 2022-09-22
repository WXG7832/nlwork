package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PoleData extends Data implements Configable,Queryable,Responsable,Cloneable{
    
	private Pole  pole = Pole.NORMAL;
	private double  poleDefine = 150;  //
	
	public enum Pole{
		
		REVERSE,NORMAL;
	}
	

	
	@Override
	public String toString() {
		return "PoleData [pole=" + pole + ", poleDefine=" + poleDefine + "]";
	}

	@Override
	public void encode() {
		
             data.add((byte)unitIndex);
        	 data.add((byte)pole.ordinal()); 
        	 data.add((byte)(poleDefine < 0 ? 1 : 0));
        	 data.addAll(Arrays.asList(ProtocolUtil.split((int)(Math.abs(poleDefine) * 10), 2, true)));
         
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
	    data = encodeData;
	    unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    if(encodeData.get(index) > Pole.values().length - 1) {
	    	//强行配置为正极性
	    	pole = Pole.NORMAL;
	    }else {
		    pole = Pole.values()[encodeData.get(index++)];
	    }
		boolean minus = encodeData.get(index++) == 1;
		poleDefine  = (double)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		if(minus)
			poleDefine = -poleDefine;
		
		
		
		
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

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof PoleData){
			
			PoleData pd = (PoleData) obj;
			if (pd.getUnitIndex() == this.unitIndex && pd.getPole() == this.pole 
					&& pd.getPoleDefine() == this.poleDefine) {
				
				return true;
			}
		}
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
	public Object clone() throws CloneNotSupportedException {
		
		return super.clone();
	}
	
}
