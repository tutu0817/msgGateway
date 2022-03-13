package com.exam.gate.service;

import com.exam.gate.domain.dto.MessageDTO;
import com.exam.gate.domain.response.Response;

public interface MessageService {

	boolean checkParams(String tels, String qos, String userName, MessageDTO messageDTO);
	
	Response sendMessage(String qos,String  tels, MessageDTO messageDTO);
	
}
