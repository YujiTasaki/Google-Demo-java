package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class SetGoogleAuth extends AbstractMngMessage{
	//@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SetGoogleAuth.class);

	private String code;

	private String accessToken;

	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
		pharseArg(hArg);
		this.accessToken = this.getServlet().getMngGoogleApi().requestAccessToken(code);
		returnOk(request, response);
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_ERROR, e.getMessage());
		ServletUtil.returnJsp(request, response, "/jsp/system_error.jsp");
	}

	public void returnOk(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_INFO, "Google認証成功:" + this.accessToken);
		ServletUtil.returnJsp(request, response, "/jsp/system_ok.jsp");
	}

	/**
	 *
	 * @param hArg
	 * @throws IOException
	 * @throws ParseException
	 * @throws SQLException
	 * @throws MsgException
	 */
	private void pharseArg(Map<String, Object> hArg) throws IOException, MsgException{
		for(String key: hArg.keySet()) {
			logger.info(String.format("%s,%s", key, hArg.get(key)));
		}
		code = (String)hArg.get("code");
		if(code == null){
			throw new IllegalArgumentException(String.format("can't found arg code."));
		}
		if(code.length() <= 0){
			throw new MsgException(String.format("codeが空欄です"));
		}
	}
}
