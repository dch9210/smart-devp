package com.fanggeek.teams.api.db.mongodb;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fanggeek.teams.api.db.DBConstants;
import com.fanggeek.teams.api.db.mongodb.model.SchemaDocument;
import com.fanggeek.teams.common.util.AssertHelper;
import com.fanggeek.teams.common.util.QueryHelper;

@Component
public class SchemaDao extends MongoDaoSupport<SchemaDocument> {

	@Override
	protected String collectionName() {
		return DBConstants.TABLE_NAME_SCHEMA;
	}

	public List<SchemaDocument> getSchemaListByUserId(String userId) {
		Query query = new Query(
				QueryHelper.NOT_DELETE().and("userId").is(userId));
		return getList(query, SchemaDocument.class);
	}

	public long countSchemaListByUserIdAndSearchKey(String userId,
			String searchKey) {
		Query query = new Query(
				QueryHelper.NOT_DELETE().and("userId").is(userId));
		if (AssertHelper.notEmpty(searchKey)) {
			Pattern pattern = Pattern.compile("^.*" + searchKey + ".*$",
					Pattern.CASE_INSENSITIVE);
			Criteria orCriteria = new Criteria();
			orCriteria.orOperator(Criteria.where("name").regex(pattern),
					Criteria.where("desc").regex(pattern));
			query.addCriteria(orCriteria);
		}
		return count(query);
	}

	public List<SchemaDocument> getSchemaListByUserIdAndSearchKey(String userId,
			String searchKey, int limit, int skip) {
		Query query = new Query(
				QueryHelper.NOT_DELETE().and("userId").is(userId));
		if (AssertHelper.notEmpty(searchKey)) {
			Pattern pattern = Pattern.compile("^.*" + searchKey + ".*$",
					Pattern.CASE_INSENSITIVE);
			Criteria orCriteria = new Criteria();
			orCriteria.orOperator(Criteria.where("name").regex(pattern),
					Criteria.where("desc").regex(pattern));
			query.addCriteria(orCriteria);
		}
		query.with(new Sort(Sort.Direction.DESC, "createTime"));
		return getList(query, SchemaDocument.class, limit, skip);
	}

}
