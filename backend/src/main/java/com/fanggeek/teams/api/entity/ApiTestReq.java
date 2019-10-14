package com.fanggeek.teams.api.entity;

import java.util.List;

public class ApiTestReq {

	private String target;

	private String method;

	private String query;

	private String reqBody;

	private List<ApiTestHeader> headers;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getReqBody() {
		return reqBody;
	}

	public void setReqBody(String reqBody) {
		this.reqBody = reqBody;
	}

	public List<ApiTestHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(List<ApiTestHeader> headers) {
		this.headers = headers;
	}

}
