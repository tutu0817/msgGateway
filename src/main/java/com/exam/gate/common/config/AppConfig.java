package com.exam.gate.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    public static final String USER_KEY_PREFIX = "GATEWAY_USER:";
    public static final String REQUEST_KEY_PREFIX = "GATEWAY_REQUEST:";
    public static final String TOKEN_KEY_PREFIX = "GATEWAY_TOKEN:";
    public static final Long TOKEN_EXPIRE_TIME_SECOND = 60 * 60L;
    public static final Long REQUEST_WAIT_TIME_SECOND = 10L;

    public static String SMS_URL_IPPORT = "";
    @Value("${message.url}")
    public void setSmsUrlIpport(String smsUrlIpport) {
        SMS_URL_IPPORT = smsUrlIpport;
    }

    public static String SMS_URL_PATH = "";
    @Value("${message.path:/v2/emp/templateSms/sendSms}")
    public void setSmsUrlPath(String smsUrlPath) {
        SMS_URL_PATH = smsUrlPath;
    }
    

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 用stringRedisSerializer来序列化和反序列化redis的value值
        redisTemplate.setValueSerializer(stringRedisSerializer);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        return factory;
    }
}
