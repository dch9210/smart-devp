package com.fanggeek.teams.common.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.fanggeek.teams.common.entity.HttpResult;
import com.fanggeek.teams.common.util.AssertHelper;

@Service
public class HttpClientService {

	@Autowired
	private CloseableHttpClient httpClient;

	@Autowired
	private RequestConfig config;

	/**
	 * 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String get(String url, Map<String, String> headerMap)
			throws Exception {
		// 声明 http get 请求
		HttpGet httpGet = new HttpGet(url);

		// 装载配置信息
		httpGet.setConfig(config);

		// 设置请求头
		if (AssertHelper.notEmpty(headerMap)) {
			for (String key : headerMap.keySet()) {
				httpGet.addHeader(key, headerMap.get(key));
			}
		}

		// 发起请求
		CloseableHttpResponse response = this.httpClient.execute(httpGet);

		// 判断状态码是否为200
		if (response.getStatusLine().getStatusCode() == 200) {
			// 返回响应体的内容
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		}
		return null;
	}

	/**
	 * 带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String get(String url, Map<String, Object> map,
			Map<String, String> headerMap) throws Exception {
		URIBuilder uriBuilder = new URIBuilder(url);
		if (map != null) {
			// 遍历map,拼接请求参数
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				uriBuilder.setParameter(entry.getKey(),
						entry.getValue().toString());
			}
		}

		// 调用不带参数的get请求
		return this.get(uriBuilder.build().toString(), headerMap);

	}

	/**
	 * 带参数的post请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public HttpResult post(String url, Map<String, Object> map, String reqBody,
			Map<String, String> headerMap) throws Exception {
		// 声明httpPost请求
		HttpPost httpPost = new HttpPost(url);
		// 加入配置信息
		httpPost.setConfig(config);

		// 设置请求头
		if (AssertHelper.notEmpty(headerMap)) {
			for (String key : headerMap.keySet()) {
				httpPost.addHeader(key, headerMap.get(key));
			}
		}

		// 判断map是否为空，不为空则进行遍历，封装from表单对象
		if (map != null) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(),
						JSON.toJSONString(entry.getValue())));
			}
			// 构造from表单对象
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					list, "UTF-8");

			// 把表单放到post里
			httpPost.setEntity(urlEncodedFormEntity);
		}

		if (AssertHelper.notEmpty(reqBody)) {
			StringEntity entity = new StringEntity(reqBody,
					Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
		}

		// 发起请求
		CloseableHttpResponse response = this.httpClient.execute(httpPost);
		return new HttpResult(response.getStatusLine().getStatusCode(),
				EntityUtils.toString(response.getEntity(), "UTF-8"));
	}
	
	public HttpResult put(String url, Map<String, Object> map, String reqBody,
			Map<String, String> headerMap) throws Exception {
		// 声明httpPost请求
		HttpPut httpPut = new HttpPut(url);
		// 加入配置信息
		httpPut.setConfig(config);

		// 设置请求头
		if (AssertHelper.notEmpty(headerMap)) {
			for (String key : headerMap.keySet()) {
				httpPut.addHeader(key, headerMap.get(key));
			}
		}

		// 判断map是否为空，不为空则进行遍历，封装from表单对象
		if (map != null) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(),
						JSON.toJSONString(entry.getValue())));
			}
			// 构造from表单对象
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					list, "UTF-8");

			// 把表单放到post里
			httpPut.setEntity(urlEncodedFormEntity);
		}

		if (AssertHelper.notEmpty(reqBody)) {
			StringEntity entity = new StringEntity(reqBody,
					Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			httpPut.setEntity(entity);
		}

		// 发起请求
		CloseableHttpResponse response = this.httpClient.execute(httpPut);
		return new HttpResult(response.getStatusLine().getStatusCode(),
				EntityUtils.toString(response.getEntity(), "UTF-8"));
	}
	
	public HttpResult delete(String url, Map<String, Object> map, String reqBody,
			Map<String, String> headerMap) throws Exception {
		// 声明httpPost请求
		HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
		// 加入配置信息
		httpDelete.setConfig(config);

		// 设置请求头
		if (AssertHelper.notEmpty(headerMap)) {
			for (String key : headerMap.keySet()) {
				httpDelete.addHeader(key, headerMap.get(key));
			}
		}

		// 判断map是否为空，不为空则进行遍历，封装from表单对象
		if (map != null) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(),
						JSON.toJSONString(entry.getValue())));
			}
			// 构造from表单对象
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					list, "UTF-8");

			// 把表单放到post里
			httpDelete.setEntity(urlEncodedFormEntity);
		}

		if (AssertHelper.notEmpty(reqBody)) {
			StringEntity entity = new StringEntity(reqBody,
					Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			httpDelete.setEntity(entity);
		}

		// 发起请求
		CloseableHttpResponse response = this.httpClient.execute(httpDelete);
		return new HttpResult(response.getStatusLine().getStatusCode(),
				EntityUtils.toString(response.getEntity(), "UTF-8"));
	}
	
}
