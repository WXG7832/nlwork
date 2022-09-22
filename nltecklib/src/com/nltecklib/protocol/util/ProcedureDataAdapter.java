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
import com.nltecklib.protocol.li.Data;




/**
 * 生产用的json适配器，防止抽象Data类转换json时出现异常
 * @author 董浩
 *
 */
public class ProcedureDataAdapter implements JsonSerializer<Data>, JsonDeserializer<Data>{

	@Override
	public Data deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		// TODO Auto-generated method stub
		
		JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            // 指定包名+类名
            String thePackage = "com.nltecklib.protocol.li.main.";
            return context.deserialize(element, Class.forName(thePackage + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
		
		
	}

	@Override
	public JsonElement serialize(Data src, Type typeOfSrc, JsonSerializationContext context) {
		// TODO Auto-generated method stub
		
		JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));
		
		return result;
		
		
	}

}
