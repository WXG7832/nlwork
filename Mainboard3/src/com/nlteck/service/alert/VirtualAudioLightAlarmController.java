package com.nlteck.service.alert;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;

public class VirtualAudioLightAlarmController extends AudioLightAlarmController {

	private ScheduledExecutorService executor = null;
	private int elpaseSecs = 0;

	public VirtualAudioLightAlarmController(int driverIndex) {
		super(driverIndex);
	}

	@Override
	public void configLightAndAudio(byte colorFlag, short lightFlag, short audioFlag) throws AlertException {

		configLightAndAudio(colorFlag, lightFlag, audioFlag, 0);
	}

	@Override
	public void configLightAndAudio(byte colorFlag, short lightFlag, short audioFlag, int audioTimeout)
			throws AlertException {

		StringBuffer msg = new StringBuffer();
		if ((colorFlag & LightColor.GREEN.getCode()) > 0) {

			msg.append("color: GREEN ");
		} else if ((colorFlag & LightColor.YELLOW.getCode()) > 0) {

			msg.append("color: YELLOW ");
		} else if ((colorFlag & LightColor.RED.getCode()) > 0) {

			msg.append("color: RED ");
		}

		msg.append("lightFlag:" + lightFlag + " ");
		msg.append("audioFlag:" + audioFlag + " ");
        
		System.out.println(msg.toString());
		beep(audioFlag, audioTimeout, 4);

	}

	private synchronized void beep(int audioFlag, int timeOut, int loopTime) {

		if (executor != null) {

			executor.shutdownNow();
			executor = null;
		}

		if (audioFlag > 0) {
			elpaseSecs = -loopTime;
			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					elpaseSecs += loopTime;
					// Æô¶¯ÉùÒô
					java.awt.Toolkit.getDefaultToolkit().beep();
					if (elpaseSecs >= timeOut && timeOut > 0) {

						executor.shutdownNow();
						executor = null;
					}
				}

			}, 0, loopTime, TimeUnit.SECONDS);

		}
	}

}
