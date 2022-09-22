package com.nltecklib.protocol.li.driver;

import java.util.Arrays;
import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * ◊‘ºÏπ ’œ≤È—Ø
 * 
 * @author admin
 */
public class DriverSelfCheck2Data extends Data implements Queryable, Responsable {

	private static final int UUID_LENGTH = 16;
	private boolean flashOK;// FlashºÏ≤‚ 0:’˝≥£ 1£∫π ’œ
	private short logicCalDataFlag;
	private short checkCalDataFlag;
	private String logicUuid;
	private String checkUuid;

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex);
		data.add((byte) (flashOK ? 0 : 1));
		data.addAll(Arrays.asList(ProtocolUtil.split(logicCalDataFlag, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(checkCalDataFlag, 2, true)));
		if (logicUuid.length() != UUID_LENGTH * 2) {

			throw new RuntimeException("error uuid :" + logicUuid);
		}
		data.addAll(ProtocolUtil.encodeUuid(logicUuid));

		if (checkUuid.length() != UUID_LENGTH * 2) {

			throw new RuntimeException("error uuid :" + checkUuid);
		}
		data.addAll(ProtocolUtil.encodeUuid(checkUuid));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;

		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		flashOK = data.get(index++) == 0;
		logicCalDataFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		checkCalDataFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		logicUuid = ProtocolUtil.decodeUuid(data.subList(index, index + UUID_LENGTH));
		index += UUID_LENGTH;
		checkUuid = ProtocolUtil.decodeUuid(data.subList(index, index + UUID_LENGTH));
		index += UUID_LENGTH;
	}

	public boolean isFlashOK() {
		return flashOK;
	}

	public void setFlashOK(boolean flashOK) {
		this.flashOK = flashOK;
	}

	public short getLogicCalDataFlag() {
		return logicCalDataFlag;
	}

	public void setLogicCalDataFlag(short logicCalDataFlag) {
		this.logicCalDataFlag = logicCalDataFlag;
	}

	public short getCheckCalDataFlag() {
		return checkCalDataFlag;
	}

	public void setCheckCalDataFlag(short checkCalDataFlag) {
		this.checkCalDataFlag = checkCalDataFlag;
	}

	public String getLogicUuid() {
		return logicUuid;
	}

	public void setLogicUuid(String logicUuid) {
		this.logicUuid = logicUuid;
	}

	public String getCheckUuid() {
		return checkUuid;
	}

	public void setCheckUuid(String checkUuid) {
		this.checkUuid = checkUuid;
	}

	@Override
	public Code getCode() {
		return DriverCode.SelfCheck2Code;
	}

	@Override
	public String toString() {
		return "DriverSelfCheck2Data [flashOK=" + flashOK + ", logicCalDataFlag=" + logicCalDataFlag
				+ ", checkCalDataFlag=" + checkCalDataFlag + ", logicUuid=" + logicUuid + ", checkUuid=" + checkUuid
				+ "]";
	}

}
