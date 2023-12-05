package com.aurora.juhe.model.dto;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 *  聚合新闻
 * @author jonk
 * @date 24/5/2023 下午2:02
 * @param 
 * @return 
 **/
@Data
public class JuheResult<T> {

    public String reason;
    public JSONObject result;
    public Number errorCode;

}
