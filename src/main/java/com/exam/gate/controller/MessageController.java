package com.exam.gate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exam.gate.domain.dto.MessageDTO;
import com.exam.gate.domain.response.Response;
import com.exam.gate.domain.response.ResponseCode;
import com.exam.gate.service.MessageService;
import com.exam.gate.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MessageController {
	@Autowired
    MessageService messageService;
	@Autowired
    UserService userService;
	
    @PostMapping(value ="/directmessage",produces = "application/json;charset=utf-8")
    public Response directMessage(@RequestParam("tels") String tels, @RequestParam("qos") String qos, @RequestParam("userName") String userName, @RequestParam("sessionId") String sessionId, @RequestBody MessageDTO messageDTO) throws Exception{
//        //检查sessionId
//        if (!userService.checkSessionId(userName, sessionId))
//            return new Response(ResponseCode.VALID_AUTH_ERROR);
//        //检查参数合法性
//        if (!messageService.checkParams(tels, qos, userName, messageDTO))
//        	return new Response(ResponseCode.PARAMETER_VERIATION_ERROR);
        //发送消息
        try {           
            return messageService.sendMessage(qos, tels, messageDTO);
        }catch(Exception e){
            log.error("directmessage fail", e.getMessage());
            return new Response(ResponseCode.CALL_FAIL);
        }
    }
}
