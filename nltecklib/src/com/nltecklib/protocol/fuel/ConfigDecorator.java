package com.nltecklib.protocol.fuel;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.Orient;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.util.ProtocolUtil;

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
		if (destData instanceof BoardNoSupportable) {
			destData.getEncodeData().add((byte) destData.getBoardNum());
		}

		if (destData instanceof ChnSupportable) {
			destData.getEncodeData().add((byte) destData.getChnNum());
		}

		if (destData instanceof ComponentSupportable) {
			Byte[] number = ProtocolUtil.split(destData.getComponent().getNumber(), 2, true);
			destData.getEncodeData().addAll(Arrays.asList(number));
		}

		destData.encode();

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;

		if (destData instanceof BoardNoSupportable) {
			destData.setBoardNum(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}

		if (destData instanceof ChnSupportable) {
			destData.setChnNum(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}

		if (destData instanceof ComponentSupportable) {
			int componentCode = (int) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]),
					true);
			index += 2;
			destData.setComponent(Component.get(componentCode));
		}
		destData.decode(encodeData.subList(index, encodeData.size()));
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

		return destData.toString();
	}

}
