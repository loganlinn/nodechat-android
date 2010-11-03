package org.vt.ece4564;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ChatClient {
	private String nick_;
	private String id_;
	private long rss_;
	private long startTime_;
	private static final String TAG = "ChatClient";
	
	public ChatClient(){
		super();
	}
	//{"id":"94883935641","nick":"lo","rss":9269248,"starttime":1283785025861}
	public ChatClient(JSONObject json){
		try {
			id_ = json.getString("id");
			nick_ = json.getString("nick");
			rss_ = json.getLong("rss");
			startTime_ = json.getLong("starttime");
		} catch (JSONException e) {
			Log.e(TAG,"Error parsing JSON");
		}
		
	}
	public String getNick_() {
		return nick_;
	}

	public void setNick_(String nick) {
		nick_ = nick;
	}

	public String getId_() {
		return id_;
	}

	public void setId_(String id) {
		id_ = id;
	}

	public long getRss_() {
		return rss_;
	}

	public void setRss_(long rss) {
		rss_ = rss;
	}

	public long getStartTime_() {
		return startTime_;
	}

	public void setStartTime_(long startTime) {
		startTime_ = startTime;
	}
}
