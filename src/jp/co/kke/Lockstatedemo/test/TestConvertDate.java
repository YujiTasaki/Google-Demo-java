package jp.co.kke.Lockstatedemo.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestConvertDate {

	public static void main(String[] args) throws ParseException {

		//開始時刻
		String startAt = "2017-08-18T06:00:00+09:00";
		//+09:00
		//DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		//java.util.Date startAtDate = df.parse(startAt);

		//System.out.println(startAtDate);

		//Calendar cal = Calendar.getInstance();
		//cal.setTime(startAtDate);

		//Date date = new Date(startAtDate + 1000 * 60 * 60 * 3);

		Date dt = new Date();
		System.out.println(dt);

		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.getTime().toString());

		DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		System.out.println(df.parse(startAt));




	}

}
