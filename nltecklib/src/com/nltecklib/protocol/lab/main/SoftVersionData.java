package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年1月7日 上午10:43:58 类说明
 */
public class SoftVersionData extends Data implements Queryable, Responsable {

	private String coreVersion = "";
	private String coreProduct = "";
	private Date coreDate; // 更新日期
	private String backupVersion = "";
	private String backupProduct = "";
	private Date backupDate; // 备份芯片更新日期
	
	private List<PickupChipInfo> chipInfos = new ArrayList<PickupChipInfo>();

	public static class PickupChipInfo {

		public String version = "";
		public String product = "";
		public Date date; // 更新日期
	}

	@Override
	public boolean supportMain() {
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

		Calendar cal = Calendar.getInstance();

		// 主控软件版本号
		data.add((byte) coreVersion.length());
		data.addAll(ProtocolUtil.convertArrayToList(coreVersion.getBytes()));
		// 产品型号
		data.add((byte) coreProduct.length());
		data.addAll(ProtocolUtil.convertArrayToList(coreProduct.getBytes()));
		// 主控更新日期
		if (coreDate == null) {

			data.add((byte) 0);
			data.add((byte) 0);
			data.add((byte) 0);
		} else {

			cal.setTime(coreDate);
			data.add((byte) (cal.get(Calendar.YEAR) - 2000));
			data.add((byte) (cal.get(Calendar.MONTH) + 1));
			data.add((byte) cal.get(Calendar.DATE));

		}
		// 备份板
		// 备份软件版本号
		data.add((byte) backupVersion.length());
		data.addAll(ProtocolUtil.convertArrayToList(backupVersion.getBytes()));
		// 产品型号
		data.add((byte) backupProduct.length());
		data.addAll(ProtocolUtil.convertArrayToList(backupProduct.getBytes()));
		// 备份更新日期
		if (backupDate == null) {

			data.add((byte) 0);
			data.add((byte) 0);
			data.add((byte) 0);
		} else {

			cal.setTime(backupDate);
			data.add((byte) (cal.get(Calendar.YEAR) - 2000));
			data.add((byte) (cal.get(Calendar.MONTH) + 1));
			data.add((byte) cal.get(Calendar.DATE));

		}
		data.add((byte) chipInfos.size());
		for(int n = 0 ; n < chipInfos.size() ; n++) {
			
			data.add((byte) chipInfos.get(n).version.length());
			data.addAll(ProtocolUtil.convertArrayToList(chipInfos.get(n).version.getBytes()));
			
			data.add((byte) chipInfos.get(n).product.length());
			data.addAll(ProtocolUtil.convertArrayToList(chipInfos.get(n).product.getBytes()));
			
			if (chipInfos.get(n).date == null) {

				data.add((byte) 0);
				data.add((byte) 0);
				data.add((byte) 0);
			} else {

				cal.setTime(chipInfos.get(n).date);
				data.add((byte) (cal.get(Calendar.YEAR) - 2000));
				data.add((byte) (cal.get(Calendar.MONTH) + 1));
				data.add((byte) cal.get(Calendar.DATE));

			}
		}
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		Calendar cal = Calendar.getInstance();
		data = encodeData;
		int index = 0;
		int len = ProtocolUtil.getUnsignedByte(data.get(index++));
		coreVersion = new String(ProtocolUtil.convertListToArray(data.subList(index, index+len)));
		index += len;
		len = ProtocolUtil.getUnsignedByte(data.get(index++));
		coreProduct = new String(ProtocolUtil.convertListToArray(data.subList(index, index+len)));
		index += len;
		//更新日期
		int year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
		int month = ProtocolUtil.getUnsignedByte(data.get(index++));
		int day = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(year != 2000 && month != 0 && day != 0) {
			
			cal.set(year, month - 1, day
			, 0, 0, 0);
			coreDate = cal.getTime();
		}
		
		//备份芯片
		len = ProtocolUtil.getUnsignedByte(data.get(index++));
		backupVersion = new String(ProtocolUtil.convertListToArray(data.subList(index, index+len)));
		index += len;
		len = ProtocolUtil.getUnsignedByte(data.get(index++));
		backupProduct = new String(ProtocolUtil.convertListToArray(data.subList(index, index+len)));
		index += len;
		year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
		month = ProtocolUtil.getUnsignedByte(data.get(index++));
		day = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(year != 2000 && month != 0 && day != 0) {
			
			cal.set(year, month - 1, day
			, 0, 0, 0);
			backupDate = cal.getTime();
		}
		
		len = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < len ; n++) {
			
			PickupChipInfo ci = new PickupChipInfo();
			
			int tmpLen  = ProtocolUtil.getUnsignedByte(data.get(index++));
			ci.version = new String(ProtocolUtil.convertListToArray(data.subList(index, index+tmpLen)));
			index += tmpLen;
			tmpLen = ProtocolUtil.getUnsignedByte(data.get(index++));
			ci.product = new String(ProtocolUtil.convertListToArray(data.subList(index, index+tmpLen)));
			index += tmpLen;
			
			year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
			month = ProtocolUtil.getUnsignedByte(data.get(index++));
			day = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(year != 2000 && month != 0 && day != 0) {
				
				cal.set(year, month - 1, day
				, 0, 0, 0);
				ci.date = cal.getTime();
			}
			
			chipInfos.add(ci);
			
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.SoftVersionCode;
	}

	public String getCoreVersion() {
		return coreVersion;
	}

	public void setCoreVersion(String coreVersion) {
		this.coreVersion = coreVersion;
	}

	public String getCoreProduct() {
		return coreProduct;
	}

	public void setCoreProduct(String coreProduct) {
		this.coreProduct = coreProduct;
	}

	public Date getCoreDate() {
		return coreDate;
	}

	public void setCoreDate(Date coreDate) {
		this.coreDate = coreDate;
	}

	public String getBackupVersion() {
		return backupVersion;
	}

	public void setBackupVersion(String backupVersion) {
		this.backupVersion = backupVersion;
	}

	public String getBackupProduct() {
		return backupProduct;
	}

	public void setBackupProduct(String backupProduct) {
		this.backupProduct = backupProduct;
	}

	public Date getBackupDate() {
		return backupDate;
	}

	public void setBackupDate(Date backupDate) {
		this.backupDate = backupDate;
	}

	public List<PickupChipInfo> getChipInfos() {
		return chipInfos;
	}

	public void setChipInfos(List<PickupChipInfo> chipInfos) {
		this.chipInfos = chipInfos;
	}

	@Override
	public String toString() {
		return "SoftVersionData [coreVersion=" + coreVersion + ", coreProduct=" + coreProduct + ", coreDate=" + coreDate
				+ ", backupVersion=" + backupVersion + ", backupProduct=" + backupProduct + ", backupDate=" + backupDate
				+ ", chipInfos=" + chipInfos + "]";
	}
	
	

}
