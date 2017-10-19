package jp.co.kke.Lockstatedemo.test;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import jp.co.kke.Lockstatedemo.util.SysParamUtil;

public class TestConvertDate {

	public static void main(String[] args) throws ParseException {


		String[] denialRakumoUsers = SysParamUtil.getResourceString("DENIAL_RAKUMO_USER").split(",");

		//開始時刻
		String startAt = "2017-08-18T10:15:00+09:00";

		//Date dt = new Date();
		//System.out.println(dt);

		//Calendar calendar = Calendar.getInstance();
		//System.out.println(calendar.getTime().toString());

		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

		//開始時刻10分前
		OffsetDateTime startAtMinus = OffsetDateTime.parse(startAt).minusSeconds(600);
		System.out.println(startAtMinus.format(formatter));

		//開始時刻10分後
		OffsetDateTime startAtPlus = OffsetDateTime.parse(startAt).plusSeconds(600);
		System.out.println(startAtPlus.format(formatter));


		OffsetDateTime updateMinDateTime2 = OffsetDateTime.now().minusSeconds(60);  //今から60秒前以降のアップデートを取ってくる
        System.out.println(updateMinDateTime2.format(formatter));

        String dateStr= "2017-08-18T14:43:47+09:00";

        OffsetDateTime dateTime =  OffsetDateTime.parse(dateStr,formatter);
        System.out.println(dateTime);


	}

}
