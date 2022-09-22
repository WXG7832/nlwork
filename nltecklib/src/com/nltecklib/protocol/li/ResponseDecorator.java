package com.nltecklib.protocol.li;

import java.util.List;

import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.Environment.Orient;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
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
		this.destData.setResult(DefaultResult.SUCCESS);

	}

	public Result getResult() {

		return this.destData.getResult();
	}

	public void setResult(Result result) {

		this.destData.setResult(result);
	}

	public void setUnitIndex(int unitIndex) {

		this.destData.setUnitIndex(unitIndex);
	}

	public void setDriverIndex(int driverIndex) {

		this.destData.setDriverIndex(driverIndex);
	}

	public void setChnIndex(int chnIndex) {

		this.destData.setChnIndex(chnIndex);
	}

	@Override
	public void encode() {

		destData.clear();

		// 插入结果码
		destData.getEncodeData().add(0, (byte) destData.getResult().getCode());

		if (destData.getCode() instanceof MainCode) {
			// 如果是和监控系统通信
			if (destData.supportUnit()) {
				destData.getEncodeData().add((byte) destData.getUnitIndex());
			}
		} else if (destData.getCode() instanceof WorkformCode || destData.getCode() instanceof MBWorkformCode
				|| destData.getCode() instanceof PCWorkformCode) {

			if ((destData.getResult().getCode() == Result.SUCCESS && !responseWithData)
					|| destData.getResult().getCode() != Result.SUCCESS) {

				if (destData.supportUnit()) {
					destData.getEncodeData().add((byte) destData.getUnitIndex());
				}
				if (destData.supportDriver()) {
					destData.getEncodeData().add((byte) destData.getDriverIndex());
				}
				if (destData.supportChannel()) {
					destData.getEncodeData().add((byte) destData.getChnIndex());
				}
			}
		}

		if (destData.getResult().getCode() == Result.SUCCESS) {

			if (responseWithData) {

				destData.encode();
			}

		} else if (destData.getCode() instanceof MainCode || destData.getCode() instanceof WorkformCode
				|| destData.getCode() instanceof MBWorkformCode || destData.getCode() instanceof PCWorkformCode) {

			destData.getEncodeData().addAll(ProtocolUtil.encodeString(info, "utf-8", INFO_NUMBERS));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;

		destData.setResult(Result.valueOf(destData.getCode(), encodeData.get(index++)));

		if (destData.getCode() instanceof MainCode) {

			if (destData.supportUnit()) {
				destData.setUnitIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
			}
		} else if (destData.getCode() instanceof WorkformCode || destData.getCode() instanceof MBWorkformCode
				|| destData.getCode() instanceof PCWorkformCode) {

			int flagCount = 0;
			if (destData.supportUnit()) {
				flagCount++;
			}
			if (destData.supportDriver()) {
				flagCount++;
			}
			if (destData.supportChannel()) {
				flagCount++;
			}

			if ((destData.getResult().getCode() == Result.SUCCESS && encodeData.size() == flagCount + index)
					|| destData.getResult().getCode() != Result.SUCCESS) {

				if (destData.supportUnit()) {
					destData.setUnitIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
				}
				if (destData.supportDriver()) {
					destData.setDriverIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
				}
				if (destData.supportChannel()) {

					destData.setChnIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
				}
			}
		}

		if (destData.getResult().getCode() == Result.SUCCESS) {

			if (encodeData.size() > index) {
				responseWithData = true;

				destData.decode(encodeData.subList(index, encodeData.size()));
			}
		} else if (destData.getCode() instanceof MainCode || destData.getCode() instanceof WorkformCode
				|| destData.getCode() instanceof MBWorkformCode || destData.getCode() instanceof PCWorkformCode) {

			info = ProtocolUtil.decodeString(encodeData, index, INFO_NUMBERS, "utf-8");
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
			if (rd.getCode() != MainCode.DeviceStateCode && rd.getCode() != MainCode.PickupCode) {

				orderCompare = -1; // 降低优先级
			}
			if (this.getCode() != MainCode.DeviceStateCode && this.getCode() != MainCode.PickupCode) {

				order = -1; // 提高优先级
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

		StringBuffer sb = new StringBuffer("ResponseDecorator [");
		if (destData.supportUnit()) {

			sb.append("unitIndex=" + destData.getUnitIndex() + ", ");
		}
		if (destData.supportDriver()) {

			sb.append("driverIndex=" + destData.getDriverIndex() + ", ");
		}
		if (destData.supportChannel()) {

			sb.append("chnIndex=" + destData.getChnIndex() + ", ");
		}

		if (responseWithData) {
			sb.append("destData=" + destData.toString() + "]");
		} else {
			sb.append("destData=" + destData.getClass().getSimpleName() + "[], ");

			if (destData.result.getCode() == Result.SUCCESS) {
				sb.append("result=" + destData.result + "]");

			} else {
				sb.append("result=" + destData.result + ", info=" + info + "]");
			}
		}

		return sb.toString();

	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
