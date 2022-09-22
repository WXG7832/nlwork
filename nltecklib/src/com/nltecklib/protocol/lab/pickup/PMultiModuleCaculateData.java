package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 实验室大电流多膜片计量协议
* @author  wavy_zheng
* @version 创建时间：2022年8月2日 上午9:34:32
* 类说明
*/
public class PMultiModuleCaculateData extends Data implements Configable, Queryable, Responsable {

	public final static int K_BIT = 6;
	public final static int B_BIT = 6;
	
	public static class AdcEntry {

		public double primitiveAdc;
		public double finalAdc;
		@Override
		public String toString() {
			return "AdcEntry [primitiveAdc=" + primitiveAdc + ", finalAdc=" + finalAdc + "]";
		}
		
		
	}

	public static class ReadonlyAdcData {
       
		
		public ReadonlyAdcData() {
			
			
			
		}
		
		/**
		 * 各模片原始adc,真实值集合
		 */
		public List<AdcEntry> adcList = new ArrayList<>();

		public double primitiveBackAdc1;
		public double finalBackAdc1;

		public double primitiveBackAdc2;
		public double finalBackAdc2;
		@Override
		public String toString() {
			return "ReadonlyAdcData [adcList=" + adcList + ", primitiveBackAdc1=" + primitiveBackAdc1
					+ ", finalBackAdc1=" + finalBackAdc1 + ", primitiveBackAdc2=" + primitiveBackAdc2
					+ ", finalBackAdc2=" + finalBackAdc2 + "]";
		}
		
		
		

		

	}

	private byte mouduleIndex; // 膜片
	private Pole pole;
	private CalMode mode;
	private double calculateDot;// 计量点
	private double programDot; // 原始计量程控下发值
	private double programKReadonly;
	private double programBReadonly;
	private double programDotReadonly;
	private double adcKReadonly;
	private double adcBReadonly;
	private double backAdcKReadonly1;
	private double backAdcBReadonly1;
	private double backAdcKReadonly2;
	private double backAdcBReadonly2;
	private int dataCount;
	private int moduleCount;
	
	/**
	 * map存放着各个模片的计量原始ADC和真实值
	 */
	private List<ReadonlyAdcData> adcDatas = new ArrayList<>();
	
	
	public PMultiModuleCaculateData() {
		
		
		
	}

	

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	private int convertIndex(byte bit) {

		for (int n = 0; n < 8; n++) {

			if ((0x01 << n & bit) > 0) {

				return n;
			}
		}

		return -1;

	}
	
	private int convertIndex(int bit) {
	       
		for (int n = 0; n < 32; n++) {

			if ((0x01 << n & bit) > 0) {

				return n;
			}
		}

		return -1;

	}
	

	private byte convertBit(int index) {

		if (index == -1 || index == 0xff) {

			return 0;
		}

		return (byte) (0x01 << index);

	}
	
    private int  convertIntBit(int index) {
		
        if(index ==-1 || index == 0xffff) {
			
			return 0;
		}
        
        return (int) (0x01 << index);
		
	}

	@Override
	public void encode() {

        
		data.add((byte)mouduleIndex);
		data.add((byte) pole.ordinal());
		data.add((byte) mode.getCode());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * Math.pow(10, 3)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) programDot, 2, true)));
		data.add((byte) dataCount);
		
		
		/*data.addAll(Arrays.asList(ProtocolUtil.split((long) (programKReadonly * Math.pow(10, 7)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programBReadonly * Math.pow(10, 7)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programDotReadonly * Math.pow(10, 0)), 2, true)));

		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcKReadonly * Math.pow(10, 7)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcBReadonly * Math.pow(10, 7)), 4, true)));

		if (mode.equals(CalMode.CV)) {

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcKReadonly1 * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcBReadonly1 * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcKReadonly2 * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcBReadonly2 * Math.pow(10, 7)), 4, true)));
		}*/
		
		/**
			 * 编码无需给下位机下发
			 */
		/*
		 * for (int n = 0 ; n < dataCount ; n++) {
		 * 
		 * ReadonlyAdcData dot = new ReadonlyAdcData();
		 * data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus( (long)
		 * (dot.primitiveAdc * Math.pow(10, 2)), 4, true)));
		 * data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus( (long)
		 * (dot.finalAdc * Math.pow(10, 2)), 4, true))); if(mode.equals(CalMode.CV)) {
		 * data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus( (long)
		 * (dot.primitiveBackAdc1 * Math.pow(10, 2)), 4, true)));
		 * data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus( (long)
		 * (dot.finalBackAdc1 * Math.pow(10, 2)), 4, true)));
		 * data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus( (long)
		 * (dot.primitiveBackAdc2 * Math.pow(10, 2)), 4, true)));
		 * data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus( (long)
		 * (dot.finalBackAdc2 * Math.pow(10, 2)), 4, true))); } }
		 */

	}
   
	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		mouduleIndex = data.get(index++);
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code > Pole.values().length - 1) {

			throw new RuntimeException("error pole code:" + code);
		}
		pole = Pole.values()[code];

		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		
//		if (code > CalMode.values().length - 1) {
//
//			throw new RuntimeException("error cal mode code:" + code);
//		}
//		mode = CalMode.values()[code];
		
		mode = CalMode.valueOf(code);

		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
		index += 4;
		programDot = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		dataCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		programKReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, K_BIT);
		index += 4;
		programBReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, B_BIT);
		index += 4;
		programDotReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		adcKReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, K_BIT);
		index += 4;
		adcBReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, B_BIT);
		index += 4;
       
		System.out.println("mode start");
		if (mode.equals(CalMode.CV)) {
			backAdcKReadonly1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;
			backAdcBReadonly1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;
			backAdcKReadonly2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;
			backAdcBReadonly2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;
		}

		adcDatas.clear();
		System.out.println("data count start:" + dataCount);
		
		//获取模片数量
		moduleCount = data.get(index++);
		
		for (int n = 0; n < dataCount; n++) {

			ReadonlyAdcData adc = new ReadonlyAdcData();
             
			System.out.println(n + " data adc start");
			for (int m = 0; m < moduleCount; m++) {
                
				if(m > 0 && mode.equals(CalMode.CV)) {
					
					//cv只计量主膜片
					break;
				}
				AdcEntry entry = new AdcEntry();
				
				//AdcEntry entry = adc.adcList.get(m);
				
				double primitiveAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				double finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				entry.primitiveAdc = primitiveAdc;
				entry.finalAdc     = finalAdc;
						
				adc.adcList.add(entry);
			}

			

			if (mode == CalMode.CV) {

				adc.primitiveBackAdc1 = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;

				index += 4;

				adc.finalBackAdc1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;

				index += 4;
				
				
				adc.primitiveBackAdc2 = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;

				index += 4;

				adc.finalBackAdc2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;

				index += 4;

			}
		
			adcDatas.add(adc);
		}
		
		System.out.println("decode end");
	}


	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.MultiModuleCalculateCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public CalMode getMode() {
		return mode;
	}

	public void setMode(CalMode mode) {
		this.mode = mode;
	}

	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	public double getProgramDot() {
		return programDot;
	}

	public void setProgramDot(double programDot) {
		this.programDot = programDot;
	}

	public Double getProgramKReadonly() {
		return programKReadonly;
	}

	public void setProgramKReadonly(Double programKReadonly) {
		this.programKReadonly = programKReadonly;
	}

	public Double getProgramBReadonly() {
		return programBReadonly;
	}

	public void setProgramBReadonly(Double programBReadonly) {
		this.programBReadonly = programBReadonly;
	}

	public Double getProgramDotReadonly() {
		return programDotReadonly;
	}

	public void setProgramDotReadonly(Double programDotReadonly) {
		this.programDotReadonly = programDotReadonly;
	}

	public Double getAdcKReadonly() {
		return adcKReadonly;
	}

	public void setAdcKReadonly(Double adcKReadonly) {
		this.adcKReadonly = adcKReadonly;
	}

	public Double getAdcBReadonly() {
		return adcBReadonly;
	}

	public void setAdcBReadonly(Double adcBReadonly) {
		this.adcBReadonly = adcBReadonly;
	}

	public Double getBackAdcKReadonly1() {
		return backAdcKReadonly1;
	}

	public void setBackAdcKReadonly1(Double backAdcKReadonly1) {
		this.backAdcKReadonly1 = backAdcKReadonly1;
	}

	public Double getBackAdcBReadonly1() {
		return backAdcBReadonly1;
	}

	public void setBackAdcBReadonly1(Double backAdcBReadonly1) {
		this.backAdcBReadonly1 = backAdcBReadonly1;
	}

	public Double getBackAdcKReadonly2() {
		return backAdcKReadonly2;
	}

	public void setBackAdcKReadonly2(Double backAdcKReadonly2) {
		this.backAdcKReadonly2 = backAdcKReadonly2;
	}

	public Double getBackAdcBReadonly2() {
		return backAdcBReadonly2;
	}

	public void setBackAdcBReadonly2(Double backAdcBReadonly2) {
		this.backAdcBReadonly2 = backAdcBReadonly2;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	public List<ReadonlyAdcData> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<ReadonlyAdcData> adcDatas) {
		this.adcDatas = adcDatas;
	}

	public byte getMouduleIndex() {
		return mouduleIndex;
	}

	public void setMouduleIndex(byte mouduleIndex) {
		this.mouduleIndex = mouduleIndex;
	}

	public int getModuleCount() {
		return moduleCount;
	}



	public void setModuleCount(int moduleCount) {
		this.moduleCount = moduleCount;
	}



	@Override
	public String toString() {
		return "PMultiModuleCaculateData [mouduleIndex=" + mouduleIndex + ", pole=" + pole + ", mode=" + mode
				+ ", calculateDot=" + calculateDot + ", programDot=" + programDot + ", programKReadonly="
				+ programKReadonly + ", programBReadonly=" + programBReadonly + ", programDotReadonly="
				+ programDotReadonly + ", adcKReadonly=" + adcKReadonly + ", adcBReadonly=" + adcBReadonly
				+ ", backAdcKReadonly1=" + backAdcKReadonly1 + ", backAdcBReadonly1=" + backAdcBReadonly1
				+ ", backAdcKReadonly2=" + backAdcKReadonly2 + ", backAdcBReadonly2=" + backAdcBReadonly2
				+ ", dataCount=" + dataCount + ", adcDatas=" + adcDatas + "]";
	}



	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

}
