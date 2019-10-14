package com.fanggeek.teams.api.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fanggeek.teams.api.db.mongodb.UserDao;
import com.fanggeek.teams.api.db.mongodb.model.UserDocument;
import com.fanggeek.teams.api.entity.User;
import com.fanggeek.teams.common.Constants;
import com.fanggeek.teams.common.entity.HttpResult;
import com.fanggeek.teams.common.util.AssertHelper;
import com.fanggeek.teams.common.util.MD5Utils;

@Controller
@RequestMapping("/sign")
public class SignController {

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	@ResponseBody
	public HttpResult check(@PathVariable("name") String name) {
		HttpResult result = new HttpResult();
		UserDocument userDocument = userDao.getUserByName(name);
		if (AssertHelper.notEmpty(userDocument)) {
			result.setCode(-1);
			result.setMsg("The Name Allready exist!");
		} else {
			result.setCode(0);
		}
		return result;
	}

	@RequestMapping(value = "/up", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult regist(@RequestBody User newUser) {
		HttpResult result = new HttpResult();
		if (AssertHelper.isAnyoneEmpty(newUser, newUser.getName(),
				newUser.getPwd())) {
			result.setCode(-1);
			result.setMsg("illegal params!");
			return result;
		}
		String passwordMd5 = MD5Utils.encryptPassword(newUser.getPwd(),
				Constants.SIGN_SALT);
		newUser.setPwd(passwordMd5);
		try {
			UserDocument userDocument = JSON.parseObject(
					JSON.toJSONString(newUser), UserDocument.class);
			userDao.save(userDocument);
			newUser.setId(userDocument.get_id());
			result.setCode(0);
			result.setData(newUser);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/in", method = RequestMethod.POST)
	@ResponseBody
	public HttpResult login(String name, String pwd) {
		HttpResult result = new HttpResult();
		if (AssertHelper.isAnyoneEmpty(name, pwd)) {
			result.setCode(-1);
			result.setMsg("illegal params!");
			return result;
		}
		String passwordMd5 = MD5Utils.encryptPassword(pwd, Constants.SIGN_SALT);
		UserDocument userDocument = userDao.getUserByNameAndPwd(name,
				passwordMd5);
		if (AssertHelper.isEmpty(userDocument)) {
			result.setCode(-1);
			result.setMsg("Name Or Password Wrong, Please Try Again!");
		} else {
			result.setCode(0);
			User user = JSON.parseObject(JSON.toJSONString(userDocument),
					User.class);
			user.setId(userDocument.get_id());
			result.setData(user);
		}
		return result;
	}

}
