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
/**
 * GoogleAPI管理クラス
 * @author KKE
 */
public class MngGoogleApi {
	/**
	 * ロガー
	 */
	private static Logger logger = Logger.getLogger(MngGoogleApi.class);
	/**
	 * アクセストークン
	 */
	private String accessToken = null;
	/**
	 * リフレッシュトークン
	 */
	private String refreshToken = null;
	/**
	 * トークン更新日時(ミリ秒)
	 */
	private long updateToken = 0;

	/**
	 * Google アクセストークン更新間隔(ミリ秒)
	 */
	private final long limitTokenMsec = SysParamUtil.getResourceLong("GOOGLE_API_ACCESS_TOKEN_LIMIT_SEC") * 1000;
	/**
	 * Google トークンチェック処理間隔(ミリ秒)
	 */
	private final long checkTokenMsec = SysParamUtil.getResourceLong("GOOGLE_API_ACCESS_TOKEN_CHECK_SEC") * 1000;

	/**
	 * アクセストークンチェックタイマー
	 */
	private Timer checkTokenTimer = null;

	/**
	 * アクセストークン用チェッククラス
	 */
	private final Lock lock = new ReentrantLock();

    /**
     * アクセストークン用コンディションクラス
     */
	private final Condition condition = lock.newCondition();
    /**
     * リフレッシュフラグ
     */
    boolean isRefresh = false;

	/**
	 * アクセストークン更新タイマタスククラス
	 * @author KKE
	 *
	 */
	private class OAuthTokeCheckTask extends TimerTask {
		@Override
		public void run() {
			if((accessToken == null)|(refreshToken == null)){
				return;
			}
			//現在時刻（ミリ秒）
			long curMsec = Calendar.getInstance().getTimeInMillis();
			//updateToken→アクセストークンを取得したタイミングのミリ秒
			if(curMsec < (updateToken + limitTokenMsec)){
				return;
			}
			refreshAccessToken();
		}
	}
	/**
	 * スケジュールの停止
	 */
	public void close(){
		try {
			if(this.checkTokenTimer != null){
				this.checkTokenTimer.cancel();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}


	/**
	 * アクセストーク取得済か否か
	 * @return
	 */
	public boolean isOkAccessToken() {
		boolean res = false;
		if(accessToken != null) {
			res = true;
		}
		return res;
	}
	/**
	 * アクセストークン要求
	 * @param authorizationCode
	 * @return
	 * @throws IOException
	 * @throws MsgException
	 */
	public String requestAccessToken(String authorizationCode) throws IOException, MsgException{
		try {
			lock.lock();
			isRefresh = true;
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
			condition.signalAll();// Signalを送ることで対応するConditionでawaitしていた処理が再開する。
		} catch (Exception e) {
			logger.error("can't request AccessToken", e);
			this.accessToken = null;
			this.refreshToken = null;
			this.updateToken = 0;
			throw e;
		} finally{
			isRefresh = false;
			lock.unlock();
		}
		return this.accessToken;
	}
	/**
	 * アクセストークン更新
	 * @return
	 */
	private String refreshAccessToken(){
		try {
			if(isOkAccessToken() == false){
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
		return this.accessToken;
	}

	/**
	 * カレンダチャンネルの生成
	 * @param uuid
	 * @param calendarId
	 * @return
	 * @throws Exception
	 */
	public GoogleResCreateChannelInfo createCalendarChannel(String uuid, String calendarId) throws Exception{
		GoogleResCreateChannelInfo res = null;
		if(isOkAccessToken() == false){
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

	/**
	 * カレンダ更新イベントの取得
	 * @param calendarId
	 * @return
	 * @throws Exception
	 */
	public GoogleResCalendarEventsListInfo getCalendarEventList(String calendarId, String updateMin) throws Exception{
		GoogleResCalendarEventsListInfo res = null;
		if(isOkAccessToken() == false){
			throw new MsgException("未認証");
		}
		lock.lock();
		try{
			while(this.isRefresh){
				condition.await();
			}
			res = GoogleApiUtil.getCalendarEventList(calendarId, updateMin, this.accessToken);
		} finally{
			lock.unlock();
		}
		return res;
	}

	/**
	 * メール送付
	 * @param mimeMessage
	 * @return
	 * @throws Exception
	 */
	public GoogleResGmailSendInfo sendMail(MimeMessage mimeMessage) throws Exception{
		GoogleResGmailSendInfo res = null;
		if(isOkAccessToken() == false){
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
