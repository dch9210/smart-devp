package com.fanggeek.teams.api.db.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.fanggeek.teams.api.db.DBConstants;
import com.fanggeek.teams.api.db.mongodb.model.MongoDocument;
import com.fanggeek.teams.common.util.AssertHelper;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public abstract class MongoDaoSupport<T extends MongoDocument> {

	@Autowired
	private MongoTemplate mongoTemplate;

	protected abstract String collectionName();

	protected DBCollection getCollection() {
		return mongoTemplate.getCollection(collectionName());
	}

	protected DBCollection getCollection(String collectionName) {
		return mongoTemplate.getCollection(collectionName());
	}

	protected String save(T t, String id) {
		t.set_id(id);
		return save(t, collectionName());
	}

	public String save(T t) {
		Date now = new Date();
		if (t.getCreateTime() == null) {
			t.setCreateTime(now);
		}
		if (t.getModifyTime() == null) {
			t.setModifyTime(now);
		}
		mongoTemplate.insert(t, collectionName());
		return t.get_id();
	}

	public String saveOnly(T t) {
		mongoTemplate.insert(t, collectionName());
		return t.get_id();
	}

	public T getById(Object id, Class<T> entityClass) {
		return mongoTemplate.findById(id, entityClass, collectionName());
	}

	public T getById(String id, Class<T> entityClass) {
		return mongoTemplate.findById(id, entityClass, collectionName());
	}

	protected T get(Query query, Class<T> entityClass) {
		return mongoTemplate.findOne(query, entityClass, collectionName());
	}

	protected boolean exists(Query query, Class<T> entityClass) {
		return mongoTemplate.exists(query, entityClass, collectionName());
	}

	public List<T> getAll(Class<T> entityClass) {
		return mongoTemplate.findAll(entityClass, collectionName());
	}

	public List<T> getByIds(Collection<String> ids, Class<T> entityClass) {
		if (AssertHelper.isEmpty(ids)) {
			return new ArrayList<>(0);
		}
		List<ObjectId> objectIds = idsToObjectIds(ids);
		Query query = new Query(Criteria.where("_id").in(objectIds));
		return getList(query, entityClass);
	}

	public List<ObjectId> idsToObjectIds(Collection<String> ids) {
		List<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toList());
		return objectIds;
	}

	protected List<T> getList(Query query, Class<T> entityClass) {
		return this.getList(query, entityClass, 0, 0);
	}

	protected <K> List<K> getList(Query query, Class<K> clazz, int limit, int skip) {
		if (limit > 0) {
			query.limit(limit);
		}

		if (skip > 0) {
			query.skip(skip);
		}
		return mongoTemplate.find(query, clazz, collectionName());
	}
	
	
	/**
	 * <br>计数
	 * @param query
	 * @return
	 * @author YellowTail
	 * @since 2018-10-30
	 */
	public long count(Query query){
		return count(query, collectionName());
	}
	
	protected long count(Query query, String collectionName){
		return mongoTemplate.count(query, collectionName);
	}
	
	public boolean removeById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		return remove(query);
	}
	
	public boolean removeByDocId(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		return remove(query);
	}
	
	protected boolean remove(Query query) {
		mongoTemplate.remove(query, collectionName());
		return true;
	}
	
	public void softDeleteById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		Update update = new Update();
		update.set(DBConstants.FIELD_DELETE, DBConstants.DEL_FLAG_INNER);
		update(query, update);
	}

	public void update(T t) {
		t.setModifyTime(new Date());
		
		mongoTemplate.save(t, collectionName());
	}
	
	public void updateOnly(T t) {
		mongoTemplate.save(t, collectionName());
	}
	
	public void update(Query query, Update update) {
		if(AssertHelper.notEmpty(update.getUpdateObject().toMap())){
			update.set("modifyTime", new Date());
			mongoTemplate.updateFirst(query, update, collectionName());
		}
	}
	
	public void updateWithoutModifyTime(Query query, Update update) {
		if(AssertHelper.notEmpty(update.getUpdateObject().toMap())){			
			mongoTemplate.updateFirst(query, update, collectionName());
		}
	}
	/**
	 * 批量更新，设置修改时间
	 * @return 
	 */
	public WriteResult updateMulti(Query query, Update update) {

		if (AssertHelper.notEmpty(update.getUpdateObject().toMap())) {
			update.set("modifyTime", new Date());
			return mongoTemplate.updateMulti(query, update, collectionName());
		}
		return null;
	}
	/**
	 * 直接批量更新
	 */
	public void updateMultiWithoutModifyTime(Query query, Update update) {

		if (AssertHelper.notEmpty(update.getUpdateObject().toMap())) {
			mongoTemplate.updateMulti(query, update, collectionName());
		}
	}
	
}
