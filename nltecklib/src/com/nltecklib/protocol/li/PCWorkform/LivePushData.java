package com.nltecklib.protocol.li.PCWorkform;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月30日 下午1:14:00 类说明
 */
public class LivePushData extends Data implements Alertable, Responsable {

	private List<PushData> pushDatas = new ArrayList<>();

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

		data.addAll(Arrays.asList(ProtocolUtil.split(pushDatas.size(), 2, true)));
		for (int n = 0; n < pushDatas.size(); n++) {

			PushData pd = pushDatas.get(n);
			data.add((byte) pd.unitIndex);
			data.add((byte) pd.chnIndex); // 注意PC与主控之间不会反序
			data.add((byte) pd.calState.ordinal());
			data.add((byte) (pd.matched ? 1 : 0));
			data.add((byte) pd.matchBoardIndex);
			data.add((byte) pd.matchChnIndex);
//			data.add((byte) pd.calMode.ordinal());
//			data.add((byte) pd.pole.ordinal());
//			data.add((byte) pd.precision);
//			data.addAll(Arrays.asList(ProtocolUtil.split((long) (pd.currentDotVal * 100), 3, true)));
//			data.addAll(Arrays.asList(ProtocolUtil.split((long) (pd.currentAdc * 100), 3, true)));
//			data.addAll(Arrays.asList(ProtocolUtil.split((long) (pd.currentMeter * 100), 3, true)));
//			data.add((byte) pd.pos);
//			data.add((byte) pd.range);
//			data.addAll(Arrays.asList(ProtocolUtil.split((long) pd.seconds, 2, true)));
			// 编码时间
			data.addAll(ProtocolUtil.encodeDate(pd.date, true));

		}
		
//		Gson gson = new Gson();
//		String content = gson.toJson(pushDatas);
//		try {
//			byte[] bytes = content.getBytes("utf-8");
//			data.addAll(ProtocolUtil.convertArrayToList(bytes));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			throw new RuntimeException("convert content to bytes error:" + e.getMessage());
//		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		int count =(int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index+=2;
		for (int n = 0; n < count; n++) {

			PushData pd = new PushData();

			pd.unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			pd.chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalState.values().length - 1) {

				throw new RuntimeException("invalid cal state code:" + code);
			}
			pd.calState = CalState.values()[code];

			pd.matched = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
			
			pd.matchBoardIndex= ProtocolUtil.getUnsignedByte(data.get(index++));
			
			pd.matchChnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			
//			code = ProtocolUtil.getUnsignedByte(data.get(index++));
//			if (code > CalMode.values().length - 1) {
//
//				throw new RuntimeException("invalid cal mode code:" + code);
//			}
//			pd.calMode = CalMode.values()[code];
//			
//			code = ProtocolUtil.getUnsignedByte(data.get(index++));
//			if (code > Pole.values().length - 1) {
//
//				throw new RuntimeException("invalid pole code:" + code);
//			}
//			pd.pole = Pole.values()[code];
//			
//			pd.precision =ProtocolUtil.getUnsignedByte(data.get(index++));
//
//			pd.currentDotVal = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
//					/ 100;
//			index += 3;
//			pd.currentAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
//					/ 100;
//			index += 3;
//			pd.currentMeter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
//					/ 100;
//			index += 3;
//			pd.pos = ProtocolUtil.getUnsignedByte(data.get(index++));
//			pd.range = ProtocolUtil.getUnsignedByte(data.get(index++));
//			pd.seconds = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
//			index += 2;
			pd.date = ProtocolUtil.decodeDate(data.subList(index, index + 6), true);
			index += 6;

			appendPushData(pd);
		}
		
//		data = encodeData;
//		int index = 0;
//		Gson gson = new Gson();
//		// 转成字符串
//		String content;
//		try {
//			content = new String(ProtocolUtil.convertListToArray(data.subList(index, data.i)), "utf-8");
//		} catch (UnsupportedEncodingException e) {
//
//			e.printStackTrace();
//			throw new RuntimeException("convert byte to json string error:" + e.getMessage());
//		}
//		pushDatas = gson.fromJson(content, List.class);
	}
	
	

	public void setPushDatas(List<PushData> pushDatas) {
		this.pushDatas = pushDatas;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.DataPushCode;
	}

	public List<PushData> getPushDatas() {
		return pushDatas;
	}

	public void appendPushData(PushData pd) {

		pushDatas.add(pd);
	}

	@Override
	public String toString() {
		return "LivePushData [pushDatas=" + pushDatas + "]";
	}

}
