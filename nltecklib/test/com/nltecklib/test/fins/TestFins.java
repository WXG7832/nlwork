package com.nltecklib.test.fins;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.nltecklib.io.mina.NetworkException;
import com.nltecklib.io.mina.SyncNetworkConnector;
import com.nltecklib.protocol.fins.Entity;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.fins.HandshakeData;
import com.nltecklib.protocol.fins.ResponseData;
import com.nltecklib.protocol.fins.WRAllFinsData;
import com.nltecklib.protocol.plc2.pief44.PIEF44TemperatureData;
import com.nltecklib.protocol.plc2.pief44.PIEF44TestResultData;
import com.nltecklib.protocol.plc2.pief44.PIEF44WaterwheelData;
import com.nltecklib.protocol.plc2.pief44.PIEF44WritePressureData;

public class TestFins {
	SyncNetworkConnector connector;
	@Before
	public void  init() throws NetworkException {
		Entity entity = new Entity("192.168.250.2", "192.168.250.1");
		connector = new SyncNetworkConnector(entity);
		boolean re = connector.connect("192.168.250.1", 9600);
		System.out.println(connector.isConnected());
		HandshakeData data = new HandshakeData();
		ResponseData data2 = (ResponseData) connector.send(data, 2000);
		System.out.println(data2.getError());
	}
	
	@Test
	public void testValues() throws NetworkException {	
		
		PIEF44WritePressureData data2 = new PIEF44WritePressureData();
//		data2.setDatalength(4);
//		List<Integer> values = new ArrayList<Integer>();
//		values.add(65536);
//		values.add(54354);
//		values.add(5464564);
//		values.add(65465464);
//		data2.setValues(values);
//		data2.setOrient(Orient.WRITE);
//		connector.send(data2,2000);
		
		
		
		data2 = new PIEF44WritePressureData();
		data2.setIC(true);
		data2.setDatalength(4);
		ResponseData data3 = (ResponseData) connector.send(data2,2000);
		data2.decode(data3.getData());
		System.out.println(data2.getValues());
		System.out.println(data3.getResult());
	}
	
	@Test
	public void temp() throws NetworkException {
		PIEF44TemperatureData pd = new PIEF44TemperatureData();
		pd.setIC(true);
		connector.send(pd, 2000);
	}
	
	@Test
	public  void testresult() throws NetworkException {
		PIEF44TestResultData data = new PIEF44TestResultData();
		data.setIC(true);
		data.setFixtureIndex(3);
		data.setOrient(Orient.WRITE);
		List<Integer> values = new ArrayList<Integer>();
		values.add(65536);
		values.add(4454);
		values.add(65536);
		values.add(65535);
		data.setValues(values);
		data.setDatalength(4);
		connector.send(data, 2000);
		
		data = new PIEF44TestResultData();
		data.setIC(true);
		data.setFixtureIndex(3);
		data.setDatalength(36);
		ResponseData rd = (ResponseData) connector.send(data, 2000);
		data.decode(rd.getData());
		System.out.println(data.getValues());
	}
	
	@Test
	public  void testWater() throws NetworkException {
		PIEF44WaterwheelData data = new PIEF44WaterwheelData();
		data.setIC(true);
		data.setOrient(Orient.WRITE);
		List<Integer> values = new ArrayList<Integer>();
		values.add(65536);
		values.add(4454);
		values.add(65536);
		values.add(65535);
		data.setValues(values);
		data.setDatalength(4);
		connector.send(data, 2000);
		
		data = new PIEF44WaterwheelData();
		data.setIC(true);
		data.setDatalength(36);
		ResponseData rd = (ResponseData) connector.send(data, 2000);
		data.decode(rd.getData());
		System.out.println(data.getValues());
	}
	
	@Test
	public void testWRALL() throws NetworkException {
		WRAllFinsData data = new WRAllFinsData();
		data.setOrient(Orient.WRITE);
		data.setAddress(200);
		List<Boolean> results = new ArrayList<>();
		results.add(true);
		results.add(false);
		results.add(false);
		results.add(true);
		results.add(true);
		results.add(true);
		results.add(true);
		results.add(true);
		results.add(false);
		results.add(true);
		results.add(false);
		results.add(false);
		results.add(true);
		results.add(true);
		results.add(false);
		results.add(true);
		data.setResults(results);
		connector.send(data, 2000);
		
		data = new WRAllFinsData();
		data.setAddress(200);
		ResponseData rd = (ResponseData) connector.send(data, 2000);
		data.decode(rd.getData());
		System.out.println(data.getResults());
	}
	
}
