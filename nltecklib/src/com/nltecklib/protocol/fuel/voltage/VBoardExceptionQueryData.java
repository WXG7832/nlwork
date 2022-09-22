package com.nltecklib.protocol.fuel.voltage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.AdChipState;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.ChnVolStatus;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.KbState;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VBoardWorkMode;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电压采集板错误查询-0x07仅支持查询
 * 
 * @author caichao_tang
 *
 */
public class VBoardExceptionQueryData extends Data implements BoardNoSupportable, Responsable, Queryable {
	private int chnNum;
	private int usedChnNum;
	private VBoardWorkMode workMode = VBoardWorkMode.AWAIT;
	private ArrayList<KbState> chnKbValueStateList = new ArrayList<KbState>();
	private AdChipState adChipState = AdChipState.NOMAL;
	private boolean isRelayOn;// 0关，1开
	private ArrayList<ChnStatus> ChnStatusList = new ArrayList<ChnStatus>();

	public int getChnNum() {
		return chnNum;
	}

	public void setChnNum(int chnNum) {
		this.chnNum = chnNum;
	}

	public int getUsedChnNum() {
		return usedChnNum;
	}

	public void setUsedChnNum(int usedChnNum) {
		this.usedChnNum = usedChnNum;
	}

	public VBoardWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(VBoardWorkMode workMode) {
		this.workMode = workMode;
	}

	public ArrayList<KbState> getChnKbValueStateList() {
		return chnKbValueStateList;
	}

	public void setChnKbValueStateList(ArrayList<KbState> chnKbValueStateList) {
		this.chnKbValueStateList = chnKbValueStateList;
	}

	public AdChipState getAdChipState() {
		return adChipState;
	}

	public void setAdChipState(AdChipState adChipState) {
		this.adChipState = adChipState;
	}

	public boolean isRelayOn() {
		return isRelayOn;
	}

	public void setRelayOn(boolean isRelayOn) {
		this.isRelayOn = isRelayOn;
	}

	public ArrayList<ChnStatus> getChnStatusList() {
		return ChnStatusList;
	}

	public void setChnStatusList(ArrayList<ChnStatus> chnStatusList) {
		ChnStatusList = chnStatusList;
	}

	@Override
	public void encode() {
		data.add((byte) chnNum);
		data.add((byte) usedChnNum);
		data.add((byte) workMode.ordinal());
		for (KbState kbState : chnKbValueStateList) {
			data.add((byte) kbState.ordinal());
		}
		data.add((byte) adChipState.ordinal());
		data.add((byte) (isRelayOn ? 1 : 0));
		for (ChnStatus chnStatus : ChnStatusList) {
			data.add((byte) chnStatus.getChnVolStatus().ordinal());
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnStatus.getChnExceptionVol() * 100), 4, true)));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		chnNum = data.get(index++);
		usedChnNum = data.get(index++);
		workMode = VBoardWorkMode.values()[data.get(index++)];

		chnKbValueStateList.clear();
		for (int i = 0; i < chnNum; i++) {
			chnKbValueStateList.add(KbState.values()[data.get(index++)]);
		}
		// ad芯片状态
		adChipState = data.get(index++) == 0 ? AdChipState.NOMAL : AdChipState.BREAK;
		// 继电器状态
		isRelayOn = data.get(index++) == 0 ? false : true;
		// 通道状态
		for (int i = 0; i < chnNum; i++) {
			ChnStatus chnStatus = new ChnStatus();
			chnStatus.setChnVolStatus(ChnVolStatus.values()[data.get(index++)]);
			chnStatus.setChnExceptionVol(
					ProtocolUtil.composeSpecialMinus(data.subList(index, index += 4).toArray(new Byte[0]), true)
							/ 100.0);
			ChnStatusList.add(chnStatus);
		}

	}

	@Override
	public Code getCode() {
		return VolCode.EXCEPTION_QUERY;
	}

	@Override
	public String toString() {
		return "VBoardExceptionQueryData [chnNum=" + chnNum + ", usedChnNum=" + usedChnNum + ", workMode=" + workMode
				+ ", chnKbValueStateList=" + chnKbValueStateList + ", adChipState=" + adChipState + ", isRelayOn="
				+ isRelayOn + ", ChnStatusList=" + ChnStatusList + "]";
	}

}
