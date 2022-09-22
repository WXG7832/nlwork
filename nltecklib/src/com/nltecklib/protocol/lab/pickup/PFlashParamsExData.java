package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalDot;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月17日 下午3:51:02
* 类说明
*/
public class PFlashParamsExData extends Data implements Configable, Queryable, Responsable {
   
	public final static int MODE_DOT_COUNT = 30;
	public final static int BACKUP_DOT_COUNT = 5;
	public final static int POWER_DOT_COUNT = 5;
	public final static int MODE_DOT_LEFT_BYTES = 2;
	
	/**
	 * 每个点固定为32个字节
	 */
	public final static int DOT_BYTES = 32;
	
	private static CalWorkMode[] modeArray = new CalWorkMode[] { CalWorkMode.CC, CalWorkMode.CV, CalWorkMode.DC,
			CalWorkMode.DV };

	private Map<CalWorkMode, List<CalDot>> dotMap;
	
	//备份电压校准ADC点集合
	private List<CalDot>  backupDots = new ArrayList<>();
	
	//功率电压校准ADC点集合
	private List<CalDot>  powerDots = new ArrayList<>();
	
	public PFlashParamsExData() {
		
		dotMap = new HashMap<CalWorkMode, List<CalDot>>();

		for (int i = 0; i < modeArray.length; i++) {
			dotMap.put(modeArray[i], new ArrayList<CalDot>());
		}
	}
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		for (int i = 0; i < modeArray.length; i++) {

			// cc15个校准点
			for (int j = 0; j < MODE_DOT_COUNT; j++) {

				CalDot dot = new CalDot();
				if (j < dotMap.get(modeArray[i]).size()) {
					dot = dotMap.get(modeArray[i]).get(j);
				}

				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 1000), 4, true)));
				data.addAll(Arrays
						.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
				data.addAll(Arrays
						.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split(dot.da, 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 1000), 4, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
				for (int k = 0; k < MODE_DOT_LEFT_BYTES; k++) {
					data.add((byte) 0);
				}
			}
		}
		//备份电压
		for (int j = 0; j < BACKUP_DOT_COUNT; j++) {
			
			CalDot dot = new CalDot();
			if (j < backupDots.size()) {
				dot = backupDots.get(j);
			}
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 1000), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
			
		}
		//功率电压
       for (int j = 0; j < POWER_DOT_COUNT; j++) {
			
    	   CalDot dot = new CalDot();
			if (j < powerDots.size()) {
				dot = powerDots.get(j);
			}
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 1000), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
			
		}
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		for (int i = 0; i < modeArray.length; i++) {
			// cc30个校准点
			for (int j = 0; j < MODE_DOT_COUNT; j++) {

				CalDot dot = new CalDot();
				dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ 1000;
				index += 4;
				dot.adcK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_K);
				index += BIT_COUNT_K;
				dot.adcB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_B);
				index += BIT_COUNT_B;
				dot.da = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ 1000;
				index += 4;
				dot.programK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_K);
				index += BIT_COUNT_K;
				dot.programB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_B);
				index += BIT_COUNT_B;
				
				if(dot.adc!=0) {
					dotMap.get(modeArray[i]).add(dot);	
				}

				index += MODE_DOT_LEFT_BYTES; // 补足8个0
			}
		}
		
		for (int j = 0; j < BACKUP_DOT_COUNT; j++) {
			
			CalDot dot = new CalDot();
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 1000;
			index += 4;
			dot.adcK = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_K);
			index += BIT_COUNT_K;
			dot.adcB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_B);
			index += BIT_COUNT_B;
			
			addBackupDot(dot);
		}
		
       for (int j = 0; j < POWER_DOT_COUNT; j++) {
			
			CalDot dot = new CalDot();
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 1000;
			index += 4;
			dot.adcK = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_K);
			index += BIT_COUNT_K;
			dot.adcB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_B);
			index += BIT_COUNT_B;
			
			addPowerDot(dot);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.FlashParamsExCode;
	}
	
	public void addDot(CalDot dot, CalWorkMode mode) {
		if (dotMap.containsKey(mode)) {
			dotMap.get(mode).add(dot);
		}
	}
	
	public void addBackupDot(CalDot dot) {
		
		backupDots.add(dot);
	}
	
	public void addPowerDot(CalDot dot) {
		
		powerDots.add(dot);
	}

	public List<CalDot> getBackupDots() {
		return backupDots;
	}

	public void setBackupDots(List<CalDot> backupDots) {
		this.backupDots = backupDots;
	}

	public List<CalDot> getPowerDots() {
		return powerDots;
	}

	public void setPowerDots(List<CalDot> powerDots) {
		this.powerDots = powerDots;
	}

	public Map<CalWorkMode, List<CalDot>> getDotMap() {
		return dotMap;
	}

	public void setDotMap(Map<CalWorkMode, List<CalDot>> dotMap) {
		this.dotMap = dotMap;
	}
	
	

}
