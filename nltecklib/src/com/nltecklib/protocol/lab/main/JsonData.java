package com.nltecklib.protocol.lab.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.JsonContentType;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.LabProtectionDataAdapter;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * json传输
 * 
 * @author Administrator
 */
public class JsonData extends Data implements Configable, Queryable, Responsable {

	private JsonContentType jsonType = JsonContentType.PROCEDURE; // 传输类型
	private List<JsonPack> jsonPacks = new ArrayList<JsonPack>(); // json内容
	private int packIndex = 1;
	private int packCount = 1;

	public static final String PROCEDURE = "procedure";
	public static final String STAMP = "stamp";
	public static final String PROTECTION = "protection";
	public static final String REFERENCE = "reference";
	public static final String TESTNAME = "testname";

	private String jsonStr;

	@Override
	public void encode() {

		data.add((byte) jsonType.ordinal());
		data.add((byte) packCount);
		data.add((byte) packIndex);

		String encodeStr = null;
		if (this.jsonStr == null) {
			Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new LabProtectionDataAdapter()).create();
			encodeStr = gson.toJson(jsonPacks);
			System.out.println(encodeStr);
		} else {

			encodeStr = jsonStr;
		}

		try {
			
			byte[] arr = encodeStr.getBytes("ISO-8859-1");

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (arr.length), 2, true)));

			for (int i = 0; i < arr.length; i++) {

				data.add(arr[i]);
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 获取编码json字符串
	 * 
	 * @author wavy_zheng 2021年9月3日
	 * @return
	 */
	public String getEncodeJsonString() {

		Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new LabProtectionDataAdapter()).create();
		return gson.toJson(jsonPacks);
	}

	/**
	 * 直接下发将要进行编码的json字符串，此种情况将不再对jsonPacks进行json编码转换
	 * 
	 * @author wavy_zheng 2021年9月3日
	 * @param jsonStr
	 */
	public void setJsonString(String jsonStr) {

		this.jsonStr = jsonStr;
	}

	public String getJsonString() {

		return this.jsonStr;
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > JsonContentType.values().length - 1) {

			throw new RuntimeException("error json transport type code :" + code);
		}
		jsonType = JsonContentType.values()[code];

		packCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		packIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		int jsonLength = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		byte[] jsonArr = new byte[jsonLength];
		for (int j = index; j < index + jsonLength; j++) {

			jsonArr[j - index] = data.get(j);
		}

		try {
			jsonStr = new String(jsonArr, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		if (!Data.isUseStringCompress()) {
			if (this.packIndex == 1 && this.packCount == 1) {
				// 没有分包直接解析
				Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new LabProtectionDataAdapter()).create();
				jsonPacks = gson.fromJson(jsonStr, new TypeToken<List<JsonPack>>() {
				}.getType());
			}
		}

		index += jsonLength;
	}

	/**
	 * 将字符串解析成json对象
	 * 
	 * @author wavy_zheng 2021年9月3日
	 * @param jsonStr
	 * @return
	 */
	public static List<JsonPack> decodeJsonString(String jsonStr) {

		Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new LabProtectionDataAdapter()).create();
		return gson.fromJson(jsonStr, new TypeToken<List<JsonPack>>() {
		}.getType());
	}

	@Override
	public Code getCode() {
		return MainCode.JsonCode;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public boolean supportMain() {
		return true;
	}

	public JsonContentType getJsonType() {
		return jsonType;
	}

	public void setJsonType(JsonContentType jsonType) {
		this.jsonType = jsonType;
	}

	public List<JsonPack> getJsonPacks() {
		return jsonPacks;
	}

	public void setJsonPacks(List<JsonPack> jsonPacks) {
		this.jsonPacks = jsonPacks;
	}

	public int getPackIndex() {
		return packIndex;
	}

	public void setPackIndex(int packIndex) {
		this.packIndex = packIndex;
	}

	public int getPackCount() {
		return packCount;
	}

	public void setPackCount(int packCount) {
		this.packCount = packCount;
	}

	@Override
	public String toString() {
		return "JsonData [jsonType=" + jsonType + ", jsonPacks=" + jsonPacks + ", packIndex=" + packIndex
				+ ", packCount=" + packCount + ", jsonStr=" + jsonStr + "]";
	}

}
