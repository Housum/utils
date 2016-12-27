package com.hotusm.utils;


import javax.servlet.http.HttpServletRequest;

/**
 * HttpRequest工具类，封装了一些HttpRequest报头内容获取方法<br>
 *
 * @author qiesai
 * @create 2016-3-17
 *
 */
public class HttpRequestUtil {

    /**
     * 获取请求的客户端IP，由于有反向代理等因素，有很多因素会干扰获取真实的IP<br>
     * 这里尽量保证获取的IP真实<br>
     * (1)request.getRemoteAddr() : 正确情况下可以获取真实IP<br>
     * (2)反向代理后，无法直接获取，但是可以在X－FORWARDED－FOR 报头中获取<br>
     * (3)如果没有x-forwarded-for则尝试用proxy-client-ip、wl-proxy-client-ip
     *
     * @param req
     * @return
     */
    public static String getRemoteHost(HttpServletRequest req) {
        // 反向代理后：转发请求的HTTP头信息中，增加了X－FORWARDED－FOR信息
        String ip = req.getHeader("x-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

}
