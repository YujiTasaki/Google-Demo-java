package jp.co.kke.Lockstatedemo.test;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.kke.Lockstatedemo.bean.lock.LockReqAccessPersonsInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResAccessPersonsInfo;
import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.util.LockApiUtil;

public class TestCreateUsers {

	public static void main(String[] args) throws IOException, MsgException {
		String access_token = "20d46ace29fccf2e33b89517b7324cf1c5653a7086a727c4e658659129fc3337";
		LockReqAccessPersonsInfo info = new LockReqAccessPersonsInfo();
		info.setType("access_guest");
		info.getAttributes().put("name", "KKEユーザー");
		info.getAttributes().put("email", "yumiko-tsunai@kke.co.jp");
		info.getAttributes().put("pin", "789012094");
		info.getAttributes().put("starts_at", "2020-01-02T16:04:00");
		info.getAttributes().put("ends_at", "2020-01-02T16:05:00");
		String json = LockApiUtil.createUsersJson(access_token, info);
		System.out.println("res:\n" + json);

		ObjectMapper mapper = new ObjectMapper();
		LockResAccessPersonsInfo test = mapper.readValue(json, LockResAccessPersonsInfo.class);
		System.out.println(test);
	}

}
