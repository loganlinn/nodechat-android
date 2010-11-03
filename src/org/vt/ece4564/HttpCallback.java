package org.vt.ece4564;

import org.apache.http.HttpResponse;

public interface HttpCallback {

	public void onResponse(HttpResponse resp);
	public void onError(Exception e);
	
}
