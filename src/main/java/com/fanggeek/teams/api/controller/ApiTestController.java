package com.fanggeek.teams.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fanggeek.teams.api.db.mongodb.ApiTestHistoryDao;
import com.fanggeek.teams.api.db.mongodb.model.ApiTestHistoryDocument;
import com.fanggeek.teams.api.entity.SaveApiTestHistoryReq;
import com.fanggeek.teams.common.entity.HttpResult;

@Controller
@RequestMapping("/apiTest")
public class ApiTestController {

	@Autowired
	private ApiTestHistoryDao apiTestHistoryDao;

	@RequestMapping(value = "/history", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult saveApiTestHistory(@RequestBody SaveApiTestHistoryReq body) {
		HttpResult result = new HttpResult();
		try {
			ApiTestHistoryDocument t = new ApiTestHistoryDocument();
			t.setContent(body.getContent());
			t.setUserId(body.getUserId());
			t.setApiId(body.getApiId());
			apiTestHistoryDao.save(t);
			result.setCode(0);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/historyList", method = RequestMethod.GET)
	@ResponseBody
	public List<ApiTestHistoryDocument> getApiTestHistoryList(String userId, String apiId) {
		return apiTestHistoryDao.getListByUserId(userId, apiId);
	}

	@RequestMapping(value = "/history/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public HttpResult deleteHistory(@PathVariable("id") String id) {
		HttpResult result = new HttpResult();
		try {
			apiTestHistoryDao.removeById(id);
			result.setCode(0);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return result;
	}

}
