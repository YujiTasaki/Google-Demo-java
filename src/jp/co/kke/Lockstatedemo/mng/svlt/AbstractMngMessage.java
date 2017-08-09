package jp.co.kke.Lockstatedemo.mng.svlt;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.kke.Lockstatedemo.MainServlet;

abstract public class AbstractMngMessage {

	/**
	 * サーブレット自身
	 */
	protected MainServlet servlet = null;

	/**
	 * 初期化処理
	 * （MngServlet内部に保持などを実行）
	 * @param servlet サーブレット自身
	 * @param request リクエスト
	 */
	public void init(MainServlet servlet, HttpServletRequest request)
	{
		this.servlet = servlet;
	}

	public MainServlet getServlet() {
		return servlet;
	}

	/**
	 * 受信した要求のデフォルト処理メソッド
	 *
	 * @param svlt サーブレット自身
	 * @param hArg ハッシュ化された受信引数
	 * @param request HTTPサーブレットの受信情報オブジェクト
	 * @param res HTTPサーブレットの返信情報オブジェクト
	 */
	abstract public void doJob(Map <String, Object> hArg, HttpServletRequest request, HttpServletResponse response) throws Exception;

	/**
	 * エラー処理（MngMessage種別ごとにエラー挙動が違う）
	 * @param request
	 * @param response
	 * @param e
	 */
	abstract public void returnError(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception;

}
