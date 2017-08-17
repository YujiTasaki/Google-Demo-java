package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
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
	private String name;

	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
		pharseArg(hArg);

		//this.getServlet().getMngDbLockParam().insertSample(name, Calendar.getInstance());
		returnOk(request, response);
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
	private void pharseArg(Map<String, Object> hArg) throws ParseException, SQLException, MsgException{
		name = (String)hArg.get("name");
		if(name == null){
			throw new IllegalArgumentException(String.format("can't found arg name."));
		}
		name = name.trim();
		if(name.length() <= 0){
			throw new MsgException(String.format("名前が空欄です"));
		}
	}

}
