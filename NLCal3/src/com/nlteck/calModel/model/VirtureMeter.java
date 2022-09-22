package com.nlteck.calModel.model;

import java.io.IOException;

import com.nlteck.calModel.base.DelayConfig.DetailConfig;
import com.nlteck.model.TestDot;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;

public class VirtureMeter implements Meter {

	private String ip;
	private boolean use;
	private int index;
	
	public VirtureMeter(int index) {
		this.index=index;
	}
	
	public void gatherMeter(TestDot dot,DetailConfig detailConfig) {
		try {
			if (dot.getTestType() == TestType.Cal) {
				CalMode mode = dot.getMode();
				switch (mode) {
				case CC:
				case DC:
					dot.setMeterVal(
							(dot.getProgramVal() - 3.14) / 1.5);
					break;
				
				case CV:
					dot.setMeterVal(
							(dot.getProgramVal() - 2.718) / 1.7);
					break;
				default:
					break;
				}

			} else {
				
				double midProgramVal=0;
				if(dot.getMode().equals(CalMode.CC)||dot.getMode().equals(CalMode.DC)) {
					midProgramVal=dot.getProgramVal()*1.5+3.14-0.05+Math.random()*0.1;
				}else {
					midProgramVal=dot.getProgramVal()*1.7+2.718-0.05+Math.random()*0.1;
				}
				for(TestDot usedot:dot.getChannelDO().getCalDotList()) {
					if(usedot.moduleIndex==dot.moduleIndex&&	// usedot.getPrecision()==dot.getPrecision()&&
							usedot.getMode().equals(dot.getMode())&&usedot.getPole().equals(dot.getPole())) {
						if(usedot.getProgramk()==0&&usedot.getProgramb()==0) {
							continue;
						}
						if(dot.getProgramVal()<usedot.getMeterVal());
						dot.setMeterVal((midProgramVal-usedot.getProgramb())/usedot.getProgramk());
						break;
					}
						
				}

			}
			System.out.println("meter :" + dot.getMeterVal());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public double ReadSingle() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double ReadRealSingle() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void connect(String ip) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public String getIpAddress() {
		// TODO Auto-generated method stub
		return ip;
	}

	@Override
	public void setIpAddress(String ip) {
		// TODO Auto-generated method stub
		this.ip=ip;
	}

	@Override
	public boolean isUse() {
		// TODO Auto-generated method stub
		return this.use;
	}

	@Override
	public void setUse(boolean use) {
		// TODO Auto-generated method stub
		this.use=use;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double ReadSingleClearBuffer() throws IOException, InterruptedException {
		return 0;
	}

}
