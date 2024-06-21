package com.base;
 
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.http.MyHttpHandler;
import com.sun.net.httpserver.HttpServer;
 
public class HttpUtil {
	//�����˿�8880  �ӿڵ�ַ http://localhost:8880/demo
	private static final int port=8880;
	private static final String Httpcontext="/demo";
	private static final int nThreads=8;
	public static void main(String[] args) {
		HttpServer httpServer;
		try {
			httpServer=HttpServer.create(new InetSocketAddress(port),0);
			httpServer.createContext(Httpcontext,new MyHttpHandler() );
//			���ò�����
			ExecutorService  executor=Executors.newFixedThreadPool(nThreads);
			httpServer.setExecutor(executor);
			httpServer.start();
			System.out.println("�����˿�:"+port);
			System.out.println("���ڵ�:"+Httpcontext);
			System.out.println("������:"+nThreads);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}