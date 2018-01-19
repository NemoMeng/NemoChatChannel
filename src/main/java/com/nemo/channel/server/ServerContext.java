/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:15
 */
package com.nemo.channel.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务上下文
 * Created by Nemo on 2018/1/19.
 */
public class ServerContext {

    private static Map<String,Object> contextMethods = new HashMap<>();

    /**
     * 在上下文添加一个方法
     * @param name
     * @param method
     */
    public static void addMethod(String name,Object method){
        if(contextMethods.get(name) != null){
            throw new RuntimeException("方法：【"+name+"】重复定义");
        }
        contextMethods.put(name,method);
    }

    /**
     * 得到一份方法
     * @param name
     * @return
     */
    public static Object getMethod(String name){
        return contextMethods.get(name);
    }
}
