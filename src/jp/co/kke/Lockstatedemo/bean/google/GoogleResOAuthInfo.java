package jp.co.kke.Lockstatedemo.bean.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * OAuth認証返信json構造クラス
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleResOAuthInfo {
	/**
	 * アクセストークン
	 */
	private String access_token;

	/**
	 * アクセストークン有効期限（秒）
	 */
	private int expires_in;

	/**
	 * トークンタイプ
	 */
	private String token_type;

	/**
	 * リフレッシュトークン
	 */
	private String refresh_token;


	/**
	 * @return access_token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @param access_token セットする access_token
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}



	/**
	 * @return expires_in
	 */
	public int getExpires_in() {
		return expires_in;
	}



	/**
	 * @param expires_in セットする expires_in
	 */
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}



	/**
	 * @return token_type
	 */
	public String getToken_type() {
		return token_type;
	}



	/**
	 * @param token_type セットする token_type
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}


	/**
	 * @return refresh_token
	 */
	public String getRefresh_token() {
		return refresh_token;
	}


	/**
	 * @param refresh_token セットする refresh_token
	 */
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}


	@Override
	public String toString() {
		return "GoogleResOAuthInfo [access_token=" + access_token + ", expires_in=" + expires_in + ", token_type="
				+ token_type + ", refresh_token=" + refresh_token + "]";
	}

}
