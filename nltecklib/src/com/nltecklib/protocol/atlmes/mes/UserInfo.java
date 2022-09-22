package com.nltecklib.protocol.atlmes.mes;

public class UserInfo {
	public String UserID;
	public int UserLevel;
	public String UserName;

	public UserInfo() {

	}

	public UserInfo(String userID, int userLevel, String userName) {
		this.UserID = userID;
		this.UserLevel = userLevel;
		this.UserName = userName;
	}
}
