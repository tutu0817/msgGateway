package com.exam.gate.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.exam.gate.domain.dto.LogoutDTO;
import com.exam.gate.domain.dto.UserDTO;
import com.exam.gate.domain.response.Response;
import com.exam.gate.domain.response.ResponseCode;
import com.exam.gate.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 航信服务接口控制器
 * @author
 */
@RestController
@Slf4j
@RequestMapping(value ="/auth/user",produces = "application/json;charset=utf-8")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public Response register(@RequestBody UserDTO registUserDto,HttpServletResponse response) throws Exception {
		try {
			String pattern = "^[A-Za-z0-9]+$";
			// 参数校验
			if (StringUtils.isBlank(registUserDto.getUserName()) || registUserDto.getUserName().length() < 3 || registUserDto.getUserName().length() > 32 || !registUserDto.getUserName().matches(pattern)){
				log.info("非法用户名，请检查参数");
				return new Response(ResponseCode.PARAMETER_VERIATION_ERROR);
			}
			if (StringUtils.isBlank(registUserDto.getPassword()) || registUserDto.getPassword().length() < 8 || registUserDto.getPassword().length() > 64 || !registUserDto.getPassword().matches(pattern)){
				log.info("非法密码，请检查参数");
				return new Response(ResponseCode.PARAMETER_VERIATION_ERROR);
			}
			userService.create(registUserDto);
			return new Response(ResponseCode.CALL_SUCCESS);


		} catch (Exception e) {
			log.error("注册用户出错：{}", e.getMessage());
			return new Response(ResponseCode.CALL_FAIL);
		}
	}


	@PostMapping("/login")
	public JSONObject login(@RequestBody UserDTO loginDto,HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		try {
			String pattern = "^[A-Za-z0-9]+$";			
			// 参数校验
			if (StringUtils.isBlank(loginDto.getUserName()) || loginDto.getUserName().length() < 3 || loginDto.getUserName().length() > 32 || !loginDto.getUserName().matches(pattern)){
				log.info("非法用户名，请检查参数");
				json.put("code", ResponseCode.PARAMETER_VERIATION_ERROR.getCode());
				json.put("msg", ResponseCode.PARAMETER_VERIATION_ERROR.getMsg());
				return json;

			}
			if (StringUtils.isBlank(loginDto.getPassword()) || loginDto.getPassword().length() < 8 || loginDto.getPassword().length() > 64 || !loginDto.getPassword().matches(pattern)){
				log.info("非法密码，请检查参数");
				json.put("code", ResponseCode.PARAMETER_VERIATION_ERROR.getCode());
				json.put("msg", ResponseCode.PARAMETER_VERIATION_ERROR.getMsg());
				return json;
			}

			String sessionId = userService.login(loginDto);
			json.put("code", ResponseCode.CALL_SUCCESS.getCode());
			json.put("msg", ResponseCode.CALL_SUCCESS.getMsg());
			json.put("sessionId", sessionId);
			return json;
		} catch (Exception e) {
			log.error("登录出错：{}", e.getMessage());
			json.put("code", ResponseCode.CALL_FAIL.getCode());
			json.put("msg", ResponseCode.CALL_FAIL.getMsg());
			return json;
		}
	}

	@PostMapping("/logout")
	public Response logout(@RequestBody LogoutDTO logoutDto){
		try {
			String pattern = "^[A-Za-z0-9]+$";
			// 参数校验
			if (StringUtils.isBlank(logoutDto.getUserName()) || logoutDto.getUserName().length() < 3 || logoutDto.getUserName().length() > 32 || !logoutDto.getUserName().matches(pattern)){
				log.info("非法用户名，请检查参数");
				return new Response(ResponseCode.PARAMETER_VERIATION_ERROR);
			}
			if (!userService.checkSessionId(logoutDto.getUserName(), logoutDto.getSessionId())) {
				log.info(logoutDto.getUserName() + "禁止未经授权访问");
				return new Response(ResponseCode.CALL_FAIL);
			}
			userService.logout(logoutDto);
			return new Response(ResponseCode.CALL_SUCCESS);
		} catch (Exception e) {
			log.error("用户登出失败：{}",e.getMessage());
			return new Response(ResponseCode.CALL_FAIL);
		}
	}
}
