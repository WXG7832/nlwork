package nltecklib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.MBWorkform.MBDeviceBaseInfoData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCheckFlashWriteData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.LivePushData;
import com.nltecklib.protocol.li.PCWorkform.LogDebugPushData;
import com.nltecklib.protocol.li.PCWorkform.LogPushData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfTestInfoData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData.CalDot;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.li.main.UpgradeProgramData;
import com.nltecklib.protocol.util.ProtocolUtil;

public class LiTestor {

	@Test
	public void encode() {
		LivePushData data = new LivePushData();
		PushData pd = new PushData();
		data.appendPushData(pd);
		data.appendPushData(pd);
		data.appendPushData(pd);

		try {
			List<Byte> code = Entity.encode(new com.nltecklib.protocol.li.ResponseDecorator(data, true));

			System.out.println(ProtocolUtil.printList(code));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void encode3() {
		LogPushData data = new LogPushData();
		PushLog pl = new PushLog();
		pl.log = "中文";
		data.appendLog(pl);

		try {
			List<Byte> code = Entity.encode(new com.nltecklib.protocol.li.ResponseDecorator(data, true));
			data = (LogPushData) Entity.decode(code).getDestData();
			System.out.println(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void encode2() {
		LogDebugPushData data = new LogDebugPushData();
		data.setLog("中文");

		try {
			List<Byte> code = Entity.encode(new com.nltecklib.protocol.li.ResponseDecorator(data, true));
			data = (LogDebugPushData) Entity.decode(code).getDestData();
			System.out.println(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test1() {
		MBDeviceBaseInfoData data = new MBDeviceBaseInfoData();
		data.setDeviceMac("11:11:22:22:33:33");
		try {
			List<Byte> list = Entity.encode(new com.nltecklib.protocol.li.QueryDecorator(data));
			System.out.println(ProtocolUtil.printList(list));
			MBDeviceBaseInfoData d1 = (MBDeviceBaseInfoData) Entity.decode(list).getDestData();
			System.out.println(d1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
//	public void test2() {
//		MBLogicCheckFlashWriteData data = new MBLogicCheckFlashWriteData();
//		data.setUnitIndex(0);
//		data.setChnIndex(0);
//		List<CalDot> calDots = new ArrayList<>();
//		calDots.add(new CalDot());
//		calDots.get(0).adcCalculate = 100;
//		data.setPowerPosDots(calDots);
//		data.setPowerNegDots(calDots);
//		data.setBackupPosDots(calDots);
//		data.setBackupNegDots(calDots);
//		List<Byte> list;
//		try {
//			list = Entity.encode(new com.nltecklib.protocol.li.ConfigDecorator(data));
//			System.out.println(ProtocolUtil.printList(list));
//			System.out.println(Entity.decode(list));
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//	}

	@Test
	public void test3() throws Exception {
		UpgradeProgramData data = new UpgradeProgramData();
		data.setPath("/var/ftp/pub");
		data.setUpgradeType(UpgradeType.Check);
		System.out.println(data);
		List<Byte> list = Entity.encode(new com.nltecklib.protocol.li.ConfigDecorator(data));
		System.out.println(list);
		data = (UpgradeProgramData) Entity.decode(list).getDestData();
		System.out.println(data);

	}

	@Test
	public void tetpro() {
		System.out.println(new Date(0));
	}

	@Test
	public void testFlash() {
		try {
			Data.setProgramBResolution(6);
			Data.setAdcBResolution(6);
			MBLogicFlashWriteData data = new MBLogicFlashWriteData();
			List<CalDot> calDots = new ArrayList<>();
			CalDot dot = new CalDot();
			dot.mode = CalMode.DC;
			dot.pole = Pole.NORMAL;
			dot.programK = 1.2345678;
			dot.programB = 2.2345678;
			dot.adcK = 3.2345678;
			dot.adcB = 4.2345678;
			calDots.add(dot);
			data.setDots(calDots);

			List<Byte> list = Entity.encode(new ConfigDecorator(data));

			data = (MBLogicFlashWriteData) Entity.decode(list).getDestData();
			System.out.println(data);
			
			Logic2FlashWriteData data2=new Logic2FlashWriteData();
			data2.setDots(data.getDots());
			
			List<Byte> list2 = Entity.encode(new ConfigDecorator(data2));

			data2 = (Logic2FlashWriteData) Entity.decode(list2).getDestData();
			
			System.out.println(data2);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
