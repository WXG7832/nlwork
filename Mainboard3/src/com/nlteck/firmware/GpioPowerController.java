package com.nlteck.firmware;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.HeartBeatData;
import com.nltecklib.protocol.li.accessory.PowerResetData;
import com.nltecklib.protocol.li.accessory.SmogAlertData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
 * GPIO电源控制管理器
 * 
 * @author Administrator
 *
 */
public class GpioPowerController extends PowerController {

	private GPIO controller; // 控制器 ;高电平打开电源，低电平关闭电源
	private GPIO monitor; // 监视器;高电平表示系统供电正常，低电平表示系统已断电

	private Timer timer;

	/**
	 * GPIO控制電平引腳複用
	 */
	public final static int POWER_CONTROL_PIN = 69; // 高电平打开电源，低电平关闭电源
	public final static int POWER_CHCEK_PIN = 70; // 输入端，高电平有电，低电平断电
	public final static int WATCHDOG_CONTROL_PIN = 71; // 200ms一次高低电平变化
	public final static int LAMP_CONTROL_PIN = 72; // 跑马灯

	public GpioPowerController(GPIO controller, GPIO monitor) throws AlertException {

		this.controller = controller;
		this.monitor = monitor;
		try {
			this.controller.export();
			this.controller.setOutput();// 设置为输出
			this.controller.writeValue(1); // 设置为高电平；打开电源供电
			this.monitor.export();
			this.monitor.setInput(); // 设置为输入端
		} catch (IOException e1) {

			// throw new AlertException(AlertCode.COMM_ERROR,"初始化电源控制器发生错误!");
			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.GpioInitError));
		}

		timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				try {
					poweroff = monitor.readValue() == 0;
					if (poweroff) {

						CommonUtil.sleep(100);
						poweroff = monitor.readValue() == 0;
						System.out.println("monitor power off!");
						triggerPowerOffEvent();
						// 退出监视器
						timer.cancel();
					}
				} catch (IOException e) {
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

	/**
	 * 切换逻辑板和回检板等辅助电源开关
	 * 
	 * @author wavy_zheng 2021年6月11日
	 * @param logicIndex
	 * @param powerOn
	 * @throws AlertException
	 */
	public void switchLogicPower(boolean powerOn) throws AlertException {

		String portName = MainBoard.startupCfg.getControlInfo().portName;
		SerialPort serialPort = Context.getPortManager().getPortByName(portName);
		ResponseDecorator response;
		PowerResetData prd = new PowerResetData();
		prd.setPs(powerOn ? PowerState.ON : PowerState.OFF);
		prd.setDriverIndex(0xff);

		try {
			response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(prd),
					3000);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError) + ":" + e.getMessage());
		}

	}

	/**
	 * 重置电源，恢复正常
	 * 
	 * @author wavy_zheng 2021年6月28日
	 * @param mainboard
	 */
	public boolean resetPower(MainBoard mainboard) {
       
		
		boolean ok = true;
		try {
			Context.getPowerProvider().switchLogicPower(false);

			CommonUtil.sleep(10000);

			Context.getPowerProvider().switchLogicPower(true);

		} catch (AlertException e) {

			e.printStackTrace();
			AlertException ex = new AlertException(AlertCode.LOGIC,"复位辅助电源失败:" + e.getMessage());
			Context.getPcNetworkService().pushSendQueue(ex);
			ok = false;
		}
		CommonUtil.sleep(20000);
		
		// 初始化极性和保护
		for (ControlUnit cu : mainboard.getControls()) {

			try {
				cu.writeBaseProtections();
			} catch (AlertException e) {

				e.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(e);
				ok = false;
			}
		}
		CommonUtil.sleep(6000);
		
		return ok;

	}

}
