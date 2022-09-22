package com.nltecklib.protocol.atlmes;

import java.text.SimpleDateFormat;

public abstract class Root {

	protected static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	public abstract String getFuncID();
	public abstract String getSessionID();
	public abstract String getResult();

}
