package com.fanggeek.teams.api.db.mongodb;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fanggeek.teams.api.db.DBConstants;
import com.fanggeek.teams.api.db.mongodb.model.ApiTestHistoryDocument;
import com.fanggeek.teams.common.util.QueryHelper;


@Component
public class ApiTestHistoryDao extends MongoDaoSupport<ApiTestHistoryDocument>{

	@Override
	protected String collectionName() {
		return DBConstants.TABLE_NAME_API_TEST_HISTORY;
	}
	
	public List<ApiTestHistoryDocument> getListByUserId(String userId, String apiId) {
		Query query = new Query(QueryHelper.NOT_DELETE().and("userId").is(userId).and("apiId").is(apiId));
		query.with(new Sort(Sort.Direction.DESC, "createTime"));
		return getList(query, ApiTestHistoryDocument.class);
	}

}
