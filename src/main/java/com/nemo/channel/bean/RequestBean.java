/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:30
 */
package com.nemo.channel.bean;

import java.util.List;
import java.util.Map;

/**
 * 请求参数Bean
 * Created by Nemo on 2018/1/19.
 */
public class RequestBean {

    private String method;

    private Map<String,Object> params;

    private String token;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
