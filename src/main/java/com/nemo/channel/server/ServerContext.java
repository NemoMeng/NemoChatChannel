/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 15:59
 */
package com.nemo.channel.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.nemo.channel.bean.ResponseBean;
import com.nemo.channel.enums.ResponseCode;
import com.nemo.channel.exception.GlobalMsgException;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;
import java.util.Map;

/**
 * 服务上下文
 * Created by Nemo on 2018/1/23.
 */
public class ServerContext<T> {

    private AsynchronousSocketChannel channel;

    //简单的键值对参数
    private Object parameter;

    protected ServerContext(AsynchronousSocketChannel channel, Object parameter) {
        this.channel = channel;
        this.parameter = parameter;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void sendPublicMsg(String msg){
        ServerCore core = ServerCore.core();
        core.sendPublic(channel,msg);
    }

    public void sendToCurrentChannel(String msg){
        ServerCore core = ServerCore.core();
        core.sendPrivate(channel,msg);
    }

    public void sendPrivateMsg(String recver,String msg){
        ServerCore core = ServerCore.core();
        AsynchronousSocketChannel channel = core.getChannelByName(recver);
        if(channel==null){
            return;
        }
        core.sendPrivate(channel,msg);
    }

    public JSONObject getParameter() {
        return JSONObject.parseObject(JSONObject.toJSONString(parameter));
    }

    public T getParameter(Class<T> cls){
        return JSON.parseObject(JSONObject.toJSONString(parameter), cls);
    }
}
