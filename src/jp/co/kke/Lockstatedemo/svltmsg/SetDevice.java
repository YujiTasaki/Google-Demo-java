package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.kke.Lockstatedemo.bean.api.ResponseInfo;
import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class SetDevice extends AbstractMngMessage {
	private String id;
	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		pharseArg(hArg);
		returnOk(request, response);
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_NG);
		responseInfo.setMsg(ex.getMessage());
		ServletUtil.returnJson(response, responseInfo, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	public void returnOk(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ResponseInfo responseInfo = new ResponseInfo();
		responseInfo.setState(ResponseInfo.S_State_OK);
		responseInfo.setMsg(id);
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
		id = (String)hArg.get("id");
		if(id == null){
			throw new IllegalArgumentException(String.format("can't found arg id."));
		}
		id = id.trim();
		if(id.length() <= 0){
			throw new MsgException(String.format("idが空欄です"));
		}

	}

}
