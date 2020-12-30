package com.gyg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @auther 郭永钢
 * @data 2020/12/30 20:57
 * @desc:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProvideApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProvideApplication.class,args);
    }
}
