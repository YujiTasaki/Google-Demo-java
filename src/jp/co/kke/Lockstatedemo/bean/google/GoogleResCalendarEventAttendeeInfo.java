package jp.co.kke.Lockstatedemo.bean.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The attendees of the event.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleResCalendarEventAttendeeInfo {
	/**
	 * The attendee's Profile ID, if available.
	 * It corresponds to theid field in the People collection of the Google+ API
	 */
	private String id;
	/**
	 * The attendee's email address, if available.
	 * This field must be present when adding an attendee.
	 * It must be a valid email address as per RFC5322.
	 */
	private String email;
	/**
	 * The attendee's name, if available. Optional.
	 */
	private String displayName;
	/**
	 * Whether the attendee is the organizer of the event.
	 * Read-only. The default is False.
	 */
	private Boolean organizer;
	/**
	 * Whether this entry represents the calendar on which this copy of the event appears. Read-only.
	 * The default is False.
	 */
	private Boolean self;
	/**
	 * Whether the attendee is a resource. Read-only.
	 * The default is False.
	 */
	private Boolean resource;
	/**
	 * Whether this is an optional attendee.
	 * Optional. The default is False.
	 */
	private Boolean optional;
	/**
	 * The attendee's response status. Possible values are:
	 * "needsAction" - The attendee has not responded to the invitation.
	 * "declined" - The attendee has declined the invitation.
	 * "tentative" - The attendee has tentatively accepted the invitation.
	 * "accepted" - The attendee has accepted the invitation.
	 */
	private String responseStatus;
	/**
	 * The attendee's response comment. Optional.
	 */
	private String comment;
	/**
	 * Number of additional guests. Optional. The default is 0.
	 */
	private Integer additionalGuests;
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
	 * @return email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email セットする email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName セットする displayName
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return organizer
	 */
	public Boolean getOrganizer() {
		return organizer;
	}
	/**
	 * @param organizer セットする organizer
	 */
	public void setOrganizer(Boolean organizer) {
		this.organizer = organizer;
	}
	/**
	 * @return self
	 */
	public Boolean getSelf() {
		return self;
	}
	/**
	 * @param self セットする self
	 */
	public void setSelf(Boolean self) {
		this.self = self;
	}
	/**
	 * @return resource
	 */
	public Boolean getResource() {
		return resource;
	}
	/**
	 * @param resource セットする resource
	 */
	public void setResource(Boolean resource) {
		this.resource = resource;
	}
	/**
	 * @return optional
	 */
	public Boolean getOptional() {
		return optional;
	}
	/**
	 * @param optional セットする optional
	 */
	public void setOptional(Boolean optional) {
		this.optional = optional;
	}
	/**
	 * @return responseStatus
	 */
	public String getResponseStatus() {
		return responseStatus;
	}
	/**
	 * @param responseStatus セットする responseStatus
	 */
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment セットする comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return additionalGuests
	 */
	public Integer getAdditionalGuests() {
		return additionalGuests;
	}
	/**
	 * @param additionalGuests セットする additionalGuests
	 */
	public void setAdditionalGuests(Integer additionalGuests) {
		this.additionalGuests = additionalGuests;
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GoogleResCalendarEventAttendeeInfo [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (email != null) {
			builder.append("email=");
			builder.append(email);
			builder.append(", ");
		}
		if (displayName != null) {
			builder.append("displayName=");
			builder.append(displayName);
			builder.append(", ");
		}
		if (organizer != null) {
			builder.append("organizer=");
			builder.append(organizer);
			builder.append(", ");
		}
		if (self != null) {
			builder.append("self=");
			builder.append(self);
			builder.append(", ");
		}
		if (resource != null) {
			builder.append("resource=");
			builder.append(resource);
			builder.append(", ");
		}
		if (optional != null) {
			builder.append("optional=");
			builder.append(optional);
			builder.append(", ");
		}
		if (responseStatus != null) {
			builder.append("responseStatus=");
			builder.append(responseStatus);
			builder.append(", ");
		}
		if (comment != null) {
			builder.append("comment=");
			builder.append(comment);
			builder.append(", ");
		}
		if (additionalGuests != null) {
			builder.append("additionalGuests=");
			builder.append(additionalGuests);
		}
		builder.append("]");
		return builder.toString();
	}

}
