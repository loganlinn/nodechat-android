package org.vt.ece4564;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends Activity implements HttpCallback {
	private static final String TAG = "ChatActivity";
	private HttpUtils utils_ = new HttpUtils();
	private List<JSONObject> chatLog_ = new ArrayList<JSONObject>(
			Options.Chat.MAX_MESSAGE_LOG_SIZE);
	private Map<String, Long> users_ = new HashMap<String, Long>();

	private ChatClient client_;
	private long lastMessageTime_;
	// private int numUsers_;
	private int numResponseErrors_;
	private int numResponses_;
	// private TextView chatStatus_;
	private Button sendButton_;
	private EditText messageText_;
	private ListView chatView_;
	private ArrayAdapter chatViewAdapter_;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		lastMessageTime_ = 0;
		numResponseErrors_ = 0;
		numResponses_ = 0;
		// chatStatus_ = (TextView) findViewById(R.id.chatStatus);
		sendButton_ = (Button) findViewById(R.id.sendMessageButton);
		messageText_ = (EditText) findViewById(R.id.chatMessage);
		chatView_ = (ListView) findViewById(R.id.chatView);
		chatViewAdapter_ = new ChatListAdapter(this, android.R.id.text1,
				chatLog_);
		chatView_.setAdapter(chatViewAdapter_);
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			try {
				client_ = new ChatClient(new JSONObject(extras
						.getString("json")));
			} catch (JSONException e) {
				Log.e(TAG, "Error: " + e.toString());
			}
		} else {
			// TODO: handle missing intent extras!
		}

		sendButton_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doMessageSend();
			}
		});
		messageText_.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean isEnter = (keyCode == KeyEvent.KEYCODE_ENTER);
				if (isEnter && event.getAction() == KeyEvent.ACTION_UP) {
					doMessageSend();
				}
				return isEnter;
			}

		});

		doLongPoll();
		doWho();
	}

	private void doMessageSend() {
		String message = messageText_.getText().toString();
		messageText_.setText("");

		if (message.length() > 0) {
			message = URLEncoder.encode(message);
			StringBuilder url = ChatUtils.getServerUrl();
			url.append("send");
			url.append("?id=");
			url.append(client_.getId_());
			url.append("&text=");
			url.append(message);

			utils_.doGet(url.toString(), new HttpCallback() {

				public void onError(Exception e) {
					Toast.makeText(getApplicationContext(), "Error Sending",
							1000).show();
					e.printStackTrace();
				}

				public void onResponse(HttpResponse resp) {

				}

			});
		}
	}

	@Override
	protected void onDestroy() {
		String clientId = client_.getId_();
		if (clientId != null) {
			StringBuilder url = ChatUtils.getServerUrl();
			url.append("part?");
			url.append("&id=" + clientId);

			utils_.doGet(url.toString(), new HttpCallback() {

				public void onError(Exception e) {
					Log.e(TAG, "onDestroy request failed");
					ChatActivity.super.onDestroy();
				}

				public void onResponse(HttpResponse resp) {
					Log.e(TAG, "onDestroy request success");

				}

			});
		}
		super.onDestroy();
	}

	private void doWho() {
		StringBuilder url = ChatUtils.getServerUrl();
		url.append("who");
		utils_.doGet(url.toString(), this);
	}

	private void doLongPoll() {
		StringBuilder url = ChatUtils.getServerUrl();
		url.append("recv?");
		url.append("_=" + new Date().getTime());
		url.append("&since=" + lastMessageTime_);
		url.append("&id=" + client_.getId_());
		utils_.doGet(url.toString(), this);

	}

	private int updateUsersOnline() {
		int s = users_.size();
		setTitle("NodeChat (" + s + " online)");
		return s;
	}

	private int userJoin(String nickname, long joinTime) {
		users_.put(nickname, joinTime);
		return updateUsersOnline();
	}

	private int userPart(String nickname) {
		users_.remove(nickname);
		return updateUsersOnline();
	}

	public void onError(Exception e) {
		Toast.makeText(getApplicationContext(), "Error: " + e.toString(), 1000)
				.show();

		// retry connection request if we havnt exceeded max errors
		if (numResponseErrors_++ < Options.Chat.MAX_REQUEST_ERRORS) {
			doLongPoll();
		} else {
			finish();
		}

		Log.w(TAG, "@onError" + e.toString());
	}

	public void onResponse(HttpResponse resp) {
		numResponses_++;
		JSONObject json = utils_.responseToJSON(resp);
		Log.i(TAG, "@onResponse: " + json.toString());
		JSONArray messages = null;
		JSONArray nicks = null;
		try {
			// Check for new messages
			if ((messages = json.optJSONArray("messages")) != null) {
				
				//TODO: Sort messages in order of timestamp
				//
				
				for (int i = 0; i < messages.length(); i++) {
					JSONObject message = messages.getJSONObject(i);
					long messageTime = message.getLong("timestamp");
					String messageType = message.getString("type");
					
					//update the display of online users
					if(numResponses_ > 1){
						if(messageType.equals("join")){
							userJoin(message.getString("nick"), messageTime);
						}else if(messageType.equals("part")){
							userPart(message.getString("nick"));
						}
					}
					
					if (messageTime > lastMessageTime_) {
						if (chatLog_.size() >= Options.Chat.MAX_MESSAGE_LOG_SIZE) {
							chatLog_.remove(0);
						}

						chatLog_.add(message);

						lastMessageTime_ = messageTime;
					}
					if(numResponses_ == 1){	//move to bottom of chat upon initial join
						chatView_.setSelection(chatLog_.size()-1);
					}
				}
			} else if((nicks = json.optJSONArray("nicks")) != null) { // no messages found, response must be for listing users in chat
				if (nicks != null) {
					users_.clear();
					for (int i = 0; i < nicks.length(); i++) {
						userJoin(nicks.getString(i), 0);
					}
					updateUsersOnline();
				}
			}else{	//otherwise throw an error
				onError(new Exception("Unrecognized server response"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (messages != null && messages.length() > 0){
			chatViewAdapter_.notifyDataSetChanged();	//update the chat log
		}
		// Start next long poll
		doLongPoll();

	}
}
