package com.nlteck.controller;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.nlteck.base.I18N;
import com.nlteck.fireware.GPIO;

/**
 * GPIO电源控制管理器
 * @author Administrator
 *
 */

public class GpioPowerController extends PowerController{
    
	private GPIO controller; //控制器 ;高电平打开电源，低电平关闭电源
	private GPIO monitor; //监视器;高电平表示系统供电正常，低电平表示系统已断电
	
	private Timer timer;
	
	/**
	 * GPIO控制電平引腳複用
	 */
	public final static int POWER_CONTROL_PIN = 69; // 高电平打开电源，低电平关闭电源
	public final static int POWER_CHCEK_PIN = 70; // 输入端，高电平有电，低电平断电
	public final static int WATCHDOG_CONTROL_PIN = 71; // 200ms一次高低电平变化
	public final static int LAMP_CONTROL_PIN = 72; // 跑马灯
	
	
	
	public GpioPowerController(GPIO controller , GPIO monitor) throws Exception {
		
		this.controller = controller;
		this.monitor    = monitor;
		try {
			this.controller.export();
			this.controller.setOutput();//设置为输出
			this.controller.writeValue(1); //设置为高电平；打开电源供电
			this.monitor.export();
			this.monitor.setInput(); //设置为输入端
		} catch (IOException e1) {
			
			throw new Exception(I18N.getVal(I18N.GpioInitError));
		} 
		
		
		timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				
				try {
					poweroff = monitor.readValue() == 0;
					if(poweroff) {
						
						Thread.sleep(100);
						poweroff = monitor.readValue() == 0;
						System.out.println("monitor power off!");
						triggerPowerOffEvent();
						//退出监视器
						timer.cancel();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
		}, 500, 1000);
		
	}
	
	@Override
	public void powerOff() {
		
		System.out.println("ok,set power voltage lower!");
		try {
			controller.writeValue(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void powerOn() {
		
		try {
			controller.writeValue(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
