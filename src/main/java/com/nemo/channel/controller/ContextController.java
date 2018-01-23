/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:20
 */
package com.nemo.channel.controller;

import com.nemo.channel.annotation.Controller;
import com.nemo.channel.annotation.UrlMapping;
import com.nemo.channel.bean.AuthBean;
import com.nemo.channel.exception.AddChannelException;
import com.nemo.channel.exception.GlobalMsgException;
import com.nemo.channel.exception.ShutdownChannelException;
import com.nemo.channel.server.ServerContext;
import com.nemo.channel.server.ServerCore;

import java.util.Map;

/**
 * Created by Nemo on 2018/1/19.
 */
@Controller("")
public class ContextController {

    @UrlMapping("login")
    public void login(AuthBean authBean, ServerContext context){
        String name = authBean.getName();
        String password = authBean.getPassword();

        if(name == null || password == null){
            throw new ShutdownChannelException("LOGIN_FAILD","用户名或者密码不能为空");
        }

        if(!password.equals("123456")){
            throw new ShutdownChannelException("LOGIN_FAILD","用户名或者密码错误");
        }

        //登录成功
        throw new AddChannelException(name.toString());
    }

    @UrlMapping("msg")
    public void getMsg(Map<String,Object> params){
        System.out.println("Invoke getMsg method.");
    }

    @UrlMapping("chat/global")
    public void chat(Map<String,Object> params){
        Object msg = params.get("msg");
        throw new GlobalMsgException(null,(msg==null?"对方没有发送任何消息":msg.toString()));
    }

    @UrlMapping("change/name")
    public void changeName(Map<String,Object> params){
        ServerCore core = ServerCore.core();
//        core.get
    }
}
