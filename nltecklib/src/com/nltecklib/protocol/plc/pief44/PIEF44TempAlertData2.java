package com.nltecklib.protocol.plc.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.plc.PlcData;
import com.nltecklib.protocol.plc.pief44.model.Alert;

/**
 * 温度报警值地址从2600开始
 * @author Administrator
 *
 */
public class PIEF44TempAlertData2 extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 2600;
	private static final int DEFAULT_AG_ADDRESS = 2612;
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
	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex * 3) + "." + dataLength * 3;//内存地址
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public PIEF44TempAlertData2() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		dataLength = 4;	// 默认读写数据长度
	}
	
	public PIEF44TempAlertData2(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}
	@Override
	public byte[] writeDataDecode() {
		byte[] data = null;
		if(!isRead && !isBit){
			data = new byte[dataLength * 6];
			for (int i = 0; i < alerts.size(); i++) {
				int value = alerts.get(i).getValue();
				byte[] byteArray = intToByteArray(value);
				data[i*6] = byteArray[1];
				data[i*6+1] = byteArray[0];
				int max= alerts.get(i).getMax();
				byte[] maxbyteArray = intToByteArray(max);
				data[i*6 + 2] = maxbyteArray[1];
				data[i*6 + 3] = maxbyteArray[0];
				int min = alerts.get(i).getMin();
				byte[] minbyteArray = intToByteArray(min);
				data[i*6 + 4] = minbyteArray[1];
				data[i*6 + 5] = minbyteArray[0];	
			}
		}
		return data;
	}

	@Override
	public void decode(byte[] data) {
		for (int i = 0; i < data.length; i += 6) {
			Alert alert = new Alert();
			int value = ((data[i] & 0x0ff) << 8) + (data[i + 1] & 0x0ff);
			alert.setValue(value);
			int max = ((data[i + 2] & 0x0ff) << 8) + (data[i + 3] & 0x0ff);
			alert.setMax(max);
			int min = ((data[i + 4] & 0x0ff) << 8) + (data[i + 5] & 0x0ff);
			alert.setMin(min);
			alerts.add(alert);
		}
	}
}
