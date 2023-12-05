package com.aurora.controller;

import com.aurora.service.RedisService;
import com.aurora.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: blog-aurora
 * @description: 对外api 无权限校验
 * @author: jonk
 * @create: 2023-08-04 15:48
 **/

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    TopicService topicService;

    @Autowired
    RedisService redisService;

}