package com.nlteck.service.accessory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nlteck.AlertException;
import com.nlteck.Environment;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.PortInfo;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月23日 下午5:43:22
* 串口管理
*/
public class PortManager {
    
	private Map<String, SerialPort> portMap = new HashMap<String, SerialPort>();
	
	/**
	 * 初始化串口
	 * @author  wavy_zheng
	 * 2020年11月23日
	 * @param virtual
	 * @throws AlertException 
	 */
	public void init(boolean virtual) throws AlertException {
		
		
		if(!virtual) {
			
			for (Iterator<String> it = MainBoard.startupCfg.getPorts().keySet().iterator(); it.hasNext();) {

				String key = it.next();
				try {
					PortInfo pi = MainBoard.startupCfg.getPorts().get(key);

					if (pi.use) {

						System.out.println("open port " + key + ",baudrate " + pi.baudrate);
						
						SerialPort port = SerialUtil.openPort(key, pi.baudrate);
						
						portMap.put(key, port);
						Environment.infoLogger.info("open " + key + " success!");
					} else {
						
						portMap.put(key, null);
					}

				} catch (Exception e) {

					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.OpenSerialFail, key));
				}

			}

			
			
		}
		
	}
	
	/**
	 * 根据串口名获取串口对象
	 * @author  wavy_zheng
	 * 2020年11月23日
	 * @param name
	 * @return
	 */
	public SerialPort  getPortByName(String name) {
		
		
		  return portMap.get(name);
	}
}
