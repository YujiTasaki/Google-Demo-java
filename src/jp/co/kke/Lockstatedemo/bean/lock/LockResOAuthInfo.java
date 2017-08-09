package jp.co.kke.Lockstatedemo.bean.lock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResOAuthInfo {
	private String access_token;
	private int expires_in;
	private String token_type;
	private String refresh_token;
	private long created_at;

	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public long getCreated_at() {
		return created_at;
	}
	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResOAuthInfo [access_token=");
		builder.append(access_token);
		builder.append(", expires_in=");
		builder.append(expires_in);
		builder.append(", token_type=");
		builder.append(token_type);
		builder.append(", refresh_token=");
		builder.append(refresh_token);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append("]");
		return builder.toString();
	}


}
