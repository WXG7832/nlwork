package com.nlteck.junit;

import java.io.IOException;

import com.nlteck.firmware.GPIO;
import com.nlteck.util.CommonUtil;

public class GPIOTestor {

	public static void main(String[] args) {
		
		
		int pin = Integer.parseInt(args[0]);
		GPIO gpio = new GPIO(pin);
		int sleep = Integer.parseInt(args[1]);
		
		try {
			
			gpio.export();
			System.out.println("export gpio success");
			gpio.setOutput();
			
			System.out.println("set gpio out direction");
			
			int level = 0;
			
			while(true) {
				
				if(level == 0) {
					level = 1;
				}
				else {
					level = 0;
				}
				System.out.println("set voltage level " + level);
				gpio.writeValue(level);
				
				CommonUtil.sleep(sleep);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
