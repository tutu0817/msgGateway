package com.exam.gate.service.impl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.utils.StringUtils;
import com.exam.gate.common.config.AppConfig;
import com.exam.gate.common.util.RedisUtil;
import com.exam.gate.common.util.RestTemplateUtil;
import com.exam.gate.domain.dto.MessageDTO;
import com.exam.gate.domain.entity.DirectMessage;
import com.exam.gate.domain.entity.RequestMessage;
import com.exam.gate.domain.entity.TemplateParam;
import com.exam.gate.domain.response.Response;
import com.exam.gate.domain.response.ResponseCode;
import com.exam.gate.service.MessageService;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Commons;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageServiceImpl  implements MessageService{
	@Autowired
	RestTemplateUtil restTemplateUtil;
	
    @Autowired
    RedisUtil redisUtil;
    private static final long TIMEOUT = 1000*5L;
    private static final String RESULT_CODE_SUCCESS = "0";
    public static final String GET_SCRIPT =
            "local n1 = redis.call('ZRANGE',KEYS[1], -1, -1)\n" +
            "if n1 ~= nil \n" +
            "then \n" +
            "redis.call('ZREM',KEYS[1], n1[1])\n" +
            "end\n" +
            "return n1";
	public boolean checkParams(String tels, String qos, String userName, MessageDTO messageDTO){
		if (!checkTels(tels)) return false;
		if (!checkQos(qos)) return false;
		if (!checkUserName(userName)) return false;
		if (!checkTitle(messageDTO.getTitle())) return false;
		return true;
	}

	private boolean checkTels(String tels){
		if (StringUtils.isBlank(tels)) { 
			return false;
		}
		//电话号码要求是数字
		String pattern = "^[0-9]*$";
		return tels.matches(pattern);
	}

	private boolean checkQos(String qos){
		if (StringUtils.isBlank(qos) ||!qos.equals("1")||!qos.equals("2")||!qos.equals("3")) {
			return false;
		}
		return true;
	}

	private boolean checkUserName(String userName){
		if (StringUtils.isBlank(userName)) {
			return false;
		}
		int nameLength = userName.length();
		if (nameLength < 3 || nameLength > 32)
			return false;
		String pattern = "^[A-Za-z0-9]+$";
		return userName.matches(pattern);

	}

	private boolean checkTitle(String title){
		if (StringUtils.isBlank(title)) {
			return false;
		}
		int titleLength = title.length();
		if (titleLength < 1 || titleLength > 64)
			return false;
		else
			return true;
	}

    public Response sendMessage(String qos,String  tels, MessageDTO messageDTO){
    	DirectMessage directMessage = new DirectMessage();
		directMessage.setQos(qos);
		directMessage.setAcceptor_tel(tels);
		TemplateParam template_param = new TemplateParam();
		template_param.setTitle(messageDTO.getTitle());
		template_param.setContent(messageDTO.getContent());
		directMessage.setTemplate_param(template_param);
        Date beginTime = new Date();
        //生成本次请求的id
        String key = AppConfig.REQUEST_KEY_PREFIX + UUID.randomUUID().toString();

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setRequestID(key);
        requestMessage.setDirectMessage(directMessage);
        String json = JSON.toJSONString(requestMessage);
        redisUtil.add("priorityQuene1", json, Integer.parseInt(qos));

        //4.等待，并处理结果
        while (true){
            Date nowTime = new Date();
            if (nowTime.getTime() - beginTime.getTime()>TIMEOUT) {
                log.info("发送超时：{}", requestMessage);
                return new Response(ResponseCode.CALL_FAIL);
            }
            String result = (String) redisUtil.get(key);
            if (result != null){
                if (result.equals(RESULT_CODE_SUCCESS)){
                    log.info("发送成功：{}", requestMessage);
                    redisUtil.del(key);
                    return new Response(ResponseCode.CALL_SUCCESS);
                }else{
                    log.info("发送失败：{}", requestMessage);
                    return new Response(ResponseCode.CALL_FAIL);
                }
            }
            sleep(200L);
        }
    }

    @PostConstruct
    @Async
    public void listen() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                1L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.submit(() -> {
            while (true) {
                try {
                    sleep(100L);
                    Long size = redisUtil.getSize("priorityQuene1");
                    if (size > 0) {
                    	
                    	RequestMessage message = null;
                        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>(GET_SCRIPT, Object.class);
                        List<String> list = new ArrayList<>();
                        list.add("priorityQuene1");
                        Object result = redisUtil.excute(redisScript, list);
                        if (result != null) {
                            ArrayList<String> re = (ArrayList<String>)result;
                            String s = re.get(0);
                            message = JSON.parseObject(s, RequestMessage.class);
                        }
                        DirectMessage directMessage = message.getDirectMessage();
                        String uuid = message.getRequestID();
                        String tel = directMessage.getAcceptor_tel();
                        Date nowDate = new Date();
                        DateTimeFormatter STANDARD_FULL_DATEFORMTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String nowTime = nowDate.toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDateTime().format(STANDARD_FULL_DATEFORMTER);
                        directMessage.setTimestamp(nowTime);
                        log.info("处理消息：{}", message);
                        Integer integer = restTemplateUtil.sendMessage(directMessage);
                        redisUtil.expire(tel, 1);
                        redisUtil.set(uuid, String.valueOf(integer), 10);
                        sleep(100L);
                    }
                }
                catch (Throwable e) {
                    log.error("线程出错。",e);;
                }
            }
        });
    }

    private void sleep(Long ms){
        try {
            Thread.sleep(ms);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    

    
//	@Override
//	public void sendMessage(String qos,String  tels, MessageDTO messageDTO) {
//		DirectMessage directMessage = new DirectMessage();
//		directMessage.setQos(qos);
//		directMessage.setAcceptor_tel(tels);
//		TemplateParam template_param = new TemplateParam();
//		template_param.setTitle(messageDTO.getTitle());
//		template_param.setContent(messageDTO.getContent());
//		directMessage.setTemplate_param(template_param);
//		restTemplateUtil.sendMessage(directMessage);
//	}
}
