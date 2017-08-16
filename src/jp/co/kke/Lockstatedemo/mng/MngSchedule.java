package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.MainServlet;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventAttendeeInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventsListInfo;
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

		String calendarId = SysParamUtil.getResourceString("GOOGLE_CHECK_CALENDAR_ID");
		GoogleResCalendarEventsListInfo res = getMngGoogleApi().getCalendarEventList(calendarId);

		Map<String, List<List<String>>> eventMap = new HashMap<String, List<List<String>>>();
		if(res != null)
		{
			eventMap = getEvent(calendarId, res);
		}
		logger.info(eventMap);

		logger.info("doCheck:end");
	}


	/**
	 * 監視時間以内に変更されたイベントを全て取得してMapにする
	 * @param calendarId
	 * @param res
	 * @return
	 */
	private Map<String, List<List<String>>> getEvent(String calendarId, GoogleResCalendarEventsListInfo res)
	{
		Map<String, List<List<String>>> eventMap = new HashMap<String, List<List<String>>>();
		//resの中から必要な値を取ってくる
		for(int i=0; i<res.getItems().size(); i++) {

			GoogleResCalendarEventInfo items = res.getItems().get(i);

			//イベントID
			String eventId = items.getId();

			//イベント情報
			List<String> infoList = new ArrayList<String>();
			String status = items.getStatus();
			String startAt = items.getStart().getDateTime();
			if(startAt == null)
			{
				startAt = items.getStart().getDate();
			}
			startAt = startAt.substring(0, 19);

			String endAt = items.getEnd().getDateTime();
			if(endAt == null)
			{
				endAt = items.getEnd().getDate();
			}
			endAt = endAt.substring(0, 19);

			String pinCode = "";
			for(int r=0; r<8; r++) {
				Random rnd = new Random();
				pinCode = pinCode + String.valueOf(rnd.nextInt(10));
			}
			infoList.add(status);
			infoList.add(startAt);
			infoList.add(endAt);
			infoList.add(pinCode);

			//参加者メールリスト
			List<String> attendEmailList = new ArrayList<String>();
			String email = items.getCreator().getEmail();
			attendEmailList.add(email);
			List<GoogleResCalendarEventAttendeeInfo> attendees = items.getAttendees();
			if(attendees != null)
			{
				//attendeesの1番目は登録者、最後はアカウントユーザーのため
				for(int j=1; j<attendees.size()-1; j++){
					String attendEmail = attendees.get(j).getEmail();
					attendEmailList.add(attendEmail);
				}
			}

			List<List<String>> valueList = new ArrayList<List<String>>();
			valueList.add(infoList);
			valueList.add(attendEmailList);

			eventMap.put(eventId,valueList);
		}
		return eventMap;
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
