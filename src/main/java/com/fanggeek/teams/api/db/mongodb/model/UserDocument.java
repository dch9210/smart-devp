package com.fanggeek.teams.api.db.mongodb.model;

public class UserDocument extends MongoDocument{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2131776399863282197L;

	private String name;

	private String pwd;

	private String avater;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getAvater() {
		return avater;
	}

	public void setAvater(String avater) {
		this.avater = avater;
	}

}
