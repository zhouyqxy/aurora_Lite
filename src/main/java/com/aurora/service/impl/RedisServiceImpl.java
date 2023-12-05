package com.aurora.service.impl;


import com.aurora.service.RedisService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@SuppressWarnings("all")
public class RedisServiceImpl implements RedisService {


    private static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap();

    @Override
    public void set(String key, Object value, long time) {
        map.put(key, value);

    }

    @Override
    public void set(String key, Object value) {
        map.put(key, value);

    }

    @Override
    public Object get(String key) {
        return this.map.get(key);
    }

    @Override
    public Boolean del(String key) {
        Object remove = map.remove(key);
        if (remove instanceof Boolean) {
            return (Boolean) remove;
        }
        return true;
    }

    @Override
    public Long del(List<String> keys) {
        long l = 0;
        for (String key : keys) {
            if (del(key)) {
                l++;
            }
            ;
        }
        return l;
    }

    @Override
    public Boolean expire(String key, long time) {
        return null;
    }

    @Override
    public Boolean sIsMember(String key, Object key2) {
        Object o = hGet(key, key2);
        return o != null;

    }

    @Override
    public void hIncr(String visitorArea, String ipProvince, long l) {
        Object o = hGet(visitorArea, ipProvince);
        if (o != null || o instanceof Long) {
            l += (Long) o;
        }
        hSet(visitorArea, ipProvince, l);
    }

    @Override
    public void incr(String blogViewsCount, int i) {
        Object o = get(blogViewsCount);
        if (o != null) {
            i = (int) o + i;
        }
        set(blogViewsCount, i);

    }

    private static final String hIncr_key = "hIncr";
    private static final String Hset_key = "Hset_key";

    //根据key 设置 map  key2 步长
    @Override
    public long hIncr(String key, Object key2, int num) {
        key = key + hIncr_key;
        Object o = get(key);
        Map<Object, Integer> map = new HashMap<>();
        if (o == null) {
            map.put(key2, 1);
            set(key, map);
            return 1;
        } else {
            map = (Map) o;
            Integer value = map.get(key2);
            value = value + num;
            map.put(key2, value);
            set(key, map);
            return value;
        }


    }

    @Override
    public int getIncrExpireList(String key, int lastSecond) {
        key = key + hIncr_key;
        Object o = get(key);
        int result = 0;
        Map<Object, Integer> newMap = new HashMap<>();
        if (o != null || o instanceof Map) {
            //获取最近5秒中的数据
            Map<Object, Integer> map = (Map) o;
            //当前秒
            Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
            for (int i = 0; i < lastSecond; i++) {
                if (second < 1) {
                    second = 60l;
                }
                Integer val = map.get(second);
                result += val;
                second--;
                //暂存 重写设置缓存 移除旧数据
                newMap.put(second, val);
            }
            //重设缓存，移除旧的
            o = null;
            del(key);
            set(key, newMap);
            ;
        }
        return result;
    }

    @Override
    public long hIncr(String key, Object second) {
        return hIncr(key, second, 1);
    }

    @Override
    public Map<String, Object> hGetAll(String key) {
        key = key + Hset_key;
        Object o = get(key);
        Map map = new HashMap();
        Map result = new HashMap();
        if (o != null) {
            map = (Map) o;
            result.putAll(map);
        }
        return result;
    }

    @Override
    public Object hGet(String key, Object key2) {
        key = key + Hset_key;
        Object o = get(key);
        Map map = new HashMap();
        if (o != null) {
            map = (Map) o;
            return map.get(key2);
        }
        return null;
    }

    @Override
    public void sAdd(String key1, Object key2) {
        hIncr(key1, key2);
    }

    @Override
    public Long sSize(String key) {
        return Long.parseLong(hGetAll(key).size() + "");
    }

    @Override
    public void hDel(String key, String key2) {
        key = key + Hset_key;
        Object o = get(key);
        if (o != null) {
            Map map = (Map) o;
            map.remove(key2);
        }
    }

    @Override
    public void hSet(String key, String key2, Object vlaue) {
        key = key + Hset_key;
        Object o = get(key);
        Map map = new HashMap();
        if (o != null) {
            map = (Map) o;
            map.put(key2, vlaue);
        } else {
            map = new HashMap<>();
            map.put(key2, vlaue);
        }
        set(key, map);


    }
}
