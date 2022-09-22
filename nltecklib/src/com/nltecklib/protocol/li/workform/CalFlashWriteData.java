package com.nltecklib.protocol.li.workform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.CalDot;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 逻辑板flash参数写入通信协议
 * 
 * @author Administrator
 *
 */
public class CalFlashWriteData extends Data implements Configable, Queryable, Responsable {

	//private int chnIndex;

	public final static int MODE_DOT_COUNT = 15;
	public final static int MODE_DOT_LEFT_BYTES = 8;

	private static String[] modeArray = new String[] { "cc", "cch", "cvp", "cvph", "cvn", "cvnh", "dc", "dch" };
	
	private Map<String, List<CalDot>> dotMap;

	public CalFlashWriteData() {
		dotMap = new HashMap<String, List<CalDot>>();
		for (int i = 0; i < modeArray.length; i++) {
			dotMap.put(modeArray[i], new ArrayList<CalDot>());
		}
	}

	@Override
	public boolean supportUnit() {

		return true;
	}

	@Override
	public boolean supportDriver() {

		return false;
	}
	
	

	public Map<String, List<CalDot>> getDotMap() {
		return dotMap;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) chnIndex);

		for (int i = 0; i < modeArray.length; i++) {

			// cc15个校准点
			for (int j = 0; j < MODE_DOT_COUNT; j++) {

				CalDot dot = new CalDot();
				if (j < dotMap.get(modeArray[i]).size()) {
					dot = dotMap.get(modeArray[i]).get(j);
				}

				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
				data.addAll(
						Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, 7)), 4, true)));
				data.addAll(
						Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, 7)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split(dot.da, 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 100), 3, true)));
				data.addAll(Arrays
						.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, 7)), 4, true)));
				data.addAll(Arrays
						.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, 7)), 4, true)));
				for (int k = 0; k < MODE_DOT_LEFT_BYTES; k++) {
					data.add((byte) 0);
				}
			}
		}

	}

	private boolean isDotEmpty(CalDot dot) {

		if (dot.adc == 0 && dot.meter == 0 && dot.adcK == 0 && dot.adcB == 0 && dot.programB == 0
				&& dot.programK == 0) {

			return true;
		}

		return false;
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < modeArray.length; i++) {
			// cc30个校准点
			for (int j = 0; j < MODE_DOT_COUNT; j++) {

				CalDot dot = new CalDot();
				dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
						/ 100;
				index += 3;
				dot.adcK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, 7);
				index += 4;
				dot.adcB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, 7);
				index += 4;
				dot.da = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
						/ 100;
				index += 3;
				dot.programK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, 7);
				index += 4;
				dot.programB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, 7);
				index += 4;

				dotMap.get(modeArray[i]).add(dot);

				index += MODE_DOT_LEFT_BYTES; // 补足8个0
			}
		}
	}

	@Override
	public Code getCode() {

		return WorkformCode.LogicFlashWriteCode;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}


	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}
	
   public void addDot(CalDot dot,String mode) {
	   if(dotMap.containsKey(mode)) {
		   dotMap.get(mode).add(dot);
	   }
   }
}
