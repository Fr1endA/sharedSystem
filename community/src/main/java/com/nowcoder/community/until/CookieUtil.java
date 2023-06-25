package com.nowcoder.community.until;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class CookieUtil {
    @Autowired
    private UserService userService;

    public static String getValue(HttpServletRequest request, String name){
        if(request == null || name==null){
            throw new IllegalArgumentException();
        }
        Cookie[] cookies=request.getCookies();
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }

}
