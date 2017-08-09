package jp.co.kke.Lockstatedemo.bean.google;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * https://www.googleapis.com/calendar/v3/calendars/calendarId/events/eventId
 * https://developers.google.com/google-apps/calendar/v3/reference/events?authuser=0&hl=ja#resource
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleResCalendarEventInfo {
	/**
	 * Type of the collection ("calendar#events").
	 */
	private String kind;
	/**
	 * ETag of the resource.
	 */
	private String etag;
	/**
	 * Opaque identifier of the event. When creating new single or recurring events, you can specify their IDs.
	 */
	private String id;
	/**
	 * Status of the event. Optional. Possible values are:
	 * "confirmed" - The event is confirmed. This is the default status.
	 * "tentative" - The event is tentatively confirmed.
	 * "cancelled" - The event is cancelled.
	 */
	private String status;
	/**
	 * An absolute link to this event in the Google Calendar Web UI. Read-only.
	 */
	private String htmlLink;
	/**
	 * Creation time of the event (as a RFC3339 timestamp). Read-only.
	 */
	private String created;
	/**
	 * Last modification time of the event (as a RFC3339 timestamp). Read-only.
	 */
	private String updated;
	/**
	 * Title of the event.
	 */
	private String summary;
	/**
	 * Description of the event. Optional.
	 */
	private String description;
	/**
	 * Geographic location of the event as free-form text. Optional.
	 */
	private String location;
	/**
	 *  The color of the event.
	 *  This is an ID referring to an entry in the event section of the colors definition (see the colors endpoint).
	 *  Optional.
	 */
	private String colorId;
	/**
	 * The creator of the event. Read-only.
	 */
	private GoogleResCalendarEventAttendeeInfo creator;
	/**
	 * The organizer of the event.
	 * If the organizer is also an attendee,
	 * this is indicated with a separate entry in attendees with the organizer field set to True.
	 * To change the organizer, use the move operation. Read-only, except when importing an event.
	 */
	private GoogleResCalendarEventAttendeeInfo organizer;

	/**
	 * The (inclusive) start time of the event. For a recurring event,
	 * this is the start time of the first instance.
	 */
	private GoogleResEventDatetimeInfo start;
	/**
	 * The (exclusive) end time of the event. For a recurring event,
	 * this is the end time of the first instance.
	 */
	private GoogleResEventDatetimeInfo end;
	/**
	 *  Whether the end time is actually unspecified.
	 *  An end time is still provided for compatibility reasons, even if this attribute is set to True.
	 *  The default is False.
	 */
	private Boolean endTimeUnspecified;
	/**
	 *  List of RRULE, EXRULE, RDATE and EXDATE lines for a recurring event, as specified in RFC5545.
	 */
	//recurrence省略
	/**
	 *  For an instance of a recurring event,
	 *  this is the id of the recurring event to which this instance belongs.
	 *  Immutable.
	 */
	private String recurringEventId;
	/**
	 * The organizer's email address, if available.
	 * It must be a valid email address as per RFC5322.
	 */
	private GoogleResEventDatetimeInfo originalStartTime;
	/**
	 * Whether the event blocks time on the calendar. Optional. Possible values are:
     * "opaque" - The event blocks time on the calendar. This is the default value.
     * "transparent" - The event does not block time on the calendar.
	 */
	private String transparency;
	/**
	 * Visibility of the event. Optional. Possible values are:
 	 * "default" - Uses the default visibility for events on the calendar. This is the default value.
 	 * "public" - The event is public and event details are visible to all readers of the calendar.
 	 * "private" - The event is private and only event attendees may view event details.
 	 * "confidential" - The event is private. This value is provided for compatibility reasons.
	 */
	private String visibility;
	/**
	 * Event unique identifier as defined in RFC5545.
	 * It is used to uniquely identify events accross calendaring systems and must be supplied when importing events via the import method.
	 */
	private String iCalUID;
	/**
	 *  Sequence number as per iCalendar.
	 */
	private Integer sequence;
	/**
	 * The attendees of the event.
	 * See the Events with attendees guide for more information on scheduling events with other calendar users.
	 */
	private List<GoogleResCalendarEventAttendeeInfo> attendees;
	/**
	 * Whether attendees may have been omitted from the event's representation.
	 * When retrieving an event, this may be due to a restriction specified by the maxAttendee query parameter.
	 * When updating an event, this can be used to only update the participant's response.
	 * Optional. The default is False.
	 */
	private Boolean attendeesOmitted;
	/**
	 * Extended properties of the event.
	 */
	//extendedProperties省略
	/**
	 * An absolute link to the Google+ hangout associated with this event. Read-only.
	 */
	private String hangoutLink;

	/**
	 *  A gadget that extends this event.
	 */
	//gadget省略
	/**
	 * Whether anyone can invite themselves to the event (currently works for Google+ events only).
	 * Optional. The default is False.
	 */
	private Boolean anyoneCanAddSelf;
	/**
	 * Whether attendees other than the organizer can invite others to the event.
	 * Optional. The default is True.
	 */
	private Boolean guestsCanInviteOthers;
	/**
	 * 説明なし
	 */
	private Boolean guestsCanModify;
	/**
	 * 説明なし
	 */
	private Boolean guestsCanSeeOtherGuests;
	/**
	 * Whether this is a private event copy where changes are not shared with other copies on other calendars.
	 * Optional. Immutable. The default is False.
	 */
	private Boolean privateCopy;
	/**
	 * Whether this is a locked event copy where no changes can be made to the main event fields "summary",
	 * "description", "location", "start", "end" or "recurrence". The default is False. Read-Only.
	 */
	private Boolean locked;
	/**
	 * Information about the event's reminders for the authenticated user.
	 */
	//reminders省略
	/**
	 * Source from which the event was created.
	 * For example, a web page, an email message or any document identifiable
	 * by an URL with HTTP or HTTPS scheme.
	 * Can only be seen or modified by the creator of the event.
	 */
	//source省略
	/**
	 * File attachments for the event. Currently only Google Drive attachments are supported.
	 * In order to modify attachments the supportsAttachments request parameter should be set to true.
	 * There can be at most 25 attachments per event,
	 */
	//attachments省略
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
	 * @return status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status セットする status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return htmlLink
	 */
	public String getHtmlLink() {
		return htmlLink;
	}
	/**
	 * @param htmlLink セットする htmlLink
	 */
	public void setHtmlLink(String htmlLink) {
		this.htmlLink = htmlLink;
	}
	/**
	 * @return created
	 */
	public String getCreated() {
		return created;
	}
	/**
	 * @param created セットする created
	 */
	public void setCreated(String created) {
		this.created = created;
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
	 * @return location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location セットする location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return colorId
	 */
	public String getColorId() {
		return colorId;
	}
	/**
	 * @param colorId セットする colorId
	 */
	public void setColorId(String colorId) {
		this.colorId = colorId;
	}
	/**
	 * @return creator
	 */
	public GoogleResCalendarEventAttendeeInfo getCreator() {
		return creator;
	}
	/**
	 * @param creator セットする creator
	 */
	public void setCreator(GoogleResCalendarEventAttendeeInfo creator) {
		this.creator = creator;
	}
	/**
	 * @return organizer
	 */
	public GoogleResCalendarEventAttendeeInfo getOrganizer() {
		return organizer;
	}
	/**
	 * @param organizer セットする organizer
	 */
	public void setOrganizer(GoogleResCalendarEventAttendeeInfo organizer) {
		this.organizer = organizer;
	}
	/**
	 * @return start
	 */
	public GoogleResEventDatetimeInfo getStart() {
		return start;
	}
	/**
	 * @param start セットする start
	 */
	public void setStart(GoogleResEventDatetimeInfo start) {
		this.start = start;
	}
	/**
	 * @return end
	 */
	public GoogleResEventDatetimeInfo getEnd() {
		return end;
	}
	/**
	 * @param end セットする end
	 */
	public void setEnd(GoogleResEventDatetimeInfo end) {
		this.end = end;
	}
	/**
	 * @return endTimeUnspecified
	 */
	public Boolean getEndTimeUnspecified() {
		return endTimeUnspecified;
	}
	/**
	 * @param endTimeUnspecified セットする endTimeUnspecified
	 */
	public void setEndTimeUnspecified(Boolean endTimeUnspecified) {
		this.endTimeUnspecified = endTimeUnspecified;
	}
	/**
	 * @return recurringEventId
	 */
	public String getRecurringEventId() {
		return recurringEventId;
	}
	/**
	 * @param recurringEventId セットする recurringEventId
	 */
	public void setRecurringEventId(String recurringEventId) {
		this.recurringEventId = recurringEventId;
	}
	/**
	 * @return originalStartTime
	 */
	public GoogleResEventDatetimeInfo getOriginalStartTime() {
		return originalStartTime;
	}
	/**
	 * @param originalStartTime セットする originalStartTime
	 */
	public void setOriginalStartTime(GoogleResEventDatetimeInfo originalStartTime) {
		this.originalStartTime = originalStartTime;
	}
	/**
	 * @return transparency
	 */
	public String getTransparency() {
		return transparency;
	}
	/**
	 * @param transparency セットする transparency
	 */
	public void setTransparency(String transparency) {
		this.transparency = transparency;
	}
	/**
	 * @return visibility
	 */
	public String getVisibility() {
		return visibility;
	}
	/**
	 * @param visibility セットする visibility
	 */
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	/**
	 * @return iCalUID
	 */
	public String getiCalUID() {
		return iCalUID;
	}
	/**
	 * @param iCalUID セットする iCalUID
	 */
	public void setiCalUID(String iCalUID) {
		this.iCalUID = iCalUID;
	}
	/**
	 * @return sequence
	 */
	public Integer getSequence() {
		return sequence;
	}
	/**
	 * @param sequence セットする sequence
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return attendees
	 */
	public List<GoogleResCalendarEventAttendeeInfo> getAttendees() {
		return attendees;
	}
	/**
	 * @param attendees セットする attendees
	 */
	public void setAttendees(List<GoogleResCalendarEventAttendeeInfo> attendees) {
		this.attendees = attendees;
	}
	/**
	 * @return attendeesOmitted
	 */
	public Boolean getAttendeesOmitted() {
		return attendeesOmitted;
	}
	/**
	 * @param attendeesOmitted セットする attendeesOmitted
	 */
	public void setAttendeesOmitted(Boolean attendeesOmitted) {
		this.attendeesOmitted = attendeesOmitted;
	}
	/**
	 * @return hangoutLink
	 */
	public String getHangoutLink() {
		return hangoutLink;
	}
	/**
	 * @param hangoutLink セットする hangoutLink
	 */
	public void setHangoutLink(String hangoutLink) {
		this.hangoutLink = hangoutLink;
	}
	/**
	 * @return anyoneCanAddSelf
	 */
	public Boolean getAnyoneCanAddSelf() {
		return anyoneCanAddSelf;
	}
	/**
	 * @param anyoneCanAddSelf セットする anyoneCanAddSelf
	 */
	public void setAnyoneCanAddSelf(Boolean anyoneCanAddSelf) {
		this.anyoneCanAddSelf = anyoneCanAddSelf;
	}
	/**
	 * @return guestsCanInviteOthers
	 */
	public Boolean getGuestsCanInviteOthers() {
		return guestsCanInviteOthers;
	}
	/**
	 * @param guestsCanInviteOthers セットする guestsCanInviteOthers
	 */
	public void setGuestsCanInviteOthers(Boolean guestsCanInviteOthers) {
		this.guestsCanInviteOthers = guestsCanInviteOthers;
	}
	/**
	 * @return guestsCanModify
	 */
	public Boolean getGuestsCanModify() {
		return guestsCanModify;
	}
	/**
	 * @param guestsCanModify セットする guestsCanModify
	 */
	public void setGuestsCanModify(Boolean guestsCanModify) {
		this.guestsCanModify = guestsCanModify;
	}
	/**
	 * @return guestsCanSeeOtherGuests
	 */
	public Boolean getGuestsCanSeeOtherGuests() {
		return guestsCanSeeOtherGuests;
	}
	/**
	 * @param guestsCanSeeOtherGuests セットする guestsCanSeeOtherGuests
	 */
	public void setGuestsCanSeeOtherGuests(Boolean guestsCanSeeOtherGuests) {
		this.guestsCanSeeOtherGuests = guestsCanSeeOtherGuests;
	}
	/**
	 * @return privateCopy
	 */
	public Boolean getPrivateCopy() {
		return privateCopy;
	}
	/**
	 * @param privateCopy セットする privateCopy
	 */
	public void setPrivateCopy(Boolean privateCopy) {
		this.privateCopy = privateCopy;
	}
	/**
	 * @return locked
	 */
	public Boolean getLocked() {
		return locked;
	}
	/**
	 * @param locked セットする locked
	 */
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	@JsonIgnore
	public LocalDateTime getCreatedLocalDateTime() {
		LocalDateTime   res = null;
		if(this.created != null) {
			//2017-07-26T08:33:11.000Z
			res = LocalDateTime.parse(this.created, DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'"));
		}
		return res;
	}

	@JsonIgnore
	public OffsetDateTime getCreatedOffsetDateTime() {
		OffsetDateTime res = null;
		LocalDateTime ldt = getCreatedLocalDateTime();
		if(ldt != null) {
			ZonedDateTime udt = ZonedDateTime.of(ldt, ZoneId.of("UTC"));
			ZonedDateTime jdt = udt.withZoneSameInstant(ZoneId.systemDefault());
			res = jdt.toOffsetDateTime();
		}
		return res;
	}

	@JsonIgnore
	public LocalDateTime getUpdatedLocalDateTime() {
		LocalDateTime   res = null;
		if(this.updated != null) {
			//2017-07-26T08:33:11.000Z
			res = LocalDateTime.parse(this.updated, DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'"));
		}
		return res;
	}

	@JsonIgnore
	public OffsetDateTime getUpdatedOffsetDateTime() {
		OffsetDateTime res = null;
		LocalDateTime ldt = getUpdatedLocalDateTime();
		if(ldt != null) {
			ZonedDateTime udt = ZonedDateTime.of(ldt, ZoneId.of("UTC"));
			ZonedDateTime jdt = udt.withZoneSameInstant(ZoneId.systemDefault());
			res = jdt.toOffsetDateTime();
		}
		return res;
	}

	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GoogleResCalendarEventInfo [");
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
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (htmlLink != null) {
			builder.append("htmlLink=");
			builder.append(htmlLink);
			builder.append(", ");
		}
		if (created != null) {
			builder.append("created=");
			builder.append(created);
			builder.append(", ");
		}
		if (updated != null) {
			builder.append("updated=");
			builder.append(updated);
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
		if (location != null) {
			builder.append("location=");
			builder.append(location);
			builder.append(", ");
		}
		if (colorId != null) {
			builder.append("colorId=");
			builder.append(colorId);
			builder.append(", ");
		}
		if (creator != null) {
			builder.append("creator=");
			builder.append(creator);
			builder.append(", ");
		}
		if (organizer != null) {
			builder.append("organizer=");
			builder.append(organizer);
			builder.append(", ");
		}
		if (start != null) {
			builder.append("start=");
			builder.append(start);
			builder.append(", ");
		}
		if (end != null) {
			builder.append("end=");
			builder.append(end);
			builder.append(", ");
		}
		if (endTimeUnspecified != null) {
			builder.append("endTimeUnspecified=");
			builder.append(endTimeUnspecified);
			builder.append(", ");
		}
		if (recurringEventId != null) {
			builder.append("recurringEventId=");
			builder.append(recurringEventId);
			builder.append(", ");
		}
		if (originalStartTime != null) {
			builder.append("originalStartTime=");
			builder.append(originalStartTime);
			builder.append(", ");
		}
		if (transparency != null) {
			builder.append("transparency=");
			builder.append(transparency);
			builder.append(", ");
		}
		if (visibility != null) {
			builder.append("visibility=");
			builder.append(visibility);
			builder.append(", ");
		}
		if (iCalUID != null) {
			builder.append("iCalUID=");
			builder.append(iCalUID);
			builder.append(", ");
		}
		if (sequence != null) {
			builder.append("sequence=");
			builder.append(sequence);
			builder.append(", ");
		}
		if (attendees != null) {
			builder.append("attendees=");
			builder.append(attendees);
			builder.append(", ");
		}
		if (attendeesOmitted != null) {
			builder.append("attendeesOmitted=");
			builder.append(attendeesOmitted);
			builder.append(", ");
		}
		if (hangoutLink != null) {
			builder.append("hangoutLink=");
			builder.append(hangoutLink);
			builder.append(", ");
		}
		if (anyoneCanAddSelf != null) {
			builder.append("anyoneCanAddSelf=");
			builder.append(anyoneCanAddSelf);
			builder.append(", ");
		}
		if (guestsCanInviteOthers != null) {
			builder.append("guestsCanInviteOthers=");
			builder.append(guestsCanInviteOthers);
			builder.append(", ");
		}
		if (guestsCanModify != null) {
			builder.append("guestsCanModify=");
			builder.append(guestsCanModify);
			builder.append(", ");
		}
		if (guestsCanSeeOtherGuests != null) {
			builder.append("guestsCanSeeOtherGuests=");
			builder.append(guestsCanSeeOtherGuests);
			builder.append(", ");
		}
		if (privateCopy != null) {
			builder.append("privateCopy=");
			builder.append(privateCopy);
			builder.append(", ");
		}
		if (locked != null) {
			builder.append("locked=");
			builder.append(locked);
		}
		builder.append("]");
		return builder.toString();
	}

}
