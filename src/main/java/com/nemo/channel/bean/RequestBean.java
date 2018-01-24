/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:30
 */
package com.nemo.channel.bean;

/**
 * 请求参数Bean
 * Created by Nemo on 2018/1/19.
 */
public class RequestBean {

    //请求的方法
    private String method;

    //请求的参数
    private Object params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }
}
