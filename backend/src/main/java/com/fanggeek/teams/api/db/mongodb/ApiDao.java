package com.fanggeek.teams.api.db.mongodb;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fanggeek.teams.api.db.DBConstants;
import com.fanggeek.teams.api.db.mongodb.model.ApiDocument;
import com.fanggeek.teams.common.util.AssertHelper;
import com.fanggeek.teams.common.util.QueryHelper;

@Component
public class ApiDao extends MongoDaoSupport<ApiDocument> {

	@Override
	protected String collectionName() {
		return DBConstants.TABLE_NAME_API;
	}

	public long countApiList(String userId, String searchKey) {
		Query query = new Query(QueryHelper.NOT_DELETE().and("userId").is(userId));
		return count(query);
	}

	public List<ApiDocument> getApiList(String userId, String searchKey, Integer pageNum, Integer pageSize) {
		Query query = new Query(QueryHelper.NOT_DELETE().and("userId").is(userId));
		if (AssertHelper.notEmpty(searchKey)) {
			Pattern pattern = Pattern.compile("^.*" + searchKey + ".*$", Pattern.CASE_INSENSITIVE);
			Criteria orCriteria = new Criteria();
			orCriteria.orOperator(Criteria.where("name").regex(pattern), Criteria.where("desc").regex(pattern), Criteria.where("path").regex(pattern));
			query.addCriteria(orCriteria);
		}
		int limit = pageSize;
		if (pageNum < 0)
			pageNum = 1;
		int skip = (pageNum - 1) * pageSize;
		query.with(new Sort(Sort.Direction.DESC, "createTime"));
		return getList(query, ApiDocument.class, limit, skip);
	}

}
