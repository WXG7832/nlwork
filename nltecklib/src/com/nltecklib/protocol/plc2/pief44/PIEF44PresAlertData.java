package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.plc2.PlcData;
import com.nltecklib.protocol.plc2.pief44.model.Alert;

/**
 * 夹具压力报警值地址送3000开始
 * @author Administrator
 *
 */
public class PIEF44PresAlertData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 3000;
	private static final int DEFAULT_AG_ADDRESS = 3500;
	private List<Alert> alerts = new ArrayList<Alert>();

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public void encode(){
		if (isIC) {
			address = DEFAULT_IC_ADDRESS + fixtureIndex * 6;
		}else {
			address = DEFAULT_AG_ADDRESS + fixtureIndex * 6;
		}
		for (int i = 0; i < alerts.size(); i++) {
			int value = alerts.get(i).getValue();
			byte[] byteArray = intToByteArray(value);
			data.add(byteArray[1]);
			data.add(byteArray[0]);
			data.add(byteArray[3]);
			data.add(byteArray[2]);
			int max= alerts.get(i).getMax();
			byte[] maxbyteArray = intToByteArray(max);
			data.add(maxbyteArray[1]);
			data.add(maxbyteArray[0]);
			data.add(maxbyteArray[3]);
			data.add(maxbyteArray[2]);
			int min = alerts.get(i).getMin();
			byte[] minbyteArray = intToByteArray(min);
			data.add(minbyteArray[1]);
			data.add(minbyteArray[0]);	
			data.add(minbyteArray[3]);
			data.add(minbyteArray[2]);
		}
	}
	
	public PIEF44PresAlertData() {
		super();
		area = Area.DM;		// 默认地址区域
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		alerts.clear();
		data = encodeData;
		for (int i = 0; i < data.size(); i += 12) {
			Alert alert = new Alert();
			int value = ((data.get(i) & 0x0ff) << 8) + (data.get(i + 1) & 0x0ff) + 
					((data.get(i + 2) & 0x0ff) << 24) + ((data.get(i + 3) & 0x0ff) << 16);
			alert.setValue(value);
			int max = ((data.get(i + 4) & 0x0ff) << 8) + (data.get(i + 5) & 0x0ff) + 
					((data.get(i + 6) & 0x0ff) << 24) + ((data.get(i + 7) & 0x0ff) << 16);
			alert.setMax(max);
			int min = ((data.get(i + 8) & 0x0ff) << 8) + (data.get(i + 9) & 0x0ff) + 
					((data.get(i + 10) & 0x0ff) << 24) + ((data.get(i + 11) & 0x0ff) << 16);
			alert.setMin(min);
			alerts.add(alert);
		}
	}
	
}
