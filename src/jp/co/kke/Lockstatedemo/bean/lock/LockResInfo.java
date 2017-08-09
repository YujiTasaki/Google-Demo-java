package jp.co.kke.Lockstatedemo.bean.lock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResInfo {
	private LockResDataInfo data;

	public LockResDataInfo getData() {
		return data;
	}

	public void setData(LockResDataInfo data) {
		this.data = data;
	}

}
