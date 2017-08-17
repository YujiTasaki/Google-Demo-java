package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
import jp.co.kke.Lockstatedemo.bean.lock.LockReqAccessPersonsAccess;
import jp.co.kke.Lockstatedemo.bean.lock.LockReqAccessPersonsInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResAccessPersonsInfo;
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


	private String updateMin = null;

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

		if(updateMin == null)
		{
			OffsetDateTime updateMinDateTime = OffsetDateTime.now();  //現在時刻
			updateMin = updateMinDateTime.format(DateTimeFormatter.ISO_INSTANT);
		}
		String calendarId = SysParamUtil.getResourceString("GOOGLE_CHECK_CALENDAR_ID");
		GoogleResCalendarEventsListInfo res = getMngGoogleApi().getCalendarEventList(calendarId, updateMin);

		Map<String, List<List<String>>> eventMap = new HashMap<String, List<List<String>>>();
		if(res != null)
		{
			eventMap = createEventList(calendarId, res);
		}
		logger.info(eventMap);

		doConnectApi(mngLockApi, eventMap);
		logger.info("最終更新時間");
		logger.info(updateMin);

		logger.info("doCheck:end");
	}


	/**
	 * ConnectApi処理の実行
	 * @param mngLockApi
	 * @param eventMap
	 * @throws Exception
	 * @throws MsgException
	 */
	private void doConnectApi(MngLockApi mngLockApi, Map<String, List<List<String>>> eventMap)
			throws Exception, MsgException {
		//API実行
		for(String key: eventMap.keySet())
		{
			List<List<String>> value = eventMap.get(key);
			List<String> datas = value.get(0);
			List<String> attendees = value.get(1);
			String status = datas.get(0);
			String startAt = datas.get(1);
			String endAt = datas.get(2);
			String update = datas.get(3);
			updateMin = update;

			for(int i=0; i<attendees.size(); i++) {

				//アクセスゲストの作成
				String email = attendees.get(i);
				String name = email;
				LockReqAccessPersonsInfo info = new LockReqAccessPersonsInfo();
				info.setType("access_guest");
				info.getAttributes().put("name", name);
				info.getAttributes().put("email", email);
				info.getAttributes().put("starts_at", startAt);
				info.getAttributes().put("ends_at", endAt);

				LockResAccessPersonsInfo resUser=null;
				for (int j = 0; j < 5; j++) {
					try {
						String pinCode = "";
						for (int r = 0; r < 8; r++) {
							Random rnd = new Random();
							pinCode = pinCode + String.valueOf(rnd.nextInt(10));
						}
						info.getAttributes().put("pin", pinCode);
						resUser = mngLockApi.createUsers(info);

					} catch (Exception e) {
						logger.error(e);
					}
					if(resUser!=null) {
						break;
					}
				}

				if(resUser==null) {
					//システム管理者にメールする？
					throw new MsgException("アクセスゲスト作成時にPINコードが5回以上重複しました。不要なPINコードを削除してください。");
				}

				String userId = resUser.getData().getId();
				logger.info(userId);

				//デバイスとの紐付け
				//String deviceId = "7888de18-1e1f-412e-8e26-75da5968cc7b";
				String deviceId = mngLockApi.getDeviceId();
				if(deviceId == null)
				{
					throw new MsgException("デバイス未設定");
				}
				else
				{
					LockReqAccessPersonsAccess access = new LockReqAccessPersonsAccess();
					access.getAttributes().put("accessible_id", deviceId);
					access.getAttributes().put("accessible_type", "lock");
					String resAccess = mngLockApi.setDeviceUsers(access, userId);
					logger.info(resAccess);
					//メール送信
					String resEmail = mngLockApi.sendEmail(userId);
				}
			}
		}
	}


	/**
	 * 監視時間以内に変更されたイベントを全て取得してMapにする
	 * @param calendarId
	 * @param res
	 * @return
	 */
	private Map<String, List<List<String>>> createEventList(String calendarId, GoogleResCalendarEventsListInfo res)
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
			String update = items.getUpdated();

			//更新時間が前回の処理実行したイベントの最終時間とイコールの場合は、イベントMapに追加しない
			if(update.equals(updateMin))
			{
				logger.info("含まれる" + update);
				continue;
			}

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

			infoList.add(status);
			infoList.add(startAt);
			infoList.add(endAt);
			infoList.add(update);

			//参加者メールリスト
			List<String> attendEmailList = new ArrayList<String>();
			String email = items.getCreator().getEmail();
			attendEmailList.add(email);
			List<GoogleResCalendarEventAttendeeInfo> attendees = items.getAttendees();
			if(attendees != null)
			{

				for(int j=0; j<attendees.size(); j++){
					String attendEmail = attendees.get(j).getEmail();
					//attendeesのうち登録者、アカウントユーザーを外す
					if((!attendEmail.equals(email)) && (!attendEmail.equals(SysParamUtil.getResourceString("GOOGLE_CHECK_CALENDAR_ID"))))
					{
						attendEmailList.add(attendEmail);
					}
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
