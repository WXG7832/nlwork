package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.Environment;
import com.nlteck.constant.Constant.DeviceType;
import com.nlteck.constant.Constant.Pole;
import com.nlteck.constant.Constant.State;
import com.nlteck.constant.Constant.Window;
import com.nlteck.exception.ScreenException;
import com.nlteck.firmware.ControlUnit.Statistics;
import com.nlteck.firmware.MainBoard;
import com.nlteck.screen.ScreenInterface;
import com.nltecklib.protocol.li.accessory.TempProbeData;
import com.nltecklib.protocol.li.main.PoleData;


public class ScreenController {

	private ScreenInterface screenInterface;
	private MainBoard mainBoard;
	private ScheduledExecutorService executor = null;
	private int   index;
	private String  password = "123456"; //密码
	private long   pickupCount;  //采样次数
	private boolean connected;

	public ScreenController(MainBoard mb, ScreenInterface screen) throws ScreenException {

		this.screenInterface = screen;
		this.mainBoard = mb;
		screenInterface.setCurrentTime(new Date());
		screenInterface.showIpAddress(MainBoard.getHostIP());
		screenInterface.setPassword(MainBoard.startupCfg.getScreenInfo().password);//初始密码

	}
	/**
	 * 设置液晶屏显示类型
	 * @param dt
	 */
	public void setDeviceType(DeviceType dt) {
		
		screenInterface.setDeviceType(dt);
	}
	

	public synchronized void switchScreen(Window window) throws ScreenException {

		screenInterface.showWindow(window);

	}
	
	
	
	public synchronized void showAlertInfo(int code , String type , Date date , String info) throws ScreenException {
		
		screenInterface.popupAlertWindow(code, type, date , info);
	}
	
	public synchronized void showProgress(int pos) throws ScreenException {
		
		screenInterface.updateProgressValue(pos);
	}

	/**
	 * 展示启动界面信息
	 * 
	 * @param result
	 * @param info
	 * @throws ScreenException 
	 */
	public synchronized void showStartupInfo(boolean result, String info) throws ScreenException {

		screenInterface.showStartUpInfo(result, info);
	}

	/**
	 * 显示主页面
	 * @throws ScreenException 
	 */
	public synchronized void showMainScreen() throws ScreenException {

		screenInterface.switchToFuncWindow();

	}

	private synchronized void refreshTempLine() throws ScreenException {
        
		// 显示网络信标
		screenInterface.checkSignal(!mainBoard.isOffline());
		if (mainBoard.getTempManager() != null ) {
            
			screenInterface.appendTempDotToChart(mainBoard.getTempManager().readTemperature());

		} else if(mainBoard.getProbeManager() != null) {
			
			/*TempProbeData probeData = mainBoard.getProbeManager().getTempData();
			if(probeData != null) {
				
				screenInterface.appendTempDotToChart(probeData.getTempList().get(0));
			}*/
			
		}
	}
	/**
	 * 更新所有通道的状态
	 * @throws ScreenException 
	 */
	private synchronized void refreshChnsWindow() throws ScreenException {
		
		// 显示网络信标
		screenInterface.checkSignal(!mainBoard.isOffline());
		if(index++ % 4 == 0) {
		    screenInterface.changeChannelState(1, mainBoard.getAllChannelStStates());
		}
		
	}
	
	/**
	 * 更新驱动板信息
	 * @throws ScreenException 
	 */
	private synchronized void refreshDriverWindow() throws ScreenException {
		
		// 显示网络信标
		screenInterface.checkSignal(!mainBoard.isOffline());
		List<State> states = new ArrayList<State>();
		for(int n = 0 ; n < MainBoard.startupCfg.getDriverCount() ; n++ ) {
			
			states.add(mainBoard.getDriverStStateByIndex(n));
		}
		screenInterface.changeDrivingState(1, states);
		
	}

	private synchronized void refreshMainWindow() throws ScreenException {
        		
		// 显示温度
		if (mainBoard.getTempManager() != null) {

		   screenInterface.showTemperature(mainBoard.getTempManager().readTemperature());
		} else if(mainBoard.getProbeManager() != null) {
			
			/*TempProbeData probeData = mainBoard.getProbeManager().getTempData();
			if(probeData != null) {
				
				screenInterface.showTemperature(probeData.getTempList().get(0));
			}*/
			
		}
		// 显示网络信标
		screenInterface.checkSignal(!mainBoard.isOffline());
		
		//显示测试名
		screenInterface.showTestName(mainBoard.getControlUnitByIndex(0).getTestName());
		
		// 极性
		if (mainBoard.getControlUnitByIndex(0).getPnAG().getPoleProtect().getPole() == PoleData.Pole.NORMAL) {
			screenInterface.showPole(Pole.POSITIVE);
		} else {

			screenInterface.showPole(Pole.NEGTIVE);
		}
        
		int charge = 0 , discharge = 0 , sleep = 0 , alert = 0;
		
		Statistics st = mainBoard.getStatistics();
		charge += st.charge;
		discharge += st.discharge;
		sleep += st.sleep;
		alert += st.alert;
		screenInterface.showStatesCount(charge, discharge, sleep, alert);

	}
	
	/**
	 * 修改IP地址
	 * @param ipAddress
	 */
	public boolean changeIpAddress(String ipAddress) {
		
		  return Environment.writeIpAddress("/etc/network/interfaces", ipAddress);
	}
	
	/**
	 * 获取用户输入的IP地址
	 * @return
	 * @throws ScreenException 
	 */
	public String getInputAddress() throws ScreenException {
		
		return screenInterface.getInputIpAddress();
	}
	

	public void stopPickup() {
		
		if (!executor.isShutdown()) {
			
			executor.shutdown();
		}
	}

	public void startPickup() {

		if (executor == null || executor.isShutdown()) {
			
			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					try {
						
						System.out.println(screenInterface.getCurrentWindow());

                        if(screenInterface.getCurrentWindow() == Window.FUNC) {
                        	
                        	refreshMainWindow();	
                        }else if(screenInterface.getCurrentWindow() == Window.DRIVING  || 
                        		screenInterface.getCurrentWindow() == Window.DRIVING_ALL
                        		) {
                        	
                        	
                        	refreshDriverWindow();	
                        }else if(screenInterface.getCurrentWindow() == Window.CHANNEL ||
                        		screenInterface.getCurrentWindow() == Window.CHANNEL_ALL) {
                        	
                        	refreshChnsWindow();
                        }else if(screenInterface.getCurrentWindow() == Window.TEMP) {
                        	
                        	refreshTempLine();
                        }else if(screenInterface.getCurrentWindow() == Window.PASSWORD) {
                        	
                        	//检测用户是否修改了密码
                        	if(screenInterface.isPasswordUpdate()) {
                        		
                        		MainBoard.startupCfg.getScreenInfo().password = screenInterface.getInputNewPassword();
                        		MainBoard.startupCfg.saveScreenPassword(MainBoard.startupCfg.getScreenInfo().password);
                        		screenInterface.setPassWordUpdateOff();
                        	}
                        	
                        }else if(screenInterface.getCurrentWindow() == Window.IP) {
                        	
                        	//检测用户是否修改IP
                        	if(screenInterface.isIpUpdate()) {
                        		
                        		String newIp = screenInterface.getInputIpAddress();
                        		Environment.infoLogger.info("changedIp:" + newIp);
                        		MainBoard.startupCfg.saveIpAddress(newIp);
                        		screenInterface.showIpAddress(newIp);
                        		screenInterface.setIpUpdateOff(changeIpAddress(newIp));
                        	}
                        }
                        pickupCount++;
                        
                        if(pickupCount % 30 == 0) {
                        	
                        	//将设备时间刷新到液晶屏
                        	screenInterface.setCurrentTime(new Date());
                        	
                        	
                        }
                        connected = true;
						
					} catch (Exception ex) {
                        
						connected = false;
						ex.printStackTrace();
					}

				}

			}, 1000, 1000, TimeUnit.MILLISECONDS);
		}
	}


	public long getPickupCount() {
		return pickupCount;
	}


	public void setPickupCount(long pickupCount) {
		this.pickupCount = pickupCount;
	}


	public boolean isConnected() {
		return connected;
	}


	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	
}
