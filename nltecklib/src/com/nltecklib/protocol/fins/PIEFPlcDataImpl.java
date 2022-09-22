package com.nltecklib.protocol.fins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nltecklib.io.mina.NetworkException;
import com.nltecklib.io.mina.SyncNetworkConnector;
import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Error;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.fins.Environment.Result;
import com.nltecklib.protocol.fins.Environment.Type;
import com.nltecklib.protocol.plc2.pief44.PIEF44AGBatterySourceData;
import com.nltecklib.protocol.plc2.pief44.PIEF44AreaData;
import com.nltecklib.protocol.plc2.pief44.PIEF44AreaDuintData;
import com.nltecklib.protocol.plc2.pief44.PIEF44BatteryCountData;
import com.nltecklib.protocol.plc2.pief44.PIEF44CompleteryOpenAddrData;
import com.nltecklib.protocol.plc2.pief44.PIEF44CompleteryOpenPressData;
import com.nltecklib.protocol.plc2.pief44.PIEF44CurrentChannelData;
import com.nltecklib.protocol.plc2.pief44.PIEF44FifthSpeedAddrData;
import com.nltecklib.protocol.plc2.pief44.PIEF44FifthSpeedBackAddrData;
import com.nltecklib.protocol.plc2.pief44.PIEF44FirstSpeedAddrData;
import com.nltecklib.protocol.plc2.pief44.PIEF44FirstSpeedBackAddrData;
import com.nltecklib.protocol.plc2.pief44.PIEF44ICSpeedData;
import com.nltecklib.protocol.plc2.pief44.PIEF44MechanicalArmCurrentBatteryData;
import com.nltecklib.protocol.plc2.pief44.PIEF44PrePressAddrData;
import com.nltecklib.protocol.plc2.pief44.PIEF44PrePressBackAddrData;
import com.nltecklib.protocol.plc2.pief44.PIEF44PresAlertData;
import com.nltecklib.protocol.plc2.pief44.PIEF44PressCompensationData;
import com.nltecklib.protocol.plc2.pief44.PIEF44PressKBData;
import com.nltecklib.protocol.plc2.pief44.PIEF44ReadPressureData;
import com.nltecklib.protocol.plc2.pief44.PIEF44TempAlertData;
import com.nltecklib.protocol.plc2.pief44.PIEF44TemperatureData;
import com.nltecklib.protocol.plc2.pief44.PIEF44TestResultData;
import com.nltecklib.protocol.plc2.pief44.PIEF44WaterWheelBatterySourceData;
import com.nltecklib.protocol.plc2.pief44.PIEF44WaterwheelData;
import com.nltecklib.protocol.plc2.pief44.PIEF44WritePressureData;
import com.nltecklib.protocol.plc2.pief44.control.PIEF44AlertReadData;
import com.nltecklib.protocol.plc2.pief44.control.PIEF44AutoReadData;
import com.nltecklib.protocol.plc2.pief44.control.PIEF44ResetWriteData;
import com.nltecklib.protocol.plc2.pief44.control.PIEF44StartWriteData;
import com.nltecklib.protocol.plc2.pief44.control.PIEF44StopReadData;
import com.nltecklib.protocol.plc2.pief44.control.PIEF44StopWriteData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44BarcodeData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44DatabaseData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadBarcodeNgData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadBarcodeOkData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadBarcodeSignData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadClipBindingData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadClipBindingFinishData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadConfirmData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadConveyorConfirmData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadConveyorData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadSignData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadToClipSignConfirmData;
import com.nltecklib.protocol.plc2.pief44.control.feeding.PIEF44UnloadToClipSignData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44AutoStateData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44FixturePressureData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44FixtureShieldData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44FixtureStateData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44FixtureTempData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44HasMaterialData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44Move1Data;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44Move2Data;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44NgData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44ProcessRequestData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44TestStateData;
import com.nltecklib.protocol.plc2.pief44.control.machine.PIEF44WorkCompletionSignalData;
import com.nltecklib.protocol.plc2.pief44.model.Alert;
import com.nltecklib.protocol.plc2.pief44.model.PressKB;
import com.nltecklib.protocol.plc2.pief44.model.Speed;

/**
 * @ClassName: PIEFPlcDataImpl
 * @Description: PIEF数据接口的实现类
 * @author zhang_longyong
 * @date 2019年12月14日
 */
public class PIEFPlcDataImpl implements PIEFPlcData {

	private SyncNetworkConnector connector;
	private int timeout;

	// private boolean signalBatteryIntoCutBelt;
	// private boolean signalCutIntoClip;
	// private boolean signalCutBarcodeScan;
	// private boolean signalCutClipBinding;
	// private int lastFeedWaterWheelFull;
	// private int currentBatteryPairIndex;
	//
	// @Override
	// public void startRunCircle() {
	// Thread thread = new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// runCircle();
	// }
	// });
	// thread.setDaemon(true);
	// thread.start();
	// }
	//
	// private void runCircle() {
	//
	// System.out.println("PLC轮询开启");
	//
	// while (true) {
	// try {
	//
	// // 轮询机械手当前电芯对数
	// int index = readMachinicalArmCurrent();
	//
	// if (currentBatteryPairIndex != index) {
	// currentBatteryPairIndex = index;
	// if (listener != null) {
	// listener.cutBatteryPairIndex(index);
	// }
	// }
	// // 下料信号
	// if (readUnloadSign()) {
	// writeUnloadConfirm(true);
	// }
	// // 电芯进入拉带
	// boolean nowBatteryIntoCutBelt = readUnloadConveyorSign();
	// if (nowBatteryIntoCutBelt && !signalBatteryIntoCutBelt) {
	// if (listener != null) {
	// listener.batteryIntoBelt();
	// }
	// }
	// signalBatteryIntoCutBelt = nowBatteryIntoCutBelt;
	// //电芯进入弹夹
	// boolean nowCutInfo = readUnloadToClipSign();
	// if (nowCutInfo && !signalCutIntoClip) {
	// if (listener != null) {
	// listener.batterIntoClip();
	// }
	// }
	// signalCutIntoClip = nowCutInfo;
	//
	// //下料弹夹扫码
	// boolean nowCutBar = readUnloadBarcodeSign();
	// if (nowCutBar && !signalCutBarcodeScan) {
	// if (listener != null) {
	// listener.cutClipScan();
	// }
	// }
	// signalCutBarcodeScan = nowCutBar;
	//
	// //下料弹夹电芯绑定
	// boolean nowCut = readUnloadClipBinding();
	// if (nowCut && !signalCutClipBinding) {
	// if (listener != null) {
	// listener.cutClipBind();
	// }
	// }
	// signalCutClipBinding = nowCut;
	//
	// //上料水车已满
	// int nowFeed = readBatterycount();
	// if (nowFeed > lastFeedWaterWheelFull) {
	// if (listener != null) {
	// listener.feedWaterWheelFull();
	// }
	// }
	// lastFeedWaterWheelFull = nowFeed;
	//
	// readFixtureState(true, 1);
	//
	// Thread.sleep(200);
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// try {
	// Thread.sleep(2000);
	// } catch (InterruptedException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// }
	// }
	// }

	public PIEFPlcDataImpl(SyncNetworkConnector connector, int timeout) {
		super();
		this.connector = connector;
		this.timeout = timeout;
	}

	public SyncNetworkConnector getConnector() {
		return connector;
	}

	public void setConnector(SyncNetworkConnector connector) {
		this.connector = connector;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public Error handShake() throws NetworkException {
		System.out.println(timeout);
		Error error = ((ResponseData) connector.send(new HandshakeData(), timeout)).getError();
		return error;
	}

	@Override
	public Map<Integer, Integer> readTemperature(int fixtureIndex) throws NetworkException {
		PIEF44TemperatureData data = new PIEF44TemperatureData();
		data.setFixtureIndex(fixtureIndex%4);
		data.setIC(fixtureIndex<4);
		data.setDatalength(37);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getTemps();
		}
		return null;
	}

	@Override
	public List<Integer> readPressure(boolean isIC) throws NetworkException {
		PIEF44ReadPressureData data = new PIEF44ReadPressureData();
		data.setIC(isIC);
		data.setDatalength(4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writePressure(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44WritePressureData data = new PIEF44WritePressureData();
		data.setOrient(Orient.WRITE);
		data.setDatalength(values.size());
		data.setValues(values);
		data.setIC(isIC);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOnePressure(int fixtureIndex, int value) throws NetworkException {
		PIEF44WritePressureData data = new PIEF44WritePressureData();
		data.setOrient(Orient.WRITE);
		List<Integer> values = new ArrayList<>();
		values.add(value);
		data.setDatalength(1);
		data.setFixtureIndex(fixtureIndex%4);
		data.setValues(values);
		data.setIC(fixtureIndex<4);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readBatterySource() throws NetworkException {
		PIEF44AGBatterySourceData data = new PIEF44AGBatterySourceData();
		data.setDatalength(4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeBatterySource(List<Integer> values) throws NetworkException {
		PIEF44AGBatterySourceData data = new PIEF44AGBatterySourceData();
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size());
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneBatterySource(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44AGBatterySourceData data = new PIEF44AGBatterySourceData();
		data.setFixtureIndex(fixtureIndex);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size());
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeTestResult(int fixtureIndex, List<Integer> values, int batteryIndex) throws NetworkException {
		PIEF44TestResultData data = new PIEF44TestResultData();
		data.setFixtureIndex(fixtureIndex);
		data.setBatteryIndex(batteryIndex);
		data.setValues(values);
		data.setOrient(Orient.WRITE);
		data.setDatalength(values.size());
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readTestResult(int fixtureIndex) throws NetworkException {
		PIEF44TestResultData data = new PIEF44TestResultData();
		data.setFixtureIndex(fixtureIndex);
		data.setDatalength(72);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}
	
	@Override
	public  List<Integer> readAreaData(int address,int length,Area area) throws NetworkException {
		PIEF44AreaData data = new PIEF44AreaData();
		data.setArea(area);
		data.setAddress(address);
		data.setDatalength(length);
		ResponseData rd = (ResponseData) connector.send(data, 10000);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}
	
	@Override
	public List<Integer> readAreaDuintData(int address,int length,Area area) throws NetworkException {
		PIEF44AreaDuintData data = new PIEF44AreaDuintData();
		data.setArea(area);
		data.setAddress(address);
		data.setDatalength(length);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeWaterWheelResult(List<Integer> values, int batteryIndex) throws NetworkException {
		PIEF44WaterwheelData data = new PIEF44WaterwheelData();
		data.setBatteryIndex(batteryIndex);
		data.setValues(values);
		data.setDatalength(values.size());
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readWaterWheelResult() throws NetworkException {
		PIEF44WaterwheelData data = new PIEF44WaterwheelData();
		data.setDatalength(72);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeFixtureKB(List<PressKB> pressKBs, boolean isIC) throws NetworkException {
		PIEF44PressKBData data = new PIEF44PressKBData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setPressKBs(pressKBs);
		data.setDatalength(pressKBs.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneFixtureKB(int fixtureIndex, PressKB kb) throws NetworkException {
		List<PressKB> pressKBs = new ArrayList<PressKB>();
		pressKBs.add(kb);
		PIEF44PressKBData data = new PIEF44PressKBData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setPressKBs(pressKBs);
		data.setDatalength(pressKBs.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<PressKB> readFixtureKB(boolean isIC) throws NetworkException {
		PIEF44PressKBData data = new PIEF44PressKBData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getPressKBs();
		}
		return null;
	}

	@Override
	public Result writePrePress(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44PrePressAddrData data = new PIEF44PrePressAddrData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOnePrePress(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44PrePressAddrData data = new PIEF44PrePressAddrData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readPrePress(boolean isIC) throws NetworkException {
		PIEF44PrePressAddrData data = new PIEF44PrePressAddrData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writePrePressBack(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44PrePressBackAddrData data = new PIEF44PrePressBackAddrData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOnePrePressBack(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44PrePressBackAddrData data = new PIEF44PrePressBackAddrData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readPrePressBack(boolean isIC) throws NetworkException {
		PIEF44PrePressBackAddrData data = new PIEF44PrePressBackAddrData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeFirstSpeed(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44FirstSpeedAddrData data = new PIEF44FirstSpeedAddrData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneFirstSpeed(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44FirstSpeedAddrData data = new PIEF44FirstSpeedAddrData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readFirstSpeed(boolean isIC) throws NetworkException {
		PIEF44FirstSpeedAddrData data = new PIEF44FirstSpeedAddrData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeFirstSpeedBack(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44FirstSpeedBackAddrData data = new PIEF44FirstSpeedBackAddrData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneFirstSpeedBack(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44FirstSpeedBackAddrData data = new PIEF44FirstSpeedBackAddrData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readFirstSpeedBack(boolean isIC) throws NetworkException {
		PIEF44FirstSpeedBackAddrData data = new PIEF44FirstSpeedBackAddrData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeFifthSpeed(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44FifthSpeedAddrData data = new PIEF44FifthSpeedAddrData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneFifthSpeed(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44FifthSpeedAddrData data = new PIEF44FifthSpeedAddrData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readFifthSpeed(boolean isIC) throws NetworkException {
		PIEF44FifthSpeedAddrData data = new PIEF44FifthSpeedAddrData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeFifthSpeedBack(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44FifthSpeedBackAddrData data = new PIEF44FifthSpeedBackAddrData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneFifthSpeedBack(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44FifthSpeedBackAddrData data = new PIEF44FifthSpeedBackAddrData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readFifthSpeedBack(boolean isIC) throws NetworkException {
		PIEF44FifthSpeedBackAddrData data = new PIEF44FifthSpeedBackAddrData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeCompleteOpenAddr(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44CompleteryOpenAddrData data = new PIEF44CompleteryOpenAddrData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneCompleteOpenAddr(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44CompleteryOpenAddrData data = new PIEF44CompleteryOpenAddrData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readCompleteOpenAddr(boolean isIC) throws NetworkException {
		PIEF44CompleteryOpenAddrData data = new PIEF44CompleteryOpenAddrData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeCompleteOpenPress(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44CompleteryOpenPressData data = new PIEF44CompleteryOpenPressData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneCompleteOpenPress(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44CompleteryOpenPressData data = new PIEF44CompleteryOpenPressData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readCompleteOpenPress(boolean isIC) throws NetworkException {
		PIEF44CompleteryOpenPressData data = new PIEF44CompleteryOpenPressData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writePressCompensation(List<Integer> values, boolean isIC) throws NetworkException {
		PIEF44PressCompensationData data = new PIEF44PressCompensationData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOnePressCompensation(int fixtureIndex, int value) throws NetworkException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(value);
		PIEF44PressCompensationData data = new PIEF44PressCompensationData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setValues(values);
		data.setDatalength(values.size() * 2);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Integer> readPressCompensation(boolean isIC) throws NetworkException {
		PIEF44PressCompensationData data = new PIEF44PressCompensationData();
		data.setIC(isIC);
		data.setDatalength(8);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValues();
		}
		return null;
	}

	@Override
	public Result writeTempAlert(List<Alert> alerts, boolean isIC) throws NetworkException {
		PIEF44TempAlertData data = new PIEF44TempAlertData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setAlerts(alerts);
		data.setDatalength(alerts.size() * 3);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneTempAlert(int fixtureIndex, Alert alert) throws NetworkException {
		List<Alert> alerts = new ArrayList<Alert>();
		alerts.add(alert);
		PIEF44TempAlertData data = new PIEF44TempAlertData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setAlerts(alerts);
		data.setDatalength(alerts.size() * 3);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Alert> readTempAlert(boolean isIC) throws NetworkException {
		PIEF44TempAlertData data = new PIEF44TempAlertData();
		data.setIC(isIC);
		data.setDatalength(12);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getAlerts();
		}
		return null;
	}

	@Override
	public Result writePresAlert(List<Alert> alerts, boolean isIC) throws NetworkException {
		PIEF44PresAlertData data = new PIEF44PresAlertData();
		data.setIC(isIC);
		data.setOrient(Orient.WRITE);
		data.setAlerts(alerts);
		data.setDatalength(alerts.size() * 6);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOnePresAlert(int fixtureIndex, Alert alert) throws NetworkException {
		List<Alert> alerts = new ArrayList<Alert>();
		alerts.add(alert);
		PIEF44PresAlertData data = new PIEF44PresAlertData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		data.setAlerts(alerts);
		data.setDatalength(alerts.size() * 6);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Alert> readPresAlert(boolean isIC) throws NetworkException {
		PIEF44PresAlertData data = new PIEF44PresAlertData();
		data.setIC(isIC);
		data.setDatalength(24);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getAlerts();
		}
		return null;
	}

	@Override
	public Result writeICSpeed(List<Speed> speeds) throws NetworkException {
		PIEF44ICSpeedData data = new PIEF44ICSpeedData();
		data.setOrient(Orient.WRITE);
		data.setSpeeds(speeds);
		data.setDatalength(speeds.size() * 20);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeOneICSpeed(int fixtureIndex, Speed speed) throws NetworkException {
		List<Speed> speeds = new ArrayList<Speed>();
		speeds.add(speed);
		PIEF44ICSpeedData data = new PIEF44ICSpeedData();
		data.setFixtureIndex(fixtureIndex);
		data.setOrient(Orient.WRITE);
		data.setSpeeds(speeds);
		data.setDatalength(speeds.size() * 20);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public List<Speed> readICSpeed() throws NetworkException {
		PIEF44ICSpeedData data = new PIEF44ICSpeedData();
		data.setDatalength(24);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getSpeeds();
		}
		return null;
	}

	@Override
	public Result writeCurrentChannel(int number) throws NetworkException {
		PIEF44CurrentChannelData data = new PIEF44CurrentChannelData();
		data.setNumber(number);
		data.setOrient(Orient.WRITE);
		data.setDatalength(1);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Integer readCurrentChannel() throws NetworkException {
		PIEF44CurrentChannelData data = new PIEF44CurrentChannelData();
		data.setDatalength(1);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getNumber();
		}
		return null;
	}

	@Override
	public Result writeBatteryCount(int number) throws NetworkException {
		PIEF44BatteryCountData data = new PIEF44BatteryCountData();
		data.setNumber(number);
		data.setOrient(Orient.WRITE);
		data.setDatalength(1);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Integer readBatterycount() throws NetworkException {
		PIEF44BatteryCountData data = new PIEF44BatteryCountData();
		data.setDatalength(1);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getNumber();
		}
		return null;
	}

	@Override
	public Result writeWaterWheelBatterySource(int number) throws NetworkException {
		PIEF44WaterWheelBatterySourceData data = new PIEF44WaterWheelBatterySourceData();
		data.setNumber(number);
		data.setOrient(Orient.WRITE);
		data.setDatalength(1);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Integer readWaterWheelBatterySource() throws NetworkException {
		PIEF44WaterWheelBatterySourceData data = new PIEF44WaterWheelBatterySourceData();
		data.setDatalength(1);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getNumber();
		}
		return null;
	}

	@Override
	public Result writeMachanicalArmCurrent(int number) throws NetworkException {
		PIEF44MechanicalArmCurrentBatteryData data = new PIEF44MechanicalArmCurrentBatteryData();
		data.setNumber(number);
		data.setOrient(Orient.WRITE);
		data.setDatalength(1);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Integer readMachinicalArmCurrent() throws NetworkException {
		PIEF44MechanicalArmCurrentBatteryData data = new PIEF44MechanicalArmCurrentBatteryData();
		data.setDatalength(1);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getNumber();
		}
		return null;
	}

	@Override
	public Result writeAlertRead(boolean isAlert) throws NetworkException {
		PIEF44AlertReadData data = new PIEF44AlertReadData();
		data.setBit(true);
		data.setAlert(isAlert);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readAlertRead() throws NetworkException {
		PIEF44AlertReadData data = new PIEF44AlertReadData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isAlert();
		}
		return false;
	}

	@Override
	public boolean readAutoRead() throws NetworkException {
		PIEF44AutoReadData data = new PIEF44AutoReadData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isAutoRun();
		}
		return false;
	}

	@Override
	public Result writeAutoRead(boolean isAuto) throws NetworkException {
		PIEF44AutoReadData data = new PIEF44AutoReadData();
		data.setBit(true);
		data.setAutoRun(isAuto);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readStopRead() throws NetworkException {
		PIEF44StopReadData data = new PIEF44StopReadData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isStop();
		}
		return false;
	}

	@Override
	public Result writeStopRead(boolean isStop) throws NetworkException {
		PIEF44StopReadData data = new PIEF44StopReadData();
		data.setStop(isStop);
		data.setBit(true);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Result writeResetWrite(boolean isReset) throws NetworkException {
		PIEF44ResetWriteData data = new PIEF44ResetWriteData();
		data.setBit(true);
		data.setReset(isReset);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readResetWrite() throws NetworkException {
		PIEF44ResetWriteData data = new PIEF44ResetWriteData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isReset();
		}
		return false;
	}

	@Override
	public Result writeStartWrite(boolean isStart) throws NetworkException {
		PIEF44StartWriteData data = new PIEF44StartWriteData();
		data.setStart(isStart);
		data.setBit(true);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readStartWrite() throws NetworkException {
		PIEF44StartWriteData data = new PIEF44StartWriteData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isStart();
		}
		return false;
	}

	@Override
	public Result writeStopWrite(boolean isStop) throws NetworkException {
		PIEF44StopWriteData data = new PIEF44StopWriteData();
		data.setBit(true);
		data.setStop(isStop);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readStopWrite() throws NetworkException {
		PIEF44StopWriteData data = new PIEF44StopWriteData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isStop();
		}
		return false;
	}

	@Override
	public Result writeAutoState(boolean isOn) throws NetworkException {
		PIEF44AutoStateData data = new PIEF44AutoStateData();
		data.setBit(true);
		data.setOn(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readAutoState() throws NetworkException {
		PIEF44AutoStateData data = new PIEF44AutoStateData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isOn();
		}
		return false;
	}

	@Override
	public Result writeFixturePres(boolean isOn,int fixtureIndex) throws NetworkException {
		PIEF44FixturePressureData data = new PIEF44FixturePressureData();
		data.setBit(true);
		data.setOn(isOn);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readFixturePres( int fixtureIndex) throws NetworkException {
		PIEF44FixturePressureData data = new PIEF44FixturePressureData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isOn();
		}
		return false;
	}

	@Override
	public Result writeFixtureShield(boolean isOn, int fixtureIndex) throws NetworkException {
		PIEF44FixtureShieldData data = new PIEF44FixtureShieldData();
		data.setShield(isOn);
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readFixtureShield( int fixtureIndex) throws NetworkException {
		PIEF44FixtureShieldData data = new PIEF44FixtureShieldData();
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isShield();
		}
		return false;
	}

	@Override
	public Result writeFixtureTemp(boolean isOn,int fixtureIndex) throws NetworkException {
		PIEF44FixtureTempData data = new PIEF44FixtureTempData();
		data.setBit(true);
		data.setOn(isOn);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readFixtureTemp(int fixtureIndex) throws NetworkException {
		PIEF44FixtureTempData data = new PIEF44FixtureTempData();
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isOn();
		}
		return false;
	}

	@Override
	public Result writeFixtureState(boolean isOn,int fixtureIndex) throws NetworkException {
		PIEF44FixtureStateData data = new PIEF44FixtureStateData();
		data.setBit(true);
		data.setOn(isOn);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readFixtureState(int fixtureIndex) throws NetworkException {
		PIEF44FixtureStateData data = new PIEF44FixtureStateData();
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isOn();
		}
		return false;
	}

	@Override
	public Result writeHasMaterial(boolean isOn, int fixtureIndex) throws NetworkException {
		PIEF44HasMaterialData data = new PIEF44HasMaterialData();
		data.setHas(isOn);
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readHasMaterial(int fixtureIndex) throws NetworkException {
		PIEF44HasMaterialData data = new PIEF44HasMaterialData();
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isHas();
		}
		return false;
	}

	@Override
	public Result writeMove1(boolean isOn, int fixtureIndex) throws NetworkException {
		PIEF44Move1Data data = new PIEF44Move1Data();
		data.setBit(true);
		data.setMove(isOn);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readMove1(int fixtureIndex) throws NetworkException {
		PIEF44Move1Data data = new PIEF44Move1Data();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isMove();
		}
		return false;
	}

	@Override
	public Result writeMove2(boolean isOn, int fixtureIndex) throws NetworkException {
		PIEF44Move2Data data = new PIEF44Move2Data();
		data.setBit(true);
		data.setMove(isOn);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readMove2(int fixtureIndex) throws NetworkException {
		PIEF44Move2Data data = new PIEF44Move2Data();
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isMove();
		}
		return false;
	}

	@Override
	public Result writeNgState(boolean isOn, int fixtureIndex) throws NetworkException {
		PIEF44NgData data = new PIEF44NgData();
		data.setNg(isOn);
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readNgState( int fixtureIndex) throws NetworkException {
		PIEF44NgData data = new PIEF44NgData();
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isNg();
		}
		return false;
	}

	@Override
	public Result writeProcessRequest(boolean isOn,  int fixtureIndex) throws NetworkException {
		PIEF44ProcessRequestData data = new PIEF44ProcessRequestData();
		data.setSend(isOn);
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readProcessRequest( int fixtureIndex) throws NetworkException {
		PIEF44ProcessRequestData data = new PIEF44ProcessRequestData();
		data.setBit(true);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isSend();
		}
		return false;
	}

	@Override
	public Result writeTestState(boolean isOn, int fixtureIndex) throws NetworkException {
		PIEF44TestStateData data = new PIEF44TestStateData();
		data.setBit(true);
		data.setOn(isOn);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readTestState(int fixtureIndex) throws NetworkException {
		PIEF44TestStateData data = new PIEF44TestStateData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isOn();
		}
		return false;
	}

	@Override
	public Result writeWorkCompletSign(boolean isOn, int fixtureIndex) throws NetworkException {
		PIEF44WorkCompletionSignalData data = new PIEF44WorkCompletionSignalData();
		data.setBit(true);
		data.setSign(isOn);
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readWorkCompletSign( int fixtureIndex) throws NetworkException {
		PIEF44WorkCompletionSignalData data = new PIEF44WorkCompletionSignalData();
		data.setIC(fixtureIndex<4);
		data.setFixtureIndex(fixtureIndex%4);
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isSign();
		}
		return false;
	}

	@Override
	public Result writeBarcodeate(boolean isOn) throws NetworkException {
		PIEF44BarcodeData data = new PIEF44BarcodeData();
		data.setBit(true);
		data.setBarcodeOk(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readBarcodeState() throws NetworkException {
		PIEF44BarcodeData data = new PIEF44BarcodeData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isBarcodeOk();
		}
		return false;
	}

	@Override
	public Result writeDatabaseState(boolean isOn) throws NetworkException {
		PIEF44DatabaseData data = new PIEF44DatabaseData();
		data.setDatabaseOK(isOn);
		data.setBit(true);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readDabaseState() throws NetworkException {
		PIEF44DatabaseData data = new PIEF44DatabaseData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isDatabaseOK();
		}
		return false;
	}

	@Override
	public Result writeUnloadBarcodeNg(boolean isOn) throws NetworkException {
		PIEF44UnloadBarcodeNgData data = new PIEF44UnloadBarcodeNgData();
		data.setBit(true);
		data.setNg(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadBarcodeNg() throws NetworkException {
		PIEF44UnloadBarcodeNgData data = new PIEF44UnloadBarcodeNgData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isNg();
		}
		return false;
	}

	@Override
	public Result writeUnloadBarcodeOk(boolean isOn) throws NetworkException {
		PIEF44UnloadBarcodeOkData data = new PIEF44UnloadBarcodeOkData();
		data.setBit(true);
		data.setBit(true);
		data.setOk(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadBarcodeOk() throws NetworkException {
		PIEF44UnloadBarcodeOkData data = new PIEF44UnloadBarcodeOkData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isOk();
		}
		return false;
	}

	@Override
	public Result writeUnloadBarcodeSign(boolean isOn) throws NetworkException {
		PIEF44UnloadBarcodeSignData data = new PIEF44UnloadBarcodeSignData();
		data.setBit(true);
		data.setSign(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadBarcodeSign() throws NetworkException {
		PIEF44UnloadBarcodeSignData data = new PIEF44UnloadBarcodeSignData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isSign();
		}
		return false;
	}

	@Override
	public Result writeUnloadClipBinding(boolean isOn) throws NetworkException {
		PIEF44UnloadClipBindingData data = new PIEF44UnloadClipBindingData();
		data.setBit(true);
		data.setBinding(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadClipBinding() throws NetworkException {
		PIEF44UnloadClipBindingData data = new PIEF44UnloadClipBindingData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isBinding();
		}
		return false;
	}

	@Override
	public Result writeUnloadClipBindingFinish(boolean isOn) throws NetworkException {
		PIEF44UnloadClipBindingFinishData data = new PIEF44UnloadClipBindingFinishData();
		data.setBit(true);
		data.setBinding(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadClipBindingFinish() throws NetworkException {
		PIEF44UnloadClipBindingFinishData data = new PIEF44UnloadClipBindingFinishData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isBinding();
		}
		return false;
	}

	@Override
	public Result writeUnloadConfirm(boolean isOn) throws NetworkException {
		PIEF44UnloadConfirmData data = new PIEF44UnloadConfirmData();
		data.setConfirm(isOn);
		data.setBit(true);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadConfirm() throws NetworkException {
		PIEF44UnloadConfirmData data = new PIEF44UnloadConfirmData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isConfirm();
		}
		return false;
	}

	@Override
	public Result writeUnloadSign(boolean isOn) throws NetworkException {
		PIEF44UnloadSignData data = new PIEF44UnloadSignData();
		data.setBit(true);
		data.setSign(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadSign() throws NetworkException {
		PIEF44UnloadSignData data = new PIEF44UnloadSignData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isSign();
		}
		return false;
	}

	@Override
	public Result writeUnloadConveyorSign(boolean isOn) throws NetworkException {
		PIEF44UnloadConveyorData data = new PIEF44UnloadConveyorData();
		data.setBit(true);
		data.setSign(isOn);
		data.setOrient(Orient.WRITE);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadConveyorSign() throws NetworkException {
		PIEF44UnloadConveyorData data = new PIEF44UnloadConveyorData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isSign();
		}
		return false;
	}

	@Override
	public Result writeUnloadConveyorConfirmSign(boolean isOn) throws NetworkException {
		PIEF44UnloadConveyorConfirmData data = new PIEF44UnloadConveyorConfirmData();
		data.setConfirm(isOn);
		data.setOrient(Orient.WRITE);
		data.setBit(true);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadConveyorConfirmSign() throws NetworkException {
		PIEF44UnloadConveyorConfirmData data = new PIEF44UnloadConveyorConfirmData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isConfirm();
		}
		return false;
	}

	@Override
	public Result writeUnloadToClipSign(boolean isOn) throws NetworkException {
		PIEF44UnloadToClipSignData data = new PIEF44UnloadToClipSignData();
		data.setSign(isOn);
		data.setOrient(Orient.WRITE);
		data.setBit(true);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadToClipSign() throws NetworkException {
		PIEF44UnloadToClipSignData data = new PIEF44UnloadToClipSignData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isSign();
		}
		return false;
	}

	@Override
	public Result writeUnloadToClipConfirmSign(boolean isOn) throws NetworkException {
		PIEF44UnloadToClipSignConfirmData data = new PIEF44UnloadToClipSignConfirmData();
		data.setConfirm(isOn);
		data.setOrient(Orient.WRITE);
		data.setBit(true);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readUnloadToClipConfirmSign() throws NetworkException {
		PIEF44UnloadToClipSignConfirmData data = new PIEF44UnloadToClipSignConfirmData();
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isConfirm();
		}
		return false;
	}

	@Override
	public Result writeCustomDMAddress(int address, Type type, int value) throws NetworkException {
		DMFinsData data = new DMFinsData();
		data.setOrient(Orient.WRITE);
		data.setType(type);
		data.setValue(value);
		data.setAddress(address);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public Integer readCustomDMAddress(int address, Type type) throws NetworkException {
		DMFinsData data = new DMFinsData();
		data.setOrient(Orient.READ);
		data.setAddress(address);
		data.setType(type);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.getValue();
		}
		return null;
	}

	@Override
	public Result writeCustomWRAddress(int address, int index, boolean isOn) throws NetworkException {
		WRFinsData data = new WRFinsData();
		data.setOrient(Orient.WRITE);
		data.setOn(isOn);
		data.setAddress(address);
		data.setIndex(index);
		data.setBit(true);
		return ((ResponseData) connector.send(data, timeout)).getResult();
	}

	@Override
	public boolean readCustomWRAddress(int address, int index) throws NetworkException {
		WRFinsData data = new WRFinsData();
		data.setAddress(address);
		data.setIndex(index);
		data.setBit(true);
		ResponseData rd = (ResponseData) connector.send(data, timeout);
		if (rd.getResult() == Result.OK) {
			data.decode(rd.getData());
			return data.isOn();
		}
		return false;
	}
}
