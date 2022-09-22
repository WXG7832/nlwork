package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.ConstantTempAlert;
import com.nltecklib.protocol.li.cal.CalEnvironment.DeviationAlert;
import com.nltecklib.protocol.li.cal.CalEnvironment.ElecTempAlert;
import com.nltecklib.protocol.li.cal.CalEnvironment.OverTempAlert;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
* @Description: 超温报警 
* @version: v1.0.0
* @date: 2022年1月19日 下午2:08:00 
*
 */
public class OverTempAlertData extends Data implements Queryable, Responsable {
	  
		private int mainTemp;//主温度
		private int backupTemp;//辅温度
		private int elecTemp1;//电子负载温度1
		private int elecTemp2;//电子负载温度2
		private OverTempAlert overTempAlert = OverTempAlert.NONE;//超温报警
		private ElecTempAlert elecTempAlert = ElecTempAlert.NONE;//电子负载温度报警
		private int fanAlert;//风机故障
		private ConstantTempAlert constantTempAlert = ConstantTempAlert.NONE;//恒温超时报警
		private DeviationAlert deviationAlert = DeviationAlert.NONE;//主辅温度偏差报警
		
		
		
		@Override
		public boolean supportUnit() {
			return false;
		}

		@Override
		public boolean supportDriver() {
			return true;
		}

		@Override
		public boolean supportChannel() {
			return false;
		}

		@Override
		public void encode() {
			
			data.add((byte) driverIndex);
			data.add((byte) mainTemp);
			data.add((byte) backupTemp);
			data.add((byte) elecTemp1);
			data.add((byte) elecTemp2);
			data.add((byte) overTempAlert.ordinal());
			data.add((byte) elecTempAlert.ordinal());
			data.add((byte) fanAlert);
			data.add((byte) constantTempAlert.ordinal());
			data.add((byte) deviationAlert.ordinal());
			
			
		}

		@Override
		public void decode(List<Byte> encodeData) {
			
			data = encodeData;
			int index = 0;
			driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			mainTemp = ProtocolUtil.getUnsignedByte(data.get(index++));
			backupTemp = ProtocolUtil.getUnsignedByte(data.get(index++));
			elecTemp1 = ProtocolUtil.getUnsignedByte(data.get(index++));
			elecTemp2 = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (mode > OverTempAlert.values().length - 1) {

				throw new RuntimeException("error OverTempAlert mode index : " + mode);
			}
			overTempAlert = OverTempAlert.values()[mode];
			
			mode = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (mode > ElecTempAlert.values().length - 1) {

				throw new RuntimeException("error elecTempAlert mode index : " + mode);
			}
			elecTempAlert = ElecTempAlert.values()[mode];
			
			fanAlert = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			mode = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (mode > ConstantTempAlert.values().length - 1) {

				throw new RuntimeException("error ConstantTempAlert mode index : " + mode);
			}
			constantTempAlert = ConstantTempAlert.values()[mode];
			
			mode = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (mode > DeviationAlert.values().length - 1) {

				throw new RuntimeException("error DeviationAlert mode index : " + mode);
			}
			deviationAlert = DeviationAlert.values()[mode];
			
			
		}

		@Override
		public Code getCode() {
			return CalEnvironment.CalCode.OverTempAlertCode;
		}

		public int getMainTemp() {
			return mainTemp;
		}

		public void setMainTemp(int mainTemp) {
			this.mainTemp = mainTemp;
		}

		public int getBackupTemp() {
			return backupTemp;
		}

		public void setBackupTemp(int backupTemp) {
			this.backupTemp = backupTemp;
		}

		public int getElecTemp1() {
			return elecTemp1;
		}

		public void setElecTemp1(int elecTemp1) {
			this.elecTemp1 = elecTemp1;
		}

		public int getElecTemp2() {
			return elecTemp2;
		}

		public void setElecTemp2(int elecTemp2) {
			this.elecTemp2 = elecTemp2;
		}

		public OverTempAlert getOverTempAlert() {
			return overTempAlert;
		}

		public void setOverTempAlert(OverTempAlert overTempAlert) {
			this.overTempAlert = overTempAlert;
		}

		public ElecTempAlert getElecTempAlert() {
			return elecTempAlert;
		}

		public void setElecTempAlert(ElecTempAlert elecTempAlert) {
			this.elecTempAlert = elecTempAlert;
		}

		public int getFanAlert() {
			return fanAlert;
		}

		public void setFanAlert(int fanAlert) {
			this.fanAlert = fanAlert;
		}

		public ConstantTempAlert getConstantTempAlert() {
			return constantTempAlert;
		}

		public void setConstantTempAlert(ConstantTempAlert constantTempAlert) {
			this.constantTempAlert = constantTempAlert;
		}

		public DeviationAlert getDeviationAlert() {
			return deviationAlert;
		}

		public void setDeviationAlert(DeviationAlert deviationAlert) {
			this.deviationAlert = deviationAlert;
		}

	

}
