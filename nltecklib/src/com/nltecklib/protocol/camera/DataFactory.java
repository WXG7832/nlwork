package com.nltecklib.protocol.camera;

import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.data.HeartReturnData;
import com.nltecklib.protocol.camera.data.ImageData;
import com.nltecklib.protocol.camera.data.TempCountData;
import com.nltecklib.protocol.camera.data.TempReturnData;

/**
 * 协议工厂类
 * 
 * @author Administrator
 *
 */
public class DataFactory {

	/**
	 * 根据code生成空协议对象
	 * 
	 * @param orient
	 * @param code
	 * @return
	 */
	public static Decode createData(CameraCode type) {

		Decode data = null;
		switch(type){
		case IMAGE:
			data = new ImageData();
			break;
		case TEMP_COUNT:
			data = new TempCountData();
			break;
		case TEMP_RETURN:
			data = new TempReturnData();
			break;
		case HEART_RETURN:
			data = new HeartReturnData();
		default:
			break;
		}
		if(data == null){
			throw new RuntimeException("unrecognized function code:" + type);
		}
		return data;
	}

}
