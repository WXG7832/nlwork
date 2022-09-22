package com.nlteck.firmware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nlteck.model.ChannelDO;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;

/**
 * Âß¼­°å¶ÔÏó
 * 
 * @author Administrator
 *
 */
public class LogicBoard {
    private int logicIndex;
    private boolean open;
    private Device device;
    private List<DriverBoard> drivers = new ArrayList<DriverBoard>();
    private List<ChannelDO>   channels = new ArrayList<>();
    
    public LogicBoard(Device device , int index) {
    	
    	this.device = device;
    	this.logicIndex = index;
    	int logicChnCount = device.getChnNumInDriver() * device.getDriverNumInLogic();
    	
    	channels = device.getChannels().subList(index * logicChnCount , (index + 1) * logicChnCount);
    	
    	for(int n = 0 ; n <  device.getDriverNumInLogic() ; n++ ) {
    		
    		 DriverBoard db = new DriverBoard(this, n);
    		 drivers.add(db);

    	}
    	
    	
    }

    public List<DriverBoard> getDrivers() {
	return drivers;
    }

    public boolean isOpen() {
	return open;
    }

    public void setOpen(boolean open) {
	this.open = open;
    }

    public int getLogicIndex() {
	return logicIndex;
    }

    public void setLogicIndex(int logicIndex) {
	this.logicIndex = logicIndex;
    }

    public Device getDevice() {
	return device;
    }

	public List<ChannelDO> getChannels() {
		return channels;
	}
    
    public List<ChannelDO> listAllSelectChns() {
    	
    	List<ChannelDO> list = new ArrayList<>();
    	for(ChannelDO chn : channels) {
    		
    		if(chn.isSelected()) {
    			
    			list.add(chn);
    		}
    	}
    	return list;
    }
    
    
    public boolean isTesting() {

		for (ChannelDO chn : channels) {

			if (chn.getState() == CalState.CALCULATE || chn.getState() == CalState.CALIBRATE ||
					chn.getState() == CalState.READY) {

				return true;
			}
		}

		return false;

	}

}
