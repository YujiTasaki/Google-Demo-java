package jp.co.kke.Lockstatedemo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.mng.MngDbLockParam;
import jp.co.kke.Lockstatedemo.mng.MngGoogleApi;
import jp.co.kke.Lockstatedemo.mng.MngLockApi;
import jp.co.kke.Lockstatedemo.mng.MngSchedule;
import jp.co.kke.Lockstatedemo.mng.svlt.AbstractMngMessage;
import jp.co.kke.Lockstatedemo.util.SysParamUtil;

/**
 * デモ用メインサーブレット
 * アクセスURLは
 * http://[サーバIPアドレス]:8080/LockstateDemo/api/*
 * http://localhost:8080/LockstateDemo/api/*
 * Servlet implementation class MainServlet
 */
//@WebServlet("/api/*")
public class MainServlet extends HttpServlet {

	/**
	 * シリアライズ用ID
	 */
	private static final long serialVersionUID = 2863292277574038351L;

	/**
	 * ロガー
	 */
	private static Logger logger = Logger.getLogger(MainServlet.class);

	/**
	 * api用　urlパタン
	 */
	/**
	 *
	 */
	private Pattern apiPattern = Pattern.compile(".*/api/(.+)$");

	/**
	 * LockstatesAPI管理クラス
	 */
	private MngLockApi mngLockApi = null;

	/**
	 * GoogleAPI管理クラス
	 */
	private MngGoogleApi mngGoogleApi = null;

	/**
	 *　スケジュールチェック定期実行管理クラス
	 */
	private MngSchedule mngSchedule = null;

	/**
	 * DBアクセス管理クラス
	 */
	private MngDbLockParam mngDbLockParam = null;


	/* (非 Javadoc)
	 * サーブレット初期化
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			String realPath = getServletContext().getRealPath("/");
			realPath = SysParamUtil.getResourceString("DIR_PATH", realPath);
			this.mngDbLockParam = new MngDbLockParam(realPath);
			this.mngLockApi = new MngLockApi();
			this.mngGoogleApi = new MngGoogleApi();
			this.mngSchedule = new MngSchedule(this);
		} catch (Exception e) {
			logger.error("init", e);
			throw new ServletException(e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		if(this.mngSchedule != null) {
			this.mngSchedule.close();
		}
		if(this.mngGoogleApi != null) {
			this.mngGoogleApi.close();
		}
		if(this.mngLockApi != null) {
			this.mngLockApi.close();
		}
		if(this.mngDbLockParam != null) {
			this.mngDbLockParam.close();
		}
	}

	/**
	 * GET要求時
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doEventMsg(request, response);
	}

	/**
	 * POST要求時
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doEventMsg(request, response);
	}

	/**
	 * エラー返却処理
	 * @param request
	 * @param response
	 * @param sysEx
	 */
	public void returnError(HttpServletResponse response, Exception ex){
		logger.error("error return.",ex);
		try {
			//500番返し
			response.sendError(500);
		} catch (IOException e) {
			logger.error("error return.",e);
		}
	}

	/**
	 * 「要求識別子」を得る
	 * （URL の / 以降の文字列（終端または "?" の直前まで）を返す。
	 *  ＊最初の一文字は大文字に変更
	 * @param request
	 * @return 要求識別子（＝要求処理クラス名）
	 */
	private String getREQ(HttpServletRequest request)
	{
		String res = null;
		try {
			//System.out.println( "request.getServletPath()=" + request.getServletPath() );
			//System.out.println( "request.getRequestURL()=" + request.getRequestURL() );
			String url = request.getRequestURL().toString();
			//System.out.println( "url=" + url );
			Matcher apiMatcher = apiPattern.matcher(url);
			if (apiMatcher.find() == false){
				return null;
			}
			String sReq = apiMatcher.group(1);
			//System.out.println( "sReq=" + sReq );
			res = sReq.trim();
			if(res.length() <= 0){//空文字の場合はnull返し
				return null;
			}
//
//			sReq = sReq.toLowerCase();//いったん全小文字
//			String sHeader = sReq.substring(0,1);//最初の1文字目を取得
//			sHeader = sHeader.toUpperCase();//1文字目を大文字に
//			res = (new StringBuilder().append(sHeader).append(sReq.substring(1,sReq.length()))).toString();
			//System.out.println( "res=" + res );
		} catch (Exception e) {
			logger.error("RequestID was not able to be found in url.:" + request.getServletPath(), e);
			return null;
		}
		return res;
	}

	/**
	 * 処理オブジェクトのインスタンス生成
	 * @param sReqType 要求識別子
	 * @return
	 */
	protected AbstractMngMessage getMngMessage(String sReqType){
		AbstractMngMessage res = null;
		try {
			String sReqTypeLower = sReqType.toLowerCase();//いったん全小文字
			String aPackageName = SysParamUtil.getResourceString("SYS_CONTROLS_PACKAGE");
			@SuppressWarnings("rawtypes")
			Class cls = null;

			/*--- 本システムクラス検索 ---*/
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL url = cl.getResource(aPackageName.replace(".", "/"));
			File dir = new File(url.getPath());
			for (String path : dir.list()) {
				if (path.endsWith(".class")) {
					String tmpClassName = path.substring(0, path.length() - 6);
					String tmpClassNameLower = tmpClassName.toLowerCase();
					if(sReqTypeLower.equals(tmpClassNameLower)) {//
						String className = (new StringBuilder())
							.append(aPackageName)
							.append(tmpClassName)
							.toString();
						cls = Class.forName(className);
						break;
					}
				}
			}
			if(cls != null) {
				/*--- 処理オブジェクトのインスタンス生成 ----*/
				res = (AbstractMngMessage)cls.newInstance();
			}
		} catch (ClassNotFoundException e) { // そんなクラスはない？
			logger.error("RequestID:[" + sReqType + "] is not exist this system.", e);
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
		return res;
	}

	/**
	 * HTTP（POST/GET）にて受信した要求の処理分岐＆実行＆返信
	 * ＊Exception発生時の処理を記述
	 * @param request HTTPサーブレットの受信情報オブジェクト
	 * @param response HTTPサーブレットの返信情報オブジェクト
	 */
	protected void doEventMsg(HttpServletRequest request, HttpServletResponse response){
		AbstractMngMessage mngMessage = null;
		try {
			/*--- 要求に対応する処理分岐 ---*/
			String	sReqType = getREQ(request);// 要求識別子
			if(sReqType == null){ // 要求識別がない？
				Exception sysEx = new IllegalArgumentException("RequestID was not able to be found in url.:" + request.getRequestURL() );
				returnError(response, sysEx);
				return;
			}
			mngMessage = getMngMessage(sReqType);
			if(mngMessage == null){ // 処理オブジェクトがない？
				Exception sysEx = new IllegalArgumentException("RequestID:[" + sReqType + "] is not exist this system.");
				returnError(response, sysEx);
				return;
			}
			mngMessage.init(this, request);
			//ここからはmngMessage種別ごとのエラー処理に移行
			/*--- 引数解析---*/
			Map <String, Object> hArg = getArgByHash(request); //引数を文字コード変換＆ハッシュ蓄積
			mngMessage.doJob(hArg, request, response);//処理
		} catch (Exception e) {	//処理エラーが生じた場合
			logger.error("error doEventMsg.", e);
			try {
				if(mngMessage != null) {
					mngMessage.returnError(request, response, e);//それぞれのメッセージに応じたエラー返却
				}else {
					returnError(response, e);//自前エラー返却
				}
			} catch (Exception e1) {//エラー返却自体に失敗した場合
				logger.error("error returnError.", e1);
				returnError(response, e);//自前エラー返却
			}
		}
	}

	/**
	 * HTTPリクエストの全引数（キー文字列と値）をハッシュマップに追加格納する
	 * @param 受信したHTTPリクエスト (HttpServletRequest）
	 * @return hash	分類結果の格納先（ハッシュマップ）
	 */
	protected Map<String, Object> getArgByHash(HttpServletRequest request) throws Exception	{
		Map<String, Object> res = new HashMap<String, Object>();
		Enumeration<String> items = request.getParameterNames();
		while(items.hasMoreElements()){
			String sKey = (items.nextElement()); // キー取得
			Object sValue = null;
			if (sKey != null) {
				sValue = request.getParameterValues(sKey)[0]; // 値取得
			}
			res.put(sKey, sValue); // キーと値追加
		}
		return res;
	}

	/**
	 * LockstatesAPI管理クラス取得
	 * @return
	 */
	public MngLockApi getMngLockApi() {
		return mngLockApi;
	}
	/**
	 * GoogleAPI管理クラス
	 * @return
	 */
	public MngGoogleApi getMngGoogleApi() {
		return mngGoogleApi;
	}

	/**
	 * スケジュールチェック定期実行管理クラス
	 * @return mngSchedule
	 */
	public MngSchedule getMngSchedule() {
		return mngSchedule;
	}

	/**
	 * @return mngDbLockParam
	 */
	public MngDbLockParam getMngDbLockParam() {
		return mngDbLockParam;
	}

}
