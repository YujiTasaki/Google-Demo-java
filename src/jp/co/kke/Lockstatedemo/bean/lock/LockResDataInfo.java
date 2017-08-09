package jp.co.kke.Lockstatedemo.bean.lock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResDataInfo {

	private String type;
	private String id;
	private LockResAttributesInfo attributes;
	private LockResLinksInfo links;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LockResAttributesInfo getAttributes() {
		return attributes;
	}
	public void setAttributes(LockResAttributesInfo attributes) {
		this.attributes = attributes;
	}
	public LockResLinksInfo getLinks() {
		return links;
	}
	public void setLinks(LockResLinksInfo links) {
		this.links = links;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResDataInfo [");
		if (type != null) {
			builder.append("type=");
			builder.append(type);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (attributes != null) {
			builder.append("attributes=");
			builder.append(attributes);
			builder.append(", ");
		}
		if (links != null) {
			builder.append("links=");
			builder.append(links);
		}
		builder.append("]");
		return builder.toString();
	}



}
