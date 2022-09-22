package com.nltecklib.protocol.power.calBox.calSoft;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.power.calBox.calSoft.model.JsonPack;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * json传输
 * 
 * @author Administrator
 */
public class JSON_DATA extends Data implements Configable, Queryable, Responsable {

    private Code funcCode = CalSoftCode.PUSH; // JsonData类型
    private JsonPack jsonPack;
    private int packIndex = 1;
    private int packCount = 1;

    private String jsonStr;

    @Override
    public void encode() {

	data.add((byte) ((CalSoftCode) funcCode).ordinal());
	data.add((byte) packCount);
	data.add((byte) packIndex);

	String encodeStr = null;
	if (this.jsonStr == null) {
	    Gson gson = new Gson();
	    encodeStr = gson.toJson(jsonPack);
	    System.out.println(encodeStr);
	} else {

	    encodeStr = jsonStr;
	}

	try {
	    byte[] arr = encodeStr.getBytes("utf-8");

	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (arr.length), 2, true)));

	    for (int i = 0; i < arr.length; i++) {

		data.add(arr[i]);
	    }
	} catch (UnsupportedEncodingException e) {
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

	Gson gson = new Gson();
	return gson.toJson(jsonPack);
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
	if (code > CalSoftCode.values().length - 1) {

	    throw new RuntimeException("error json transport type code :" + code);
	}
	funcCode = CalSoftCode.values()[code];

	packCount = ProtocolUtil.getUnsignedByte(data.get(index++));
	packIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	int jsonLength = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;

	byte[] jsonArr = new byte[jsonLength];
	for (int j = index; j < index + jsonLength; j++) {

	    jsonArr[j - index] = data.get(j);
	}

	try {
	    jsonStr = new String(jsonArr, "utf-8");

	    if (!Data.isUseStringCompress()) {
		if (this.packIndex == 1 && this.packCount == 1) {
		    // 没有分包直接解析

		    Gson gson = new Gson();
		    jsonPack = gson.fromJson(jsonStr, new TypeToken<JsonPack>() {
		    }.getType());
		}
	    }

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
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

	Gson gson = new Gson();
	return gson.fromJson(jsonStr, new TypeToken<List<JsonPack>>() {
	}.getType());
    }

    @Override
    public Code getCode() {
	return CalSoftCode.JSON_DATA;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    @Override
    public boolean supportDriver() {
	// TODO Auto-generated method stub
	return true;
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

    public Code getFuncCode() {
	return funcCode;
    }

    public void setFuncCode(Code funcCode) {
	this.funcCode = funcCode;
    }

    public JsonPack getJsonPack() {
	return jsonPack;
    }

    public void setJsonPack(JsonPack jsonPack) {
	this.jsonPack = jsonPack;
    }

}
