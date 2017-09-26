package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	 * ディレクトリパス
	 */
	private static final String REAL_PATH        = SysParamUtil.getResourceString("DIR_PATH");

	/**
	 * カレンダーイベント追加・変更時のステータス
	 */
	private static final String EVENT_CONFIRMED  = "confirmed";

	/**
	 * カレンダーイベント削除時のステータス
	 */
	private static final String EVENT_CANCELED  = "cancelled";

	/**
	 * カレンダーイベント非表示時のステータス
	 */
	private static final String EVENT_PRIVATE  = "private";



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
	 * スケジュールの停止
	 */
	public void close(){
		try {
			if(this.checkTimer != null){
				this.checkTimer.cancel();
			}
		} catch (Exception e) {
			logger.error(e);
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
	 * DBアクセス管理クラス
	 * @return
	 */
	public MngDbLockParam getMngDbLockParam() {
		return mainServlet.getMngDbLockParam();
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
		//String calendarId2 = SysParamUtil.getResourceString("GOOGLE_CHECK_CALENDAR_ID2");
		GoogleResCalendarEventsListInfo res = getMngGoogleApi().getCalendarEventList(calendarId, updateMin);
		//GoogleResCalendarEventsListInfo res2 = getMngGoogleApi().getCalendarEventList(calendarId2, updateMin);

		Map<String, List<List<String>>> eventMap = new HashMap<String, List<List<String>>>();
		//Map<String, List<List<String>>> eventMap2 = new HashMap<String, List<List<String>>>();
		if(res != null)
		{
			eventMap = createEventList(calendarId, res);
		}
		//if(res2 != null)
		//{
		//	eventMap2 = createEventList(calendarId, res2);
		//}

		logger.info(eventMap);

		doConnectApi(eventMap);

		logger.info("最終更新時間");
		logger.info(updateMin);
		logger.info("doCheck:end");
	}


	/**
	 * イベント毎に処理を実行
	 * @param mngLockApi
	 * @param eventMap
	 * @throws Exception
	 * @throws MsgException
	 */
	private void doConnectApi(Map<String, List<List<String>>> eventMap)
			throws Exception, MsgException {

		MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
		//イベントごとに処理を実行
		for(String key: eventMap.keySet())
		{
			List<List<String>> value = eventMap.get(key);
			String eventId = key;
			List<String> datas = value.get(0);
			List<String> attendees = value.get(1);
			String status = datas.get(0);
			String startAt = datas.get(1);
			String endAt = datas.get(2);
			String update = datas.get(3);
			//String eventId = datas.get(4);
			updateMin = update;

			//T_EVENT_USER.eventIdをキーにしてT_EVENT_USERからuserIdを検索
			List<String> userList = mngDbLockParam.getEventUser(eventId);

			//予約の新規作成（変更でない）の場合
			if(status.equals(EVENT_CONFIRMED) && userList.size() == 0)
			{
				//アクセスゲスト作成とメール送信
				createAccessGuest(attendees, startAt, endAt, eventId);
			}
			//予約の変更の場合
			else if(status.equals(EVENT_CONFIRMED) && userList.size() != 0)
			{
				//アクセスゲストの削除
				deleteAccessGuest(userList);
				//アクセスゲスト作成とメール送信
				createAccessGuest(attendees, startAt, endAt, eventId);
			}
			//予約の削除の場合
			else if(status.equals(EVENT_CANCELED))
			{
				//アクセスゲストの削除
				deleteAccessGuest(userList);
			}
		}
	}


	/**
	 * アクセスゲスト作成・デバイス紐付け・メール送信
	 * @param mngLockApi
	 * @param attendees
	 * @param startAt
	 * @param endAt
	 * @param eventId
	 * @throws MsgException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	private void createAccessGuest(List<String> attendees, String startAt, String endAt,
			String eventId) throws MsgException, ClassNotFoundException, SQLException, Exception {

		MngLockApi mngLockApi = getMngLockApi();

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

			//PINコードの自動生成機能が追加されたため、追加 (2017/9/22)
			info.getAttributes().put("generate_pin", "true");

			LockResAccessPersonsInfo resUser=null;
			//PINコードの自動生成機能が追加されたため、追加 (2017/9/22)
			resUser = mngLockApi.createUsers(info);

			//PINコードの自動生成機能が追加されたため、削除 (2017/9/22)
//			for (int j = 0; j < 10; j++) {
//				try {
//					String pinCode = "";
//					for (int r = 0; r < 5; r++) {
//						Random rnd = new Random();
//						pinCode = pinCode + String.valueOf(rnd.nextInt(10));
//					}
//					info.getAttributes().put("pin", pinCode);
//					logger.info("PINコード" + pinCode);
//					resUser = mngLockApi.createUsers(info);
//
//				} catch (Exception e) {
//					logger.error(e);
//				}
//				if(resUser!=null) {
//					break;
//				}
//			}
//
//			if(resUser==null) {
//				//システム管理者にメールする？
//				throw new MsgException("アクセスゲスト作成に10回以上失敗しました。不要なPINコードを削除してください。もしくは入力データが間違っている可能性がございます。");
//			}


			String userId = resUser.getData().getId();

			//DBにイベントIDとユーザーIDを登録
			//String REAL_PATH        = SysParamUtil.getResourceString("DIR_PATH");
			MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
			mngDbLockParam.insertEventUser(eventId, userId);

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


	/**
	 * アクセスゲストの削除
	 * @param userList
	 * @throws Exception
	 */
	private void deleteAccessGuest(List<String> userList) throws Exception
	{
		MngLockApi mngLockApi = getMngLockApi();
		for (int i=0; i<userList.size(); i++)
		{
			String userId = userList.get(i);
			//アクセスゲストの削除
			String res = mngLockApi.deleteUsers(userId);
			//T_EVENT_USER.delete_flgをTRUEにセット
			String REAL_PATH        = SysParamUtil.getResourceString("DIR_PATH");
			MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
			mngDbLockParam.updateEventUser(userId);
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
			String visibility = items.getVisibility();

			//非表示イベントは情報が取れないため、処理しない
			if(visibility != null)
			{
				logger.info("非表示イベント 処理しない " + eventId);
				continue;
			}

			if(status.equals(EVENT_CONFIRMED))
			{
				String update = items.getUpdated();

				//更新時間が前回の処理実行したイベントの最終時間とイコールの場合は、イベントMapに追加しない
				if(update.equals(updateMin))
				{
					logger.info("含まれる" + update);
					continue;
				}

				//2017/8/23 修正
				boolean dayFlg = false;
				String startAt = items.getStart().getDateTime();
				if(startAt == null)
				{
					startAt = items.getStart().getDate();
					dayFlg = true;
				}

				String endAt = items.getEnd().getDateTime();
				if(endAt == null)
				{
					endAt = items.getEnd().getDate();
					dayFlg = true;
				}

				if(dayFlg == false)
				{
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

					//開始時刻10分前
					OffsetDateTime startAtMinus = OffsetDateTime.parse(startAt).minusSeconds(600);
					String startAtMinusStr = startAtMinus.format(formatter);

					//終了時刻10分後
					OffsetDateTime endAtPlus = OffsetDateTime.parse(endAt).plusSeconds(600);
					String endAtPlusStr = endAtPlus.format(formatter);

					infoList.add(status);
					infoList.add(startAtMinusStr);
					infoList.add(endAtPlusStr);
					infoList.add(update);
					infoList.add(eventId);
				}
				else
				{
					infoList.add(status);
					infoList.add(startAt);
					infoList.add(endAt);
					infoList.add(update);
					infoList.add(eventId);
				}

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
			else if(status.equals(EVENT_CANCELED))
			{
				infoList.add(status);
				infoList.add(null);
				infoList.add(null);
				infoList.add(null);
				List<List<String>> valueList = new ArrayList<List<String>>();
				valueList.add(infoList);
				valueList.add(null);
				eventMap.put(eventId,valueList);
			}
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
