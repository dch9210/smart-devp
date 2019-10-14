package com.fanggeek.teams.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fanggeek.teams.api.db.mongodb.ApiDao;
import com.fanggeek.teams.api.db.mongodb.model.ApiDocument;
import com.fanggeek.teams.api.entity.ApiList;
import com.fanggeek.teams.api.entity.ApiTestHeader;
import com.fanggeek.teams.api.entity.ApiTestReq;
import com.fanggeek.teams.common.entity.HttpResult;
import com.fanggeek.teams.common.service.HttpClientService;
import com.fanggeek.teams.common.util.AssertHelper;

@Controller
@RequestMapping("/swaggerTool/apiManage")
public class ApiManageController {

	@Autowired
	private ApiDao apiDao;

	@Autowired
	private HttpClientService httpClientService;

	@RequestMapping(value = "/api/list", method = RequestMethod.GET)
	@ResponseBody
	public ApiList getApiList(String userId, Integer pageNum, Integer pageSize, String searchKey) {
		if (AssertHelper.isEmpty(pageNum))
			pageNum = 1;
		if (AssertHelper.isEmpty(pageSize))
			pageSize = 20;
		ApiList result = new ApiList();
		List<ApiDocument> datas = apiDao.getApiList(userId, searchKey, pageNum, pageSize);
		long count = apiDao.countApiList(userId, null);
		result.setDatas(datas);
		result.setTotalCount(count);
		return result;
	}

	@RequestMapping(value = "/api/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteApi(@PathVariable("id") String id) {
		apiDao.softDeleteById(id);
	}

	@RequestMapping(value = "/api/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ApiDocument getApi(@PathVariable("id") String id) {
		return apiDao.getById(id, ApiDocument.class);
	}

	@RequestMapping(value = "/testApi", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult testApi(@RequestBody ApiTestReq apiTestReq) {
		HttpResult result = new HttpResult();
		String agentUrl = apiTestReq.getTarget() + apiTestReq.getQuery();
		Map<String, String> headerMap = new HashMap<String, String>();
		if (AssertHelper.notEmpty(apiTestReq.getHeaders())) {
			for (ApiTestHeader header : apiTestReq.getHeaders()) {
				headerMap.put(header.getKey(), header.getVal());
			}
		}
		long sTime = System.currentTimeMillis();
		if ("POST".equalsIgnoreCase(apiTestReq.getMethod())) {
			try {

				HttpResult resp = httpClientService.post(agentUrl, null, apiTestReq.getReqBody(), headerMap);
				resp.setConsumeTime(System.currentTimeMillis() - sTime);
				return resp;
			} catch (Exception e) {
				result.setMsg(e.getMessage());
			}
		} else if ("GET".equalsIgnoreCase(apiTestReq.getMethod())) {
			try {
				String respStr = httpClientService.get(agentUrl, null, headerMap);
				result.setMsg(respStr);
				result.setConsumeTime(System.currentTimeMillis() - sTime);
				return result;
			} catch (Exception e) {
				result.setMsg(e.getMessage());
			}
		} else if ("PUT".equalsIgnoreCase(apiTestReq.getMethod())) {
			try {
				HttpResult resp = httpClientService.put(agentUrl, null, apiTestReq.getReqBody(), headerMap);
				resp.setConsumeTime(System.currentTimeMillis() - sTime);
				return resp;
			} catch (Exception e) {
				result.setMsg(e.getMessage());
			}
		} else if ("DELETE".equalsIgnoreCase(apiTestReq.getMethod())) {
			try {
				HttpResult resp = httpClientService.delete(agentUrl, null, apiTestReq.getReqBody(), headerMap);
				resp.setConsumeTime(System.currentTimeMillis() - sTime);
				return resp;
			} catch (Exception e) {
				result.setMsg(e.getMessage());
			}
		}

		return result;
	}

}
