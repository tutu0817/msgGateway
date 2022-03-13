package com.exam.gate.common.util;

import javax.annotation.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.exam.gate.common.config.AppConfig;
import com.exam.gate.domain.entity.DirectMessage;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class RestTemplateUtil {
    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;
    
    /**
     * post 请求
     * @param url 请求路径
     * @param data body数据
     * @return
     */
    public String post(String url, JSONObject data) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Encoding", "UTF-8");
        headers.add("Content-Type", "application/json; charset=UTF-8");
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(data, headers);
        String result =  restTemplate.postForObject(url, requestEntity, String.class);
        return result;
    }
    
    public Integer sendMessage(DirectMessage directMessage){
        try {
            String res = post(AppConfig.SMS_URL_IPPORT+AppConfig.SMS_URL_PATH, JSONObject.parseObject(JSON.toJSONString(directMessage)));
            log.info("短信网关返回：{}",res);
            return 0;
        }catch (Exception e){
            log.error("短信网关返回：{}", e.getMessage());
            return 500;
        }
    }
}
