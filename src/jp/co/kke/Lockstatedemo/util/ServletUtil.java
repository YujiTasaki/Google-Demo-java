package jp.co.kke.Lockstatedemo.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServletUtil {
	/**
	 * ロガー
	 */
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ServletUtil.class);
	/**
	 * Request Key for Error
	 * エラーメッセージ取得用リクエストキー
	 */
	public static final String S_REQ_ATT_KEY_ERROR = "ERROR";
	public static final String S_REQ_ATT_KEY_INFO = "INFO";

	/**
	 * 　URL
	 * 相対URL
	 */

	/**
	 * Json形式で返信
	 * @param response
	 * @param responseInfo
	 * @param sc
	 * @throws IOException
	 */
	public static void returnJson(HttpServletResponse response, Object responseInfo, int sc) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		String responseJson = mapper.writeValueAsString(responseInfo);
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(sc);
		PrintWriter out = response.getWriter();
		out.print(responseJson);
	}

	/**
	 * Json形式で返信
	 * @param response
	 * @param responseInfo
	 * @throws IOException
	 */
	public static void returnJson(HttpServletResponse response, Object responseInfo) throws IOException{
		returnJson(response, responseInfo, HttpServletResponse.SC_OK);
	}

	/**
	 * Jspで返信
	 * @param request
	 * @param response
	 * @param jspPath
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void returnJsp(HttpServletRequest request, HttpServletResponse response, String jspPath) throws ServletException, IOException{
		RequestDispatcher dispatchers = request.getRequestDispatcher(jspPath);
		dispatchers.forward(request, response);
	}

	/**
	 * Get Bundle URL
	 * URL取得
	 * @return
	 */
	public static String getUrl(HttpServletRequest request){
		//AWS ELB対策　http>https
		String scheme = request.getScheme();
		String hscheme = request.getHeader("X-Forwarded-Proto");
	    if (hscheme != null && hscheme != "") {
	    	scheme = hscheme;
	    }
		int port = request.getServerPort();
		//httpリクエストポート番号（デフォルト80）
		String hport = request.getHeader("X-Forwarded-Port");
	    if (hport != null && hport != "") {
	      port = Integer.valueOf(hport);
	    }
	    String contextPath = SysParamUtil.getResourceString("URL_ALIAS", request.getContextPath());
		StringBuilder builder = new StringBuilder();
		builder.append(scheme);
		builder.append("://");
		builder.append(request.getServerName());
		builder.append(':');
		builder.append(port);
		builder.append(contextPath);
		return builder.toString();
	}
}
