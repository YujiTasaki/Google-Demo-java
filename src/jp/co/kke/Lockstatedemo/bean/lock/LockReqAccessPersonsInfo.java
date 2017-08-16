package jp.co.kke.Lockstatedemo.bean.lock;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LockReqAccessPersonsInfo {

	private String type;
	private Map<String,String> attributes = new LinkedHashMap<String, String>();






	public String getType() {
		return type;
	}




	public void setType(String type) {
		this.type = type;
	}






	public Map<String, String> getAttributes() {
		return attributes;
	}






	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}






//	public static void main(String[] args) throws Exception {
//		String json = "{  \"type\": \"access_guest\",  \"attributes\": {    \"starts_at\": \"2020-01-02T16:04:00\",    \"ends_at\": \"2021-01-02T16:04:00\",    \"name\": \"Ann Smith\",    \"pin\": \"1234\"  }}";
//		ObjectMapper mapper = new ObjectMapper();
//		LockReqAccessPersonsInfo test = mapper.readValue(json, LockReqAccessPersonsInfo.class);
//		System.out.println(test.getAttributes().get("name"));
//		System.out.println(test);
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockReqAccessPersonsInfo [type=");
		builder.append(type);
		builder.append(", attributes=");
		builder.append(attributes);
		builder.append("]");
		return builder.toString();
	}




	public static void main(String[] args) throws Exception {

		LockReqAccessPersonsInfo info = new LockReqAccessPersonsInfo();
		info.setType("accessGuest");
		info.getAttributes().put("starts_at", "2020-01-02T16:04:00");
		ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        System.out.println(json);
	}



}
