package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.bean.lock.LockResDataInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResInfoList;
import jp.co.kke.Lockstatedemo.bean.lock.LockResOAuthInfo;
import jp.co.kke.Lockstatedemo.util.LockApiUtil;
import jp.co.kke.Lockstatedemo.util.SysParamUtil;
/**
 * LockstatesAPI管理クラス
 * @author KKE
 */
public class MngLockApi {
	/**
	 * ロガー
	 */
	private static Logger logger = Logger.getLogger(MngLockApi.class);
	/**
	 * アクセストークン
	 */
	private String accessToken = null;
	/**
	 * リフレッシュトークン
	 */
	private String refreshToken = null;
	/**
	 * アクセストークン更新日時(ミリ秒)
	 */
	private long updateToken = 0;
	/**
	 * LockState アクセストークン更新間隔（ミリ秒）
	 */
	private final long limitTokenMsec = SysParamUtil.getResourceLong("LOCK_API_ACCESS_TOKEN_LIMIT_SEC") * 1000;

	/**
	 * LockState トークンチェック処理間隔（ミリ秒）
	 */
	private final long checkTokenMsec = SysParamUtil.getResourceLong("LOCK_API_ACCESS_TOKEN_CHECK_SEC") * 1000;

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
     */
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

	/**
	 * アクセストーク取得済?
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
			LockResOAuthInfo oAuthInfo = LockApiUtil.requestOAuthToken(authorizationCode);
			this.accessToken = oAuthInfo.getAccess_token();
			this.refreshToken = oAuthInfo.getRefresh_token();
			this.updateToken = Calendar.getInstance().getTimeInMillis();
			logger.info(String.format("init accessToken:%s refreshToken:%s", this.accessToken, this.refreshToken));
			this.checkTokenTimer.schedule(new OAuthTokeCheckTask(), 0, this.checkTokenMsec);
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
			lock.lock();
			isRefresh = true;
			LockResOAuthInfo oAuthInfo = LockApiUtil.refreshOAuthToken(this.refreshToken);
			this.accessToken = oAuthInfo.getAccess_token();
			this.refreshToken = oAuthInfo.getRefresh_token();
			logger.info(String.format("refresh accessToken:%s refreshToken:%s", this.accessToken, this.refreshToken));
			this.updateToken = Calendar.getInstance().getTimeInMillis();
			condition.signalAll();// Signalを送ることで対応するConditionでawaitしていた処理が再開する。
		} catch (Exception e) {
			logger.error("can't refresh AccessToken",e);
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
	 *　全デバイス情報の取得(返信オブジェクト形式)
	 * @return
	 * @throws Exception
	 */
	public LockResInfoList getAllDevices() throws Exception{
		LockResInfoList res = null;
		if(isOkAccessToken() == false){
			throw new MsgException("未認証");
		}
		lock.lock();
		try{
			while(this.isRefresh){
				condition.await();
			}
			res = LockApiUtil.getAllDevices(this.accessToken);
		} finally{
			lock.unlock();
		}
		return res;
	}

	/**
	 *　全デバイス情報の取得(返信オブジェクト形式)
	 * @param id
	 * @param isLock　鍵の開閉（true:閉める false:開ける)
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws Exception
	 */
	public LockResInfo doLockDevice(String id, boolean isLock) throws Exception{
		LockResInfo res = null;
		if(isOkAccessToken() == false){
			throw new MsgException("未認証");
		}
		lock.lock();
		try{
			while(this.isRefresh){
				condition.await();
			}
			res = LockApiUtil.doLockDevice(this.accessToken , id, isLock);
		} finally{
			lock.unlock();
		}
		return res;
	}
	/**
	 * 指定デバイスの情報取得
	 * @param serial_number
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws Exception
	 */
	public LockResDataInfo getLockResDataInfo(String serial_number) throws Exception{
		LockResDataInfo res = null;
		if(isOkAccessToken() == false){
			throw new MsgException("未認証");
		}

		lock.lock();
		try{
			while(this.isRefresh){
				condition.await();
			}
			res = LockApiUtil.getLockResDataInfo(this.accessToken, serial_number);
		} finally{
			lock.unlock();
		}
		return res;
	}
}
