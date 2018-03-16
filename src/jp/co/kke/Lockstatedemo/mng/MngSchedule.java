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
import java.util.regex.Pattern;

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
	public synchronized void doCheck(String calendarId) throws Exception{
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

		//String calendarId = SysParamUtil.getResourceString("GOOGLE_CHECK_CALENDAR_ID");
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
		//	eventMap2 = createEventList(calendarId2, res2);
		//}

		logger.info(eventMap);

		// 複数デバイスに対応するため、doConnectApiにcalendarIdを引き継ぐ
		//doConnectApi(eventMap);
		doConnectApi(eventMap, calendarId);

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
	private void doConnectApi(Map<String, List<List<String>>> eventMap, String calendarId)
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

			//List<String> userList = mngDbLockParam.getEventUser(eventId);
			List<String> delmenberList = new ArrayList<String>();	// 削除メンバー格納エリア
			//現在の登録T_EVENT_USER.eventIdをキーにしてT_EVENT_USERからuserIdを検索
			List<String> eventList = mngDbLockParam.getEventInfo(eventId);
			logger.info("EVENTLIST →" + eventList);
			//logger.info("USERLIST →" + userList);

			//予約の新規作成（変更でない）の場合
			if(status.equals(EVENT_CONFIRMED) && eventList.size() == 0)
			{
				logger.info("TRACE:新規予約");
				//アクセスゲスト作成とメール送信
				createAccessGuest(attendees, startAt, endAt, eventId, calendarId);

			}
			//予約の変更の場合
			else if(status.equals(EVENT_CONFIRMED) && eventList.size() != 0)
			{
				logger.info("TRACE:予約変更");

				int diffStatus = 0;	// 変更確認フラグ　0:変更無し(default) 1:日時変更 2:メンバー変更
				checkReserveInfo(diffStatus, eventId, datas, attendees, delmenberList);

				// 旧予約情報のDB(T_EVENT_INFO)削除
				mngDbLockParam.deleteEventInfo(eventId);
				//アクセスゲストの削除
				//deleteAccessGuest(delmenber);
				//アクセスゲスト作成とメール送信
				createAccessGuest(attendees, startAt, endAt, eventId, calendarId);

			}
			//予約の削除の場合
			else if(status.equals(EVENT_CANCELED))
			{
				logger.info("TRACE:予約削除");
				//アクセスゲストの削除
				//List<String> userList = null;
				for(int i = 0; i < eventList.size();) {
					//String cal_id = eventList.get(i);		// 未使用
					//String st = eventList.get(i+1);		// 未使用
					//String ed = eventList.get(i+2);		// 未使用
					String user_id = eventList.get(i+3);
					//String mail = eventList.get(i+4);		// 未使用
					//logger.info("index=" + i + "cal_id→" + cal_id + "st→" + st + "ed→" + ed +"user_id→" + user_id + "mail→" + mail);
					delmenberList.add(user_id);
					i = i+5;
				}
				logger.info("TRACE : deleteAccessGuest(delmenberList):" + delmenberList);
				deleteAccessGuest(delmenberList);

				// 予約情報のDB(T_EVENT_INFO)削除
				mngDbLockParam.deleteEventInfo(eventId);
			}
		}
	}

	/**
	 * 予約情報の差分確認
	 * @param datas
	 * @param attendees
	 * @throws Exception
	 */
	private void checkReserveInfo(int dateChgFlg, String eventId, List<String> datas, List<String> attendees, List<String> delmenber) throws Exception
	{
		//logger.info("TRACE : checkReserveInfo START");
		String memberList = null;
		// 変更情報の開始日時・終了日時の抽出
		String chgStartDate = datas.get(1);
		String chgEndDate = datas.get(2);

		// 変更情報のユーザ抽出(本関数の入力情報であるattendeesより変更後の予約対象メンバーを抽出)
		String addmenber = null;
		for(int i = 0; i < attendees.size(); i++) {
			if(i == 0) {
				addmenber = attendees.get(i);
			}
			else {
				addmenber += ",";
				addmenber += attendees.get(i);
			}
		}

		//現在の登録T_EVENT_USER.eventIdをキーにしてT_EVENT_USERからuserIdを検索
		MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
		List<String> eventList = mngDbLockParam.getEventInfo(eventId);
		logger.info(eventId);
		int size = eventList.size();
		logger.info(eventId + "で取得したT_EVENT_INFOのサイズは⇒" + size);

		// 現在の登録情報の開始日時・終了日時の抽出
		String orgStartDate = eventList.get(1);
		String orgEndDate = eventList.get(2);
		logger.info("変更前の開始日時" + orgStartDate + " ⇒ 変更後の開始日時" + chgStartDate);
		logger.info("変更前の終了日時" + orgEndDate + " ⇒ 変更後の終了日時" + chgEndDate);
		// 開始日時の比較
		if(!chgStartDate.equals(orgStartDate)) {
			// 開始日時が異なるので、変更対象とする。
			dateChgFlg = 1;
		}
		// 終了時刻の比較
		else if(!chgEndDate.equals(orgEndDate)) {
			// 終了日時が異なるので、変更対象とする。
			dateChgFlg = 1;
		}

		// 開始日時・終了日時に変更がなくても、参加メンバーが変更である場合の対応
		if(dateChgFlg == 0) {

			// 現在の予約ユーザの抽出
			for(int i = 0; i < eventList.size();) {
				if( i == 0)
				{
					memberList = eventList.get(i+4);
				}
				else
				{
					memberList += (",");
					memberList += eventList.get(i+4);
				}
				i = i+5;
			}
			logger.info("参加メンバー ⇒ " + memberList);

			attendees =new ArrayList<String>();		// リスト初期化

			logger.info("memberList:" + memberList);
			logger.info("addmenber:" + addmenber);

			// 参加メンバーの一致性確認
			if(!memberList.equals(addmenber)) {
				//参加メンバーが異なるので、追加・削除対象を検索
				logger.info("memberList:" + memberList);
				logger.info("addmenber:" + addmenber);
				String[] before_member = memberList.split(Pattern.quote(","));
				String[] change_member = addmenber.split(Pattern.quote(","));

				// 追加メンバーの検索
				for(int i = 0; i < change_member.length; i++) {
					String chkMem = change_member[i] ;
					boolean flg = false;
					for(int j= 0; j < before_member.length; j++) {
						if(chkMem.equals(before_member[j])) {
							// 同一人物を検出
							flg = true;
							break;
						}
					}
					if(flg == false) {
						dateChgFlg = 2;
						// 追加メンバーをリストに追加
						attendees.add(chkMem);
					}
				}
				logger.info("★変更後の新規追加メンバー⇒" + attendees);

				// 削除メンバーの検索
				for(int k = 0; k < before_member.length; k++) {
					String chkMem = before_member[k] ;
					boolean flg = false;
					for(int l= 0; l < change_member.length; l++) {
						if(chkMem.equals(change_member[l])) {
							// 同一人物を検出
							flg = true;
							break;
						}
					}
					if(flg == false) {
						dateChgFlg = 2;
						// 追加メンバーをリストに追加
						delmenber.add(chkMem);
					}
				}
				logger.info("★変更後の削除メンバー⇒" + delmenber);
			}
		}
		logger.info("変更確認状況(0:変更無し/1:日時変更/2:メンバー変更) ⇒ " + dateChgFlg);
	}

	/**
	 * アクセスゲスト作成・デバイス紐付け・メール送信
	 * @param mngLockApi
	 * @param attendees
	 * @param startAt
	 * @param endAt
	 * @param eventId
	 * @param calendarId
	 * @throws MsgException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	private void createAccessGuest(List<String> attendees, String startAt, String endAt,
			String eventId, String calendarId) throws MsgException, ClassNotFoundException, SQLException, Exception {

		MngLockApi mngLockApi = getMngLockApi();

		//DBからCalenderIdに対応した開始前有効延長時間及び終了後有効延長時間を取得
		List<String> List = null;
		MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
		List = mngDbLockParam.getDeviceInfo(calendarId);
		String addStTime = List.get(1);	// calendarIdに対応するadd_sttime(1番目のデータ)を抽出
		String addEdTime = List.get(2);	// calendarIdに対応するadd_edtime(2番目のデータ)を抽出

		//開始前有効延長時間及び終了後有効延長時間を秒単位変換
		int tmpAddStTime = Integer.parseInt(addStTime) * 60;
		int tmpAddEdTime = Integer.parseInt(addEdTime) * 60;
		// Paseeするために時間編集しておく
		String tmpStartAt = startAt + "+09:00" ;
		String tmpEndAt = startAt + "+09:00" ;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

		//開始時刻に = Off開始前有効延長時間を加味する
		OffsetDateTime startAtMinusset = OffsetDateTime.parse(tmpStartAt).minusSeconds(tmpAddStTime);
		String startAtMinusStr = startAtMinusset.format(formatter);
		//終了時刻に数量後有効延長時間を加味する
		OffsetDateTime endAtPlus = OffsetDateTime.parse(tmpEndAt).plusSeconds(tmpAddEdTime);
		String endAtPlusStr = endAtPlus.format(formatter);


		for(int i=0; i<attendees.size(); i++) {

			//アクセスゲストの作成
			String email = attendees.get(i);


			//同意拒否ユーザーを除く場合
			//String[] denialRakumoUsers = SysParamUtil.getResourceString("DENIAL_RAKUMO_USER").split(",");
			List<String> denialRakumoUsers = new ArrayList<String>();
			denialRakumoUsers = mngDbLockParam.getDisagreeAllUser();

			//同意拒否の場合は、処理しない
			boolean denialFlg = false;
			for(int k=0; k<denialRakumoUsers.size(); k++) {
				String denialUser = denialRakumoUsers.get(k);
				if(email.equals(denialUser))
				{
					denialFlg = true;
					break;
				}
			}
			if(denialFlg)
			{
				logger.info("rakumo登録されましたが、ご同意頂けなかったため、アクセスゲスト作成処理を行いませんでした");
				continue;
			}

			/* 同意ユーザの制限は行わないように変更 A.Sビル勤務者のみという制限がなくなったら、この処理はコメントにすること */

			//同意ユーザーのみ処理を行う場合
			String[] rakumoUsers = SysParamUtil.getResourceString("RAKUMO_USER").split(",");
			boolean consentFlg = false;
			for(int k=0; k<rakumoUsers.length; k++) {
				String denialUser = rakumoUsers[k];
				if(email.equals(denialUser))
				{
					consentFlg = true;
					break;
				}
			}
			if(!consentFlg)
			{
				logger.info("rakumo登録されましたが、同意ユーザーではないため、アクセスゲスト作成処理を行いませんでした");
				continue;
			}

			String name = email;
			LockReqAccessPersonsInfo info = new LockReqAccessPersonsInfo();
			info.setType("access_guest");
			info.getAttributes().put("name", name);
			info.getAttributes().put("email", email);
			//info.getAttributes().put("starts_at", startAt);
			//info.getAttributes().put("ends_at", endAt);
			info.getAttributes().put("starts_at", startAtMinusStr);
			info.getAttributes().put("ends_at", endAtPlusStr);

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
			//MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
			mngDbLockParam.insertEventUser(eventId, userId);

			// 予約情報のDB(T_EVENT_INFO)登録
			mngDbLockParam.insertEventInfo(eventId, calendarId, startAt, endAt, userId, name);

			logger.info(userId);

			//デバイスとの紐付け
			//String deviceId = "7888de18-1e1f-412e-8e26-75da5968cc7b";
			//String deviceId = mngLockApi.getDeviceId();
			List<String> devList = null;
			devList = mngDbLockParam.getDeviceInfo(calendarId);
			String deviceId = devList.get(0);	// calendarIdに対応するdeviceIdは必ず１つなので、0番目のデータを抽出
			logger.info("deviceId:" + deviceId);
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
				// メール送信
				String resEmail = mngLockApi.sendEmail(userId);
			}


			//ここに、登録されたカレンダーによる場合分けが必要

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

					//開始時刻10分前 → ここでは時間の加減は行わず、指定日時とする。
					//OffsetDateTime startAtMinus = OffsetDateTime.parse(startAt).minusSeconds(600);
					OffsetDateTime startAtMinus = OffsetDateTime.parse(startAt);
					String startAtMinusStr = startAtMinus.format(formatter);

					//終了時刻10分後 → ここでは時間の加減は行わず、指定日時とする。
					//OffsetDateTime endAtPlus = OffsetDateTime.parse(endAt).plusSeconds(600);
					OffsetDateTime endAtPlus = OffsetDateTime.parse(endAt);
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
				String calendarId = null;

				List<String> calenderIdList = null;
				MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
				calenderIdList = mngDbLockParam.getDeviceAllUser();
				logger.info("calIdList" + calenderIdList);
				int length = calenderIdList.size();
				logger.info("Length:" + length);
				for (int i=0; i < length; i++)
				{
					calendarId = String.valueOf(calenderIdList.get(i));
					logger.info("実行対象calenderID→" + calendarId);
					doCheck(calendarId);

					// 次設定のCalenderIdを取得するため、変数に3をプラス
					// T_DEVICE_INFOテーブル
					//   calender_id  ←ここで使用するのはcalender_idのみ
					//   device_id
					//   add_sttime
					//   add_edtime
					i = i+3;
				}
			} catch (Exception e) {
				// このルートは未承認状態の時に定期的に通るルートなので、ERRORとして出力しない
				//logger.error(e);
				logger.info(e);
			}
		}
	}
}
