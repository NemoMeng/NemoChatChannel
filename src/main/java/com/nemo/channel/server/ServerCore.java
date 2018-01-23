/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:13
 */
package com.nemo.channel.server;

import com.nemo.channel.bean.RouteBean;
import com.nemo.channel.utils.NemoFrameworkUrlUtils;

import java.lang.reflect.Method;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务核心
 * Created by Nemo on 2018/1/19.
 */
public class ServerCore {

    private static ServerCore core;

    private Routers routers = new Routers();

    private Map<AsynchronousSocketChannel,String> channels = new HashMap<>();

    /**
     * 是否已经初始化的标志
     */
    private boolean isInited = false;

    public static ServerCore core() {
        if(core == null){
            core = new ServerCore();
        }
        return core;
    }

    private ServerCore(){
    }

    private void init(){
    }

    public RouteBean getRoute(String url){
        return routers.getRouteByPath(url);
    }

    public RouteBean getRouteByFullUrl(String url){
        url = NemoFrameworkUrlUtils.getFullRequestUrl(url);
        return getRoute(url);
    }

    public void addRoute(String url, Object cls, Method method){
        routers.addRoute(url,cls,method);
    }

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean isInited){
        core.isInited = isInited;
    }

    public synchronized Map<AsynchronousSocketChannel, String>  getChannels(){
        return channels;
    }

    public void addChannel(AsynchronousSocketChannel channel,String name){
        getChannels().put(channel,name);
    }

    public boolean isChannelExits(AsynchronousSocketChannel channel){
        return channels.get(channel)!=null;
    }

    public void removeChannel(AsynchronousSocketChannel channel){
        getChannels().remove(channel);
    }

    public String getChannelName(AsynchronousSocketChannel channel){
        return channels.get(channel);
    }
}
