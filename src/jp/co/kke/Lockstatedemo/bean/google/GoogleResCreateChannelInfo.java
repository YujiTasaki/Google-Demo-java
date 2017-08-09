package jp.co.kke.Lockstatedemo.bean.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleResCreateChannelInfo {
	/**
	 * "api#channel",
	 */
	private String kind;
	/**
	 * ID you specified for this channel.
	 */
	private String id;
	/**
	 * ID of the watched resource.
	 */
	private String resourceId;
	/**
	 * Version-specific ID of the watched resource
	 */
	private String resourceUri;
	/**
	 * Present only if one was provided.
	 */
	private String token;
	/**
	 * Actual expiration time as Unix timestamp (in ms), if applicable.
	 */
	private Long expiration;
	/**
	 * @return kind
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind セットする kind
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
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
	 * @return resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}
	/**
	 * @param resourceId セットする resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	/**
	 * @return resourceUri
	 */
	public String getResourceUri() {
		return resourceUri;
	}
	/**
	 * @param resourceUri セットする resourceUri
	 */
	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
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
