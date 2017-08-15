package jp.co.kke.Lockstatedemo.svltmsg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.bean.api.ResponseInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventsListInfo;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;
import jp.co.kke.Lockstatedemo.util.SysParamUtil;

public class GetCalendarList  extends AbstractMngMessage{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SetGoogleAuth.class);

	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String calendarId = SysParamUtil.getResourceString("GOOGLE_CHECK_CALENDAR_ID");
		GoogleResCalendarEventsListInfo res = this.getServlet().getMngGoogleApi().getCalendarEventList(calendarId);

		//resの中から必要な値を取ってくる
		for(int i=0; i<res.getItems().size(); i++) {

			GoogleResCalendarEventInfo items = res.getItems().get(i);

			String startAt = items.getStart().getDateTime();
			String endAt = items.getEnd().getDate();

			logger.info(startAt + " " + endAt);


		}





		ServletUtil.returnJson(response, res);
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_NG);
		responseInfo.setMsg(e.getMessage());
		ServletUtil.returnJson(response, responseInfo, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
}