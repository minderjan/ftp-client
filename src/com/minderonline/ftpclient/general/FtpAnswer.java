package com.minderonline.ftpclient.general;

public class FtpAnswer {
	Boolean IsConnected;
	Boolean IsLogin;

	int LoginCode;
	String LoginError;

	// Instantiate an Filled object
	public FtpAnswer(Boolean IsConnected, Boolean IsLogin, int LoginCode,
			String LoginError) {
		this.IsConnected = IsConnected;
		this.IsLogin = IsLogin;
		this.LoginCode = LoginCode;
		this.LoginError = LoginError;

	}

	// Instantiate an object
	public FtpAnswer() {
		this.IsConnected = false;
		this.IsLogin = false;
		this.LoginCode = 0;
		this.LoginError = null;

	}

	// Getter Methods
	public Boolean getIsConnected() {
		return this.IsConnected;
	}

	public Boolean getIsLogin() {
		return this.IsLogin;
	}

	public int getLoginCode() {
		return this.LoginCode;
	}

	public String getLoginError() {
		return this.LoginError;
	}

	// Setter Methods

	public void setIsConnected(Boolean IsConnected) {
		this.IsConnected = IsConnected;

	}

	public void setIsLogin(Boolean IsLogin) {
		this.IsLogin = IsLogin;

	}

	public void setLoginCode(int LoginCode) {

		this.LoginCode = LoginCode;
	}

	public void setLoginError(String LoginError) {

		this.LoginError = LoginError;
	}
}
