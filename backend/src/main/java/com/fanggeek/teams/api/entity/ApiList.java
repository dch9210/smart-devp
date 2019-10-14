package com.fanggeek.teams.api.entity;

import java.util.List;

import com.fanggeek.teams.api.db.mongodb.model.ApiDocument;

public class ApiList {

	private Long totalCount;

	private List<ApiDocument> datas;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public List<ApiDocument> getDatas() {
		return datas;
	}

	public void setDatas(List<ApiDocument> datas) {
		this.datas = datas;
	}

}
