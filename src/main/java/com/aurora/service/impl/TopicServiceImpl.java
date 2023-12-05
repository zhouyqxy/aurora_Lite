package com.aurora.service.impl;

import com.aurora.entity.Topic;
import com.aurora.mapper.TopicMapper;
import com.aurora.service.TopicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

}

