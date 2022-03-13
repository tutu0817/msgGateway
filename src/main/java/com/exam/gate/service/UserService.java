package com.exam.gate.service;

import com.exam.gate.domain.dto.LogoutDTO;
import com.exam.gate.domain.dto.UserDTO;
import com.exam.gate.domain.entity.User;

public interface UserService {

    void create(UserDTO user) throws Exception;

    User queryUserByAccount(String account) throws Exception;
    
    String login(UserDTO userDTO) throws Exception;

    boolean checkSessionId(String userName, String sessionId) throws Exception;
    
    public void logout(LogoutDTO logoutDto) throws Exception;
}
