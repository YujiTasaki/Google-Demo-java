package jp.co.kke.Lockstatedemo.test;

import java.io.IOException;
import java.util.UUID;

import javax.mail.MessagingException;

import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.util.GoogleApiUtil;

public class TestCreateCalendarChannel {
	public static void main(String[] args) throws MessagingException, IOException, MsgException {
		String access_token = "ya29.GluTBKUB-I5MqSlnZ8svDqKxcm_KkVLwoy9UATWxyFptqaS3Zpkgop-tDgbqUYUj0mJZZnyxD0cX_zQdOr1dh1rM3iTzB0HmARxZrnEIQKKAICMXaeq0l4ypIGMi";
		String calendarId = "miyake@kke.co.jp";
		String uuid = UUID.randomUUID().toString();
		String json = GoogleApiUtil.createCalendarChannelJson(uuid, calendarId, access_token);
		System.out.println("res:\n" + json);
	}
}
