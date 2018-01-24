/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 15:59
 */
package com.nemo.channel.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

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

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void setChannel(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    public JSONObject getParameter() {
        return JSONObject.parseObject(JSONObject.toJSONString(parameter));
    }

    public T getParameter(Class<T> cls){
        return JSON.parseObject(JSONObject.toJSONString(parameter), cls);
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }
}
