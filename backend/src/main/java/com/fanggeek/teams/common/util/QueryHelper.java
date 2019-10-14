package com.fanggeek.teams.common.util;

import org.springframework.data.mongodb.core.query.Criteria;

import com.fanggeek.teams.api.db.DBConstants;

public class QueryHelper {
	public static final Criteria NOT_DELETE() {
		return Criteria.where(DBConstants.FIELD_DELETE).ne(1);
	}

	public static final Criteria DELETE() {
		return Criteria.where(DBConstants.FIELD_DELETE).is(1);
	}
}
