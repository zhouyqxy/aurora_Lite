package com.aurora.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aurora.util.LazycatDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * @program: aurora_Lite
 * @description: 拦截请求解决时间兼容性问题处理时间戳问题
 * @author: jonk
 * @create: 2023-12-05 17:49
 **/

@Slf4j
@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        try {
            String bodyStr = JSON.toJSONString(body);
            JSONObject jsonObject = JSONObject.parseObject(bodyStr);
            log.info(" bodyStr {}", bodyStr);
            try {
                JSONObject data = jsonObject.getJSONObject("data");
                if (data != null) {
                    updateTime(jsonObject);
                }
                jsonObject.forEach((key, value) -> {
                    if (value instanceof JSONObject) {
                        updateTime((JSONObject) value);
                    } else if (value instanceof JSONArray) {
                        arrUpdate((JSONArray) value);
                    }
                });
            } catch (ClassCastException e) {
                JSONArray dataArr = jsonObject.getJSONArray("data");
                arrUpdate(dataArr);
            }
            body = jsonObject;
            log.info(" bodyStr {}", body);
        }catch (Exception e){

        }
        return body;

    }

    private  void arrUpdate(JSONArray dataArr ){
        for (Object datum : dataArr) {
            if (datum instanceof JSONObject) {
                updateTime((JSONObject)datum);
            }else if(datum instanceof JSONArray ){
                arrUpdate((JSONArray)datum);
            }
        }
    }

    /**
     * 修改创建时间和修改时间
     * @param data
     */
    private void updateTime(JSONObject data) {
        try {
            data.put("createTime", LazycatDateUtil.timestamToDate(data.getLong("createTime")));
            data.put("updateTime", LazycatDateUtil.timestamToDate(data.getLong("updateTime")));
        }catch (Exception e){
            log.info("err ",e.getMessage());
        }
    }
}