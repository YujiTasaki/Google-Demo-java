package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.MainServlet;
import jp.co.kke.Lockstatedemo.util.SysParamUtil;
/**
 * スケジュールチェック定期実行管理クラス
 * @author KKE
 */
public class MngSchedule {
	private static Logger logger = Logger.getLogger(MngSchedule.class);

	/**
	 * メインサーブレット
	 */
	private MainServlet mainServlet;

	/**
	 * スケジュールチェック処理間隔（秒）
	 * *0以下および未設定時は定期実行なし
	 */
	private final long checkTimerSec = SysParamUtil.getResourceLong("SCHEDULE_CHECK_SEC", 0) * 1000;

	/**
	 * チェックタイマクラス
	 */
	private Timer checkTimer = new Timer();

	/**
	 * コンストラクタ
	 * @param mainServlet　メインサーブレット
	 * @throws IOException
	 * @throws MsgException
	 */
	public MngSchedule(MainServlet mainServlet){
		super();
		this.mainServlet = mainServlet;
		if(checkTimerSec >= 0) {//0以下の場合は定期実行無
			logger.info("ScheduleCheckTask:start");
			this.checkTimer.schedule(new ScheduleCheckTask(), 0, this.checkTimerSec);
		}else {
			logger.info("ScheduleCheckTask:no");
		}
	}
	/**
 * LockstatesAPI管理クラス
	 * @return
	 */
	public MngLockApi getMngLockApi() {
		return mainServlet.getMngLockApi();
	}
	/**
	 * GoogleAPI管理クラス取得
	 * @return
	 */
	public MngGoogleApi getMngGoogleApi() {
		return mainServlet.getMngGoogleApi();
	}

	/**
	 * スケジュールチェック定期実行
	 * *同時実行禁止
	 * @throws Exception
	 */
	public synchronized void doCheck() throws Exception{
		logger.info("doCheck:start");
		MngLockApi mngLockApi = getMngLockApi();
		MngGoogleApi mngGoogleApi = getMngGoogleApi();
		if(mngLockApi.isOkAccessToken() == false){
			throw new MsgException("Lock未承認");
		}
		if(mngGoogleApi.isOkAccessToken() == false){
			throw new MsgException("Google未承認");
		}
		logger.info("doCheck:end");
	}

	/**
	 * スケジュールチェック定期実行用
	 */
	private class ScheduleCheckTask extends TimerTask {
		@Override
		public void run() {
			try {
				doCheck();
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
