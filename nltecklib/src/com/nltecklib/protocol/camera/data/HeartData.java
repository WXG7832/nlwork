package com.nltecklib.protocol.camera.data;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.camera.Environment.State;
/**
 * ÐÄÌøÐ­Òé
 * @author Administrator
 *
 */
public class HeartData extends Data implements Encode{
	private State state;
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public void encode() {
		data.clear();
		data.add((byte) state.ordinal());
	}

	@Override
	public Code getCode() {
		return CameraCode.HEART;
	}

	@Override
	public String toString() {
		return "HeartData [state=" + state + "]";
	}
	
}
