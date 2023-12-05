package com.aurora.interceptor;

import com.alibaba.fastjson.JSON;
import com.aurora.annotation.AccessLimit;
import com.aurora.model.vo.ResultVO;
import com.aurora.service.RedisService;
import com.aurora.util.IpUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.aurora.constant.CommonConstant.APPLICATION_JSON;

@Log4j2
@Component
@SuppressWarnings("all")
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                long seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                String key = IpUtil.getIpAddress(httpServletRequest) + "-" + handlerMethod.getMethod().getName();
                try {
//                    long q = redisService.incrExpire(key, seconds); //59*3  3
                    long q = slideWindow(accessLimit, key);
                    if (q > maxCount) {
                        render(httpServletResponse, ResultVO.fail("请求过于频繁，" + seconds + "秒后再试"));
                        log.warn(key + "请求次数超过每" + seconds + "秒" + maxCount + "次");
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    log.warn("redis错误: " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    //滑动窗口时间 不大于1分钟
    private  static  final  int lastSecond= 5;
    /**
     * 滑动时间窗口
     * 每隔1s累加前5s内每1s的请求数量，判断是否超出限流阈值
     */
    public long slideWindow(AccessLimit accessLimit, String key) {
        //将限制时间分割 成多分窗口， 分别记录窗口
        //每次请求记录当前窗口的请求次数
        //请求达到统计有效期内的请求次数
        int maxCount = accessLimit.maxCount();
        int outTime = accessLimit.seconds();
        //定义窗口 1s
        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        //以当前时间戳秒作为key
        long q = redisService.hIncr(key , second);
        //获取所有以key为前缀的所有值
        long sum = redisService.getIncrExpireList(key,lastSecond);
        return sum;
    }

    private void render(HttpServletResponse response, ResultVO<?> resultVO) throws Exception {
        response.setContentType(APPLICATION_JSON);
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(resultVO);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

}
