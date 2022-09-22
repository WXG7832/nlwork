package com.nlteck.fireware;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.nlteck.base.I18N;

public class RunningLamp {

	private GPIO gpio;
	private State state = State.OFFLINE;
	private int index = 0;
	private Thread thread;
	private boolean running;
	private String controlString = "00000011"; // 0表示灯灭0.5s，1表示灯亮0.5s
	private final static int UNIT_LEN = 8;
	private Timer timer; // 定时器

	/**
	 * 
	 * @author Administrator ONLINE 2灭，2亮 RUNNING 2灭，1亮1灭1亮1灭 OFFLINE 3灭 ,1亮 灭置高电平，
	 *         亮置低电平
	 */
	public enum State {

		ONLINE, RUNNING, OFFLINE, ALERT
	}

	public RunningLamp(GPIO gpio) throws Exception {

		this.gpio = gpio;
		try {
			gpio.export();
			gpio.setOutput();
		} catch (IOException e) {

			throw new Exception(I18N.getVal(I18N.RunningLampInitError));

		}

	}

	public void start() {

		running = true;
		System.out.println("running lamp...");

		timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				try {
					char controlCode = controlString.charAt(index++ % UNIT_LEN);
					if (controlCode == '0') {

						// 灭置高电平
						gpio.writeValue(1);

					} else {
						// 亮置低电平
						gpio.writeValue(0);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}, 1000, 500);
	}

	public void stop() {

		if (timer != null) {

			timer.cancel();
		}
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
		switch (state) {

		case OFFLINE:
			controlString = "00000011";
			break;
		case ONLINE:
			controlString = "00001111";
			break;
		case RUNNING:
			controlString = "00001010";
			break;
		case ALERT:
			controlString = "01010101";
			break;
		}

	}

}
