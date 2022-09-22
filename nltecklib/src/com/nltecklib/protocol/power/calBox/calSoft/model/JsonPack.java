package com.nltecklib.protocol.power.calBox.calSoft.model;

import com.nltecklib.protocol.power.calBox.calSoft.PUSH;

/**
 * json传输时，通道包内的json数据的组成结构
 * 
 * @author Administrator
 */
public class JsonPack {

    private PUSH push_log; // 推送给上位机软件的日志

    public PUSH getPush_log() {
	return push_log;
    }

    public void setPush_log(PUSH push_log) {
	this.push_log = push_log;
    }

}
