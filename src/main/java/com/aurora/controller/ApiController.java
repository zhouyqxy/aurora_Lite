package com.aurora.controller;

import com.alibaba.fastjson.JSON;
import com.aurora.entity.About;
import com.aurora.mapper.SqliteAboutMapper;
import com.aurora.service.RedisService;
import com.aurora.service.TopicService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Autowired
    SqliteAboutMapper sqliteAboutMapper;

    @RequestMapping("/test")
    public String test(){
        List<About> abouts = sqliteAboutMapper.selectList(new LambdaQueryWrapper<>());
        return JSON.toJSONString(abouts);
    }

}