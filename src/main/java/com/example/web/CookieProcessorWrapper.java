package com.example.web;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;

import javax.servlet.http.Cookie;

public class CookieProcessorWrapper extends Rfc6265CookieProcessor {

    @Override
    public String generateHeader(Cookie cookie) {
        cookie.setSecure(true);
        cookie.setHttpOnly(false);
        return super.generateHeader(cookie);
    }
}
