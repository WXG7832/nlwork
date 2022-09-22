package com.nltecklib.protocol.power;

import java.util.List;

import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.Environment.Orient;
import com.nltecklib.protocol.power.Environment.Result;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ResponseDecorator implements Decorator, Comparable {

	private static final int INFO_NUMBERS = 100;
	private String info = ""; // 错误信息
	private Data destData;
	private boolean responseWithData; // 在编码时回复报文是否需要携带数据区，如果是查询一般选true,其它情况选false,解码无需设置

	private static final int NON_CHN_INDEX = -1;
	private static final int ERR_COUNT_WITH_SUCCESS = 0;

	/**
	 * 
	 * @param data
	 *            该参数必须不能为null
	 * @param responseWithData
	 *            在编码时回复报文是否需要携带数据区，如果是查询回复报文一般选true,配置和报警回复情况选false,解码无需设置
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

	public void setChnIndex(int chnIndex) {

		this.destData.setChnIndex(chnIndex);
	}

	@Override
	public void encode() {

		destData.clear();

		// 插入结果码
		destData.getEncodeData().add((byte) destData.getResult().ordinal());

		if (destData.getCode() instanceof CalBoxDeviceCode) {

			if (destData.supportDriver()) {
				destData.getEncodeData().add((byte) destData.getDriverIndex());
			}
			if (destData.supportChannel()) {
				destData.getEncodeData().add((byte) destData.getChnIndex());
			}
			
			if (responseWithData) {
				
				destData.encode();
			}
			
			if (destData.getResult() != Result.SUCCESS) {

				destData.getEncodeData().addAll(ProtocolUtil.encodeString(info, "utf-8", INFO_NUMBERS));
			}
			

		} else {

			if (destData.getResult() == Result.SUCCESS) {

				if (responseWithData) {

					if (destData.supportDriver()) {
						destData.getEncodeData().add((byte) destData.getDriverIndex());
					}
					if (destData.supportChannel()) {

						destData.getEncodeData().add((byte) destData.getChnIndex());
					}

					destData.encode();
				}
			}

		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		int errBytes = 0;
		destData.setResult(Result.values()[encodeData.get(index++)]);
		
		if (destData.getCode() instanceof CalBoxDeviceCode) {
			
			if (destData.supportDriver()) {

				destData.setDriverIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
			}
			if (destData.supportChannel()) {

				destData.setChnIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
			}
			
			if (encodeData.size() > index && destData.getResult() == Result.SUCCESS) {

				responseWithData = true;// 包含数据区

				destData.decode(encodeData.subList(index, encodeData.size()));
			}
			
			if(destData.getResult() != Result.SUCCESS) {
				
				info = ProtocolUtil.decodeString(encodeData, index, INFO_NUMBERS, "utf-8");
				
			}

			
		} else {

		if (destData.getResult() == Result.SUCCESS) {

			if (encodeData.size() > index) {
				// 子板回复成功解析板号通道号

				if (destData.supportDriver()) {

					destData.setDriverIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
				}
				if (destData.supportChannel()) {

					destData.setChnIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
				}

				if (encodeData.size() > index) {

					responseWithData = true;// 包含数据区

					destData.decode(encodeData.subList(index, encodeData.size()));
				}
			}
		}
		
		}

		// else if (destData.getCode() instanceof MainCode) {
		//
		// info = ProtocolUtil.decodeString(encodeData, index, errBytes, "utf-8");
		// }
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
			// if (rd.getCode() != MainCode.DeviceStateCode && rd.getCode() !=
			// MainCode.PickupCode) {
			//
			// orderCompare = -1; // 降低优先级
			// }
			// if (this.getCode() != MainCode.DeviceStateCode && this.getCode() !=
			// MainCode.PickupCode) {
			//
			// order = -1; // 提高优先级
			// }

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

}
