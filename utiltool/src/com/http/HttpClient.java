package com.http;

import java.io.IOException;

import com.sun.media.jfxmedia.Media;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sun.rmi.runtime.Log;

public class HttpClient {
	public static OkHttpClient client = new OkHttpClient();
	public static void main(String[] args) {
		try {
			

			
			String url = "http://localhost:8880/demo";
			postJson(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void postJson(String url) {
		
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
		RequestBody requestBody = RequestBody.create(mediaType,"{\r\n" + 
				"    \"CmdID\":6426,\r\n" + 
				"    \"EID\":\"EQUIP01\",\r\n" + 
				"    \"StepName\":\"FCT\",\r\n" + 
				"    \"OperatorId\":\"600001\",\r\n" + 
				"    \"BatchNo\":\"WO2106010001\"\r\n" + 
				"}");
		Request request = new Request.Builder()
		    .url(url)
		    .post(requestBody)
		    .build();
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
		    @Override
		    public void onFailure(Call call, IOException e) {
		        e.printStackTrace();
		    }
		    @Override
		    public void onResponse(Call call, Response response) throws IOException {
		        System.out.println("onResponse: "+response.body().string());
		    }
		});

	}
}
