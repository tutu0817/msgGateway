package com.exam.gate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@EnableTransactionManagement
@EnableAsync
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class msgGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(msgGatewayApplication.class, args);
    }

}
