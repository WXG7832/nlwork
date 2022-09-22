package com.nltecklib.protocol.li.PCWorkform;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月30日 下午1:51:37 日志推送协议,注意此为生产日志，每条日志最大字节数50
 */
public class LogPushData extends Data implements Alertable, Responsable {

	public static final int LOG_BYTE_LEN = 255;
	private List<PushLog> logs = new ArrayList<>();

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

//	@Override
//	public void encode() {
//
//		data.add((byte) unitIndex);
//
//		Gson gson = new Gson();
//		String content = gson.toJson(logs);
//		try {
//			byte[] bytes = content.getBytes("utf-8");
//			data.addAll(ProtocolUtil.convertArrayToList(bytes));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			throw new RuntimeException("convert content to bytes error:" + e.getMessage());
//		}
//
//	}
//
//	@Override
//	public void decode(List<Byte> encodeData) {
//
//		data = encodeData;
//		int index = 0;
//
//		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
//
//		Gson gson = new Gson();
//		// 转成字符串
//		String content;
//		try {
//			content = new String(ProtocolUtil.convertListToArray(data.subList(index, data.size())), "utf-8");
//		} catch (UnsupportedEncodingException e) {
//
//			e.printStackTrace();
//			throw new RuntimeException("convert byte to json string error:" + e.getMessage());
//		}
//		logs = gson.fromJson(content, logs.getClass());
//
//	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) logs.size());
		for (int n = 0; n < logs.size(); n++) {

			PushLog log = logs.get(n);
			data.add((byte) log.chnIndexInLogic);
			data.add((byte) (log.error ? 1 : 0));
			data.addAll(ProtocolUtil.encodeString(log.log, "utf-8", LOG_BYTE_LEN));
			data.addAll(ProtocolUtil.encodeDate(log.date, true));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			PushLog log = new PushLog();
			int chnIndexInLogic = ProtocolUtil.getUnsignedByte(data.get(index++));

			log.chnIndexInLogic = chnIndexInLogic;
			log.error = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
			log.log = ProtocolUtil.decodeString(data, index, LOG_BYTE_LEN, "UTF-8");
			index += LOG_BYTE_LEN;
			log.date = ProtocolUtil.decodeDate(data.subList(index, index + 6), true);
			index += 6;
			appendLog(log);

		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.LogPushData;
	}

	public List<PushLog> getLogs() {
		return logs;
	}

	public void appendLog(PushLog log) {

		logs.add(log);
	}

	public void setLogs(List<PushLog> logs) {
		this.logs = logs;
	}

	@Override
	public String toString() {
		return "LogPushData [logs=" + logs + "]";
	}
	
	

}
