/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:13
 */
package com.nemo.channel.server;

import com.nemo.channel.controller.ContextController;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 服务核心
 * Created by Nemo on 2018/1/19.
 */
public class ServerCore {

    public static ServerCore core() throws IOException {
        return new ServerCore();
    }

    private ServerCore() throws IOException {
        //初始化容器
        init();
        //服务开启
        Server.open();
    }

    private void init(){
        //暂时只扫描ContextController类的所有处理方法
        Method[] methods = ContextController.class.getDeclaredMethods();
        if(methods!=null){
            for(Method method : methods){
                ServerContext.addMethod(method.getName(),method);
            }
        }

    }
}
