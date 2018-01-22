/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:13
 */
package com.nemo.channel.server;

import com.nemo.channel.bean.RouteBean;
import com.nemo.channel.utils.NemoFrameworkUrlUtils;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 服务核心
 * Created by Nemo on 2018/1/19.
 */
public class ServerCore {

    private static ServerCore core;

    public Routers routers = new Routers();

    /**
     * 是否已经初始化的标志
     */
    private boolean isInited = false;

    public static ServerCore core() throws Exception {
        if(core == null){
            core = new ServerCore();
        }
        return core;
    }

    private ServerCore() throws Exception {
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
}
