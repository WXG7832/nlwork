package com.nltecklib.protocol.power;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.Environment.Orient;
import com.nltecklib.protocol.util.ProtocolUtil;


public class QueryDecorator implements Decorator, Comparable {

	private Data destData;

	//附加查询条件
	private byte[] params;
	
	public QueryDecorator(Data destData) {

		if (!(destData instanceof Queryable)) {

			throw new RuntimeException("该协议工(功能码:" + destData.getCode() + ")不适用于查询命令");
		}

		this.destData = destData;
		destData.setOrient(Orient.QUERY);
	}

	/**
	 * 支持多个查询条件
	 * @param destData
	 * @param params
	 */
	public QueryDecorator(Data destData ,byte ...params) {

		this(destData);
		
		this.params = params;
	}
	
	@Override
	public void encode() {

		this.destData.clear();
		
		if (destData.supportDriver()) {

			destData.getEncodeData().add((byte) destData.getDriverIndex());
		}
		
		if (destData.supportChannel()) {

			destData.getEncodeData().add((byte) destData.getChnIndex());
		}
		
		if(params != null && params.length > 0) {
			
			for(Byte param : params) {
				destData.getEncodeData().add(param);
			}
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		destData.clear();
		destData.getEncodeData().addAll(encodeData);
		int index = 0;
		if (destData.supportDriver()) {

			destData.setDriverIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}
		
		if (destData.supportChannel()) {

			destData.setChnIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}
		
		if(params != null && params.length > 0) {
			
			int paramSize = params.length;

			for(int n = 0; n < paramSize ;n++) {
				params[n] = (byte) ProtocolUtil.getUnsignedByte(encodeData.get(index++));
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
		// 优先级越高返回-1 ， 越低返回1

		int order = 0, orderCompare = 0;
		if (obj instanceof QueryDecorator) {

			QueryDecorator qd = (QueryDecorator) obj;

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

		StringBuffer sb = new StringBuffer("QueryDecorator[");
		if(destData.supportDriver()) {
		    sb.append("driverIndex = " + destData.getDriverIndex() + ",");
		}
		if (destData.supportChannel()) {

			sb.append("driverIndex = " + destData.getChnIndex() + ",");
		}
		if (sb.charAt(sb.length() - 1) == ',') {

			sb = new StringBuffer(sb.substring(0, sb.length() - 1) + "]");
		}
		return sb.toString();
	}

}
