package jp.co.kke.Lockstatedemo.bean.lock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LockResAttributesInfo {

	private String name;
	public List<LockResAttributesDateInfo> dates;
	private String title;
	private String body;
	private Integer author_id;
	private String email;
	private String pin;
	private String card_number;
	private String starts_at;
	private String ends_at;
	private String created_at;
	private String updated_at;
	private List<String>local_pins;
	public List<String> getLocal_pins() {
		return local_pins;
	}
	public void setLocal_pins(List<String> local_pins) {
		this.local_pins = local_pins;
	}
	public String getPower_source() {
		return power_source;
	}
	public void setPower_source(String power_source) {
		this.power_source = power_source;
	}
	private String connected_at;

	private String access_person_id;
	private String access_person_type;
	private String accessible_id;
	private String accessible_type;
	private String power_source;
	private String primary_owner_id;
	private String owner_role_id;

	private String device_type;
	private String sio_input;
	private Integer sio_output;
	private Integer strike_time;
	private String strike_mode;
	private String held_open_time;

 	private String location_id;
	private String controller_id;
	private String controller_panel_id;

	private Integer sio;
	private Boolean internal;
	private Integer baud_rate;
	private String model_id;

	private Boolean ip_client;
	private String ip_address;
	private Integer port;
	private String serial_number;

	private Integer heartbeat_interval;
	private Boolean wake_wifi;

	private Boolean muted;
	private Boolean auto_lock;
	private Integer auto_lock_timeout;
	private String programming_code;
	private Integer wifi_level;
	private Integer power_level;
	private Boolean connected;
	private Boolean alive;

	private String default_guest_start_time;
	private String default_guest_end_time;




	private Boolean online;
	private String internal_controller_panel_id;


	private String state;
	private String entry_reader_id;

	private Integer entry_reader_sio;
	private Integer strike_sio_output;
	private Integer rex_sio_input;
	private Integer door_monitor_sio_input;
	private Integer door_monitor_held_open_time;

	private String strike_id;
	private String rex_id;
	private String door_monitor_id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<LockResAttributesDateInfo> getDates() {
		return dates;
	}
	public void setDates(List<LockResAttributesDateInfo> dates) {
		this.dates = dates;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Integer getAuthor_id() {
		return author_id;
	}
	public void setAuthor_id(Integer author_id) {
		this.author_id = author_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getCard_number() {
		return card_number;
	}
	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}
	public String getStarts_at() {
		return starts_at;
	}
	public void setStarts_at(String starts_at) {
		this.starts_at = starts_at;
	}
	public String getEnds_at() {
		return ends_at;
	}
	public void setEnds_at(String ends_at) {
		this.ends_at = ends_at;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getConnected_at() {
		return connected_at;
	}
	public void setConnected_at(String connected_at) {
		this.connected_at = connected_at;
	}
	public String getAccess_person_id() {
		return access_person_id;
	}
	public void setAccess_person_id(String access_person_id) {
		this.access_person_id = access_person_id;
	}
	public String getAccess_person_type() {
		return access_person_type;
	}
	public void setAccess_person_type(String access_person_type) {
		this.access_person_type = access_person_type;
	}
	public String getAccessible_id() {
		return accessible_id;
	}
	public void setAccessible_id(String accessible_id) {
		this.accessible_id = accessible_id;
	}
	public String getAccessible_type() {
		return accessible_type;
	}
	public void setAccessible_type(String accessible_type) {
		this.accessible_type = accessible_type;
	}
	public String getPrimary_owner_id() {
		return primary_owner_id;
	}
	public void setPrimary_owner_id(String primary_owner_id) {
		this.primary_owner_id = primary_owner_id;
	}
	public String getOwner_role_id() {
		return owner_role_id;
	}
	public void setOwner_role_id(String owner_role_id) {
		this.owner_role_id = owner_role_id;
	}
	public String getDevice_type() {
		return device_type;
	}
	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}
	public String getSio_input() {
		return sio_input;
	}
	public void setSio_input(String sio_input) {
		this.sio_input = sio_input;
	}
	public Integer getSio_output() {
		return sio_output;
	}
	public void setSio_output(Integer sio_output) {
		this.sio_output = sio_output;
	}
	public Integer getStrike_time() {
		return strike_time;
	}
	public void setStrike_time(Integer strike_time) {
		this.strike_time = strike_time;
	}
	public String getStrike_mode() {
		return strike_mode;
	}
	public void setStrike_mode(String strike_mode) {
		this.strike_mode = strike_mode;
	}
	public String getHeld_open_time() {
		return held_open_time;
	}
	public void setHeld_open_time(String held_open_time) {
		this.held_open_time = held_open_time;
	}
	public String getLocation_id() {
		return location_id;
	}
	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}
	public String getController_id() {
		return controller_id;
	}
	public void setController_id(String controller_id) {
		this.controller_id = controller_id;
	}
	public String getController_panel_id() {
		return controller_panel_id;
	}
	public void setController_panel_id(String controller_panel_id) {
		this.controller_panel_id = controller_panel_id;
	}
	public Integer getSio() {
		return sio;
	}
	public void setSio(Integer sio) {
		this.sio = sio;
	}
	public Boolean getInternal() {
		return internal;
	}
	public void setInternal(Boolean internal) {
		this.internal = internal;
	}
	public Integer getBaud_rate() {
		return baud_rate;
	}
	public void setBaud_rate(Integer baud_rate) {
		this.baud_rate = baud_rate;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public Boolean getIp_client() {
		return ip_client;
	}
	public void setIp_client(Boolean ip_client) {
		this.ip_client = ip_client;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public Integer getHeartbeat_interval() {
		return heartbeat_interval;
	}
	public void setHeartbeat_interval(Integer heartbeat_interval) {
		this.heartbeat_interval = heartbeat_interval;
	}
	public Boolean getWake_wifi() {
		return wake_wifi;
	}
	public void setWake_wifi(Boolean wake_wifi) {
		this.wake_wifi = wake_wifi;
	}
	public Boolean getMuted() {
		return muted;
	}
	public void setMuted(Boolean muted) {
		this.muted = muted;
	}
	public Boolean getAuto_lock() {
		return auto_lock;
	}
	public void setAuto_lock(Boolean auto_lock) {
		this.auto_lock = auto_lock;
	}
	public Integer getAuto_lock_timeout() {
		return auto_lock_timeout;
	}
	public void setAuto_lock_timeout(Integer auto_lock_timeout) {
		this.auto_lock_timeout = auto_lock_timeout;
	}
	public String getProgramming_code() {
		return programming_code;
	}
	public void setProgramming_code(String programming_code) {
		this.programming_code = programming_code;
	}
	public Integer getWifi_level() {
		return wifi_level;
	}
	public void setWifi_level(Integer wifi_level) {
		this.wifi_level = wifi_level;
	}
	public Integer getPower_level() {
		return power_level;
	}
	public void setPower_level(Integer power_level) {
		this.power_level = power_level;
	}
	public Boolean getConnected() {
		return connected;
	}
	public void setConnected(Boolean connected) {
		this.connected = connected;
	}
	public Boolean getAlive() {
		return alive;
	}
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	public String getDefault_guest_start_time() {
		return default_guest_start_time;
	}
	public void setDefault_guest_start_time(String default_guest_start_time) {
		this.default_guest_start_time = default_guest_start_time;
	}
	public String getDefault_guest_end_time() {
		return default_guest_end_time;
	}
	public void setDefault_guest_end_time(String default_guest_end_time) {
		this.default_guest_end_time = default_guest_end_time;
	}
	public Boolean getOnline() {
		return online;
	}
	public void setOnline(Boolean online) {
		this.online = online;
	}
	public String getInternal_controller_panel_id() {
		return internal_controller_panel_id;
	}
	public void setInternal_controller_panel_id(String internal_controller_panel_id) {
		this.internal_controller_panel_id = internal_controller_panel_id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getEntry_reader_id() {
		return entry_reader_id;
	}
	public void setEntry_reader_id(String entry_reader_id) {
		this.entry_reader_id = entry_reader_id;
	}
	public Integer getEntry_reader_sio() {
		return entry_reader_sio;
	}
	public void setEntry_reader_sio(Integer entry_reader_sio) {
		this.entry_reader_sio = entry_reader_sio;
	}
	public Integer getStrike_sio_output() {
		return strike_sio_output;
	}
	public void setStrike_sio_output(Integer strike_sio_output) {
		this.strike_sio_output = strike_sio_output;
	}
	public Integer getRex_sio_input() {
		return rex_sio_input;
	}
	public void setRex_sio_input(Integer rex_sio_input) {
		this.rex_sio_input = rex_sio_input;
	}
	public Integer getDoor_monitor_sio_input() {
		return door_monitor_sio_input;
	}
	public void setDoor_monitor_sio_input(Integer door_monitor_sio_input) {
		this.door_monitor_sio_input = door_monitor_sio_input;
	}
	public Integer getDoor_monitor_held_open_time() {
		return door_monitor_held_open_time;
	}
	public void setDoor_monitor_held_open_time(Integer door_monitor_held_open_time) {
		this.door_monitor_held_open_time = door_monitor_held_open_time;
	}
	public String getStrike_id() {
		return strike_id;
	}
	public void setStrike_id(String strike_id) {
		this.strike_id = strike_id;
	}
	public String getRex_id() {
		return rex_id;
	}
	public void setRex_id(String rex_id) {
		this.rex_id = rex_id;
	}
	public String getDoor_monitor_id() {
		return door_monitor_id;
	}
	public void setDoor_monitor_id(String door_monitor_id) {
		this.door_monitor_id = door_monitor_id;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResAttributesInfo [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (dates != null) {
			builder.append("dates=");
			builder.append(dates);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (body != null) {
			builder.append("body=");
			builder.append(body);
			builder.append(", ");
		}
		if (author_id != null) {
			builder.append("author_id=");
			builder.append(author_id);
			builder.append(", ");
		}
		if (email != null) {
			builder.append("email=");
			builder.append(email);
			builder.append(", ");
		}
		if (pin != null) {
			builder.append("pin=");
			builder.append(pin);
			builder.append(", ");
		}
		if (card_number != null) {
			builder.append("card_number=");
			builder.append(card_number);
			builder.append(", ");
		}
		if (starts_at != null) {
			builder.append("starts_at=");
			builder.append(starts_at);
			builder.append(", ");
		}
		if (ends_at != null) {
			builder.append("ends_at=");
			builder.append(ends_at);
			builder.append(", ");
		}
		if (created_at != null) {
			builder.append("created_at=");
			builder.append(created_at);
			builder.append(", ");
		}
		if (updated_at != null) {
			builder.append("updated_at=");
			builder.append(updated_at);
			builder.append(", ");
		}
		if (connected_at != null) {
			builder.append("connected_at=");
			builder.append(connected_at);
			builder.append(", ");
		}
		if (access_person_id != null) {
			builder.append("access_person_id=");
			builder.append(access_person_id);
			builder.append(", ");
		}
		if (access_person_type != null) {
			builder.append("access_person_type=");
			builder.append(access_person_type);
			builder.append(", ");
		}
		if (accessible_id != null) {
			builder.append("accessible_id=");
			builder.append(accessible_id);
			builder.append(", ");
		}
		if (accessible_type != null) {
			builder.append("accessible_type=");
			builder.append(accessible_type);
			builder.append(", ");
		}
		if (primary_owner_id != null) {
			builder.append("primary_owner_id=");
			builder.append(primary_owner_id);
			builder.append(", ");
		}
		if (owner_role_id != null) {
			builder.append("owner_role_id=");
			builder.append(owner_role_id);
			builder.append(", ");
		}
		if (device_type != null) {
			builder.append("device_type=");
			builder.append(device_type);
			builder.append(", ");
		}
		if (sio_input != null) {
			builder.append("sio_input=");
			builder.append(sio_input);
			builder.append(", ");
		}
		if (sio_output != null) {
			builder.append("sio_output=");
			builder.append(sio_output);
			builder.append(", ");
		}
		if (strike_time != null) {
			builder.append("strike_time=");
			builder.append(strike_time);
			builder.append(", ");
		}
		if (strike_mode != null) {
			builder.append("strike_mode=");
			builder.append(strike_mode);
			builder.append(", ");
		}
		if (held_open_time != null) {
			builder.append("held_open_time=");
			builder.append(held_open_time);
			builder.append(", ");
		}
		if (location_id != null) {
			builder.append("location_id=");
			builder.append(location_id);
			builder.append(", ");
		}
		if (controller_id != null) {
			builder.append("controller_id=");
			builder.append(controller_id);
			builder.append(", ");
		}
		if (controller_panel_id != null) {
			builder.append("controller_panel_id=");
			builder.append(controller_panel_id);
			builder.append(", ");
		}
		if (sio != null) {
			builder.append("sio=");
			builder.append(sio);
			builder.append(", ");
		}
		if (internal != null) {
			builder.append("internal=");
			builder.append(internal);
			builder.append(", ");
		}
		if (baud_rate != null) {
			builder.append("baud_rate=");
			builder.append(baud_rate);
			builder.append(", ");
		}
		if (model_id != null) {
			builder.append("model_id=");
			builder.append(model_id);
			builder.append(", ");
		}
		if (ip_client != null) {
			builder.append("ip_client=");
			builder.append(ip_client);
			builder.append(", ");
		}
		if (ip_address != null) {
			builder.append("ip_address=");
			builder.append(ip_address);
			builder.append(", ");
		}
		if (port != null) {
			builder.append("port=");
			builder.append(port);
			builder.append(", ");
		}
		if (serial_number != null) {
			builder.append("serial_number=");
			builder.append(serial_number);
			builder.append(", ");
		}
		if (heartbeat_interval != null) {
			builder.append("heartbeat_interval=");
			builder.append(heartbeat_interval);
			builder.append(", ");
		}
		if (wake_wifi != null) {
			builder.append("wake_wifi=");
			builder.append(wake_wifi);
			builder.append(", ");
		}
		if (muted != null) {
			builder.append("muted=");
			builder.append(muted);
			builder.append(", ");
		}
		if (auto_lock != null) {
			builder.append("auto_lock=");
			builder.append(auto_lock);
			builder.append(", ");
		}
		if (auto_lock_timeout != null) {
			builder.append("auto_lock_timeout=");
			builder.append(auto_lock_timeout);
			builder.append(", ");
		}
		if (programming_code != null) {
			builder.append("programming_code=");
			builder.append(programming_code);
			builder.append(", ");
		}
		if (wifi_level != null) {
			builder.append("wifi_level=");
			builder.append(wifi_level);
			builder.append(", ");
		}
		if (power_level != null) {
			builder.append("power_level=");
			builder.append(power_level);
			builder.append(", ");
		}
		if (connected != null) {
			builder.append("connected=");
			builder.append(connected);
			builder.append(", ");
		}
		if (alive != null) {
			builder.append("alive=");
			builder.append(alive);
			builder.append(", ");
		}
		if (default_guest_start_time != null) {
			builder.append("default_guest_start_time=");
			builder.append(default_guest_start_time);
			builder.append(", ");
		}
		if (default_guest_end_time != null) {
			builder.append("default_guest_end_time=");
			builder.append(default_guest_end_time);
			builder.append(", ");
		}
		if (online != null) {
			builder.append("online=");
			builder.append(online);
			builder.append(", ");
		}
		if (internal_controller_panel_id != null) {
			builder.append("internal_controller_panel_id=");
			builder.append(internal_controller_panel_id);
			builder.append(", ");
		}
		if (state != null) {
			builder.append("state=");
			builder.append(state);
			builder.append(", ");
		}
		if (entry_reader_id != null) {
			builder.append("entry_reader_id=");
			builder.append(entry_reader_id);
			builder.append(", ");
		}
		if (entry_reader_sio != null) {
			builder.append("entry_reader_sio=");
			builder.append(entry_reader_sio);
			builder.append(", ");
		}
		if (strike_sio_output != null) {
			builder.append("strike_sio_output=");
			builder.append(strike_sio_output);
			builder.append(", ");
		}
		if (rex_sio_input != null) {
			builder.append("rex_sio_input=");
			builder.append(rex_sio_input);
			builder.append(", ");
		}
		if (door_monitor_sio_input != null) {
			builder.append("door_monitor_sio_input=");
			builder.append(door_monitor_sio_input);
			builder.append(", ");
		}
		if (door_monitor_held_open_time != null) {
			builder.append("door_monitor_held_open_time=");
			builder.append(door_monitor_held_open_time);
			builder.append(", ");
		}
		if (strike_id != null) {
			builder.append("strike_id=");
			builder.append(strike_id);
			builder.append(", ");
		}
		if (rex_id != null) {
			builder.append("rex_id=");
			builder.append(rex_id);
			builder.append(", ");
		}
		if (door_monitor_id != null) {
			builder.append("door_monitor_id=");
			builder.append(door_monitor_id);
		}
		builder.append("]");
		return builder.toString();
	}

}



