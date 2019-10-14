package com.fanggeek.teams.api.controller;

import com.fanggeek.teams.api.db.mongodb.ApiDao;
import com.fanggeek.teams.api.db.mongodb.SchemaDao;
import com.fanggeek.teams.api.db.mongodb.model.ApiDocument;
import com.fanggeek.teams.api.db.mongodb.model.SchemaDocument;
import com.fanggeek.teams.common.entity.HttpResult;
import com.fanggeek.teams.common.service.HttpClientService;
import com.fanggeek.teams.common.util.AssertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 新增Api控制器
 * 
 * 
 * @author eric
 *
 */
@Controller
@RequestMapping("/swaggerTool/newApi")
public class AddNewApiController {

	@Value("${swagger.url}")
	private String swaggerUrl;

	@Autowired
	private HttpClientService httpClientService;

	@Autowired
	private SchemaDao schemaDao;

	@Autowired
	private ApiDao apiDao;

	/**
	 * 获取开发环境中最新版本的swagger.json定义
	 * 
	 * @return
	 * @author dengchenghao
	 * @throws Exception
	 */
	@RequestMapping(value = "/swagger/resource", method = RequestMethod.GET)
	public void getSwaggerData(HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		String swaggerJson = "";
		try {
			swaggerJson = httpClientService.get(swaggerUrl, null);
		} catch (Exception e) {
			// 服务器异常，使用本地的先
			File localSwaggerFile = new File("D:\\devp\\git_repo\\server-api\\tools\\swagger\\data\\swagger.json");
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localSwaggerFile));
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[1024];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			if (bis != null)
				bis.close();
			if (bos != null) {
				bos.flush();
				bos.close();
			}
			return;
		}
		byte[] datas = swaggerJson.getBytes("UTF-8");
		OutputStream os = response.getOutputStream();
		os.write(datas);
		os.flush();
		os.close();
	}

	@RequestMapping(value = "/schema", method = RequestMethod.PUT)
	@ResponseBody
	public HttpResult saveNewSchema(@RequestBody SchemaDocument schema) {
		HttpResult result = new HttpResult();
		if (AssertHelper.isAnyoneEmpty(schema, schema.getContent(), schema.getUserId(), schema.getName())) {
			result.setCode(-1);
			result.setMsg("bad query params!");
			return result;
		}
		try {
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

	@RequestMapping(value = "/schema/list", method = RequestMethod.GET)
	@ResponseBody
	public List<SchemaDocument> getSchemaList(String userId) {
		return schemaDao.getSchemaListByUserId(userId);
	}

	@RequestMapping(value = "/api", method = RequestMethod.PUT)
	@ResponseBody
	public HttpResult saveNewApi(@RequestBody ApiDocument api) {
		HttpResult result = new HttpResult();
		if (AssertHelper.isAnyoneEmpty(api, api.getContent(), api.getUserId(), api.getName(), api.getPath(), api.getMethod())) {
			result.setCode(-1);
			result.setMsg("bad query params!");
			return result;
		}
		// 如果有_id，则需要删除原来的数据，保存新的数据
		if (AssertHelper.notEmpty(api.get_id())) {
			apiDao.removeById(api.get_id());
		}
		try {
			api.set_id(null);
			apiDao.save(api);
			result.setCode(0);
			result.setMsg("success!");
			result.setData(api);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/api/{apiId}", method = RequestMethod.GET)
	@ResponseBody
	public ApiDocument getApiDocument(@PathVariable("apiId") String apiId) {
		return apiDao.getById(apiId, ApiDocument.class);
	}

}
