package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.AirPressureStateData;
import com.nltecklib.protocol.li.accessory.AirPressureStateData.AirPressureState;
import com.nltecklib.protocol.li.accessory.AirValveSwitchData;
import com.nltecklib.protocol.li.accessory.FourLightStateData;
import com.nltecklib.protocol.li.accessory.PingStateData;
import com.nltecklib.protocol.li.accessory.PingStateData.TempProbe;

/**
* @author  wavy_zheng
* @version 创建时间：2022年6月20日 下午4:32:37
* 类说明
*/
public class VirtualPingController extends AbsPingController {
    
	private PingStateData  innerState = new PingStateData();
	private AirPressureState   airPressureState = AirPressureState.NORMAL;
	
	
	public VirtualPingController(MainBoard mainboard) {
		super(mainboard);
		
		innerState.setDoorCylinder1PosOk(true);
		innerState.setDoorCylinder2PosOk(false);
		innerState.setSmogCheck1Ok(true);
		innerState.setSmogCheck2Ok(true);
		innerState.setTrayBackPosOk(true);
		innerState.setTrayCylinder1PosOk(true);
		innerState.setTrayCylinder2PosOk(true);
		innerState.setTrayCylinder3PosOk(true);
		innerState.setTrayCylinder4PosOk(true);
		innerState.setTrayFrontPosOk(true);
		innerState.setTrayOffsetOk(true);
		
		List<TempProbe> probes = new ArrayList<>();
		for(int n = 0 ; n < 16 ; n++) {
			
			TempProbe probe = new TempProbe();
			probe.temperature = 25;
			probe.tempOk = true;
			probes.add(probe);
		}
		innerState.setTempProbes(probes);
		
		List<PingStateData.Fan> fans = new ArrayList<>();
		
        for(int n = 0 ; n < 6 ; n++) {
			
        	PingStateData.Fan fan = new PingStateData.Fan();
        	fan.normal = true;
        	fan.open = true;
        	fans.add(fan);
			
		}
        innerState.setFans(fans);
		
	}

	@Override
	public PingStateData readPingState(int driverIndex) throws Exception {
		// TODO Auto-generated method stub
		return innerState;
	}

	@Override
	public void writeFourLightState(FourLightStateData lightData) throws Exception {
		
		System.out.println("write lightData:" + lightData);
		
	}

	@Override
	public AirPressureStateData readAirPressure(int driverIndex) throws Exception {
		
		
		AirPressureStateData data = new AirPressureStateData();
		data.setUnitIndex(driverIndex);
		data.setAirPressureState(airPressureState);
		
		return data;
	}

	@Override
	public void writeAirPressureControl(boolean lift) throws Exception {
		
		System.out.println(lift ?  " lift cylinder" : "slowdown cylinder!");
		
	}
    
	
	
	
	
}
