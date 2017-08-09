package jp.co.kke.Lockstatedemo.bean.lock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResErrorInfo {
	private String attribute;
	private List<String> messages;
	private List<String> full_messages;
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public List<String> getFull_messages() {
		return full_messages;
	}
	public void setFull_messages(List<String> full_messages) {
		this.full_messages = full_messages;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResErrorInfo [");
		if (attribute != null) {
			builder.append("attribute=");
			builder.append(attribute);
			builder.append(", ");
		}
		if (messages != null) {
			builder.append("messages=");
			builder.append(messages);
			builder.append(", ");
		}
		if (full_messages != null) {
			builder.append("full_messages=");
			builder.append(full_messages);
		}
		builder.append("]");
		return builder.toString();
	}

}
