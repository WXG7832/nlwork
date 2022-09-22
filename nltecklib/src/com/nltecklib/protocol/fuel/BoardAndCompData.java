package com.nltecklib.protocol.fuel;

import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

@Deprecated
public abstract class BoardAndCompData extends Data implements BoardNoSupportable, ComponentSupportable {
	protected int boardNum;
	protected Component componentCode;

	public Component getComponent() {
		return componentCode;
	}

	public void setComponent(Component componentCode) {
		this.componentCode = componentCode;
	}

	public int getBoardNum() {
		return boardNum;
	}

	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}

}
