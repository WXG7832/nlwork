package com.nltecklib.protocol.plc.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.plc.PlcData;
import com.nltecklib.protocol.plc.pief44.model.Alert;

/**
 * 夹具压力报警值地址送3000开始
 * @author Administrator
 *
 */
public class PIEF44PresAlertData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 3000;
	private static final int DEFAULT_AG_ADDRESS = 3500;
	private static final String DEFAULT_AREA = "DM";
	private List<Alert> alerts = new ArrayList<Alert>();
	private boolean isIC;
	public boolean isIC() {
		return isIC;
	}

	public void setIC(boolean isIC) {
		this.isIC = isIC;
		if (isIC) {
			address = DEFAULT_IC_ADDRESS;
		}else {
			address = DEFAULT_AG_ADDRESS;
		}
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex * 6) + "." + dataLength * 6;//内存地址
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public PIEF44PresAlertData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		dataLength = 4;	// 默认读写数据长度
	}
	
	public PIEF44PresAlertData(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
	}
	
	@Override
	public byte[] writeDataDecode() {
		byte[] data = null;
		if(!isRead && !isBit){
			data = new byte[dataLength * 12];
			for (int i = 0; i < alerts.size(); i++) {
				int value = alerts.get(i).getValue();
				byte[] byteArray = intToByteArray(value);
				data[i*12] = byteArray[1];
				data[i*12+1] = byteArray[0];
				data[i*12 + 2] = byteArray[3];
				data[i*12+3] = byteArray[2];
				int max= alerts.get(i).getMax();
				byte[] maxbyteArray = intToByteArray(max);
				data[i*12 + 4] = maxbyteArray[1];
				data[i*12 + 5] = maxbyteArray[0];
				data[i*12 + 6] = maxbyteArray[3];
				data[i*12+7] = maxbyteArray[2];
				int min = alerts.get(i).getMin();
				byte[] minbyteArray = intToByteArray(min);
				data[i*12 + 8] = minbyteArray[1];
				data[i*12 + 9] = minbyteArray[0];	
				data[i*12 + 10] = minbyteArray[3];
				data[i*12 + 11] = minbyteArray[2];
			}
		}
		return data;
	}

	@Override
	public void decode(byte[] data) {
		for (int i = 0; i < data.length; i += 12) {
			Alert alert = new Alert();
			int value = ((data[i] & 0x0ff) << 8) + (data[i + 1] & 0x0ff) + 
					((data[i + 2] & 0x0ff) << 24) + ((data[i + 3] & 0x0ff) << 16);
			alert.setValue(value);
			int max = ((data[i + 4] & 0x0ff) << 8) + (data[i + 5] & 0x0ff) + 
					((data[i + 6] & 0x0ff) << 24) + ((data[i + 7] & 0x0ff) << 16);
			alert.setMax(max);
			int min = ((data[i + 8] & 0x0ff) << 8) + (data[i + 9] & 0x0ff) + 
					((data[i + 10] & 0x0ff) << 24) + ((data[i + 11] & 0x0ff) << 16);
			alert.setMin(min);
			alerts.add(alert);
		}
	}
	
}
