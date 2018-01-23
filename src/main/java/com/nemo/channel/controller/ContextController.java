/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:20
 */
package com.nemo.channel.controller;

import com.nemo.channel.annotation.Controller;
import com.nemo.channel.annotation.UrlMapping;
import com.nemo.channel.exception.AddChannelException;
import com.nemo.channel.exception.ShutdownChannelException;

import java.util.Map;

/**
 * Created by Nemo on 2018/1/19.
 */
@Controller("")
public class ContextController {

    @UrlMapping("login")
    public void login(Map<String,Object> params){
        Object name = params.get("name");
        Object password = params.get("password");

        if(name == null || password == null){
            throw new ShutdownChannelException("LOGIN_FAILD","用户名或者密码不能为空");
        }

        if(!name.toString().equals("Nemo") || !password.toString().equals("123456")){
            throw new ShutdownChannelException("LOGIN_FAILD","用户名或者密码错误");
        }

        //登录成功
        throw new AddChannelException();
    }

    @UrlMapping("msg")
    public void getMsg(Map<String,Object> params){
        System.out.println("Invoke getMsg method.");
    }

}
