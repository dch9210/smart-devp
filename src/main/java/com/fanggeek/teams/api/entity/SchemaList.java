package com.fanggeek.teams.api.entity;

import java.util.List;

import com.fanggeek.teams.api.db.mongodb.model.SchemaDocument;

public class SchemaList {

	private Long total;

	private List<SchemaDocument> datas;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<SchemaDocument> getDatas() {
		return datas;
	}

	public void setDatas(List<SchemaDocument> datas) {
		this.datas = datas;
	}

}
