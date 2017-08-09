package jp.co.kke.Lockstatedemo.bean.lock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResInfoList {
	private List<LockResDataInfo> data;
	private LockResMetaInfo meta;
	public List<LockResDataInfo> getData() {
		return data;
	}
	public void setData(List<LockResDataInfo> data) {
		this.data = data;
	}
	public LockResMetaInfo getMeta() {
		return meta;
	}
	public void setMeta(LockResMetaInfo meta) {
		this.meta = meta;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResInfo [");
		if (data != null) {
			builder.append("data=");
			builder.append(data);
			builder.append(", ");
		}
		if (meta != null) {
			builder.append("meta=");
			builder.append(meta);
		}
		builder.append("]");
		return builder.toString();
	}


}
