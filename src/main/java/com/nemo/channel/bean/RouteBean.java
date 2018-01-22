/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/23 15:59
 */
package com.nemo.channel.bean;

import java.lang.reflect.Method;

/**
 * 路由实体，用来定义控制器/方法/访问路径
 * Created by Nemo on 2017/11/23.
 */
public class RouteBean {

    /**
     * 访问路径
     */
    private String path;

    /**
     * 需要执行的路由方法
     */
    private Method method;

    /**
     * 路由所在的控制器
     */
    private Object controller;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
}