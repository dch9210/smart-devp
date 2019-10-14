package com.fanggeek.teams.api.db.mongodb;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fanggeek.teams.api.db.DBConstants;
import com.fanggeek.teams.api.db.mongodb.model.UserDocument;
import com.fanggeek.teams.common.util.QueryHelper;

@Component
public class UserDao extends MongoDaoSupport<UserDocument> {

	@Override
	protected String collectionName() {
		return DBConstants.TABLE_NAME_USER;
	}

	public UserDocument getUserByName(String name) {
		Query query = new Query(QueryHelper.NOT_DELETE().and("name").is(name));
		return get(query, UserDocument.class);
	}
	
	public UserDocument getUserByNameAndPwd(String name, String pwd) {
		Query query = new Query(QueryHelper.NOT_DELETE().and("name").is(name).and("pwd").is(pwd));
		return get(query, UserDocument.class);
	}

}
