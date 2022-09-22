package com.nltecklib.protocol.li.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.Pole;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 工作模式配置
 * 
 * 查询只需 基板号 driverIndex ，无需通道号
 * @Desc：   
 * @author：LLC   
 * @Date：2021年11月29日 下午1:48:46   
 * @version
 */
public class CalWorkModeData extends Data implements Queryable, Configable, Responsable {
	  
	    /**
	     * 查询不带通道号
	     * 低位到高位分别表示1-8号基板
	     * Bit位上1表示选用该基板，0表示不选用该基板
	     */
		private int channelNo;
		/** enable？ 校准启动 ：无工作*/
		private boolean enable;
		private WorkMode workMode = WorkMode.SLEEP;
		private int precision; //0表示最低档
		private Pole pole = Pole.NORMAL;
		private int standardVoltage;//基准电压 0-2500mv
		private int standardCurrent;//基准电流 0-2500ma
		
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
			if (isReverseDriverChnIndex()) {

				channelNo = Data.getDriverChnCount() - 1 - channelNo;
			}
			data.add((byte) channelNo); // 通道号
			data.add((byte) (enable ? 0x01 : 0x00));
			data.add((byte) workMode.ordinal());
			data.add((byte) precision);
			data.add((byte) pole.ordinal());
			data.addAll(Arrays.asList(ProtocolUtil.split(standardVoltage, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(standardCurrent, 2, true)));
			
		}

		@Override
		public void decode(List<Byte> encodeData) {
			
			data = encodeData;
			int index = 0;
			
			driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			// 通道号
			channelNo = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (isReverseDriverChnIndex()) {

				channelNo = Data.getDriverChnCount() - 1 - channelNo;
			}
			
			//校准启用
			enable = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
			
			// 工作模式
			int pos = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (pos > CalMode.values().length - 1) {

				throw new RuntimeException("error work mode index : " + pos);
			}
			workMode = WorkMode.values()[pos];

			precision = ProtocolUtil.getUnsignedByte(data.get(index++));

			// 极性
			pos = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (pos > Pole.values().length - 1) {

				throw new RuntimeException("error pole index : " + pos);
			}
			pole = Pole.values()[pos];

			standardVoltage = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			standardCurrent = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

		}

		@Override
		public Code getCode() {
			return CalEnvironment.CalCode.WorkModeCode;
		}

		
		
	


		@Override
		public String toString() {
			return "CalWorkModeData [channelNo=" + channelNo + ", enable=" + enable + ", workMode=" + workMode
					+ ", precision=" + precision + ", pole=" + pole + ", standardVoltage=" + standardVoltage
					+ ", standardCurrent=" + standardCurrent + "]";
		}

		public int getChannelNo() {
			return channelNo;
		}

		public void setChannelNo(int channelNo) {
			this.channelNo = channelNo;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public WorkMode getWorkMode() {
			return workMode;
		}

		public void setWorkMode(WorkMode workMode) {
			this.workMode = workMode;
		}

		public int getPrecision() {
			return precision;
		}

		public void setPrecision(int precision) {
			this.precision = precision;
		}

		public Pole getPole() {
			return pole;
		}

		public void setPole(Pole pole) {
			this.pole = pole;
		}

		public int getStandardVoltage() {
			return standardVoltage;
		}

		public void setStandardVoltage(int standardVoltage) {
			this.standardVoltage = standardVoltage;
		}

		public int getStandardCurrent() {
			return standardCurrent;
		}

		public void setStandardCurrent(int standardCurrent) {
			this.standardCurrent = standardCurrent;
		}
}

