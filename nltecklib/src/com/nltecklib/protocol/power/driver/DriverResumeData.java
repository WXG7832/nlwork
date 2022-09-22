package com.nltecklib.protocol.power.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年1月17日 下午2:47:48 类说明
 */
public class DriverResumeData extends Data implements Configable, Responsable {

	public static class ResumeUnit {

		public int chnIndex;
		public double capacity; // 接续容量
		public long miliseconds; // 接续时间
		public int loopIndex;
		public int stepIndex;

		@Override
		public String toString() {
			return "ResumeUnit [chnIndex=" + chnIndex + ", capacity=" + capacity + ", miliseconds=" + miliseconds
					+ ", loopIndex=" + loopIndex + ", stepIndex=" + stepIndex + "]";
		}

	}

	private List<ResumeUnit> units = new ArrayList<>();

	@Override
	public boolean supportDriver() {
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

		data.add((byte) units.size());
		for (int n = 0; n < units.size(); n++) {

			ResumeUnit unit = units.get(n);

			data.add((byte) unit.chnIndex);
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (unit.capacity * 10), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(unit.miliseconds, 4, true)));
			data.add((byte) unit.loopIndex);
			data.add((byte) unit.stepIndex);
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		int count = ProtocolUtil.getUnsignedByte(data.get(index++));

		units.clear();
		for (int n = 0; n < count; n++) {

			ResumeUnit unit = new ResumeUnit();

			// 接续容量
			double capacity = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 10;
			index += 4;

			// 接续时间
			long miliseconds = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			int loopIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			int stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			unit.capacity = capacity;
			unit.miliseconds = miliseconds;
			unit.loopIndex = loopIndex;
			unit.stepIndex = stepIndex;
			units.add(unit);
			
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.ResumeCode;
	}

	public List<ResumeUnit> getUnits() {
		return units;
	}

	public void setUnits(List<ResumeUnit> units) {
		this.units = units;
	}

	@Override
	public String toString() {
		return "DriverResumeData [units=" + units + "]";
	}

}
