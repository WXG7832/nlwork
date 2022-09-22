package com.nltecklib.protocol.fuel;

public abstract class BoardData extends Data implements BoardNoSupportable{
	protected int boardNum;

	public int getBoardNum() {
		return boardNum;
	}

	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}
	
}
