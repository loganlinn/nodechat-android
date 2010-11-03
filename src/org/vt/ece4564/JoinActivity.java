package org.vt.ece4564;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class JoinActivity extends Activity implements HttpCallback,
		View.OnClickListener {
	private final String TAG = "JoinActivity";

	private HttpUtils utils_ = new HttpUtils();
	private Button joinButton_;
	private EditText joinNickname_;
	private ProgressDialog loadingDialog_;
	private Spinner serverSpinner_;
	private ArrayAdapter serversAdapter_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);

		joinButton_ = (Button) findViewById(R.id.joinButton);
		joinNickname_ = (EditText) findViewById(R.id.joinNickname);
		serverSpinner_ = (Spinner) findViewById(R.id.serverSpinner);
		joinButton_.setOnClickListener(this);
		//joinNickname_.setText("android");
		serversAdapter_ = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, Options.Chat.SERVERS);
		serversAdapter_
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		serverSpinner_.setAdapter(serversAdapter_);
		serverSpinner_.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		       Options.Chat.SERVER_URL = (String) parent.getItemAtPosition(pos);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		joinNickname_.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean isEnter = (keyCode == KeyEvent.KEYCODE_ENTER);
				if (isEnter) {
					joinChat();
				}
				return isEnter;
			}
		});
	}

	public void joinChat() {
		String nickname = joinNickname_.getText().toString();
		Log.i(TAG, "Attempting to join chat as " + nickname);
		if (nickname != null || !Pattern.matches("[^\\w_\\-^!]", nickname)) {
			nickname = URLEncoder.encode(nickname);
			StringBuilder url = ChatUtils.getServerUrl();
			String serverName = url.toString().replace("http", "").replace("https","").replace("://", "").replace("www.", "");
			serverName = serverName.substring(0,serverName.length()-1);	//display without trailing /
			loadingDialog_ = ProgressDialog.show(this, "", "Connecting to "+serverName+"...",
					true);
			url.append("join?nick=");
			url.append(nickname);
			
			utils_.doGet(url.toString(), this);
		} else {
			Toast
					.makeText(
							getApplicationContext(),
							"Bad character in nick. Can only have letters, numbers, and '_', '-', '^', '!'",
							1000).show();
		}
	}

	public void onClick(View v) {
		joinChat();
	}

	public void onError(Exception e) {
		loadingDialog_.hide();
		Toast.makeText(getApplicationContext(), "Error: " + e.toString(), 1000)
				.show();
		Log.i(TAG, "@onError: " + e.toString());
	}

	public void onResponse(HttpResponse resp) {
		loadingDialog_.hide();
		JSONObject json = utils_.responseToJSON(resp);
		Log.i(TAG, "@onResponse\n" + json.toString());
		if (json != null) {
			int status = resp.getStatusLine().getStatusCode();
			if (status == 200) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(joinNickname_.getWindowToken(), 0);

				Intent chatIntent = new Intent(getApplicationContext(),
						ChatActivity.class);

				chatIntent.putExtra("json", json.toString());
				startActivity(chatIntent);

			} else {
				String toastText = "Error: " + json.optString("error");
				Toast.makeText(getApplicationContext(), toastText,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.i(TAG, "onResponse's JSON failed");
			Toast.makeText(getApplicationContext(), "Failed to connect.",
					Toast.LENGTH_SHORT).show();
		}
	}
}
