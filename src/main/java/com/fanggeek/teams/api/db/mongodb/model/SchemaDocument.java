package com.fanggeek.teams.api.db.mongodb.model;

import java.util.List;

public class SchemaDocument extends MongoDocument{

	/**
	 * 
	 */
	private static final long serialVersionUID = -508748015329256379L;

	private String userId;

	private String name;

	private String desc;

	private List<SchemaParameter> parameters;

	private String content;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<SchemaParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<SchemaParameter> parameters) {
		this.parameters = parameters;
	}

}
