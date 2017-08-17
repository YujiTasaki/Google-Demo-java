package jp.co.kke.Lockstatedemo.svltmsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.kke.Lockstatedemo.bean.lock.LockResDataInfo;
import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.ServletUtil;

public class SetDevice extends AbstractMngMessage {
	private String id;
	@Override
	public void doJob(Map<String, Object> hArg, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		pharseArg(hArg);
		//LockResDataInfo lockResDataInfo = getDevice(id);
		LockResDataInfo lockResDataInfo = this.getServlet().getMngLockApi().getLockResDataInfo(id);
		if(lockResDataInfo == null) {
			throw new MsgException(String.format("デバイスが存在しません:%s", id));
		}
		returnOk(request, response ,"発見!:" + lockResDataInfo.toString());
	}

	@Override
	public void returnError(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_ERROR, e.getMessage());
		ServletUtil.returnJsp(request, response, "/jsp/system_error.jsp");
	}

	public void returnOk(HttpServletRequest request, HttpServletResponse response, String msg) throws IOException, ServletException{
		request.setAttribute(ServletUtil.S_REQ_ATT_KEY_INFO,  "成功:" + msg);
		ServletUtil.returnJsp(request, response, "/jsp/system_ok.jsp");
	}



	/**
	 *
	 * @param hArg
	 * @throws Exception
	 */
//	private LockResDataInfo getDevice(String serial_number) throws Exception{
//		LockResDataInfo res = null;
//		LockResInfoList lockResInfoList = this.getServlet().getMngLockApi().getAllDevices();
//		if(lockResInfoList == null) {
//			return null;
//		}
//		for(LockResDataInfo lockResDataInfo: lockResInfoList.getData()) {
//			LockResAttributesInfo tmpLockResAttributesInfo = lockResDataInfo.getAttributes();
//			String tmpSerial_number = tmpLockResAttributesInfo.getSerial_number();
//			if(serial_number.equals(tmpSerial_number)) {
//				res = lockResDataInfo;
//				break;
//			}
//		}
//		return res;
//	}





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
