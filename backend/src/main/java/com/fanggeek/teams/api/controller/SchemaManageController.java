package com.fanggeek.teams.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fanggeek.teams.api.db.mongodb.SchemaDao;
import com.fanggeek.teams.api.db.mongodb.model.SchemaDocument;
import com.fanggeek.teams.api.entity.SchemaList;
import com.fanggeek.teams.common.entity.HttpResult;
import com.fanggeek.teams.common.util.AssertHelper;

@Controller
@RequestMapping("/schemaManage/")
public class SchemaManageController {

	@Autowired
	private SchemaDao schemaDao;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public SchemaList getSchemaList(String userId, String searchKey,
			Integer pageSize, Integer page) {
		if (AssertHelper.isEmpty(page))
			page = 1;
		if (AssertHelper.isEmpty(pageSize))
			pageSize = 20;
		int limit = pageSize;
		int skip = (page - 1) * pageSize;
		SchemaList result = new SchemaList();
		result.setTotal(schemaDao.countSchemaListByUserIdAndSearchKey(userId,
				searchKey));
		result.setDatas(schemaDao.getSchemaListByUserIdAndSearchKey(userId,
				searchKey, limit, skip));
		return result;
	}

	@RequestMapping(value = "/schema", method = RequestMethod.PUT)
	@ResponseBody
	public HttpResult updateSchema(@RequestBody SchemaDocument schema) {
		HttpResult result = new HttpResult();
		if (AssertHelper.isAnyoneEmpty(schema, schema.getContent(),
				schema.getUserId(), schema.getName())) {
			result.setCode(-1);
			result.setMsg("bad query params!");
			return result;
		}
		try {
			schemaDao.removeById(schema.get_id());
			schema.set_id(null);
			schemaDao.save(schema);
			result.setCode(0);
			result.setMsg("success!");
			result.setData(schema);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/schema", method = RequestMethod.DELETE)
	@ResponseBody
	public HttpResult deleteSchema(String id) {
		HttpResult result = new HttpResult();
		try {
			schemaDao.removeById(id);
			result.setCode(0);
			result.setMsg("success!");
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return result;
	}

}
