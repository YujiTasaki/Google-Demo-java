package jp.co.kke.Lockstatedemo.bean.lock;

import java.util.LinkedHashMap;
import java.util.Map;

public class LockResAccessPersonsDataInfo {

	private String type;
	private Map<String,String> attributes = new LinkedHashMap<String, String>();

	private String id;
	private Map<String,String> links = new LinkedHashMap<String, String>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getLinks() {
		return links;
	}

	public void setLinks(Map<String, String> links) {
		this.links = links;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResAccessPersonsDataInfo [type=");
		builder.append(type);
		builder.append(", attributes=");
		builder.append(attributes);
		builder.append(", id=");
		builder.append(id);
		builder.append(", links=");
		builder.append(links);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
