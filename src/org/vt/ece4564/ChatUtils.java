package org.vt.ece4564;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatUtils {
	
	public static StringBuilder getServerUrl() {
		StringBuilder url = new StringBuilder();
		url.append(Options.Chat.SERVER_URL);
		if(Options.Chat.SERVER_URL.endsWith("/") == false){
			url.append("/");
		}
		return url;
	}

	public static String formatMessage(JSONObject json) throws JSONException {
		StringBuilder msg = new StringBuilder();
		String msgType = json.getString("type");
		if (msgType.equals("msg")) {
			msg.append(formatTimestamp(json.getLong("timestamp")));
			msg.append(" <b>");
			msg.append(json.getString("nick"));
			msg.append("</b>: ");
			msg.append(json.getString("text"));
		} else if (msgType.equals("join")) {
			msg.append(formatTimestamp(json.getLong("timestamp")));
			msg.append("  <b>");
			msg.append(json.getString("nick"));
			msg.append("</b> joined");
		} else if (msgType.equals("part")) {
			msg.append(formatTimestamp(json.getLong("timestamp")));
			msg.append("  <b>");
			msg.append(json.getString("nick"));
			msg.append("</b> left");
		}
		return msg.toString();
	}

	protected static String formatTimestamp(long time) {
		return new java.text.SimpleDateFormat("HH:mm")
				.format(new java.util.Date(time));
	}
}