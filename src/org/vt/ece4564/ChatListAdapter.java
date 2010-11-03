package org.vt.ece4564;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatListAdapter extends ArrayAdapter<JSONObject> {
	public static final String TAG = "ChatListAdapter";

	public ChatListAdapter(Context context, int textViewResourceId,
			List<JSONObject> objects) {
		super(context, R.layout.chat_row, R.id.chatRowText, objects);

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		JSONObject json = getItem(position);
		boolean reusedView = true;
		if (convertView == null) {
			reusedView = false;
			Activity activity = (Activity) getContext();
			LayoutInflater inflater = (LayoutInflater) activity
					.getApplicationContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.chat_row, null);
			
		}

		TextView messageText = (TextView) convertView
				.findViewById(R.id.chatRowText);
		try {

			messageText.setText(Html.fromHtml(ChatUtils.formatMessage(json)));
			
			if (json.getString("type").equals("msg") == false) {
				messageText.setTextColor(convertView.getResources().getColor(android.R.color.tertiary_text_light));
			}else if(reusedView == true){
				messageText.setTextColor(convertView.getResources().getColor(android.R.color.primary_text_light));
			}
			//messageText.setBackgroundColor(android.R.color.background_light);
		} catch (JSONException e) {
			messageText.setText("JSON Exception");
		}
		return convertView;
	}
}
