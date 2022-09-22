package com.nltecklib.protocol.li.workform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 校准板电阻系数，查询带档位     功能码0x35  支持出厂配置，支持查询
 * 
 * @author Administrator
 *
 */
public class CalResistivityData extends Data implements Queryable, Configable, Responsable {


	private int calBoard; // 校准板号
	private int highPrecision; // 高精度
	private double resistivity;//电阻系数
	
	private final static int  FACTOR_EXP = 8;


	public CalResistivityData(){}
	
	public CalResistivityData(int calBoard,int highPrecision){
		this.calBoard = calBoard;
		this.highPrecision = highPrecision;
	}
	
	@Override
	public boolean supportUnit() {

		return true;
	}

	@Override
	public boolean supportDriver() {

		return true;
	}

	@Override
	public boolean supportChannel() {

		return true;
	}

	@Override
	public void encode() {

		data.add((byte) calBoard);
		data.add((byte) highPrecision);

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		calBoard = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		highPrecision = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		resistivity =   (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP);
		
		

	}

	@Override
	public Code getCode() {

		return WorkformCode.ResisterFactorCode;
	}



	public int getCalBoard() {
		return calBoard;
	}

	public void setCalBoard(int calBoard) {
		this.calBoard = calBoard;
	}

	public int getHighPrecision() {
		return highPrecision;
	}

	public void setHighPrecision(int highPrecision) {
		this.highPrecision = highPrecision;
	}

	public double getResistivity() {
		return resistivity;
	}

	public void setResistivity(double resistivity) {
		this.resistivity = resistivity;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalResistivityData other = (CalResistivityData) obj;
		if (calBoard != other.calBoard)
			return false;
		if (highPrecision != other.highPrecision)
			return false;
/*		if (Double.doubleToLongBits(resistivity) != Double.doubleToLongBits(other.resistivity))
			return false;*/
		return true;
	}
	
	
	
}
