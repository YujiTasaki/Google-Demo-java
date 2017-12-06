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
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.kke.Lockstatedemo.bean.google.GoogleReqCreateChannelInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleReqGmailSendInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCalendarEventsListInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResCreateChannelInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResGmailSendInfo;
import jp.co.kke.Lockstatedemo.bean.google.GoogleResOAuthInfo;
import jp.co.kke.Lockstatedemo.mng.MsgException;
/**
 * GoogleAPIユーティリティークラス
 * @author KKE
 */
public class GoogleApiUtil {
	/**
	 * ロガー
	 */
	private static Logger logger = Logger.getLogger(GoogleApiUtil.class);

	/**
	 * 通信用文字コード
	 */
    private final static String S_CHARSET = "UTF-8";

	/**
	 * OAuth2クライアントID
	 */
	private static final String S_CLIENT_ID       = SysParamUtil.getResourceString("GOOGLE_OAUTH_CLIENT_ID");

	/**
	 * OAuth2クライアント・シークレット（暗号化のソルトにあたるもの）
	 */
	private static final String S_CLIENT_SECRET   = SysParamUtil.getResourceString("GOOGLE_OAUTH_CLIENT_SECRET");

	/**
	 * OAuth2リダイレクトURL
	 */
	private static final String S_REDIRECT_URI    = SysParamUtil.getResourceString("GOOGLE_OAUTH_REDIRECT_URI");

	/**
	 * OAuth 2.0認証を行うページのURL
	 */
	private static final String S_OAUTH_ENDPOINT        = SysParamUtil.getResourceString("GOOGLE_OAUTH_ENDPOINT");

	/**
	 * カレンダ参照用スコープ
	 */
	private static final String S_API_SCOPE_CALENDAR    = "https://www.googleapis.com/auth/calendar";

	/**
	 * Gmail送付用スコープ
	 */
	private static final String S_API_SCOPE_GMAIL    = "https://www.googleapis.com/auth/gmail.send";

	/**
	 * カレンダ参照用スコープ
	 */
	private static final String S_API_SCOPES    = S_API_SCOPE_CALENDAR + " " + S_API_SCOPE_GMAIL;


	/**
	 * LockStateAPIのベースURL
	 */
	private static final String S_API_ENDPOINT        = SysParamUtil.getResourceString("GOOGLE_API_ENDPOINT");

	/**
	 * OAuth2トークン取得用URL
	 */
	private static final String S_API_GET_OAUTH_TOKEN_URI    = S_API_ENDPOINT + "/oauth2/v4/token";

	/**
	 * Gmail送付用URL
	 */
	private static final String S_API_SEND_GMAIL_URI    = S_API_ENDPOINT + "/gmail/v1/users/me/messages/send";


	/**
	 * Authorizationコード取得用URL
	 * リダイレクトで使用
	 * @return
	 */
	/*
	public static String getAuthorizationCodeUrl() {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("response_type", "code");
		paramMap.put("client_id", S_CLIENT_ID);
		paramMap.put("redirect_uri", S_REDIRECT_URI);
		paramMap.put("scope", S_API_SCOPES);
		paramMap.put("access_type", "offline");
		paramMap.put("prompt", "consent");
		String param = convParam(paramMap);
		return S_OAUTH_ENDPOINT + "/o/oauth2/v2/auth?" + param;
	}
	*/
	public static String getAuthorizationCodeUrl(String s_CID, String s_URL) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("response_type", "code");
		paramMap.put("client_id", s_CID);
		paramMap.put("redirect_uri", s_URL);
		paramMap.put("scope", S_API_SCOPES);
		paramMap.put("access_type", "offline");
		paramMap.put("prompt", "consent");
		String param = convParam(paramMap);
		return S_OAUTH_ENDPOINT + "/o/oauth2/v2/auth?" + param;
	}

	/**
	 * CalendarChannel作成用URL
	 * リダイレクトで使用
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getCalendarEventsWatchUrl(String calendarId) throws UnsupportedEncodingException {
		return getCalendarEventsUrl(calendarId)+"/watch";
	}

	/**
	 * Calendar作成用URL
	 * リダイレクトで使用
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getCalendarEventsUrl(String calendarId) throws UnsupportedEncodingException {
		calendarId = URLEncoder.encode(calendarId, S_CHARSET);
		return S_API_ENDPOINT + "/calendar/v3/calendars/" + calendarId + "/events";
	}

	/**
	 * アクセストークン取得(Json形式)
	 * @param authorizationCode
	 * @return　返信データ(Json形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static String requestOAuthTokenJson(String authorizationCode) throws IOException, MsgException{
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("code", authorizationCode);
		paramMap.put("client_id", S_CLIENT_ID);
		paramMap.put("client_secret", S_CLIENT_SECRET);
		paramMap.put("redirect_uri", S_REDIRECT_URI);
		paramMap.put("grant_type", "authorization_code");
		paramMap.put("access_type", "offline");
		String param = convParam(paramMap);
		return doOAuthRequest(S_API_GET_OAUTH_TOKEN_URI, param);
	}

	/**
	 * アクセストークン取得(返信オブジェクト形式)
	 * @param authorizationCode
	 * @return　返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static GoogleResOAuthInfo requestOAuthToken(String authorizationCode) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = requestOAuthTokenJson(authorizationCode);
		logger.info("requestOAuthToken:"+json);
		return mapper.readValue(json, GoogleResOAuthInfo.class);
	}

	/**
	 * アクセストークンのリフレッシュ(Json形式)
	 * @param refresh_token
	 * @return 返信データ(Json形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static String refreshOAuthTokenJson(String refresh_token) throws IOException, MsgException{
		Map<String, String> paramMap =  new HashMap<String, String>();
		paramMap.put("client_id", S_CLIENT_ID);
		paramMap.put("client_secret", S_CLIENT_SECRET);
		paramMap.put("refresh_token", refresh_token);
		paramMap.put("grant_type", "refresh_token");
		paramMap.put("access_type", "offline");
		paramMap.put("prompt", "consent");
		String param = convParam(paramMap);
		return doOAuthRequest(S_API_GET_OAUTH_TOKEN_URI, param);
	}

	/**
	 * アクセストークンのリフレッシュ(返信オブジェクト形式)
	 * @param refresh_token
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws IOException
	 * @throws MsgException
	 */
	public static GoogleResOAuthInfo refreshOAuthToken(String refresh_token) throws IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = refreshOAuthTokenJson(refresh_token);
		logger.info("refreshOAuthToken:"+json);
		return mapper.readValue(json, GoogleResOAuthInfo.class);
	}

	/**
	 * Gmail送付(Json形式)
	 * @param mimeMessage メール構造
	 * @param access_token
	 * @return 返信データ(Json形式)
	 * @throws MessagingException
	 * @throws IOException
	 * @throws MsgException
	 */
	public static String sendMailJson(MimeMessage mimeMessage, String access_token) throws MessagingException, IOException, MsgException{
		String msg = convMessageWithEmail(mimeMessage);
		GoogleReqGmailSendInfo info = new GoogleReqGmailSendInfo();
		info.setRaw(msg);
		ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(info);
		return doApiRequest(S_API_SEND_GMAIL_URI, "POST", access_token, json);
	}

	/**
	 * Gmail送付(返信オブジェクト形式)
	 * @param mimeMessage メール構造
	 * @param access_token
	 * @return 返信データ(返信オブジェクト形式)
	 * @throws MessagingException
	 * @throws IOException
	 * @throws MsgException
	 */
	public static GoogleResGmailSendInfo sendMail(MimeMessage mimeMessage, String access_token) throws MessagingException, IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = sendMailJson(mimeMessage, access_token);
		logger.info("sendMail:"+json);
		return mapper.readValue(json, GoogleResGmailSendInfo.class);
	}

	/**
	 * カレンダ更新通知用チャンネル作成(Json形式)
	 * @param uuid
	 * @param calendarId
	 * @param access_token
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 * @throws MsgException
	 */
	public static String createCalendarChannelJson(String uuid, String calendarId, String access_token) throws MessagingException, IOException, MsgException{
		GoogleReqCreateChannelInfo info = new GoogleReqCreateChannelInfo();
		info.setId(uuid);
		info.setType("web_hook");
		info.setAddress(SysParamUtil.getResourceString("GOOGLE_PUSH_NOTIFICATIONS_URI"));
		ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(info);
        logger.info("json:" + json);
		return doApiRequest(getCalendarEventsWatchUrl(calendarId), "POST", access_token, json);
	}

	/**
	 * カレンダ更新通知用チャンネル作成(返信オブジェクト形式)
	 * @param uuid
	 * @param calendarId
	 * @param access_token
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 * @throws MsgException
	 */
	public static GoogleResCreateChannelInfo createCalendarChannel(String uuid, String calendarId, String access_token) throws MessagingException, IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = createCalendarChannelJson(uuid, calendarId, access_token);
		logger.info("sendCreateCalendarChannel:"+json);
		return mapper.readValue(json, GoogleResCreateChannelInfo.class);
	}

//	/**
//	 * 更新カレンダ取得
//	 * @param calendarId
//	 * @param access_token
//	 * @return
//	 * @throws MessagingException
//	 * @throws IOException
//	 * @throws MsgException
//	 */
//	public static String getCalendarEventListJson(String calendarId, String access_token) throws MessagingException, IOException, MsgException{
//		Map<String, String> paramMap = new HashMap<String, String>();
//		paramMap.put("orderBy", "startTime");
//		paramMap.put("singleEvents", "true");
//		paramMap.put("timeZone", "Asia/Tokyo");
//		OffsetDateTime updateMinDateTime = OffsetDateTime.now().minusSeconds(60);  //今から60秒前以降のアップデートを取ってくる
//		paramMap.put("updatedMin", updateMinDateTime.format(DateTimeFormatter.ISO_INSTANT));
//		String param = convParam(paramMap);
//		String url = getCalendarEventsUrl(calendarId)+ "?" + param;
//		logger.info("url:" + url);
//		return doApiRequest(url, "GET", access_token, null);
//	}
	/**
	 * 更新カレンダ取得
	 * @param calendarId
	 * @param access_token
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 * @throws MsgException
	 */
	public static String getCalendarEventListJson(String calendarId, String updatedMin, String access_token) throws MessagingException, IOException, MsgException{
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("orderBy", "updated");
		paramMap.put("singleEvents", "true");
		paramMap.put("timeZone", "Asia/Tokyo");
		paramMap.put("updatedMin", updatedMin);
		String param = convParam(paramMap);
		String url = getCalendarEventsUrl(calendarId)+ "?" + param;
		logger.info("url:" + url);
		return doApiRequest(url, "GET", access_token, null);
	}
	/**
	 * 更新カレンダ取得
	 * @param calendarId
	 * @param access_token
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 * @throws MsgException
	 */
	public static GoogleResCalendarEventsListInfo getCalendarEventList(String calendarId, String updateMin, String access_token) throws MessagingException, IOException, MsgException{
		ObjectMapper mapper = new ObjectMapper();
		String json = getCalendarEventListJson(calendarId, updateMin, access_token);
		logger.info("getCalendarEventList:"+json);
		return mapper.readValue(json, GoogleResCalendarEventsListInfo.class);
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
	        InputStream stream;
	        int responseCode = connection.getResponseCode();
	        boolean isErroCode = isErrorCode(responseCode);
	        if(isErroCode){
	        	stream = connection.getErrorStream();
	        }else{
	        	stream = connection.getInputStream();
	        }
	        reader  = new BufferedReader(new InputStreamReader(stream, S_CHARSET));
	        String line = null;
	        while((line = reader.readLine()) != null){
	        	res.append(line).append("\n");
	        }
	        if(isErroCode){
	        	logger.error("error doOAuthRequest:\n" + res);
	        	throw new MsgException("認証に失敗しました");
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
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty( "Authorization" , "Bearer " + access_token);
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
	        if(isErroCode){
	        	stream = connection.getErrorStream();
	        }else{
	        	stream = connection.getInputStream();
	        }
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

	private static boolean isErrorCode(int responseCode){
		boolean res = false;
        if(responseCode != HttpURLConnection.HTTP_OK){
        //if(responseCode / 100 == 4 || responseCode / 100 == 5){
        	res = true;
        }
        return res;
	}

    /**
     * メール構造をURLセーフ
     * @param emailContent
     * @return
     * @throws MessagingException
     * @throws IOException
     */
	private static String convMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        return Base64.encodeBase64URLSafeString(bytes);
    }

	/**
	 *
	 * @param paramMap
	 * @return
	 */
	private static String convParam(Map<String, String> paramMap){
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
				//byte[] binaryValue = value.getBytes(S_CHARSET);
				//value = Base64.encodeBase64URLSafeString(binaryValue);
				res.append(key).append('=').append(value);
			} catch (UnsupportedEncodingException e) {
				logger.error("can't encode:" + value);
			}
		}
		return res.toString();
	}
}
