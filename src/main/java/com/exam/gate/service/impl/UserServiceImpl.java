package com.exam.gate.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.gate.common.config.AppConfig;
import com.exam.gate.common.util.RedisUtil;
import com.exam.gate.domain.dto.LogoutDTO;
import com.exam.gate.domain.dto.UserDTO;
import com.exam.gate.domain.entity.User;
import com.exam.gate.service.UserService;

@Service
public class UserServiceImpl implements UserService{
    //数据持久层使用redis来实现
    @Autowired
    RedisUtil redisUtil;

    //新增用户
    public void create(UserDTO userDTO) throws Exception {
        User userPOExist = queryUserByAccount(userDTO.getUserName());
        if(userPOExist!=null) {
            throw new Exception("重复用户,此用户已存在");
        }
        redisUtil.set(AppConfig.USER_KEY_PREFIX + userDTO.getUserName(),userDTO.getPassword());
    }


    public String login(UserDTO userDTO) throws Exception {
        User userPOExist = queryUserByAccount(userDTO.getUserName());
        if (userPOExist != null) {
            String password = userPOExist.getPassword();
            if (!userDTO.getPassword().equals(password)){
                throw new Exception("无效密码登录");
            }
        }
        if (userPOExist == null){
            throw new Exception("非法用户登录,用户不存在");
        }
        String uuid = UUID.randomUUID().toString();
        redisUtil.set(AppConfig.TOKEN_KEY_PREFIX+uuid,userDTO.getUserName(),AppConfig.TOKEN_EXPIRE_TIME_SECOND);
        return uuid;
    }


    public User queryUserByAccount(String account) throws Exception {
        String password = (String) redisUtil.get(AppConfig.USER_KEY_PREFIX+account);
        if (password == null || "".equals(password.trim())){
            return null;
        }
        User user = new User();
        user.setUserName(account);
        user.setPassword(password);
        return user;
    }
    
    public boolean checkSessionId(String userName, String sessionId) throws Exception {
        if (userName == null || sessionId == null) return false;
        String result =  (String)redisUtil.get(AppConfig.TOKEN_KEY_PREFIX + sessionId);
        if (result == null || !result.equals(userName)) {
            return false;
        }
        else {
            return true;
        }
    }
    
    public void logout(LogoutDTO logoutDto) throws Exception {
        String userName = logoutDto.getUserName();
        String sessionId = logoutDto.getSessionId();
        if (userName == null || sessionId == null) {
            throw new Exception("鉴权失败");
        }
        String result =  (String)redisUtil.get(AppConfig.TOKEN_KEY_PREFIX + sessionId);
        if (result == null || !result.equals(userName)){
            throw new Exception("鉴权失败");
        }
        redisUtil.del(AppConfig.USER_KEY_PREFIX + logoutDto.getUserName());
    }
}
