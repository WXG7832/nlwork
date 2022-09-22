package com.nlteck.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.PCWorkform.CalMatchValueData;
import com.nltecklib.protocol.li.cal.VoltageBaseData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData.CalDot;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BaseTest {

	@Test
	public void tt1() {
		System.out.println(Arrays.asList(CalMode.values()));
	}

	// public static void main(String[] args) {
	// threadPoolTest();
	// }

	@Test
	public void threadPoolTest() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1000, 1000, 60, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>());

		// new Thread(new Runnable() {
		// public void run() {
		// while (true) {
		// System.out.println(executor.getCorePoolSize());
		// System.out.println(executor.getActiveCount());
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// }).start();
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		for (int i = 0; i < 50; i++) {
			final int id = i;
			executor.execute(new Runnable() {
				public void run() {
					System.out.println("start +++++++++" + id);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("end ----------" + id);
				}
			});
		}

	}

	@Test
	public void ThreadPoolTest() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						System.out.println("thread - " + 9);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	@Test
	public void lamdaTest() {

		List<CalDot> list = new ArrayList<>();
		for (int i = 1; i < 5; i++) {
			for (int j = 0; j < i + 3; j++) {
				CalDot dot = new CalDot();
				dot.mode = CalMode.values()[i];
				dot.adc = 100 * (j + 1);
				list.add(dot);
			}
		}

		Map<CalMode, List<CalDot>> map = new HashMap<>();
		map.put(CalMode.CC, list.stream().filter(x -> x.mode == CalMode.CC).collect(Collectors.toList()));
		map.put(CalMode.CV, list.stream().filter(x -> x.mode == CalMode.CV).collect(Collectors.toList()));
		map.put(CalMode.DC, list.stream().filter(x -> x.mode == CalMode.DC).collect(Collectors.toList()));
		map.put(CalMode.CV2, list.stream().filter(x -> x.mode == CalMode.CV2).collect(Collectors.toList()));

		for (CalMode key : map.keySet()) {
			if (!map.containsKey(key)) {
				continue;
			}
			map.get(key).stream().forEach(x -> System.out.println(x.mode + "," + x.adc));
		}
	}

	@Test
	public void test11() {
		List<Integer> list = new CopyOnWriteArrayList<>();
		for (int i = 0; i < 5; i++) {
			list.add(i);
		}
		try {
			System.out.println(list.stream().filter(x -> x == 10).count());
			System.out.println(list.stream().filter(x -> x == 10).findFirst());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Test
	public void test2() {
		System.out.println(-1 >> 1);
		System.out.println(-1 >>> 1);
	}

	@Test
	public void testReserve() {
		// 初始化协议参数
		Data.setReverseDriverChnIndex(true);// 通道反序，针对校准板
		// 通道数
		Data.setDriverChnCount(16);

		VoltageBaseData data = new VoltageBaseData();
		data.setDriverIndex(0);
		for (int i = 0; i < 16; i++) {
			data.setChnIndex(i);
			try {
				System.out.println(ProtocolUtil.printList(Entity.encode(new ConfigDecorator(data))));
			} catch (Exception ex) {

			}
		}
	}
	
	@Test
	public void testFormat() {
		System.out.println(String.format("%0"+5+"d", 2,5));
	}

}
