/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:20
 */
package com.nemo.channel.controller;

import com.nemo.channel.exception.ChannelException;

import java.util.Map;

/**
 * Created by Nemo on 2018/1/19.
 */
public class ContextController {

    public String login(Map<String,Object> params){
        Object name = params.get("name");
        Object password = params.get("password");

        if(name == null || password == null){
            throw new ChannelException("LOGIN_FAILD","用户名或者密码不能为空");
        }

        if(!name.toString().equals("Nemo") || !password.toString().equals("123456")){
            throw new ChannelException("LOGIN_FAILD","用户名或者密码错误");
        }

        return name.toString();
    }

}
