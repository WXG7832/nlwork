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
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.BoardType;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.CalBoardTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 地址配置：0x02  支持配置，查询
* @version: v1.0.0
* @author: Admin
* @date: 2021年11月16日 上午11:35:04 
*
*/
public class AddressConfigData extends Data implements Configable, Queryable, Responsable {

	//private BoardType boardType;
	private int boardIndex;
	
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

		//data.add((byte) boardType.ordinal());
		data.add((byte) unitIndex);//板类型
		data.add((byte) boardIndex);//板地址
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
//		int code_boardType = ProtocolUtil.getUnsignedByte(data.get(index++));
//		if (code_boardType > BoardType.values().length - 1) {
//
//			throw new RuntimeException("error boardType mode code : " + code_boardType);
//		}
//		boardType = BoardType.values()[code_boardType];
		
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		boardIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
	}

	@Override
	public Code getCode() {
		return CalBoardTestCode.AddressConfig;
	}

	public int getBoardIndex() {
		return boardIndex;
	}

	public void setBoardIndex(int boardIndex) {
		this.boardIndex = boardIndex;
	}

//	public BoardType getBoardType() {
//		return boardType;
//	}
//
//	public void setBoardType(BoardType boardType) {
//		this.boardType = boardType;
//	}
	

}
