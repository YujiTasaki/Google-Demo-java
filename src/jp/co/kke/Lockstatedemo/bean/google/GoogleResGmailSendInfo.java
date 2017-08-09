package jp.co.kke.Lockstatedemo.bean.google;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Gmail送付返信Json構造クラス
 * Users.messages
 * https://developers.google.com/gmail/api/v1/reference/users/messages#resource
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleResGmailSendInfo {
	/**
	 * The immutable ID of the message.
	 */
	private String id;

	/**
	 * The ID of the thread the message belongs to.
	 */
	private String threadId;

	/**
	 * List of IDs of labels applied to this message.
	 */
	private String[] labelIds;


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
	 * @return threadId
	 */
	public String getThreadId() {
		return threadId;
	}


	/**
	 * @param threadId セットする threadId
	 */
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}


	/**
	 * @return labelIds
	 */
	public String[] getLabelIds() {
		return labelIds;
	}


	/**
	 * @param labelIds セットする labelIds
	 */
	public void setLabelIds(String[] labelIds) {
		this.labelIds = labelIds;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GoogleResSendMailInfo [id=");
		builder.append(id);
		builder.append(", threadId=");
		builder.append(threadId);
		builder.append(", labelIds=");
		builder.append(Arrays.toString(labelIds));
		builder.append("]");
		return builder.toString();
	}

}
