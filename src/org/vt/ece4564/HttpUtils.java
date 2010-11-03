package org.vt.ece4564;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpUtils {
	public static final String TAG = "HttpUtils";
	
	public void doGet(String url, HttpCallback callback) {
		Log.i(TAG, "GET:"+url);
		HttpGet get = new HttpGet(url);
		HttpRequestInfo rinfo = new HttpRequestInfo(get, callback);
		AsyncHttpTask task = new AsyncHttpTask();
		task.execute(rinfo);
	}

	public void doPost(String url, Map<String, String> params,
			HttpCallback callback) {
		try {

			HttpPost post = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					params.size());

			for (String key : params.keySet()) {
				nameValuePairs
						.add(new BasicNameValuePair(key, params.get(key)));
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					nameValuePairs);
			post.setEntity(entity);

			HttpRequestInfo rinfo = new HttpRequestInfo(post, callback);
			AsyncHttpTask task = new AsyncHttpTask();
			task.execute(rinfo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public JSONObject responseToJSON(HttpResponse resp){
		String strResponse;
		JSONObject jsonResponse = null;
		try {
			strResponse = responseToString(resp);
			jsonResponse = new JSONObject(strResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonResponse;
	}
	public String responseToString(HttpResponse response) throws IOException{
		InputStream in = response.getEntity().getContent();
		InputStreamReader ir = new InputStreamReader(in);
		BufferedReader bin = new BufferedReader(ir);
		String line = null;
		StringBuffer buff = new StringBuffer();
		while((line = bin.readLine())!=null){
			buff.append(line+"\n");
		}
		bin.close();
		return buff.toString();
	}
}
