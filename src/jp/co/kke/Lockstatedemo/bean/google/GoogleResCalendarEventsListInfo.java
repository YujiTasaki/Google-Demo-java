package jp.co.kke.Lockstatedemo.bean.google;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleResCalendarEventsListInfo {
	/**
	 * Type of the collection ("calendar#events").
	 */
	String kind;
	/**
	 * ETag of the collection.
	 */
	String etag;
	/**
	 * Title of the calendar. Read-only.
	 */
	String summary;
	/**
	 * Description of the calendar. Read-only.
	 */
	String description;
	/**
	 * Last modification time of the calendar (as a RFC3339 timestamp). Read-only.
	 */
	String updated;
	/**
	 * The time zone of the calendar. Read-only.
	 */
	String timeZone;
	/**
	 * The user's access role for this calendar. Read-only. Possible values are:
     * "none" - The user has no access.
	 * "freeBusyReader" - The user has read access to free/busy information.
	 * "reader" - The user has read access to the calendar. Private events will appear to users with reader access, but event details will be hidden.
	 * "writer" - The user has read and write access to the calendar. Private events will appear to users with writer access, and event details will be visible.
	 * "owner" - The user has ownership of the calendar. This role has all of the permissions of the writer role with the additional ability to see and manipulate ACLs.
	 */
	String accessRole;
	//defaultRemindersは省略

	/**
	 * Token used to access the next page of this result.
	 * Omitted if no further results are available,
	 * in which case nextSyncToken is provided.
	 */
	String nextPageToken;
	/**
	 * Token used at a later point in time to retrieve
	 * only the entries that have changed since this result was returned.
	 * Omitted if further results are available, in which case nextPageToken is provided.
	 */
	String nextSyncToken;
	/**
	 * List of events on the calendar.
	 */
	List<GoogleResCalendarEventInfo> items;
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
	 * @return etag
	 */
	public String getEtag() {
		return etag;
	}
	/**
	 * @param etag セットする etag
	 */
	public void setEtag(String etag) {
		this.etag = etag;
	}
	/**
	 * @return summary
	 */
	public String getSummary() {
		return summary;
	}
	/**
	 * @param summary セットする summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description セットする description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return updated
	 */
	public String getUpdated() {
		return updated;
	}
	/**
	 * @param updated セットする updated
	 */
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	/**
	 * @return timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}
	/**
	 * @param timeZone セットする timeZone
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	/**
	 * @return accessRole
	 */
	public String getAccessRole() {
		return accessRole;
	}
	/**
	 * @param accessRole セットする accessRole
	 */
	public void setAccessRole(String accessRole) {
		this.accessRole = accessRole;
	}
	/**
	 * @return nextPageToken
	 */
	public String getNextPageToken() {
		return nextPageToken;
	}
	/**
	 * @param nextPageToken セットする nextPageToken
	 */
	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}
	/**
	 * @return nextSyncToken
	 */
	public String getNextSyncToken() {
		return nextSyncToken;
	}
	/**
	 * @param nextSyncToken セットする nextSyncToken
	 */
	public void setNextSyncToken(String nextSyncToken) {
		this.nextSyncToken = nextSyncToken;
	}
	/**
	 * @return items
	 */
	public List<GoogleResCalendarEventInfo> getItems() {
		return items;
	}
	/**
	 * @param items セットする items
	 */
	public void setItems(List<GoogleResCalendarEventInfo> items) {
		this.items = items;
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GoogleResCalendarEventsListInfo [");
		if (kind != null) {
			builder.append("kind=");
			builder.append(kind);
			builder.append(", ");
		}
		if (etag != null) {
			builder.append("etag=");
			builder.append(etag);
			builder.append(", ");
		}
		if (summary != null) {
			builder.append("summary=");
			builder.append(summary);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (updated != null) {
			builder.append("updated=");
			builder.append(updated);
			builder.append(", ");
		}
		if (timeZone != null) {
			builder.append("timeZone=");
			builder.append(timeZone);
			builder.append(", ");
		}
		if (accessRole != null) {
			builder.append("accessRole=");
			builder.append(accessRole);
			builder.append(", ");
		}
		if (nextPageToken != null) {
			builder.append("nextPageToken=");
			builder.append(nextPageToken);
			builder.append(", ");
		}
		if (nextSyncToken != null) {
			builder.append("nextSyncToken=");
			builder.append(nextSyncToken);
			builder.append(", ");
		}
		if (items != null) {
			builder.append("items=");
			builder.append(items);
		}
		builder.append("]");
		return builder.toString();
	}

}
