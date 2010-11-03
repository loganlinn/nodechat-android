package org.vt.ece4564;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpRequestInfo {
//	public static final int RESPONSE_HTML = 0x0;
//	public static final int RESPONSE_JSON = 0x1;
//	public static final int RESPONSE_XML = 0x2;
//	public static final int RESPONSE_RSS = 0x3;
	
	private HttpUriRequest request_;
	private HttpCallback callback_;
	private Exception exception_;
	private HttpResponse response_;
	private Class responseClass_;
	
	public HttpRequestInfo(HttpUriRequest request, HttpCallback callback){
		super();
		request_ = request;
		callback_ = callback;
		responseClass_ = callback.getClass();
	}
	

	public HttpUriRequest getRequest() {
		return request_;
	}

	public void setRequest(HttpUriRequest request) {
		request_ = request;
	}

	public HttpCallback getCallback() {
		return callback_;
	}

	public void setCallback(HttpCallback callback) {
		callback_ = callback;
	}

	public Exception getException() {
		return exception_;
	}

	public void setException(Exception exception) {
		exception_ = exception;
	}

	public HttpResponse getResponse() {
		return response_;
	}

	public void setResponse(HttpResponse response) {
		response_ = response;
	}

	public void requestFinished(){
		if(exception_ != null){
			callback_.onError(exception_);
		}
		else {
			callback_.onResponse(response_);
		}
	}
}
