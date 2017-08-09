package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventsListInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCreateChannelInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResGmailSendInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResOAuthInfo;
import jp.co.kke.Lockstatedemo.util.GoogleApiUtil;
import jp.co.kke.Lockstatedemo.util.SysParamUtil;

public class MngGoogleApi {
	private static Logger logger = Logger.getLogger(MngGoogleApi.class);

	private String accessToken = null;
	private String refreshToken = null;
	private long updateToken = 0;

	private final long limitTokenMsec = SysParamUtil.getResourceLong("GOOGLE_API_ACCESS_TOKEN_LIMIT_SEC") * 1000;
	private final long checkTokenMsec = SysParamUtil.getResourceLong("GOOGLE_API_ACCESS_TOKEN_CHECK_SEC") * 1000;

	private Timer checkTokenTimer = null;

    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();
    boolean isRefresh = false;

	public String requestAccessToken(String authorizationCode) throws IOException, MsgException{
		if(this.checkTokenTimer != null){
			this.checkTokenTimer.cancel();
		}
		this.checkTokenTimer = new Timer();
		this.accessToken = null;
		this.refreshToken = null;
		this.updateToken = 0;
		GoogleResOAuthInfo oAuthInfo = GoogleApiUtil.requestOAuthToken(authorizationCode);
		this.accessToken = oAuthInfo.getAccess_token();
		this.refreshToken = oAuthInfo.getRefresh_token();
		this.updateToken = Calendar.getInstance().getTimeInMillis();
		logger.info(String.format("init accessToken:%s refreshToken:%s", this.accessToken, this.refreshToken));
		this.checkTokenTimer.schedule(new OAuthTokeCheckTask(), 0, checkTokenMsec);
		return this.accessToken;
	}

	private void refreshAccessToken(){
		try {
			if(this.accessToken == null){
				throw new MsgException("未認証");
			}
			lock.lock();
			isRefresh = true;
			GoogleResOAuthInfo oAuthInfo = GoogleApiUtil.refreshOAuthToken(this.refreshToken);
			this.accessToken = oAuthInfo.getAccess_token();
			//this.refreshToken = oAuthInfo.getRefresh_token();//リフレッシュトークンの更新はなし
			logger.info(String.format("refresh accessToken:%s refreshToken:%s", this.accessToken, this.refreshToken));
			this.updateToken = Calendar.getInstance().getTimeInMillis();
			condition.signalAll();// Signalを送ることで対応するConditionでawaitしていた処理が再開する。
		} catch (Exception e) {
			logger.error("can't refresh AccessToken", e);
			this.accessToken = null;
			this.refreshToken = null;
			this.updateToken = 0;
		} finally{
			isRefresh = false;
			lock.unlock();
		}
	}

	private class OAuthTokeCheckTask extends TimerTask {
		@Override
		public void run() {
			if((accessToken == null)|(refreshToken == null)){
				return;
			}
			long curMsec = Calendar.getInstance().getTimeInMillis();
			if(curMsec < (updateToken + limitTokenMsec)){
				return;
			}
			refreshAccessToken();
		}
	}

	public GoogleResCreateChannelInfo createCalendarChannel(String uuid, String calendarId) throws Exception{
		GoogleResCreateChannelInfo res = null;
		if(this.accessToken == null){
			throw new MsgException("未認証");
		}
		lock.lock();
		try{
			while(this.isRefresh){
				condition.await();
			}
			res = GoogleApiUtil.createCalendarChannel(uuid, calendarId, this.accessToken);
		} finally{
			lock.unlock();
		}
		return res;
	}

	public GoogleResCalendarEventsListInfo getCalendarEventList(String calendarId) throws Exception{
		GoogleResCalendarEventsListInfo res = null;
		if(this.accessToken == null){
			throw new MsgException("未認証");
		}
		lock.lock();
		try{
			while(this.isRefresh){
				condition.await();
			}
			res = GoogleApiUtil.getCalendarEventList(calendarId, this.accessToken);
		} finally{
			lock.unlock();
		}
		return res;
	}

	public GoogleResGmailSendInfo sendMail(MimeMessage mimeMessage) throws Exception{
		GoogleResGmailSendInfo res = null;
		if(this.accessToken == null){
			throw new MsgException("未認証");
		}
		lock.lock();
		try{
			while(this.isRefresh){
				condition.await();
			}
			res = GoogleApiUtil.sendMail(mimeMessage, this.accessToken);
		} finally{
			lock.unlock();
		}
		return res;
	}

}
