package com.nltecklib.protocol.fuel.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.process.fuel.ProcessEnvironment.ProcessType;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控板程序协议数据——0x14
 * 
 * @author caichao_tang
 *
 */
public class ProcessData extends Data implements BoardNoSupportable, Configable, Responsable, Queryable {

	private static final int NAME_BYTES = 50; // 固定50个字节
	// public final int SINGLE_PAG_MAX_BYTES=1024;

	private String name;
	private int totalPagCount;
	private int currentIndex;// 从1开始

	private List<Byte> processCode = new ArrayList<>();

	@Override
	public void encode() {

		byte[] array = null;
		try {
			array = name.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < NAME_BYTES; i++) {

			if (i < array.length) {

				data.add(array[i]);
			} else {

				data.add((byte) 0);
			}
		}
		data.addAll(Arrays.asList(ProtocolUtil.split(totalPagCount, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(currentIndex, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(processCode.size(), 2, true)));
		data.addAll(processCode);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
//		processType = ProcessType.values()[ProtocolUtil.getUnsignedByte(data.get(index++))];
		byte[] nameBytes = new byte[NAME_BYTES];
		int strLen = 0;
		for (int i = 0; i < NAME_BYTES; i++) {

			if (data.get(i) == 0) {

				break;
			}
			nameBytes[i] = data.get(i);
			strLen++;
		}
		try {

			name = new String(Arrays.copyOfRange(nameBytes, 0, strLen), "utf-8");

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("decode process name error :" + e.getMessage());
		}
		index += NAME_BYTES;
		totalPagCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		currentIndex = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		int count = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		processCode = data.subList(index, index + count);
	}

	@Override
	public Code getCode() {
		return MainCode.PROCESS_CODE;
	}

	public int getTotalPagCount() {
		return totalPagCount;
	}

	public void setTotalPagCount(int totalPagCount) {
		this.totalPagCount = totalPagCount;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public List<Byte> getProcessCode() {
		return processCode;
	}

	public void setProcessCode(List<Byte> processCode) {
		this.processCode = processCode;
	}

	public ProcessType getProcessType() {
		return ProcessType.values()[boardNum];
	}

	public void setProcessType(ProcessType processType) {
		this.boardNum = processType.ordinal();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ProcessData [name=" + name + ", totalPagCount=" + totalPagCount + ", currentIndex=" + currentIndex
				+ ", processCode=" + processCode + "]";
	}

}
