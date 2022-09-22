/**
 * 
 */
package com.nltecklib.protocol.li.test.CalBoard;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.BoardType;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.CalBoardTestCode;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.PowerSwitch;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.PrecisionSwitch;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.WorkPattern;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.ChargeMode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 工作模式：0x01   支持配置，查询
* @version: v1.0.0
* @author: Admin
* @date: 2021年11月16日 上午11:34:12 
*
*/
public class WorkPatternData extends Data implements Configable, Queryable, Responsable {

	//private BoardType boardType;
	private WorkPattern workPattern;
	private PrecisionSwitch precision;
	private long volReference;//基准电压
	private long currReference;//基准电流
	private long delay;
	//private PowerSwitch powerSwitch;
	
	@Override
	public boolean supportUnit() {
		return true;
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

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		//data.add((byte) boardType.ordinal());
		data.add((byte) workPattern.ordinal());
		data.add((byte) precision.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(volReference, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(currReference, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(delay, 2, true)));
		
		//data.add((byte) powerSwitch.ordinal());
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
//		int code_boardType = ProtocolUtil.getUnsignedByte(data.get(index++));
//
//		if (code_boardType > BoardType.values().length - 1) {
//
//			throw new RuntimeException("error boardType mode code : " + code_boardType);
//		}
//		boardType = BoardType.values()[code_boardType];
		
		int code_workPattern = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_workPattern > WorkPattern.values().length - 1) {

			throw new RuntimeException("error workPattern mode code : " + code_workPattern);
		}
		workPattern = WorkPattern.values()[code_workPattern];
		
		int code_precision = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_precision > PrecisionSwitch.values().length - 1) {

			throw new RuntimeException("error precision mode code : " + code_precision);
		}
		precision = PrecisionSwitch.values()[code_precision];
		
		volReference = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		currReference = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
//		int code_powerSwitch = ProtocolUtil.getUnsignedByte(data.get(index++));
//
//		if (code_powerSwitch > PowerSwitch.values().length - 1) {
//
//			throw new RuntimeException("error powerSwitch mode code : " + code_powerSwitch);
//		}
//		powerSwitch = PowerSwitch.values()[code_powerSwitch];
		
	}

	@Override
	public Code getCode() {
		return CalBoardTestCode.WorkPattern;
	}

//	public BoardType getBoardType() {
//		return boardType;
//	}
//
//	public void setBoardType(BoardType boardType) {
//		this.boardType = boardType;
//	}

	public WorkPattern getWorkPattern() {
		return workPattern;
	}

	public void setWorkPattern(WorkPattern workPattern) {
		this.workPattern = workPattern;
	}

	public PrecisionSwitch getPrecision() {
		return precision;
	}

	public void setPrecision(PrecisionSwitch precision) {
		this.precision = precision;
	}

	public long getVolReference() {
		return volReference;
	}

	public void setVolReference(long volReference) {
		this.volReference = volReference;
	}

	public long getCurrReference() {
		return currReference;
	}

	public void setCurrReference(long currReference) {
		this.currReference = currReference;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	
}
