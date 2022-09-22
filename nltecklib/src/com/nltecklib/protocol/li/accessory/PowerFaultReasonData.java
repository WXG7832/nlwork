package com.nltecklib.protocol.li.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
* @author  llinc
* @version 创建时间：2020年11月
* 类说明
*/
public class PowerFaultReasonData extends Data implements Queryable, Responsable {


	private CommState commState = CommState.NORMAL;
	private WorkMode workMode = WorkMode.FORWARDCHARGE;
	private ProtectState protectState = ProtectState.NORMAL;
	private FanState fanState = FanState.NORMAL;
	private TempState tempState = TempState.NORMAL;
	private ElecFrequencyState  elecFrequencyState = ElecFrequencyState.NORMAL;
	private OverVoltageState overVoltageState = OverVoltageState.NORMAL;
	private UnderVoltageState underVoltageState = UnderVoltageState.NORMAL;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
        int code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > CommState.values().length - 1) {
        	
        	throw new RuntimeException("error comm state code : " + code);
        }
        commState = CommState.values()[code];
        
        code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > WorkMode.values().length - 1) {
        	
        	throw new RuntimeException("error workMode state code : " + code);
        }
        workMode = WorkMode.values()[code];
        
        code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > ProtectState.values().length - 1) {
        	
        	throw new RuntimeException("error protect state code : " + code);
        }
        protectState = ProtectState.values()[code];
        
        code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > FanState.values().length - 1) {
        	
        	throw new RuntimeException("error fan state code : " + code);
        }
        fanState = FanState.values()[code];
        
        code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > TempState.values().length - 1) {
        	
        	throw new RuntimeException("error temp state code : " + code);
        }
        tempState = TempState.values()[code];
        
        code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > ElecFrequencyState.values().length - 1) {
        	
        	throw new RuntimeException("error elecFrequency state code : " + code);
        }
        elecFrequencyState = ElecFrequencyState.values()[code];
        
        code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > OverVoltageState.values().length - 1) {
        	
        	throw new RuntimeException("error overVoltage state code : " + code);
        }
        overVoltageState = OverVoltageState.values()[code];
        
        code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > UnderVoltageState.values().length - 1) {
        	
        	throw new RuntimeException("error underVoltage state code : " + code);
        }
        underVoltageState = UnderVoltageState.values()[code];
        
        
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.PowerFaultReasonCode;
	}

	/**
	 * 通讯状态 0 通讯正常 1通讯中断
	 */
	public enum CommState{
		NORMAL,INTERRUPT;
		
		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "通讯正常";
			case INTERRUPT:
				return "通讯中断";
			}
			return super.toString();
		}
	}
	
	/**
	 * 工作模式 0正向充电 1反向放电
	 */
	public enum WorkMode{
		FORWARDCHARGE,REVERSEDISCHARGE;
		
		@Override
		public String toString() {
			switch (this) {
			case FORWARDCHARGE:
				return "正向充电";
			case REVERSEDISCHARGE:
				return "反向放电";
			}
			return super.toString();
		}
	}
	
	/**
	 * 总保护状态 0正常  1保护
	 */
	public enum ProtectState{
		NORMAL,PROTECT;
		
		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "正常";
			case PROTECT:
				return "保护";
			}
			return super.toString();
		}
	}
	
	/**
	 * 风机状态 0正常  1故障
	 */
	public enum FanState{
		NORMAL,FAULT;
		
		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "正常";
			case FAULT:
				return "故障";
			}
			return super.toString();
		}
	}
	
	/**
	 * 温度状态 0正常  1 过温
	 */
	public enum TempState{
		NORMAL,OVERTEMP;
		
		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "正常";
			case OVERTEMP:
				return "过温";
			}
			return super.toString();
		}
	}
	
	/**
	 * 市电频率  0正常  1 异常
	 */
	public enum ElecFrequencyState{
		NORMAL,ABNORMAL;
		
		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "正常";
			case ABNORMAL:
				return "异常";
			}
			return super.toString();
		}
	}
	
	/**
	 * 市电频率 0正常  1 过压
	 */
	public enum OverVoltageState{
		NORMAL,OVER;
		
		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "正常";
			case OVER:
				return "过压";
			}
			return super.toString();
		}
	}
	
	/**
	 * 市电欠压 0正常  1 欠压
	 */
	public enum UnderVoltageState{
		NORMAL,UNDER;
		
		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "正常";
			case UNDER:
				return "欠压";
			}
			return super.toString();
		}
	}
	

	public CommState getCommState() {
		return commState;
	}

	public void setCommState(CommState commState) {
		this.commState = commState;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public ProtectState getProtectState() {
		return protectState;
	}

	public void setProtectState(ProtectState protectState) {
		this.protectState = protectState;
	}

	public FanState getFanState() {
		return fanState;
	}

	public void setFanState(FanState fanState) {
		this.fanState = fanState;
	}

	public TempState getTempState() {
		return tempState;
	}

	public void setTempState(TempState tempState) {
		this.tempState = tempState;
	}

	public ElecFrequencyState getElecFrequencyState() {
		return elecFrequencyState;
	}

	public void setElecFrequencyState(ElecFrequencyState elecFrequencyState) {
		this.elecFrequencyState = elecFrequencyState;
	}

	public OverVoltageState getOverVoltageState() {
		return overVoltageState;
	}

	public void setOverVoltageState(OverVoltageState overVoltageState) {
		this.overVoltageState = overVoltageState;
	}

	public UnderVoltageState getUnderVoltageState() {
		return underVoltageState;
	}

	public void setUnderVoltageState(UnderVoltageState underVoltageState) {
		this.underVoltageState = underVoltageState;
	}
	
	
	public void setPowerAddress(int address) {
		
		this.chnIndex = address;
	}
	
	public int  getPowerAddress() {
		
		return chnIndex;
	}
}
