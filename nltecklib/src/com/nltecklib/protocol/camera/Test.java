package com.nltecklib.protocol.camera;

import java.io.IOException;

import org.apache.mina.core.session.IoSession;

import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.io.mina.NetworkServer;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.FusionRadio;
import com.nltecklib.protocol.camera.data.FusionRadioData;

public class Test {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		NetworkServer server = new NetworkServer(new Entity());
		server.createServer("192.168.0.201", 8123, new NetworkListener() {
			
			@Override
			public void send(IoSession session, Object message) {				
			}
			
			@Override
			public void receive(IoSession session, Object message) {
				if (((Data)message).getCode() != CameraCode.HEART_RETURN) {					
					System.out.println(message);
				}
			}
			
			@Override
			public void idled(IoSession session) {		
			}
			
			@Override
			public void exception(IoSession session, Throwable cause) {
			}
			
			@Override
			public void disconnected(IoSession session) {
			}
			
			@Override
			public void connected(IoSession session) {
			}
		});
		Thread.sleep(2000);
		FusionRadioData data = new FusionRadioData();
		data.setRadio(FusionRadio.R075);
		server.send(data);
	}
}
