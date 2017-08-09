package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.MailUtil;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class TestSendMail extends AbstractMngMessage{

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Login.class);
	private String to;
	private String subject;
	private String bodyText;

	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception {
		pharseArg(hArg);
		MimeMessage mimeMessage = MailUtil.makeMimeMessageWithEmail(
				to,
				subject,
				bodyText);
		this.getServlet().getMngGoogleApi().sendMail(mimeMessage);
		returnOk(request, response);
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_ERROR, e.getMessage());
		ServletUtil.returnJsp(request, response, "/jsp/system_error.jsp");
//		ResponseInfo responseInfo = new ResponseInfo();
//		responseInfo.setState(ResponseInfo.S_State_NG);
//		responseInfo.setMsg(e.getMessage());
//		ServletUtil.returnJson(response, responseInfo, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	public void returnOk(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_INFO, "メール送付成功:");
		ServletUtil.returnJsp(request, response, "/jsp/system_ok.jsp");
//		ResponseInfo responseInfo = new ResponseInfo();
//		responseInfo.setState(ResponseInfo.S_State_OK);
//		ServletUtil.returnJson(response, responseInfo);
	}
	/**
	 *
	 * @param hArg
	 * @throws ParseException
	 * @throws SQLException
	 * @throws MsgException
	 */
	private void pharseArg(Map<String, Object> hArg) throws ParseException, SQLException, MsgException{
		to = (String)hArg.get("to");
		if(to == null){
			throw new IllegalArgumentException(String.format("can't found arg to."));
		}
		to = to.trim();
		subject = (String)hArg.get("subject");
		if(subject == null){
			throw new IllegalArgumentException(String.format("can't found arg subject."));
		}
		subject = subject.trim();

		bodyText = (String)hArg.get("bodyText");
		if(bodyText == null){
			throw new IllegalArgumentException(String.format("can't found arg bodyText."));
		}
		bodyText = bodyText.trim();

		if(to.length() <= 0){
			throw new MsgException(String.format("宛先が空欄です"));
		}
		if(subject.length() <= 0){
			throw new MsgException(String.format("題名が空欄です"));
		}
		if(bodyText.length() <= 0){
			throw new MsgException(String.format("本文が空欄です"));
		}
	}
}
