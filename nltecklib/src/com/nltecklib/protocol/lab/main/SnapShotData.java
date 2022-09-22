package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年5月27日 上午9:50:56
* 用于实验室上位机通知工控机对当前选中的通道进行拍照，记录当前通道的状态和数据；
*/
public class SnapShotData extends Data implements Configable, Responsable {
    
	/**
	 * 通道选择
	 */
	private short  chnSelectFlag = 0;
	
	@Override
	public boolean supportMain() {
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
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnSelectFlag), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		chnSelectFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) ;
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.SnapShotCode;
	}

	public short getChnSelectFlag() {
		return chnSelectFlag;
	}

	public void setChnSelectFlag(short chnSelectFlag) {
		this.chnSelectFlag = chnSelectFlag;
	}

	@Override
	public String toString() {
		return "SnapShotData [chnSelectFlag=" + chnSelectFlag + "]";
	}
	
	
	

}
