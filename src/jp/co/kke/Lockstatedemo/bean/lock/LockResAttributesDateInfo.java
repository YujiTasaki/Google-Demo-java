package jp.co.kke.Lockstatedemo.bean.lock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResAttributesDateInfo {
	private String start_date;
	private String end_date;
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResAttributesDateInfo [");
		if (start_date != null) {
			builder.append("start_date=");
			builder.append(start_date);
			builder.append(", ");
		}
		if (end_date != null) {
			builder.append("end_date=");
			builder.append(end_date);
		}
		builder.append("]");
		return builder.toString();
	}


}
