package jp.co.kke.Lockstatedemo.bean.lock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResLinksInfo {
	private String self;
	private String author;
	private String comments;

	private String access_person;
	private String accessible;

	private String primary_owner;
	private String owner_role;

	private String location;
	private String controller;
	private String controller_panel;
	private String model;
	private String internal_controller_panel;

	private String entry_reader;
	private String strike;
	private String rex;
	private String door_monitor;
	public String getSelf() {
		return self;
	}
	public void setSelf(String self) {
		this.self = self;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getAccess_person() {
		return access_person;
	}
	public void setAccess_person(String access_person) {
		this.access_person = access_person;
	}
	public String getAccessible() {
		return accessible;
	}
	public void setAccessible(String accessible) {
		this.accessible = accessible;
	}
	public String getPrimary_owner() {
		return primary_owner;
	}
	public void setPrimary_owner(String primary_owner) {
		this.primary_owner = primary_owner;
	}
	public String getOwner_role() {
		return owner_role;
	}
	public void setOwner_role(String owner_role) {
		this.owner_role = owner_role;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
	}
	public String getController_panel() {
		return controller_panel;
	}
	public void setController_panel(String controller_panel) {
		this.controller_panel = controller_panel;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getInternal_controller_panel() {
		return internal_controller_panel;
	}
	public void setInternal_controller_panel(String internal_controller_panel) {
		this.internal_controller_panel = internal_controller_panel;
	}
	public String getEntry_reader() {
		return entry_reader;
	}
	public void setEntry_reader(String entry_reader) {
		this.entry_reader = entry_reader;
	}
	public String getStrike() {
		return strike;
	}
	public void setStrike(String strike) {
		this.strike = strike;
	}
	public String getRex() {
		return rex;
	}
	public void setRex(String rex) {
		this.rex = rex;
	}
	public String getDoor_monitor() {
		return door_monitor;
	}
	public void setDoor_monitor(String door_monitor) {
		this.door_monitor = door_monitor;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResLinksInfo [");
		if (self != null) {
			builder.append("self=");
			builder.append(self);
			builder.append(", ");
		}
		if (author != null) {
			builder.append("author=");
			builder.append(author);
			builder.append(", ");
		}
		if (comments != null) {
			builder.append("comments=");
			builder.append(comments);
			builder.append(", ");
		}
		if (access_person != null) {
			builder.append("access_person=");
			builder.append(access_person);
			builder.append(", ");
		}
		if (accessible != null) {
			builder.append("accessible=");
			builder.append(accessible);
			builder.append(", ");
		}
		if (primary_owner != null) {
			builder.append("primary_owner=");
			builder.append(primary_owner);
			builder.append(", ");
		}
		if (owner_role != null) {
			builder.append("owner_role=");
			builder.append(owner_role);
			builder.append(", ");
		}
		if (location != null) {
			builder.append("location=");
			builder.append(location);
			builder.append(", ");
		}
		if (controller != null) {
			builder.append("controller=");
			builder.append(controller);
			builder.append(", ");
		}
		if (controller_panel != null) {
			builder.append("controller_panel=");
			builder.append(controller_panel);
			builder.append(", ");
		}
		if (model != null) {
			builder.append("model=");
			builder.append(model);
			builder.append(", ");
		}
		if (internal_controller_panel != null) {
			builder.append("internal_controller_panel=");
			builder.append(internal_controller_panel);
			builder.append(", ");
		}
		if (entry_reader != null) {
			builder.append("entry_reader=");
			builder.append(entry_reader);
			builder.append(", ");
		}
		if (strike != null) {
			builder.append("strike=");
			builder.append(strike);
			builder.append(", ");
		}
		if (rex != null) {
			builder.append("rex=");
			builder.append(rex);
			builder.append(", ");
		}
		if (door_monitor != null) {
			builder.append("door_monitor=");
			builder.append(door_monitor);
		}
		builder.append("]");
		return builder.toString();
	}


}
