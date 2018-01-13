package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Zz
 **/
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".happymmall.com";
    private final static String COOKIE_NAME = "mmal_login_token";

    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");//代表设置在根目录
        ck.setHttpOnly(true);//不许通过脚本访问cookie

        //单位是秒
        //如果不设置的话，cookie就不会写入硬盘，而是写入内存，只在当前页面有效
        ck.setMaxAge(60 * 60 * 24 * 365); //-1 代表永久

        log.info("write cookieNmae {} , cookieValue {} ", ck.getName(), ck.getValue());

        response.addCookie(ck);
    }

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();

        if (cks != null) {
            for (Cookie ck : cks) {
                log.info("read cookieName:{} , cookieValue:{} ", ck.getName(), ck.getValue());
                //建议使用StringUtils，已经做了判空处理
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{} ,cookieValue:{}", ck.getName(), ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks != null) {
            for (Cookie ck : cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置为0，代表删除此cookie
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }


}
