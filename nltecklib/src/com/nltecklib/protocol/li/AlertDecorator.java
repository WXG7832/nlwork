package com.nltecklib.protocol.li;

import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.Environment.Orient;

public class AlertDecorator implements Decorator {

	private Data destData;

	public AlertDecorator(Data destData) {

		if (!(destData instanceof Alertable)) {

			throw new RuntimeException("func code:" + destData.getCode() + " do not support alert protocol");
		}

		this.destData = destData;
		this.destData.setOrient(Orient.ALERT);

	}

	@Override
	public void encode() {

		destData.clear();
		destData.encode();
	}

	@Override
	public void decode(List<Byte> encodeData) {

		destData.decode(encodeData);
	}

	@Override
	public Code getCode() {

		return destData.getCode();
	}

	public Data getDestData() {
		return destData;
	}

	@Override
	public Orient getOrient() {

		return Orient.ALERT;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("AlertDecoretor [");
		if (destData.supportUnit()) {

			sb.append("unitIndex=" + destData.getUnitIndex() + ", ");
		}
		if (destData.supportDriver()) {

			sb.append("driverIndex=" + destData.getDriverIndex() + ", ");
		}
		if (destData.supportChannel()) {

			sb.append("chnIndex=" + destData.getChnIndex() + ", ");
		}

		sb.append("destData=" + destData.toString());

		sb.append("]");

		return sb.toString();
	}
}
