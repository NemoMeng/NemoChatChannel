/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/23 16:04
 */
package com.nemo.channel.server;

import com.nemo.channel.utils.CollectionUtils;
import com.nemo.channel.bean.RouteBean;
import com.nemo.channel.utils.NemoFrameworkUrlUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由管理器基类，所有的路由管理器都需要继承自它
 * Created by Nemo on 2017/11/23.
 */
public class Routers {

    /**
     * 路由列表，key为路由访问路径，val为路由本身
     */
    private Map<String,RouteBean> routes = new HashMap<String,RouteBean>();

    /**
     * 添加单个路由
     * @param route
     */
    public void addRoute(RouteBean route){
        if(route.getPath() == null) {
            route.setPath("/");
        }
        routes.put(route.getPath(),route);
    }

    /**
     * 批量增加路由
     * @param routeBeanList
     */
    public void addRoutes(List<RouteBean> routeBeanList){
        if(CollectionUtils.isNotEmpty(routeBeanList)){
            for(RouteBean route : routeBeanList) {
                if(route.getPath() == null){
                    route.setPath("/");
                }
                routes.put(route.getPath(), route);
            }
        }
    }

    /**
     * 移除单个路由
     * @param routeBean
     */
    public void removeRoute(RouteBean routeBean){
        if(routeBean.getPath() == null){
            routeBean.setPath("/");
        }
        routes.remove(routeBean.getPath());
    }

    /**
     * 添加单个路由
     * @param path
     * @param controller
     * @param method
     */
    public void addRoute(String path, Object controller, Method method){
        if(path == null){
            path = "/";
        }
        RouteBean routeBean = new RouteBean();
        routeBean.setPath(path);
        routeBean.setController(controller);
        routeBean.setMethod(method);

        routes.put(routeBean.getPath(),routeBean);
    }

    public RouteBean getRouteByPath(String path){
        path = NemoFrameworkUrlUtils.getFullRequestUrl(path);
        return routes.get(path);
    }

    public  Map<String, RouteBean> getRoutes(){
        return routes;
    }

    public void setRoutes(Map<String, RouteBean> routes){
        this.routes = routes;
    }
}