package com.nlteck.fireware;

import java.io.IOException;

import com.nltecklib.device.Meter;

/**
 * –Èƒ‚ÕÚ”√±Ì
 * 
 * @author guofang_ma
 *
 */
public class VirtualMeter implements Meter {

	private String ip;
	private boolean use;
	private int index;
	private CalibrateCore core;

	public VirtualMeter(int index, CalibrateCore core) {
		// TODO Auto-generated constructor stub
		this.index = index;
		this.core = core;
	}

	@Override
	public double ReadSingle() throws IOException, InterruptedException {

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
		this.ip = ip;
	}

	@Override
	public boolean isUse() {
		// TODO Auto-generated method stub
		return use;
	}

	@Override
	public void setUse(boolean use) {
		// TODO Auto-generated method stub
		this.use = use;
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

		int calIndex = core.getMeterParamMap().get(this).lastCalIndex;
		VirtualCalBoard cb = ((VirtualCalBoard) core.getCalBoardMap().get(calIndex));
		return cb.getCurrentCalBoardChannel().getBindingChannel().getVirtualChannelData().meter;

	}

}
