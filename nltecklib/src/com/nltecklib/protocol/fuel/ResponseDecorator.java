package com.nltecklib.protocol.fuel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.Orient;
import com.nltecklib.protocol.fuel.Environment.Result;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ResponseDecorator implements Decorator, Comparable {

	private static final int INFO_NUMBERS = 100;
	private String info = ""; // 错误信息
	private Data destData;
	private boolean responseWithData; // 在编码时回复报文是否需要携带数据区，如果是查询一般选true,其它情况选false,解码无需设置

	/**
	 * 
	 * @param data             该参数必须不能为null
	 * @param responseWithData 在编码时回复报文是否需要携带数据区，如果是查询回复报文一般选true,配置和报警回复情况选false,解码无需设置
	 */
	public ResponseDecorator(Data data, boolean responseWithData) {

		if (!(data instanceof Responsable)) {

			throw new RuntimeException("the func code :" + destData.getCode() + " do not support response");
		}
		this.destData = data;
		this.responseWithData = responseWithData;
		this.destData.setOrient(Orient.RESPONSE);
		this.destData.setResult(Result.SUCCESS);

	}

	public Result getResult() {

		return this.destData.getResult();
	}

	public void setResult(Result result) {

		this.destData.setResult(result);
	}

	@Override
	public void encode() {

		destData.clear();

		// 插入结果码
		destData.getEncodeData().add(0, (byte) destData.getResult().ordinal());

		if (destData.getCode() instanceof MainCode) {

			if (destData.getResult() == Result.SUCCESS) {

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

				if (responseWithData) {
					destData.encode();
				}
			} else {
				destData.getEncodeData().addAll(ProtocolUtil.encodeString(info, "utf-8", INFO_NUMBERS));
			}
		} else {
			if (destData.getResult() == Result.SUCCESS) {

				if (responseWithData) {
					destData.encode();
				}
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;

		destData.setResult(Result.values()[encodeData.get(index++)]);

		if (destData.getCode() instanceof MainCode) {

			if (destData.getResult() == Result.SUCCESS) {

				if (destData instanceof BoardNoSupportable) {
					destData.setBoardNum(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
				}

				if (destData instanceof ChnSupportable) {
					destData.setChnNum(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
				}

				if (destData instanceof ComponentSupportable) {
					int componentCode = (int) ProtocolUtil
							.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
					index += 2;
					destData.setComponent(Component.get(componentCode));
				}

				if (encodeData.size() > index) {
					responseWithData = true;
					destData.decode(encodeData.subList(index, encodeData.size()));
				}
			} else {

				info = ProtocolUtil.decodeString(encodeData, index, INFO_NUMBERS, "utf-8");
			}

		} else {
			if (destData.getResult() == Result.SUCCESS) {

				if (encodeData.size() > index) {

					if (destData instanceof BoardNoSupportable) {
						destData.setBoardNum(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
					}

					if (destData instanceof ChnSupportable) {
						destData.setChnNum(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
					}

					if (destData instanceof ComponentSupportable) {
						int componentCode = (int) ProtocolUtil
								.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
						index += 2;
						destData.setComponent(Component.get(componentCode));
					}

					responseWithData = true;
					destData.decode(encodeData.subList(index, encodeData.size()));
				}
			}
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
		int order = 0, orderCompare = 0;
		if (obj instanceof AlertDecorator) {
			orderCompare = 1;
		} else if (obj instanceof ResponseDecorator) {

			ResponseDecorator rd = (ResponseDecorator) obj;
			if (rd.getCode() != MainCode.PICKUP_CODE) {

				orderCompare = -1;
			}
			if (this.getCode() != MainCode.PICKUP_CODE) {

				order = -1;
			}

		} else {

			throw new RuntimeException("接收队列只能装入报警和回复命令");
		}
		return order - orderCompare; // 数字越小指令优先级越高
	}

	@Override
	public Orient getOrient() {

		return Orient.RESPONSE;
	}

	@Override
	public String toString() {

		return destData.toString();
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void checkResult() throws IOException {
		if (getResult() != Result.SUCCESS) {
			throw new IOException("result code err:" + getResult().name() + ", info = " + info);
		}
	}

}
