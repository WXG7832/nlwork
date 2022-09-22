package com.nltecklib.protocol.atlmes;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MesFactory {

	static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static String encode(Root root) {
		// Gson gson = new GsonBuilder().create();
		return gson.toJson(root);
	}

	private static Pattern pattern = Pattern.compile(".*\\\"FunctionID\\\": *\\\"(A\\d*)\\\".*");

	// public static void main(String[] args) {
	//
	// String a = "B3\",\"FunctionID\":\"A011\",\"P";
	// System.out.println(a);
	// Matcher matcher = Pattern.compile(pattern).matcher(a);
	// if (matcher.matches()) {
	// System.out.println(matcher.group(1));
	// }
	// }

	public static Root decode(String jsonStr) throws Exception {

		// Gson gson = new GsonBuilder().create();

		Matcher matcher = pattern.matcher(jsonStr);

		if (matcher.matches()) {

			String functionID = matcher.group(1);

			// System.out.println("functionID = " + functionID);

			Type type = Class.forName("com.nltecklib.protocol.atlmes.mes." + functionID);

			return gson.fromJson(jsonStr, type);

			// return Double.parseDouble(matcher.group(1)) * Math.pow(10,
			// Double.parseDouble(matcher.group(2)));
		}
		throw new Exception("无法解析此Json：" + jsonStr);
	}
}
