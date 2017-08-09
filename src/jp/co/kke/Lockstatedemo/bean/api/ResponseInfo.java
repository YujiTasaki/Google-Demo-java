package jp.co.kke.Lockstatedemo.bean.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseInfo {
	@JsonIgnore
	public static final String S_State_OK = "ok";
	@JsonIgnore
	public static final String S_State_NG = "ng";

	private String state = S_State_OK;
	private String msg ="";


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
