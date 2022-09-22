package com.nlteck.service;

import com.nlteck.firmware.MainBoard;

/**
 * 内存管理器
 * 
 * @author Administrator
 *
 */
public class MemoryManager {

	private MainBoard mb;
    
	public static class MemoryInfo {
		
		public long vmFree = 0;
		public long vmUse = 0;
		public long vmTotal = 0;
		public long vmMax = 0;
	}
	
	public MemoryManager(MainBoard mb) {

		this.mb = mb;
//		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//		executor.scheduleAtFixedRate(new Runnable() {
//
//			@Override
//			public void run() {
//
//				// 虚拟机级内存情况查询
//				long vmFree = 0;
//				long vmUse = 0;
//				long vmTotal = 0;
//				long vmMax = 0;
//				int byteToMb = 1024 * 1024;
//
//				Runtime rt = Runtime.getRuntime();
//				vmTotal = rt.totalMemory() / byteToMb;
//				vmFree = rt.freeMemory() / byteToMb;
//				vmMax = rt.maxMemory() / byteToMb;
//				vmUse = vmTotal - vmFree;
//				System.out.println("JVM used memory :" + vmUse + " MB");
//				System.out.println("JVM freed memory:" + vmFree + " MB");
//				System.out.println("JVM total memory:" + vmTotal + " MB");
//				System.out.println("JVM maximum memory:" + vmMax + " MB");
//
//			}
//
//		}, 10000, 30000, TimeUnit.MILLISECONDS);

	}
	
	public MemoryInfo getMemoryInfo() {
		
		MemoryInfo mi = new MemoryInfo();
		Runtime rt = Runtime.getRuntime();
		int byteToMb = 1024 * 1024;
		mi.vmTotal = rt.totalMemory() / byteToMb;
		mi.vmFree = rt.freeMemory() / byteToMb;
		mi.vmMax = rt.maxMemory() / byteToMb;
		mi.vmUse = mi.vmTotal - mi.vmFree;
		return mi;
	}
	
	

}
