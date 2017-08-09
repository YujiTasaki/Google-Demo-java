package jp.co.kke.Lockstatedemo.bean.google;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Gmail送付要求Json構造クラス
 * 5MBまではこの形式で送付可能
 *-----------------------------------------------------
 * POST https://www.googleapis.com/gmail/v1/users/{USER_ID}/messages/send
　* Content-Type: application/json
 * Authorization: Bearer {ACCESS_TOKEN}
 *
 * {
 *  "raw": "{MESSAGE_URL_SAFE_BASE64_ENCODED}"
 * }
 *-----------------------------------------------------
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleReqGmailSendInfo {
	/**
	 * メール情報（MESSAGE_URL_SAFE_BASE64_ENCODE済文字列）
	 * 5MB以下
	 */
	private String raw;

	/**
	 * @return raw
	 */
	public String getRaw() {
		return raw;
	}

	/**
	 * @param raw セットする raw
	 */
	public void setRaw(String raw) {
		this.raw = raw;
	}

}
