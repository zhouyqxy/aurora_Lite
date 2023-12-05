package com.aurora.service;


import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public interface RedisService {

    void set(String key, Object value, long time);

    void set(String key, Object value);

    Object get(String key);

    Boolean del(String key);

    Long del(List<String> keys);

    Boolean expire(String key, long time);

    Boolean sIsMember(String s, Object articleId);

    void hIncr(String visitorArea, String ipProvince, long l);

    void incr(String key,int i);
    //根据key 设置 map  key2 步长
    long hIncr(String key, Object key2, int num);

    int getIncrExpireList(String key, int lastSecond);

    long hIncr(String key, Object second);

    Map<String, Object> hGetAll(String key);

    void hSet(String key, String key2, Object vlaue);

    Object hGet(String key, Object key2);

    void sAdd(String key1, Object key2);

    Long sSize(String uniqueVisitor);

    void hDel(String loginUser, String valueOf);

}

