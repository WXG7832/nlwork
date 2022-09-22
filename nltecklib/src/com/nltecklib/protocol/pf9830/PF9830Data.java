package com.nltecklib.protocol.pf9830;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PF9830Data {
	
//	private Date time;
	private String time;
	private Date date;
	private List<PF1EData> data = new ArrayList<>();
	
	public enum PF1E {
		
		U, U3, U2, U1, I, I3, I2, I1, P, P3, P2, P1, PF, PF3, PF2, PF1,
		VA, VA3, VA2, VA1, VAR, VAR3, VAR2, VAR1, DEG, DEG3, DEG2, DEG1,
		HZ, UCF3, UCF2, UCF1, ICF3, ICF2, ICF1, KWH;
		
	}
	
	private PfData pfData = new PfData();
	
	public void decode(List<Byte> encodeData) {

		int index = 0;
		index += 2;
		/*pfData.setU(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setU3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setU2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setU1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setI(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setI3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setI2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setI1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setP(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setP3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setP2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setP1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setPf(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setPf3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setPf2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setPf1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVa(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVa3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVa2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVa1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVar(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVar3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVar2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setVar1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setDeg(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setDeg3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setDeg2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setDeg1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setHz(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setUcf3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setUcf2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setUcf1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setIcf3(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setIcf2(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setIcf1(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;
		pfData.setKwh(calculate(encodeData.subList(index, index + 4)).doubleValue());
		index =+ 4;*/
		
		for (int i = 0; i < PF1E.values().length; i++) {
			
			PF1EData pf1eData = new PF1EData();
			PF1E pf1e = PF1E.values()[i];
			double value = calculate(encodeData.subList(index, index + 4)).doubleValue();
			pf1eData.setName(pf1e);
			pf1eData.setValue(value);
			try {
				Field field = pfData.getClass().getDeclaredField(pf1e.name().toLowerCase());
				field.setAccessible(true);
				field.set(pfData, value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			data.add(pf1eData);
			index += 4;
		}
		int hrs = encodeData.get(index++);
		int min = encodeData.get(index++);
		int sec = encodeData.get(index++);
		
		time = Integer.toHexString(hrs) + ":" + Integer.toHexString(min) + ":" + Integer.toHexString(sec);
		pfData.setTime(time);
		
	}
	
	
	private static BigDecimal calculate(List<Byte> data) {
		
		BigDecimal bigDecimal = new BigDecimal("0");
		for (int i = data.size() - 1; i >= 0; i--) {
			
			bigDecimal = bigDecimal.add(new BigDecimal(Long.toString(((data.get(i) & 0xff) << (i * 8)))));
		}

		// 数符S
		BigDecimal s = new BigDecimal(Long.toString(bigDecimal.longValue() >> 31));
		// 阶码E
		BigDecimal e = new BigDecimal(Long.toString(bigDecimal.longValue() & 0xff));
		// 尾数M
		BigDecimal m = new BigDecimal(Long.toString(bigDecimal.longValue() >> 8 & 0x7fffff));
		// 实际值D
		BigDecimal tmp;
		if (e.intValue() < 127) {
			
			tmp = new BigDecimal("1").divide(new BigDecimal(1 << (127 - e.intValue())));
		} else {
			
			tmp = new BigDecimal(1 << (e.intValue() - 127));
		}
		
		BigDecimal d = m.divide(new BigDecimal(1 << 24), 10, BigDecimal.ROUND_HALF_UP)
				.add(new BigDecimal("0.5")).multiply(tmp);
		
		if (s.intValue() != 0) {
			d = d.negate();
		}
		
		return d;
	}
	
	public class PF1EData {
		
		private PF1E name;
		private double value;
		public PF1E getName() {
			return name;
		}
		public void setName(PF1E name) {
			this.name = name;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		
	}
	
	public static void main(String[] args) {
		
		List<Byte> list = new ArrayList<>();
		list.add((byte) 0x87);
		list.add((byte) 0xF8);
		list.add((byte) 0xB2);
		list.add((byte) 0x63);
		
		System.out.println(calculate(list));	// 235.0996
		
	}
	

	public PfData getPfData() {
		return pfData;
	}

	public void setPfData(PfData pfData) {
		this.pfData = pfData;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<PF1EData> getData() {
		return data;
	}

	public void setData(List<PF1EData> data) {
		this.data = data;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		pfData.setDate(date);
	}


	@Override
	public String toString() {
		return "PF9830Data [time=" + time + ", date=" + date + ", data=" + data + ", pfData=" + pfData + "]";
	}
	
}
