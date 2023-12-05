package com.aurora.listener;

import com.aurora.entity.ExceptionLog;
import com.aurora.entity.OperationLog;
import com.aurora.event.ExceptionLogEvent;
import com.aurora.event.OperationLogEvent;
import com.aurora.mapper.ExceptionLogMapper;
import com.aurora.mapper.OperationLogMapper;
import com.aurora.model.dto.EmailDTO;
import com.aurora.model.dto.UserInfoDTO;
import com.aurora.service.OperationLogService;
import com.aurora.service.RedisService;
import com.aurora.service.UserInfoService;
import com.aurora.util.EmailUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.aurora.constant.CommonConstant.BLOGGER_ID;

@Component
@Log4j2
public class AuroraListener {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    @Autowired
    OperationLogService operationLogService;

    @Autowired
    RedisService redisService;

    @Resource
    EmailUtil emailUtil;
    @Resource
    UserInfoService userInfoService;

    @Async
    @EventListener(OperationLogEvent.class)
    public void saveOperationLog(OperationLogEvent operationLogEvent) {
        operationLogMapper.insert((OperationLog) operationLogEvent.getSource());
    }

    @Async
    @EventListener(ExceptionLogEvent.class)
    public void saveExceptionLog(ExceptionLogEvent exceptionLogEvent) {
        ExceptionLog source = (ExceptionLog) exceptionLogEvent.getSource();
        exceptionLogMapper.insert(source);
        if (source.getUnknownException()) {
            //未知异常 发送邮件 即使处理
            sendExceptionEmail(source);
        }
    }

    private void sendExceptionEmail(ExceptionLog source) {
        EmailDTO emailDTO = new EmailDTO();
        Map<String, Object> map = new HashMap<>();
        UserInfoDTO userInfo = userInfoService.getUserInfoById(BLOGGER_ID);
        emailDTO.setEmail(userInfo.getEmail());
        emailDTO.setSubject("未知异常提醒");
        emailDTO.setTemplate("common.html");
        String url = "https://www.blog.jonk.top/admin/#/login";
        map.put("content", "一只懒猫@博客【" + source.getOptDesc() + "】发生未知异常，"
                + "<a style=\"text-decoration:none;color:#12addb\" href=\"" + url + "  \">点击处理</a> <br/><br/>"
                + source.getExceptionInfo().substring(0, source.getExceptionInfo().indexOf("at"))
        );
        emailDTO.setCommentMap(map);
        emailUtil.sendHtmlMail(emailDTO);
    }

}
