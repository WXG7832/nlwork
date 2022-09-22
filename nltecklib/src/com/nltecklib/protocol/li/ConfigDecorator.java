package com.nltecklib.protocol.li;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.Environment.Orient;

public class ConfigDecorator implements Decorator, Comparable {

	private Data destData;
	

	public ConfigDecorator(Data destData) {

		if (!(destData instanceof Configable)) {

			throw new RuntimeException("data code (func code:" + destData.getCode() + ")is not suitalbe for config");
		}
		this.destData = destData;
		this.destData.setOrient(Orient.CONFIG);

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
	public int compareTo(Object obj) {
		int order = 0, orderCompare = 0;
		if (obj instanceof QueryDecorator) {

			orderCompare = 1;
		} else if (obj instanceof ConfigDecorator) {

		} else {

			throw new RuntimeException("发送队列只能装入配置和查询命令");
		}
		return order - orderCompare; // 数字越小指令优先级越高
	}

	@Override
	public Orient getOrient() {
		// TODO Auto-generated method stub
		return Orient.CONFIG;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer("ConfigDecoretor [");
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
