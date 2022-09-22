package com.nltecklib.protocol.fins;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.fins.Environment.Type;

public class DMFinsData extends Data{
	private int value;
	private Type type;
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public void encode() {
		data.clear();
		if (type == Type.DINT) {
			datalength = 2;
			if (orient == Orient.WRITE) {
				byte[] byteArray = intToByteArray(value);
				data.add(byteArray[1]);
				data.add(byteArray[0]);
				data.add(byteArray[3]);
				data.add(byteArray[2]);
			}
		}else if (type == Type.INT) {
			datalength = 1;
			if (orient == Orient.WRITE) {
				byte[] byteArray = intToByteArray(value);
				data.add(byteArray[1]);
				data.add(byteArray[0]);
			}
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		if (type == Type.DINT) {
			value = ((data.get(0) & 0x0ff) << 8) + (data.get(1) & 0x0ff) + 
					((data.get(2) & 0x0ff) << 24) + ((data.get(3) & 0x0ff) << 16);
		}else if (type == Type.INT) {
			value = ((data.get(0) & 0x0ff) << 8) + (data.get(1) & 0x0ff);
		}
	}

}
