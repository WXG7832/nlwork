package com.nltecklib.protocol.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nltecklib.protocol.lab.Data;

/**
* @author  wavy_zheng
* @version 创建时间：2021年8月6日 下午10:45:58
* 实验室保护类适配器
*/
public class LabProtectionDataAdapter implements JsonSerializer<Data>, JsonDeserializer<Data> {

	@Override
	public Data deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            // 指定包名+类名
            String thePackage = "com.nltecklib.protocol.lab.main.";
            return context.deserialize(element, Class.forName(thePackage + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
		
		
	}

	@Override
	public JsonElement serialize(Data src, Type typeOfSrc, JsonSerializationContext context) {
		
		JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));
		
		return result;
		
		
	}

}
