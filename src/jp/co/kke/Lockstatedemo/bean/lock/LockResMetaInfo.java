package jp.co.kke.Lockstatedemo.bean.lock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResMetaInfo {
	private Integer page;
	private Integer per_page;
	private Integer total_pages;
	private Integer total_count;
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPer_page() {
		return per_page;
	}
	public void setPer_page(Integer per_page) {
		this.per_page = per_page;
	}
	public Integer getTotal_pages() {
		return total_pages;
	}
	public void setTotal_pages(Integer total_pages) {
		this.total_pages = total_pages;
	}
	public Integer getTotal_count() {
		return total_count;
	}
	public void setTotal_count(Integer total_count) {
		this.total_count = total_count;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResMetaInfo [");
		if (page != null) {
			builder.append("page=");
			builder.append(page);
			builder.append(", ");
		}
		if (per_page != null) {
			builder.append("per_page=");
			builder.append(per_page);
			builder.append(", ");
		}
		if (total_pages != null) {
			builder.append("total_pages=");
			builder.append(total_pages);
			builder.append(", ");
		}
		if (total_count != null) {
			builder.append("total_count=");
			builder.append(total_count);
		}
		builder.append("]");
		return builder.toString();
	}


}
