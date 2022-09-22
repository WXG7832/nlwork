package com.nltecklib.protocol.li.workform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 校准板匹配项
 * @author Administrator
 *弃用，使用CalBaseVoltageData 和 LogicBaseVoltageData代替
 */
@Deprecated
public class CheckBoardMatchData extends Data implements Configable, Responsable {
    
	private  short  matchFlag; //匹配标识;低字节在前，高字节在后；从低位开始依次标识对接情况，1标识对接OK，0标识对接fail
	private  int    boardIndex; //校准板号
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
        data.add((byte) unitIndex);
        data.add((byte) driverIndex); //驱动板号
        data.add((byte) boardIndex);
        data.addAll(Arrays.asList(ProtocolUtil.split((long)matchFlag, 2, true)));
        
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		boardIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		matchFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		
	}

	@Override
	public Code getCode() {
		
		return  WorkformCode.BoardMatchCode;
	}

	

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	public short getMatchFlag() {
		return matchFlag;
	}

	public void setMatchFlag(short matchFlag) {
		this.matchFlag = matchFlag;
	}

	public int getBoardIndex() {
		return boardIndex;
	}

	public void setBoardIndex(int boardIndex) {
		this.boardIndex = boardIndex;
	}

	
	
	

}
