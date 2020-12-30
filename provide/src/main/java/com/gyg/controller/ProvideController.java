package com.gyg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @auther 郭永钢
 * @data 2020/12/30 21:05
 * @desc:
 */
@RestController
public class ProvideController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server-url.nacos-user-service}")
    private String ip;
    @GetMapping("/cost")
    @ResponseBody
    public String cost(){
        String s = restTemplate.getForObject(ip + "/buy/", String.class);
        return s;
    }
}
