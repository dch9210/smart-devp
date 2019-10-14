package com.fanggeek.teams.common.entity;

public class HttpResult {

	private Integer code;

	private String msg;

	private Object data;

	private long consumeTime;

	public long getConsumeTime() {
		return consumeTime;
	}

	public void setConsumeTime(long consumeTime) {
		this.consumeTime = consumeTime;
	}

	public HttpResult() {
	}

	public HttpResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
