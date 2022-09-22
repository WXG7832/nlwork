/**
 * 
 */
package com.nltecklib.protocol.li.test.CalBoard;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.CalBoardTestCode;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.PowerSwitch;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 电源开关配置：0x03  支持配置，查询
* @version: v1.0.0
* @author: Admin
* @date: 2021年11月19日 上午9:53:01 
*
*/
public class PowerSwitchControlData extends Data implements Configable, Queryable, Responsable {

	private int boardType;//板类型
	private PowerSwitch powerSwitch = PowerSwitch.Close;
	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);//板类型
		data.add((byte) boardType);//板地址
		data.add((byte) powerSwitch.ordinal());
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		boardType = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int code_powerSwitch = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_powerSwitch > PowerSwitch.values().length - 1) {

			throw new RuntimeException("error powerSwitch mode code : " + code_powerSwitch);
		}
		powerSwitch = PowerSwitch.values()[code_powerSwitch];
		
	}

	public PowerSwitch getPowerSwitch() {
		return powerSwitch;
	}

	public void setPowerSwitch(PowerSwitch powerSwitch) {
		this.powerSwitch = powerSwitch;
	}

	@Override
	public Code getCode() {
		return CalBoardTestCode.PowerSwitch;
	}

	public int getBoardType() {
		return boardType;
	}

	public void setBoardType(int boardType) {
		this.boardType = boardType;
	}
	
	@Override
	public String toString() {
		return "PowerSwitchControlData [boardType=" + boardType + ", powerSwitch=" + powerSwitch + "]";
	}

}
