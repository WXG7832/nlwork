package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class VoiceAlertData extends Data implements Cloneable, Configable, Queryable, Responsable {

	public int time = 0;
 
	private boolean audioAlertOpen;
	@Override
	public void encode() {
		data.add((byte) (audioAlertOpen ? 1 : 0));
		data.addAll(Arrays.asList(ProtocolUtil.split((int) time, 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;

		int flag = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		audioAlertOpen = flag == 1;
		
		time = (int) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;


	}

	@Override
	public Code getCode() {

		return MainCode.BeepAlertCode;
	}

	public boolean isAudioAlertOpen() {
		return audioAlertOpen;
	}

	public void setAudioAlertOpen(boolean audioAlertOpen) {
		this.audioAlertOpen = audioAlertOpen;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString() {
		return "VoiceAlertData [time=" + time + ", audioAlertOpen=" + audioAlertOpen + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (audioAlertOpen ? 1231 : 1237);
		result = prime * result + time;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoiceAlertData other = (VoiceAlertData) obj;
		if (audioAlertOpen != other.audioAlertOpen)
			return false;
		if (time != other.time)
			return false;
		return true;
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