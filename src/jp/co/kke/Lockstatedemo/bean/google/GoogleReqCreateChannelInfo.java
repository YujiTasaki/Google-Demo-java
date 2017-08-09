package jp.co.kke.Lockstatedemo.bean.google;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleReqCreateChannelInfo {
	/**
	 * channel ID.
	 */
	private String id;
	/**
	 * receiving URL
	 */
	private String type;
	/**
	 * receiving URL
	 */
	private String address;
	/**
	 * (Optional) Your token
	 */
	private String token;
	/**
	 * (Optional) requested channel expiration time.
	 */
	private Long expiration;
	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id セットする id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type セットする type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address セットする address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token セットする token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return expiration
	 */
	public Long getExpiration() {
		return expiration;
	}
	/**
	 * @param expiration セットする expiration
	 */
	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

}
