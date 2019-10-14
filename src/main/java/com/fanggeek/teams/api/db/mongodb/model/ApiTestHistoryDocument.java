package com.fanggeek.teams.api.db.mongodb.model;

public class ApiTestHistoryDocument extends MongoDocument {

	/**
	 * [serialVersionUID]
	 */
	private static final long serialVersionUID = 1547903901082518581L;

	private String userId;

	private String content;

	private String apiId;

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
