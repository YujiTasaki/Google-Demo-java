package jp.co.kke.Lockstatedemo.svltmsg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.bean.api.ResponseInfo;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class GetCalendarList  extends AbstractMngMessage{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SetGoogleAuth.class);

	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String calendarId = SysParamUtil.getResourceString("GOOGLE_CHECK_CALENDAR_ID");
//		GoogleResCalendarEventsListInfo res = this.getServlet().getMngGoogleApi().getCalendarEventList(calendarId);

		//pullされたら、チェック処理をを呼び出す（今は、pullを仕掛けていない）
		//getCalendarリストボタンを押したときの処理
		//this.getServlet().getMngSchedule().doCheck();

//		//resの中から必要な値を取ってくる
//		for(int i=0; i<res.getItems().size(); i++) {
//
//			GoogleResCalendarEventInfo items = res.getItems().get(i);
//
//			//イベントID
//			String eventId = items.getId();
//
//			//イベント情報
//			List<String> infoList = new ArrayList<String>();
//			String status = items.getStatus();
//			String startAt = items.getStart().getDateTime();
//			if(startAt == null)
//			{
//				startAt = items.getStart().getDate();
//			}
//			startAt = startAt.substring(0, 19);
//
//			String endAt = items.getEnd().getDateTime();
//			if(endAt == null)
//			{
//				endAt = items.getEnd().getDate();
//			}
//			endAt = endAt.substring(0, 19);
//			infoList.add(status);
//			infoList.add(startAt);
//			infoList.add(endAt);
//
//			//参加者メールリスト
//			List<String> attendEmailList = new ArrayList<String>();
//			String email = items.getCreator().getEmail();
//			attendEmailList.add(email);
//			List<GoogleResCalendarEventAttendeeInfo> attendees = items.getAttendees();
//			if(attendees != null)
//			{
//				//attendeesの1番目は登録者、最後はアカウントユーザーのため
//				for(int j=1; j<attendees.size()-1; j++){
//					String attendEmail = attendees.get(j).getEmail();
//					attendEmailList.add(attendEmail);
//				}
//			}
//
//			List<List<String>> valueList = new ArrayList<List<String>>();
//			valueList.add(infoList);
//			valueList.add(attendEmailList);
//
//			eventMap.put(eventId,valueList);
//		}
//		ServletUtil.returnJson(response, res);
//
//		logger.info("イベント情報");
//		logger.info(eventMap);
	}




	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_NG);
		responseInfo.setMsg(e.getMessage());
		ServletUtil.returnJson(response, responseInfo, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	/**
	 * 名前を取得
	 * @param ie ユーザーのＩＤ
	 * @return ユーザー名
	 */
	public String getName(int ie)
	{
		return "a";
	}



}