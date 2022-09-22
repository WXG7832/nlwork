package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年7月16日 下午1:34:18
* 分区控制单元的配置和查询
*/
public class ControlUnitData extends Data implements Configable, Queryable, Responsable {
   
	private ProcedureMode   mode = ProcedureMode.DEVICE;
	private List<Byte> controls = new ArrayList<>();
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void encode() {
		
		//最小控制单元
		data.add((byte) mode.ordinal());
		//最小控制单元总数
		data.add((byte) controls.size());
		//分区分配
		data.addAll(controls);
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		//控制单元
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > ProcedureMode.values().length - 1) {
			
			throw new RuntimeException("error minimum control unit code :" + code);
		}
		mode = ProcedureMode.values()[code];
		//控制单元总数
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		controls = data.subList(index, index + count);
		index += count;
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.ControlUnitCode;
	}

	public ProcedureMode getMode() {
		return mode;
	}

	public void setMode(ProcedureMode mode) {
		this.mode = mode;
	}

	public List<Byte> getControls() {
		return controls;
	}

	public void setControls(List<Byte> controls) {
		this.controls = controls;
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
	
	

}
