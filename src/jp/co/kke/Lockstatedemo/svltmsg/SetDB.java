package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class SetDB extends AbstractMngMessage {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Login.class);
	private String ChkKind;
	private String CalenderID;
	private String deviceID;
	private String AddStTime;
	private String AddEdTime;
	private String DisAgreeUser1;
	private String DisAgreeUser2;
	private String DisAgreeUser3;

	private String AgreeUser;

	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
		pharseArg(hArg);
		List<String> res = null;
		if(ChkKind.length() == 6) {			// length長=6 : カレンダーIDとデバイスの括り付け
			res = this.getServlet().getMngDbLockParam().getDeviceInfo(CalenderID);
			if(!res.isEmpty())
			{
				logger.info("START : カレンダーIDとデバイスの括り付け");
				logger.info("res:" + res);
				// 登録済みのGoogleカレンダーIDのデータを削除(delete_flgの値に1設定)
				this.getServlet().getMngDbLockParam().deleteDeviceInfo(CalenderID);
			}
			this.getServlet().getMngDbLockParam().insertDeviceInfo(CalenderID, deviceID, AddStTime, AddEdTime);
			returnOk(request, response);
		}
		else if(ChkKind.length() == 8) {	// length長=8 : LockStateConnect登録拒否ユーザの設定
			logger.info("START : LockStateConnect登録拒否ユーザの設定");
			String disAgreeuser;
			for(int i = 0; i < 3; i++) {
				disAgreeuser = null;
				if(i == 0) disAgreeuser = DisAgreeUser1;
				if(i == 1) disAgreeuser = DisAgreeUser2;
				if(i == 2) disAgreeuser = DisAgreeUser3;

				if(disAgreeuser.isEmpty()) {
					// 登録拒否ユーザが設定されていない場合、何もしない
					continue;
				}
				else {
					res = this.getServlet().getMngDbLockParam().getDisagreeUser(disAgreeuser);
					logger.info(res);
					if(res.isEmpty()) {
						this.getServlet().getMngDbLockParam().insertDisagreeUser(disAgreeuser);
					}
				}
			}
			returnOk(request, response);
		}
		else if(ChkKind.length() == 5) {	// length長=5 : LockStateConnect登録許容ユーザの設定
			logger.info("START : LockStateConnect登録許容ユーザの設定");
			res = this.getServlet().getMngDbLockParam().getDisagreeUser(AgreeUser);
			if(res.isEmpty()) {
				// 未登録状態なので何もしない
			}
			else {
				this.getServlet().getMngDbLockParam().deleteDisagreeUser(AgreeUser);

			}
			returnOk(request, response);
		}
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_ERROR, e.getMessage());
		ServletUtil.returnJsp(request, response, "/jsp/system_error.jsp");
	}

	public void returnOk(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_INFO, "DB設定成功:");
		ServletUtil.returnJsp(request, response, "/jsp/system_ok.jsp");
	}
	/**
	 *
	 * @param hArg
	 * @throws ParseException
	 * @throws SQLException
	 * @throws MsgException
	 */
	private void pharseArg(Map<String, Object> hArg) throws IOException, MsgException{
		ChkKind = (String)hArg.get("check_kind");
		// 内容で判断できなかったので、ChkKindのLength長で判断する。
		//  length長=6 : カレンダーIDとデバイスの括り付け
		//  length長=8 : LockStateConnect登録拒否ユーザの設定
		//  length長=5 : LockStateConnect登録許容ユーザの設定
		if(ChkKind.length() == 6) {
			CalenderID = (String)hArg.get("CalenderID");
			if(CalenderID.length() <= 0){
				throw new MsgException(String.format("GoogleカレンダーIDが空欄です"));
			}
			deviceID = (String)hArg.get("DeviceID");
			if(deviceID.length() <= 0){
				throw new MsgException(String.format("デバイスIDが空欄です"));
			}
			if(AddStTime.length() <= 0){
				throw new MsgException(String.format("開始前延長時間(分)が空欄です"));
			}
			if(AddEdTime.length() <= 0){
				throw new MsgException(String.format("終了後延長時間(分)が空欄です"));
			}
		}
		else if(ChkKind.length() == 8) {
			DisAgreeUser1 = (String)hArg.get("DisagreeUser1");
			DisAgreeUser2 = (String)hArg.get("DisagreeUser2");
			DisAgreeUser3 = (String)hArg.get("DisagreeUser3");
			if(DisAgreeUser1.isEmpty()&&DisAgreeUser2.isEmpty()&&DisAgreeUser3.isEmpty()) {
				throw new MsgException(String.format("非許容ユーザを少なくとも1つは設定してください。"));
			}
		}
		else if(ChkKind.length() == 5) {
			AgreeUser = (String)hArg.get("agreeUser");
			if(AgreeUser.isEmpty()) {
				throw new MsgException(String.format("許容ユーザを設定してください。"));
			}
		}
		else {
			throw new MsgException(String.format("設定項目(ラジオボタン)が未選択状態です。"));
		}
	}
}
