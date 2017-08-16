package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventsListInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResInfoList;
import jp.co.kke.Lockstatedemo.bean.lock.LockResOAuthInfo;
import jp.co.kke.Lockstatedemo.util.GoogleApiUtil;
import jp.co.kke.Lockstatedemo.util.LockApiUtil;
import jp.co.kke.Lockstatedemo.util.SysParamUtil;
/**
 * LockstatesAPI管理クラス
 * @author KKE
 */
public class MngLockApi {

	private static Logger logger = Logger.getLogger(MngLockApi.class);

	private String accessToken = null;
	private String refreshToken = null;
	private long updateToken = 0;

	private final long limitTokenMsec = SysParamUtil.getResourceLong("LOCK_API_ACCESS_TOKEN_LIMIT_SEC") * 1000;
	private final long checkTokenMsec = SysParamUtil.getResourceLong("LOCK_API_ACCESS_TOKEN_CHECK_SEC") * 1000;

	private Timer checkTokenTimer = null;

	/**
	 * アクセストーク取得済?
	 * @return
	 */
	public boolean isOkAccessToken() {
		boolean res = false;
		if(accessToken != null) {
			res = false;
		}
		return res;
	}

	public String requestAccessToken(String authorizationCode) throws IOException, MsgException{
		if(this.checkTokenTimer != null){
			this.checkTokenTimer.cancel();
		}
		this.checkTokenTimer = new Timer();
		this.accessToken = null;
		this.refreshToken = null;
		this.updateToken = 0;
		LockResOAuthInfo oAuthInfo = LockApiUtil.initOAuthToken(authorizationCode);
		this.accessToken = oAuthInfo.getAccess_token();
		this.refreshToken = oAuthInfo.getRefresh_token();
		this.updateToken = Calendar.getInstance().getTimeInMillis();
		logger.info(String.format("init accessToken:%s refreshToken:%s", this.accessToken, this.refreshToken));
		this.checkTokenTimer.schedule(new OAuthTokeCheckTask(), 0, this.checkTokenMsec);
		return this.accessToken;
	}

	private void refreshAccessToken(){
		try {
			LockResOAuthInfo oAuthInfo = LockApiUtil.refreshOAuthToken(this.refreshToken);
			this.accessToken = oAuthInfo.getAccess_token();
			this.refreshToken = oAuthInfo.getRefresh_token();
			logger.info(String.format("refresh accessToken:%s refreshToken:%s", this.accessToken, this.refreshToken));
			this.updateToken = Calendar.getInstance().getTimeInMillis();
		} catch (Exception e) {
			logger.error("can't refresh AccessToken",e);
			this.accessToken = null;
			this.refreshToken = null;
			this.updateToken = 0;
		}
	}


	public LockResInfoList getAllDevices() throws Exception{
		LockResInfoList res = null;
		if(this.accessToken == null){
			throw new MsgException("未認証");
		}
		res = LockApiUtil.getAllDevices(this.accessToken);
		return res;
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
}
