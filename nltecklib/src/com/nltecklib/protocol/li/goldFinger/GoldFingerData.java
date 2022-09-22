package com.nltecklib.protocol.li.goldFinger;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.goldFinger.GoldEnvironment.GoldCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class GoldFingerData extends Data implements Queryable, Responsable {
	
	private int ShortCircuitInformation;//短路信息
	
	private boolean  abFace_ok;//ab面是否短路 
	
	private int scCount;//短路总组数
	
	private int aCount;//a短路组数
	
	private int bCount;//b短路组数
	
	private List<Integer> aList = new ArrayList<>(); 
	
	private List<Integer> bList = new ArrayList<>(); 

	@Override
	public void encode() {

		

		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		
		ShortCircuitInformation = ProtocolUtil.getUnsignedByte(data.get(index++));

		scCount = ProtocolUtil.getUnsignedByte(data.get(index++));// 短路总组数

		// 有短路
		if (ShortCircuitInformation == 1) {
			// 判断是哪一面
			abFace_ok = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0xAA;
			// AA面
			if (abFace_ok == true) {
				
				aCount = ProtocolUtil.getUnsignedByte(data.get(index++));

				if (scCount - aCount == 0) {
					for (int i = 0; i < aCount * 2; i++) {

						int aOnly = ProtocolUtil.getUnsignedByte(data.get(index));
						index++;
						aList.add(aOnly);
					}
				}

			}

			// BB面
			if (abFace_ok == false ) {

				bCount = ProtocolUtil.getUnsignedByte(data.get(index++));

				if (scCount - bCount == 0) {
					for (int i = 0; i < bCount * 2; i++) {

						int bOnly = ProtocolUtil.getUnsignedByte(data.get(index));
						index++;
						bList.add(bOnly);
					}	
				}
			}
			
		}
	
		
		
		if (ShortCircuitInformation == 2) {
			
			    int aFace = ProtocolUtil.getUnsignedByte(data.get(index++));
		        
				aCount = ProtocolUtil.getUnsignedByte(data.get(index++));

				for (int i = 0; i < aCount * 2; i++) {

					int aOnly = ProtocolUtil.getUnsignedByte(data.get(index));
					index++;
					aList.add(aOnly);
				}
	
				
				 int bFace  = ProtocolUtil.getUnsignedByte(data.get(index++));

				 bCount = ProtocolUtil.getUnsignedByte(data.get(index++));

				for (int i = 0; i < bCount * 2; i++) {

					int bOnly = ProtocolUtil.getUnsignedByte(data.get(index));
					index++;
					bList.add(bOnly);
				}				
			}
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return GoldCode.GoldFinCode;
	}

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

	public int getShortCircuitInformation() {
		return ShortCircuitInformation;
	}

	public void setShortCircuitInformation(int shortCircuitInformation) {
		ShortCircuitInformation = shortCircuitInformation;
	}

	public boolean isAbFace_ok() {
		return abFace_ok;
	}

	public void setAbFace_ok(boolean abFace_ok) {
		this.abFace_ok = abFace_ok;
	}

	public int getScCount() {
		return scCount;
	}

	public void setScCount(int scCount) {
		this.scCount = scCount;
	}

	public int getaCount() {
		return aCount;
	}

	public void setaCount(int aCount) {
		this.aCount = aCount;
	}

	public int getbCount() {
		return bCount;
	}

	public void setbCount(int bCount) {
		this.bCount = bCount;
	}

	public List<Integer> getaList() {
		return aList;
	}

	public void setaList(List<Integer> aList) {
		this.aList = aList;
	}

	public List<Integer> getbList() {
		return bList;
	}

	public void setbList(List<Integer> bList) {
		this.bList = bList;
	}
	
	
}
