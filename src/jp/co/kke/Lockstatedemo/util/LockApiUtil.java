
package jp.co.kke.Lockstatedemo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.kke.Lockstatedemo.bean.lock.LockReqAccessPersonsInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResAccessPersonsInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResAttributesInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResDataInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResInfo;
import jp.co.kke.Lockstatedemo.bean.lock.LockResInfoList;
import jp.co.kke.Lockstatedemo.bean.lock.LockResOAuthInfo;
import jp.co.kke.Lockstatedemo.mng.MsgException;
/**
 * LockstatesAPIユーティリティークラス
 * @author KKE
 */
public class LockApiUtil {

	//b60a015f448b35b8e990fb8d875468e952b0a0679fbff1cd42452ec8ead23a26
	/**
	 * ロガー
	 */
	private static Logger logger = Logger.getLogger(LockApiUtil.class);
	/**
	 * OAuth 2.0認証を行うページのURL
	 */
	private static final String S_OAUTH_ENDPOINT        = SysParamUtil.getResourceString("LOCK_OAUTH_ENDPOINT");

	/**
	 * LockStateAPIのURL
	 */
	private static final String S_API_ENDPOINT        = SysParamUtil.getResourceString("LOCK_API_ENDPOINT");

	/**
	 * OAuth2クライアントID。
	 */
	private static final String S_CLIENT_ID       = SysParamUtil.getResourceString("LOCK_OAUTH_CLIENT_ID");

	/**
	 * OAuth2クライアント・シークレット（暗号化のソルトにあたるもの）
	 */
	private static final String S_CLIENT_SECRET   = SysParamUtil.getResourceString("LOCK_OAUTH_CLIENT_SECRET");

	/**
	 * OAuth2リダイレクトURL(変更できないのでアプリ用のURL)
	 */
	private static final String S_REDIRECT_URI    = SysParamUtil.getResourceString("LOCK_OAUTH_REDIRECT_URI");

	/**
	 * OAuth2トークン取得用URL
	 */
	private static final String S_API_GET_OAUTH_TOKEN_URI    = S_OAUTH_ENDPOINT + "/oauth/token";

	/**
	 * 全デバイス情報取得用URL
	 */
	private static final String S_API_ALL_DEVICES_URI    = S_API_ENDPOINT + "/devices";


	private static final String S_API_CREATE_USERS_URI    = S_API_ENDPOINT + "/access_persons";


	/**
	 * 通信用文字コード
	 */
    private final static String S_CHARSET = "UTF-8";
	/**
	 * Authorizationコード取得用URL
	 * リダイレクトで使用
	 * @return
	 */
	public static String getAuthorizationCodeUrl() {
		Map<String, String> paramMap = getAuthorizationCodeParamMap();
		String param = getParam(paramMap);
		return S_OAUTH_ENDPOINT + "/oauth/authorize?" + param;
	}

	/**
	 * デバイスの鍵の開閉用URL
	 * @param id
	 * @param isLock
	 * @return
	 */
	private static String getLockDeviceUrl(String id, boolean isLock){
		StringBuilder res = new StringBuilder();
		res.append(S_API_ENDPOINT);
//		res.append("/devices/");
		res.append("/locks/");
		res.append(id);
		res.append('/');
		if(isLock){
			res.append("lock");
		}else{
			res.append("unlock");
		}
		return res.toString();
	}

    /**
     * アクセストークン更新用パラメータ生成
     * @param refresh_token
     * @return
     */
	private static Map<String, String> getRefreshTokenParamMap(String refresh_token){
		Map<String, String> res = new HashMap<String, String>();
		res.put("client_id", S_CLIENT_ID);
		res.put("client_secret", S_CLIENT_SECRET);
		res.put("refresh_token", refresh_token);
		res.put("grant_type", "refresh_token");
		return res;
	}

	/**
	 * OAuth認証用パラメータ生成
	 * @param authorizationCode
	 * @return
	 */
	private static Map<String, String> getOAuthTokenParamMap(String authorizationCode){
		Map<String, String> res = new HashMap<String, String>();
		res.put("code", authorizationCode);
		res.put("client_id", S_CLIENT_ID);
		res.put("client_secret", S_CLIENT_SECRET);
		res.put("redirect_uri", S_REDIRECT_URI);
		res.put("grant_type", "authorization_code");
		return res;
	}

	/**
	 * AuthorizationCode生成用パラメータ生成
	 * @return
	 */
	private static Map<String, String> getAuthorizationCodeParamMap(){
		Map<String, String> res = new HashMap<String, String>();
		res.put("client_id", S_CLIENT_ID);
		res.put("response_type", "code");
		res.put("redirect_uri", S_REDIRECT_URI);
		return res;
	}

	/**
	 * パラメータをGet用文字列に変換
	 * @param paramMap
	 * @return
	 */
	private static String getParam(Map<String, String> paramMap){
		StringBuilder res = new StringBuilder();
		boolean isFast = true;
		for(String key: paramMap.keySet()){
			if(isFast){
				isFast = false;
			}else{
				res.append('&');
			}
			String value = paramMap.get(key);
			try {
				value = URLEncoder.encode(value, S_CHARSET);
				res.append(key).append('=').append(value);
			} catch (UnsupportedEncodingException e) {
				logger.error("can't encode:" + value);
			}

		}
		return res.toString();
	}

	/**
	 * アクセストークン取得(Json形式)
	 * @param authorizationCode
	 * @return　返信データ(Json形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	private static String requestOAuthTokenJson(String authorizationCode) throws IOException, MsgException{
		Map<String, String> paramMap = getOAuthTokenParamMap(authorizationCode);
		String param = getParam(paramMap);
		return doOAuthRequest(S_API_GET_OAUTH_TOKEN_URI, param);
	}
	/**
	 * アクセストークンのリフレッシュ(Json形式)
	 * @param refresh_token
	 * @return 返信データ(Json形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	private static String refreshOAuthTokenJson(String refresh_token) throws IOException, MsgException{
		Map<String, String> paramMap = getRefreshTokenParamMap(refresh_token);
		String param = getParam(paramMap);
		return doOAuthRequest(S_API_GET_OAUTH_TOKEN_URI, param);
	}

	/**
	 * 全デバイス情報の取得(Json形式)
	 * @param access_token
	 * @return 返信データ(Json形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	private static String getAllDevicesJson(String access_token) throws IOException, MsgException{
		return doApiRequest(S_API_ALL_DEVICES_URI, "GET", access_token, null);
	}


	/**
	 *アクセスゲストの作成(Json形式)
	 * @param access_token
	 * @param info
	 * @return 返信データ(Json形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static String createUsersJson(String access_token, LockReqAccessPersonsInfo info) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        logger.info(json);
		return doApiRequest(S_API_CREATE_USERS_URI, "POST", access_token, json);
	}



	/**
	 *アクセスゲストの作成(返信オブジェクト形式)
	 * @param access_token
	 * @param info
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static LockResAccessPersonsInfo createUsers(String access_token, LockReqAccessPersonsInfo info) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = createUsersJson(access_token, info);
		return mapper.readValue(json, LockResAccessPersonsInfo.class);
	}


	/**
	 * デバイスの鍵の開閉(Json形式)
	 * @param access_token
	 * @param id
	 * @param isLock　鍵の開閉（true:閉める false:開ける)
	 * @return 返信データ(Json形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	private static String doLockDeviceJson(String access_token, String id, boolean isLock) throws IOException, MsgException{
		String url = getLockDeviceUrl(id, isLock);
		//return doApiPutRequest(url, access_token);
		return doApiRequest(url, "PUT", access_token, null);
	}
	/**
	 * アクセストークン取得(返信オブジェクト形式)
	 * @param authorizationCode
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static LockResOAuthInfo requestOAuthToken(String authorizationCode) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = requestOAuthTokenJson(authorizationCode);
		return mapper.readValue(json, LockResOAuthInfo.class);
	}
	/**
	 * アクセストークンのリフレッシュ(返信オブジェクト形式)
	 * @param refresh_token
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static LockResOAuthInfo refreshOAuthToken(String refresh_token) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = refreshOAuthTokenJson(refresh_token);
		return mapper.readValue(json, LockResOAuthInfo.class);
	}
	/**
	 * 全デバイス情報の取得(返信オブジェクト形式)
	 * @param access_token
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static LockResInfoList getAllDevices(String access_token) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = getAllDevicesJson(access_token);
		return mapper.readValue(json, LockResInfoList.class);
	}
	/**
	 * デバイスの鍵の開閉(返信オブジェクト形式)
	 * @param access_token
	 * @param id
	 * @param isLock　鍵の開閉（true:閉める false:開ける)
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static LockResInfo doLockDevice(String access_token, String id, boolean isLock) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = doLockDeviceJson(access_token, id, isLock);
		return mapper.readValue(json, LockResInfo.class);
	}

	/**
	 * 指定デバイスの情報取得
	 * @param access_token
	 * @param serial_number
	 * @return  返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static LockResDataInfo getLockResDataInfo(String access_token, String serial_number) throws IOException, MsgException{
		LockResDataInfo res = null;
		LockResInfoList resInfo = getAllDevices(access_token);
		List<LockResDataInfo> datas = resInfo.getData();
		if(datas!= null){
			for(LockResDataInfo tmpDataInfo: datas){
				LockResAttributesInfo tmpAttributesInfo = tmpDataInfo.getAttributes();
				if(tmpAttributesInfo == null){
					continue;
				}
				String tmpSerial_number = tmpAttributesInfo.getSerial_number();
				if(serial_number.equals(tmpSerial_number)){
					res = tmpDataInfo;
					break;
				}
			}
		}
		return res;
	}

	/**
	 * アクセストークン取得通信
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 * @throws MsgException
	 */
	private static String doOAuthRequest(String url, String param) throws IOException, MsgException{
		logger.info("doOAuthRequest:"+url);
		StringBuilder res = new StringBuilder();
        HttpURLConnection connection = null;
		BufferedReader reader =null;
		try{
	        byte[] payload = param.toString().getBytes(S_CHARSET);
	        connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setRequestMethod("POST");		//POSTでもGETでも関係なく動いた
	        connection.setDoOutput(true);
	        connection.setRequestProperty("Content-Length", String.valueOf(payload.length));
	        connection.getOutputStream().write(payload);
	        connection.getOutputStream().flush();
	        reader  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line = null;
	        while((line = reader.readLine()) != null){
	        	res.append(line).append("\n");
	          //System.out.println("line:"+line);
	        }
	        res.append(line).append("\n");
	        int status = connection.getResponseCode();
	        if (status != HttpURLConnection.HTTP_OK) {
	        	throw new MsgException(String.format("認証に失敗しました：%s", res.toString()));
	        }
		} finally{
			//コネクションおよびリーダーのクローズ処理
			if( reader != null){//リーダーが正常に生成されているなら
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
					//内部で例外発生かつコネクションクローズで例外が発生すると
					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
					//例外はキャッチしておく
				}
			}
			//コネクションおよびリーダーのクローズ処理
			if(connection != null){//コネクションが正常に生成されているなら
				try {
					connection.disconnect();//クローズ
				} catch (Exception e) {
					e.printStackTrace();
					//内部で例外発生かつコネクションクローズで例外が発生すると
					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
					//例外はキャッチしておく
				}
			}
		}
		return res.toString();
	}

	/**
	 * API通信
	 * @param url
	 * @param method
	 * @param access_token
	 * @param param
	 * @return
	 * @throws IOException
	 * @throws MsgException
	 */
	private static String doApiRequest(String url, String method, String access_token, String param) throws IOException, MsgException{
		logger.info("doApiRequest:"+method +":"+ url);
		StringBuilder res = new StringBuilder();
        HttpURLConnection connection = null;
		BufferedReader reader =null;
		try{
	        connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setDoOutput(true);
	        connection.setRequestMethod(method);
	        connection.setRequestProperty( "Host" , "api.lockstate.jp");
	        connection.setRequestProperty( "Accept" , "application/vnd.lockstate.v1+json");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty( "Authorization" , "Bearer " + access_token );
	        if(param != null){//ボディーパラメータ有の場合
	        	byte[] payload = param.toString().getBytes(S_CHARSET);
	        	connection.getOutputStream().write(payload);
	        	connection.getOutputStream().flush();
	        }else {//ボディーパラメータ無の場合
	        	connection.connect();
	        }
	        InputStream stream;
	        int responseCode = connection.getResponseCode();
	        boolean isErroCode = isErrorCode(responseCode);
	        logger.info(responseCode);
	        stream = connection.getInputStream();
	        if(stream == null) {
	        	logger.info("stream == null");
	        	stream = connection.getErrorStream();
	        }
//	        if(isErroCode){
//	        	stream = connection.getErrorStream();
//	        }else{
//	        	stream = connection.getInputStream();
//	        }
	        reader  = new BufferedReader(new InputStreamReader(stream, S_CHARSET));
	        String line = null;
	        while((line = reader.readLine()) != null){
	        	res.append(line).append("\n");
	        }
	        if(isErroCode){
		        logger.error("error doApiRequest:\n" + res);
		        throw new MsgException("処理に失敗しました");
	        }
		} finally{
			//コネクションおよびリーダーのクローズ処理
			if( reader != null){//リーダーが正常に生成されているなら
				try {
					reader.close();
				} catch (Exception e) {
					logger.error(e.getMessage());
					//内部で例外発生かつコネクションクローズで例外が発生すると
					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
					//例外はキャッチしておく
				}
			}
			//コネクションおよびリーダーのクローズ処理
			if(connection != null){//コネクションが正常に生成されているなら
				try {
					connection.disconnect();//クローズ
				} catch (Exception e) {
					logger.error(e.getMessage());
					//内部で例外発生かつコネクションクローズで例外が発生すると
					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
					//例外はキャッチしておく
				}
			}
		}
		return res.toString();
	}



	/**
	 * レスポンスコードがエラーコードか否か
	 * @param responseCode
	 * @return
	 */
	private static boolean isErrorCode(int responseCode){
		boolean res = false;
        //if(responseCode != HttpURLConnection.HTTP_OK){
        if(responseCode / 100 == 4 || responseCode / 100 == 5){
        	res = true;
        }
        return res;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, InterruptedException, MsgException{
		String authorizationCode = "49f94d1e8738fe8b58427632e575c9e96eff4cd542ef1f90b14745f50460cfed";
		ObjectMapper mapper = new ObjectMapper();
		String resp;
		LockResOAuthInfo oAuthInfo;

//		oAuthInfo = mng.initOAuthToken(authorizationCode);
//		String access_token = oAuthInfo.getAccess_token();
//		String refresh_token = oAuthInfo.getRefresh_token();
//		System.out.println("access_token:" + access_token);
//		System.out.println("refresh_token:" + refresh_token);

		String access_token = "2abc871796ed059075cb7991cec31368aab570114069d659b4bcf52b43602f3f";


		LockResInfoList resInfo = LockApiUtil.getAllDevices(access_token);
		System.out.println(resInfo);

		String serial_number = "AC000W000195252";

		LockResDataInfo dataInfo = null;
		List<LockResDataInfo> datas = resInfo.getData();
		if(datas!= null){
			for(LockResDataInfo tmpDataInfo: datas){
				LockResAttributesInfo tmpAttributesInfo = tmpDataInfo.getAttributes();
				if(tmpAttributesInfo == null){
					continue;
				}
				String tmpSerial_number = tmpAttributesInfo.getSerial_number();
				if(serial_number.equals(tmpSerial_number)){
					dataInfo = tmpDataInfo;
					break;
				}
			}
		}
		System.out.println(dataInfo);

		String id = dataInfo.getId();
		System.out.println("id:" + id);
		Thread.sleep(10000);
		LockApiUtil.doLockDevice(access_token, id, false);
		Thread.sleep(10000);
		LockApiUtil.doLockDevice(access_token, id, true);
		Thread.sleep(10000);
		LockApiUtil.doLockDevice(access_token, id, false);
//		resp = mng.refreshOAuthToken(refresh_token);
//		oAuthInfo = mapper.readValue(resp, LockResOAuthInfo.class);
//		access_token = oAuthInfo.getAccess_token();
//		refresh_token = oAuthInfo.getRefresh_token();
//		System.out.println("access_token:" + access_token);
//		System.out.println("refresh_token:" + refresh_token);
//		System.out.println(oAuthInfo);
	}

//	private static String doApiPutRequest(String url, String access_token) throws IOException{
//		logger.info("doApiPutRequest:"+url);
//		StringBuilder res = new StringBuilder();
//        HttpURLConnection connection = null;
//		BufferedReader reader =null;
//		try{
//	        connection = (HttpURLConnection) new URL(url).openConnection();
//	        connection.setDoOutput(true);
//	        connection.setRequestMethod("PUT");
//	        connection.setRequestProperty( "Host" , "api.lockstate.com");
//	        connection.setRequestProperty( "Accept" , "application/vnd.lockstate.v1+json");
//	        connection.setRequestProperty( "Authorization" , "Bearer " + access_token );
//	        connection.connect();
//	        reader  = new BufferedReader(new InputStreamReader(connection.getInputStream(), S_CHARSET));
//	        String line = null;
//	        while((line = reader.readLine()) != null){
//	        	res.append(line);
//	        	System.out.println("line:"+line);
//	        }
//		} finally{
//			//コネクションおよびリーダーのクローズ処理
//			if( reader != null){//リーダーが正常に生成されているなら
//				try {
//					reader.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//					//内部で例外発生かつコネクションクローズで例外が発生すると
//					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
//					//例外はキャッチしておく
//				}
//			}
//			//コネクションおよびリーダーのクローズ処理
//			if(connection != null){//コネクションが正常に生成されているなら
//				try {
//					connection.disconnect();//クローズ
//				} catch (Exception e) {
//					e.printStackTrace();
//					//内部で例外発生かつコネクションクローズで例外が発生すると
//					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
//					//例外はキャッチしておく
//				}
//			}
//		}
//		return res.toString();
//	}
//

//	private static String doApiGetRequest(String url, String access_token) throws IOException{
//		logger.info("doApiGetRequest:"+url);
//		StringBuilder res = new StringBuilder();
//        HttpURLConnection connection = null;
//		BufferedReader reader =null;
//		try{
//	        connection = (HttpURLConnection) new URL(url).openConnection();
//
//	        connection.setRequestMethod("GET");
//	        connection.setRequestProperty( "Host" , "api.lockstate.com");
//	        connection.setRequestProperty( "Accept" , "application/vnd.lockstate.v1+json");
//	        connection.setRequestProperty( "Authorization" , "Bearer " + access_token );
//	        reader  = new BufferedReader(new InputStreamReader(connection.getInputStream(), S_CHARSET));
//	        String line = null;
//	        while((line = reader.readLine()) != null){
//	        	res.append(line);
//	        	logger.info("line:"+line);
//	        }
//		} finally{
//			//コネクションおよびリーダーのクローズ処理
//			if( reader != null){//リーダーが正常に生成されているなら
//				try {
//					reader.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//					//内部で例外発生かつコネクションクローズで例外が発生すると
//					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
//					//例外はキャッチしておく
//				}
//			}
//			//コネクションおよびリーダーのクローズ処理
//			if(connection != null){//コネクションが正常に生成されているなら
//				try {
//					connection.disconnect();//クローズ
//				} catch (Exception e) {
//					e.printStackTrace();
//					//内部で例外発生かつコネクションクローズで例外が発生すると
//					//内部生成時した例外がコネクションクルー時の例外で上書きされてしまう為
//					//例外はキャッチしておく
//				}
//			}
//		}
//		return res.toString();
//	}

}
