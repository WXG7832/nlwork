package nltecklib;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.lab.ConfigDecorator;
import com.nltecklib.protocol.lab.Decorator;
import com.nltecklib.protocol.lab.Entity;
import com.nltecklib.protocol.lab.ResponseDecorator;
import com.nltecklib.protocol.lab.main.CCProtectData;
import com.nltecklib.protocol.lab.main.ChannelOperateData;
import com.nltecklib.protocol.lab.main.JsonData;
import com.nltecklib.protocol.lab.main.JsonPack;
import com.nltecklib.protocol.lab.main.MainEnvironment.JsonContentType;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.lab.main.ProcedureData;
import com.nltecklib.protocol.lab.main.ProcedureData.Step;
import com.nltecklib.protocol.lab.main.ProtectionParamPack;
import com.nltecklib.utils.BaseUtil;
import com.nltecklib.utils.LogUtil;

public class LabTestor {

	@Test
	public void testFormat() {

		System.out.println(BaseUtil.formatSeconds(90));

	}

	@Test
	public void d1() {
		System.out.println(new LabTestor().getClass().getSimpleName());
	}
	

	@Test
	public void decode() {

		try {
			String data = "18 20 17 02 01 31 01 01 14 00 01 09 27 C0 02 F2 59 AF 89 FB EF 61 73 CE 01 7E 08 5D 00 58 83 9A 01 94 CB BA 00 3B 24 93 1E 01 60 B6 03 73 4E 09 27 72 01 60 B7 03 73 51 09 27 7A 01 60 B8 03 73 53 09 27 7F 01 60 B9 03 73 56 09 27 87 01 60 B7 03 73 51 09 27 7A 01 60 B3 03 73 47 09 27 5F 01 60 B7 03 73 51 09 27 7A 01 60 BA 03 73 58 09 27 8C 01 60 B9 03 73 56 09 27 87 01 60 BA 03 73 58 09 27 8C 01 60 B7 03 73 51 09 27 7A 01 60 B9 03 73 56 09 27 87 01 60 B8 03 73 53 09 27 7F 01 60 B7 03 73 51 09 27 7A 01 60 B5 03 73 4C 09 27 6C 01 60 B7 03 73 51 09 27 7A 01 60 B6 03 73 4E 09 27 72 01 60 B9 03 73 56 09 27 87 01 60 B8 03 73 53 09 27 7F 01 60 BA 03 73 58 09 27 8C 01 60 B8 03 73 53 09 27 7F 01 60 BA 03 73 58 09 27 8C 01 60 B9 03 73 56 09 27 87 01 60 BB 03 73 5B 09 27 94 01 60 B8 03 73 53 09 27 7F 01 60 B9 03 73 56 09 27 87 01 60 BB 03 73 5B 09 27 94 01 60 BC 03 73 5D 09 27 99 01 60 BB 03 73 5B 09 27 94 01 60 B9 03 73 56 09 27 87 51 4F 16";
			data = data.replace(" ", "");

			List<Byte> array = new ArrayList<>();
			for (int i = 0; i < data.length() / 2; i++) {
				array.add((byte) Integer.parseInt(data.substring(2 * i, 2 * i + 2), 16));
			}

			System.out.println(com.nltecklib.protocol.li.Entity.decode(array).getDestData());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void decodeLab() {

		try {
			String data = "68 04 01 02 00 08 01 01 00 01 00 00 00 00 5A B1 86";
			data = data.replace(" ", "");

			List<Byte> array = new ArrayList<>();
			for (int i = 0; i < data.length() / 2; i++) {
				array.add((byte) Integer.parseInt(data.substring(2 * i, 2 * i + 2), 16));
			}

			System.out.println(com.nltecklib.protocol.lab.Entity.decode(array).getDestData());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJsonPack() {

		JsonData json = new JsonData();

		json.setJsonType(JsonContentType.PROCEDURE);
		List<JsonPack> jsonPacks = new ArrayList<>();

		JsonPack pack = new JsonPack();

		// 流程
		ProcedureData procedure = new ProcedureData();
		procedure.setName("procedure");
		procedure.setStepCount(500);
		List<Step> steps = new ArrayList<Step>();

		Step s = new Step();
		s.stepIndex = 1;
		s.loopIndex = 2;
		s.workMode = WorkMode.CCC;
		steps.add(s);
		procedure.setSteps(steps);

		// 保护
		ProtectionParamPack protection = new ProtectionParamPack();

		CCProtectData cc = new CCProtectData();
		cc.setCapacityUpper(1000);
		cc.setCurrOffsetVal(500);
		protection.setCc(cc);

		pack.setProcedure(procedure);

		// pack.setProtection(protection);

		Gson gson = new GsonBuilder().create();
		String jsonStr = gson.toJson(jsonPacks);
		System.out.println(jsonStr);
	}

	public class Person {
		public int userid;
		public String username;
		public String usersex;
		public String banji;
		public int phone;

		@Override
		public String toString() {
			return "Person [userid=" + userid + ", username=" + username + ", usersex=" + usersex + ", banji=" + banji
					+ ", phone=" + phone + "]";
		}
	}

	@Test
	public void testJsonToList() {

		String jsonList = "[{'userid':'1001','username':'张三','usersex':'男','banji':'计算机1班','phone':'1213123'},"
				+ "{'userid':'1002','username':'李四','usersex':'男','banji':'计算机1班','phone':'232323'},"
				+ "{'userid':'1003','username':'王五','usersex':'男','banji':'计算机1班','phone':'432423423'}]";
		Gson gson1 = new Gson();

		List<Person> list = gson1.fromJson(jsonList, new TypeToken<List<Person>>() {
		}.getType());
		for (Person person1 : list) {
			System.out.println(person1.toString());
		}
	}

	@Test
	public void testDouble() {

		double val = 0;
		System.out.println(new BigDecimal("0.25").multiply(new BigDecimal("12.0")));

		System.out.println(1 + 20.2 + 300.03);

//		for(int n = 0 ; n < 100 ; n++) {
//			System.out.println(new BigDecimal("0.25").divide(new BigDecimal("12"),10,RoundingMode.HALF_UP).doubleValue());
//			System.out.println(new BigDecimal("0.25").multiply(new BigDecimal("12.0")));
//			System.out.println(0.25 * 12.0);
//		}
		ChannelOperateData cData = new ChannelOperateData();
		cData.setChnIndex(1);
		cData.setFlag((byte) 1);
		Decorator data = new ConfigDecorator(cData);
		data.encode();

		data = new ResponseDecorator(data.getDestData(), false);

		data.encode();

		NlteckIOPackage pack = (NlteckIOPackage) data;

		byte[] encodeData = new Entity().encode(pack);

		String log = "-->" + LogUtil.printArray(encodeData);
		System.out.println(log);
	}

	

}
