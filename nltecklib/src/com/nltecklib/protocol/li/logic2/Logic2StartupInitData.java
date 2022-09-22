package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 逻辑板通道启动前的初始化!
 * @author wavy_zheng
 * 2020年11月21日
 *
 */
public class Logic2StartupInitData extends Data  implements Configable,Queryable,Responsable{

	private List<ChnInitData> chnInitDatas = new ArrayList<>();

	public static class ChnInitData {
       
		public int index;
		public double totalCapacity;// 累计通道容量
		public long stepTime;// 步次流逝时间ms
		public int loopIndex;
		public int stepIndex;
		@Override
		public String toString() {
			return "ChnInitData [index=" + index + ", totalCapacity=" + totalCapacity + ", stepTime=" + stepTime + ", loopIndex="
					+ loopIndex + ", stepIndex=" + stepIndex + "]";
		}

	}

	

	public List<ChnInitData> getChnInitDatas() {
		return chnInitDatas;
	}

	public void setChnInitDatas(List<ChnInitData> chnInitDatas) {
		this.chnInitDatas = chnInitDatas;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) unitIndex);
		data.add((byte) chnInitDatas.size());
		for (ChnInitData temp : chnInitDatas) {
			data.add((byte) (isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(temp.index, Data.getDriverChnCount()) : temp.index));
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (temp.totalCapacity * Math.pow(10, Data.getCapacityResolution())), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) temp.stepTime, 4, true)));
			data.add((byte) temp.loopIndex);
			data.add((byte) temp.stepIndex);
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			ChnInitData temp = new ChnInitData();
			int val = ProtocolUtil.getUnsignedByte(data.get(index++));
			temp.index =  isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(val, Data.getDriverChnCount()) : val;
			temp.totalCapacity = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, Data.getCapacityResolution());
			index += 4;
			temp.stepTime = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			temp.loopIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			temp.stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			chnInitDatas.add(temp);
		}
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.OffLineRecoveryCode;
	}

	@Override
	public String toString() {
		return "Logic2OffLineRecoveryData [unitIndex=" + unitIndex + ", driverIndex=" + driverIndex
				+ ", offLineChnDatas=" + chnInitDatas + "]";
	}



}
