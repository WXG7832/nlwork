package com.nltecklib.protocol.li;

import java.util.List;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.Environment.Orient;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
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
		if (destData.supportUnit()) {
			destData.getEncodeData().add((byte) destData.getUnitIndex());
		}
		if (destData.supportDriver()) {
			destData.getEncodeData().add((byte) destData.getDriverIndex());

		}
		if (destData.supportChannel()) {

			int chnIndex = destData.getChnIndex();
			if((getCode() instanceof Logic2Code || getCode() instanceof Check2Code) 
					&& Data.isReverseDriverChnIndex()) {
				
				if(getCode() != Logic2Code.UUIDCode  && getCode() != Check2Code.UUID_CODE 
						) {
				
				    chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
				}
				
			}
			destData.getEncodeData().add((byte) chnIndex);
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
		if (destData.supportUnit()) {

			destData.setUnitIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}
		if (destData.supportDriver()) {

			destData.setDriverIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}
		if (destData.supportChannel()) {
            
			int chnIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
			if((getCode() instanceof Logic2Code || getCode() instanceof Check2Code) 
					&& Data.isReverseDriverChnIndex()) {
				
				chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
			}
			
			destData.setChnIndex(chnIndex);
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

			if (qd.getCode() != MainCode.DeviceStateCode && qd.getCode() != MainCode.PickupCode) {

				orderCompare = -1;
			}
			if (this.getCode() != MainCode.DeviceStateCode && this.getCode() != MainCode.PickupCode) {

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

		StringBuffer sb = new StringBuffer("QueryDecorator[");
		if (destData.supportUnit()) {

			sb.append("unitIndex=" + destData.getUnitIndex() + ", ");
		}
		if (destData.supportDriver()) {

			sb.append("driverIndex=" + destData.getDriverIndex() + ", ");
		}
		if (destData.supportChannel()) {

			sb.append("chnIndex=" + destData.getChnIndex() + ", ");
		}
		
		sb.append("destData=" + destData.getClass().getSimpleName()+"[]");
		
		sb.append("]");
		
		return sb.toString();
	}

	

}
