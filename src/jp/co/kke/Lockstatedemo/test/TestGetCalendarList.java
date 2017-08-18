package jp.co.kke.Lockstatedemo.test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import javax.mail.MessagingException;

import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventAttendeeInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventsListInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResEventDatetimeInfo;
import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.util.GoogleApiUtil;

public class TestGetCalendarList {
	public static void main(String[] args) throws MessagingException, IOException, MsgException {
		String access_token = "ya29.GluUBPUO1SB-odd5SHRfVxXOE3vLAs3iLRkf-m0NJt7BPxr2eY_sZXX1lEv959ejwELZH2ZJLQo63fTtP8XqPvEbSuKUNM7swVZAAJXXd0zsYYvzPDyXrGGzpDKa";
		//String calendarId = "miyake@kke.co.jp";
		String calendarId = "kke.co.jp_2d3337313238383832353636@resource.calendar.google.com";
		String json = GoogleApiUtil.getCalendarEventListJson(calendarId, null, access_token);
		System.out.println("res:\n" + json);
		GoogleResCalendarEventsListInfo res = GoogleApiUtil.getCalendarEventList(calendarId, null, access_token);
		System.out.println("res:\n" + res.toString());
		List<GoogleResCalendarEventInfo> items = res.getItems();
		if(items== null) {
			System.out.println("items== null");
			return;
		}
		for(GoogleResCalendarEventInfo item: items) {
			String id = item.getId();
			String summary = item.getSummary();

			OffsetDateTime createdOffsetDateTime = item.getCreatedOffsetDateTime();
			OffsetDateTime updatedOffsetDateTime = item.getUpdatedOffsetDateTime();

			GoogleResEventDatetimeInfo start = item.getStart();
			GoogleResEventDatetimeInfo end = item.getEnd();

			GoogleResCalendarEventAttendeeInfo creator = item.getCreator();
			GoogleResCalendarEventAttendeeInfo organizer = item.getOrganizer();

			System.out.println("--------");
			System.out.println(String.format("id:%s", id));
			System.out.println(String.format("summary:%s", summary));
			System.out.println(String.format("created:%s", createdOffsetDateTime.toLocalDateTime()));
			System.out.println(String.format("updated:%s", updatedOffsetDateTime.toLocalDateTime()));
			System.out.println(String.format("start:%s", start.getOffsetDateTime().toLocalDateTime()));
			System.out.println(String.format("end:%s", end.getOffsetDateTime().toLocalDateTime()));

			System.out.println(String.format("creator:%s", creator.getEmail()));
			System.out.println(String.format("organizer:%s", organizer.getEmail()));

			List<GoogleResCalendarEventAttendeeInfo> attendees = item.getAttendees();
			if(items == null) {
				break;
			}
			for(GoogleResCalendarEventAttendeeInfo attendee: attendees) {
				System.out.println(String.format("attendee:%s", attendee.getEmail()));
			}

		}
	}
}
