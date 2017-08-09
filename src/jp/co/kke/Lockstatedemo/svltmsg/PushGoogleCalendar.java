package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.bean.api.ResponseInfo;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class PushGoogleCalendar  extends AbstractMngMessage{
	//@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SetGoogleAuth.class);
//	{
//		 "kind": "api#channel",
//		 "id": "8a256c9d-29f6-4036-9ee3-8284ace4671c",
//		 "resourceId": "We5TwUBU5cUNcX4_jRCvInD0n1E",
//		 "resourceUri": "https://www.googleapis.com/calendar/v3/calendars/miyake@kke.co.jp/events?maxResults=250&alt=json",
//		 "expiration": "1501585453000"
//		}
	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
		for(String key: Collections.list(request.getHeaderNames())) {
			logger.info(String.format("%s=%s", key, request.getHeader(key)));
		}
		returnOk(request, response);
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_NG);
		responseInfo.setMsg(e.getMessage());
		ServletUtil.returnJson(response, responseInfo, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	public void returnOk(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_OK);
		ServletUtil.returnJson(response, responseInfo);
	}
}
