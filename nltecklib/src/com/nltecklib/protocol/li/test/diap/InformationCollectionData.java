/**
 * 
 */
package com.nltecklib.protocol.li.test.diap;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 信息采集功能码0x07 支持查询
* @version: v1.0.0
* @author: Admin
* @date: 2021年11月15日 上午11:33:54 
*
*/
public class InformationCollectionData extends Data implements /*Configable,*/ Queryable, Responsable {

	private int collectNumber;//采集数量
	private long mainVolAD;//主电压AD原始值
	private long mainCurrAD;//主电流AD原始值
	private long backupVolAD;//备份电压AD原始值
	
	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) collectNumber);
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(mainVolAD, 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(mainCurrAD, 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(backupVolAD, 2, true)));

	}
	

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		collectNumber = ProtocolUtil.getUnsignedByte(data.get(index++));
		mainVolAD = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true);
		index += 4;
		mainCurrAD = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true);
		index += 4;
		backupVolAD = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
	}

	@Override
	public Code getCode() {
		return DiapTestCode.InformationCollection;
	}

	public int getCollectNumber() {
		return collectNumber;
	}

	public void setCollectNumber(int collectNumber) {
		this.collectNumber = collectNumber;
	}

	public long getMainVolAD() {
		return mainVolAD;
	}

	public void setMainVolAD(long mainVolAD) {
		this.mainVolAD = mainVolAD;
	}

	public long getMainCurrAD() {
		return mainCurrAD;
	}

	public void setMainCurrAD(long mainCurrAD) {
		this.mainCurrAD = mainCurrAD;
	}

	public long getBackupVolAD() {
		return backupVolAD;
	}

	public void setBackupVolAD(long backupVolAD) {
		this.backupVolAD = backupVolAD;
	}
	
	

}
