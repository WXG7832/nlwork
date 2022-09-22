package com.nltecklib.protocol.fuel;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.Orient;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 燃料电池查询报文装饰器
 * 
 * @author caichao_tang
 *
 */
public class QueryDecorator implements Decorator, Comparable {

	private Data destData;

	public QueryDecorator(Data destData) {

		if (!(destData instanceof Queryable)) {

			throw new RuntimeException("该协议工(功能码:" + destData.getCode() + ")不适用于查询命令");
		}

		this.destData = destData;
		destData.setOrient(Orient.QUERY);
	}

	@Override
	public void encode() {

		this.destData.clear();

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
	}

	@Override
	public void decode(List<Byte> encodeData) {

		destData.clear();
		destData.getEncodeData().addAll(encodeData);
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
		// 优先级越高返回-1 ， 越低返回1

		int order = 0, orderCompare = 0;
		if (obj instanceof QueryDecorator) {

			QueryDecorator qd = (QueryDecorator) obj;

			if (qd.getCode() != MainCode.PICKUP_CODE) {

				orderCompare = -1;
			}
			if (this.getCode() != MainCode.PICKUP_CODE) {

				order = -1;
			}

			return order - orderCompare;
		} else if (obj instanceof ConfigDecorator) {

			orderCompare = -1;
		} else {

			throw new RuntimeException("发送队列只能装入配置和查询命令");
		}

		return order - orderCompare;

	}

	@Override
	public Orient getOrient() {
		// TODO Auto-generated method stub
		return Orient.QUERY;
	}

	@Override
	public String toString() {

		return destData.toString();
	}

}
