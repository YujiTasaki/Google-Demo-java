package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.bean.api.ResponseInfo;
import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class Login extends AbstractMngMessage{

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Login.class);

	private String user_id;
	private String password;


	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
		pharseArg(hArg);
		returnOk(request, response);
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_NG);
		responseInfo.setMsg(e.getMessage());
		response.setStatus(400);
		ServletUtil.returnJson(response, responseInfo, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	public void returnOk(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_OK);
		ServletUtil.returnJson(response, responseInfo);
	}

	/**
	 *
	 * @param hArg
	 * @throws ParseException
	 * @throws SQLException
	 * @throws MsgException
	 */
	private void pharseArg(Map<String, Object> hArg) throws ParseException, SQLException, MsgException{


		if(user_id.length() <= 0){
			throw new MsgException(String.format("ユーザIDが空欄です"));
		}

		if(password.length() <= 0){
			throw new MsgException(String.format("パスワードが空欄です"));
		}
	}

}
