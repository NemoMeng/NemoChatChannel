/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:20
 */
package com.nemo.channel.controller;

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.annotation.Controller;
import com.nemo.channel.annotation.UrlMapping;
import com.nemo.channel.bean.AuthBean;
import com.nemo.channel.bean.MsgBean;
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
    public void login(ServerContext<AuthBean> context){
        AuthBean parameter = context.getParameter(AuthBean.class);

        String name = parameter.getName();
        String password = parameter.getPassword();

        if(name == null || password == null){
            throw new ShutdownChannelException("LOGIN_FAILD","用户名或者密码不能为空");
        }

        if(!password.toString().equals("123456")){
            throw new ShutdownChannelException("LOGIN_FAILD","用户名或者密码错误");
        }

        //登录成功
        throw new AddChannelException(name.toString());
    }

    @UrlMapping("msg")
    public void getMsg(){
        System.out.println("Invoke getMsg method.");
    }

    @UrlMapping("chat/global")
    public void chat(ServerContext<MsgBean> context){
        MsgBean parameter = context.getParameter(MsgBean.class);
        String msg = parameter.getMsg();
        msg = (msg==null)?"对方没有发送任何消息":msg.toString();
        context.sendPublicMsg(msg);
    }

    @UrlMapping("change/name")
    public void changeName(ServerContext<MsgBean> context){
        MsgBean msgBean = context.getParameter(MsgBean.class);
    }
}
