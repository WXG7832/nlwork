package com.nltecklib.protocol.lab.screen;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.Title;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月2日 下午4:15:51
* 液晶屏标题设定
*/
public class TitleData extends Data implements Configable, Queryable, Responsable {
   
	private Title  title = Title.V5A6;
	
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) title.getCode());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int code = ProtocolUtil.getUnsignedByte(data.get(0));
		if(code > Title.values().length - 1) {
			
			throw new RuntimeException("error title code: " + code);
		}
		
		this.title = Title.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.TitleCode;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "TitleData [title=" + title + "]";
	}
	
	

}
