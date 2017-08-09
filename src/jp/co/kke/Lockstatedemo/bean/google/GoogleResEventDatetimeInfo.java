package jp.co.kke.Lockstatedemo.bean.google;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleResEventDatetimeInfo {

	/**
	 * The date, in the format "yyyy-mm-dd",
	 *  if this is an all-day event.
	 */
	private String date;

	/**
	 * The time, as a combined date-time value (formatted according to RFC3339).
	 * A time zone offset is required unless a time zone is explicitly specified in timeZone.
	 * 	 * "2012-08-04T11:15:00+09:00"
	 */
	private String dateTime;

	/**
	 * The time zone in which the time is specified.
	 * (Formatted as an IANA Time Zone Database name, e.g. "Europe/Zurich".)
	 * For recurring events this field is required and
	 * specifies the time zone in which the recurrence is expanded.
	 * For single events this field is optional and indicates a custom time zone for the event start/end.
	 */
	private String timeZone;

	/**
	 * @return date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date セットする date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime セットする dateTime
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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
	@JsonIgnore
	public ZoneId getZoneId(){
		ZoneId res = null;
		if(this.timeZone != null) {
			res = ZoneId.of(this.timeZone);
		}
		return res;
	}
	@JsonIgnore
	public OffsetDateTime getOffsetDateTime() {
		OffsetDateTime res = null;
		if(this.dateTime != null) {
			//2014-07-21T22:16:46.348+09:00
			res = OffsetDateTime.parse(this.dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		return res;
	}
	@JsonIgnore
	public OffsetDateTime getOffsetDate() {
		OffsetDateTime res = null;
		if(this.date != null) {
			//2014-07-21
			ZoneId zoneId = getZoneId();
			if(zoneId == null) {
				res = OffsetDateTime.parse(this.dateTime, DateTimeFormatter.ISO_LOCAL_DATE);
			}else {
				res = OffsetDateTime.parse(this.dateTime, DateTimeFormatter.ISO_LOCAL_DATE.withZone(zoneId));
			}
		}
		return res;
	}

	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GoogleResEventDatetimeInfo [");
		if (date != null) {
			builder.append("date=");
			builder.append(date);
			builder.append(", ");
		}
		if (dateTime != null) {
			builder.append("dateTime=");
			builder.append(dateTime);
			builder.append(", ");
		}
		if (timeZone != null) {
			builder.append("timeZone=");
			builder.append(timeZone);
		}
		builder.append("]");
		return builder.toString();
	}
}
