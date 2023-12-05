package com.aurora.juhe.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aurora.juhe.common.JuheConstant;
import com.aurora.juhe.model.dto.JuheNetworkhotResult;
import com.aurora.juhe.model.dto.JuheResult;
import com.aurora.juhe.model.dto.JuheSoupResult;
import com.aurora.juhe.service.JuheService;
import com.aurora.juhe.util.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * @ClassName JuheServiceImpl
 * @Author jonk
 * @Date 下午2:06 24/5/2023
 * @Version 1.0
 **/
@Slf4j
@Service
public class JuheServiceImpl implements JuheService {


    @Override
    public JuheNetworkhotResult queryNetworkhot() {
        JuheResult result = httpGetJuhe(JuheConstant.NET_WORK_HOT_URL,null);
        JSONObject result1 = result.getResult();
        JuheNetworkhotResult result2 = result1.toJavaObject(JuheNetworkhotResult.class);
        return  result2;
    }

    @Override
    public JuheSoupResult soup() {
        JuheResult result = httpGetJuhe(JuheConstant.SOUP_URL,null);
        JSONObject result1 = result.getResult();
        JuheSoupResult result2 = result1.toJavaObject(JuheSoupResult.class);
        return  result2;
    }


    private  JuheResult   httpGetJuhe(String url, JSONObject variables) {
        LocalDate now = LocalDate.now();

        log.info(" {} 调用聚合{}  ", now, url);
        JSONObject result = RestUtil.getAndUrlencoded(url, variables);
        JuheResult juheResult = JSONObject.toJavaObject(result, JuheResult.class);
        if (juheResult.getErrorCode().intValue()==0) {
            return juheResult;
        } else {
            log.error("调用聚合接口查询异常 url {}, variables {},result {}", url, variables, result);
        }
        return null;
    }
}
