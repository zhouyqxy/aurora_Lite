package com.aurora.util;

import com.aurora.model.dto.UserDetailsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.AccessControlException;


@Component
public class UserUtil {

    public static UserDetailsDTO getUserDetailsDTO() {
        try{
            return (UserDetailsDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (Exception e){
            throw  new AccessControlException("登录超时");
        }

    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
