/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:13
 */
package com.nemo.channel.server;

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.bean.MsgBean;
import com.nemo.channel.bean.PrivateMsgBean;
import com.nemo.channel.bean.ResponseBean;
import com.nemo.channel.bean.RouteBean;
import com.nemo.channel.enums.ResponseCode;
import com.nemo.channel.exception.GlobalMsgException;
import com.nemo.channel.utils.NemoFrameworkUrlUtils;

import java.lang.reflect.Method;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 服务核心
 * Created by Nemo on 2018/1/19.
 */
public class ServerCore {

    private static ServerCore core;

    //路由集合
    private Routers routers = new Routers();

    //私有信息队列
    protected static final LinkedBlockingQueue<PrivateMsgBean> privateMsgQueue = new LinkedBlockingQueue<PrivateMsgBean>();
    //公共信息队列
    protected static final LinkedBlockingQueue<String> publicMsgQueue = new LinkedBlockingQueue<>();
    //在发送信息的客户端队列
    protected static Map<AsynchronousSocketChannel,Integer> writingChannel = new HashMap<>();

    //在线的客户端列表
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

    /**
     * 根据访问路径得到路由
     * @param url
     * @return
     */
    public RouteBean getRoute(String url){
        return routers.getRouteByPath(url);
    }

    /**
     * 根据访问路径得到路由
     * @param url
     * @return
     */
    public RouteBean getRouteByFullUrl(String url){
        url = NemoFrameworkUrlUtils.getFullRequestUrl(url);
        return getRoute(url);
    }

    /**
     * 路由添加
     * @param url
     * @param cls
     * @param method
     */
    public void addRoute(String url, Object cls, Method method){
        routers.addRoute(url,cls,method);
    }

    /**
     * 是否已经初始化完成
     * @return
     */
    public boolean isInited() {
        return isInited;
    }

    /**
     * 设施已经初始化标志
     * @param isInited
     */
    public void setInited(boolean isInited){
        core.isInited = isInited;
    }

    /**
     * 得到所有的客户端
     * @return
     */
    public synchronized Map<AsynchronousSocketChannel, String>  getChannels(){
        return channels;
    }

    /**
     * 添加客户端到在线集合
     * @param channel
     * @param name
     */
    public void addChannel(AsynchronousSocketChannel channel,String name){
        getChannels().put(channel,name);
    }

    /**
     * 客户端是否已经登录/存在
     * @param channel
     * @return
     */
    public boolean isChannelExits(AsynchronousSocketChannel channel){
        return channels.get(channel)!=null;
    }

    /**
     * 从在线队列移除客户端
     * @param channel
     */
    public void removeChannel(AsynchronousSocketChannel channel){
        getChannels().remove(channel);
    }

    /**
     * 得到客户端名称
     * @param channel
     * @return
     */
    public String getChannelName(AsynchronousSocketChannel channel){
        return channels.get(channel);
    }

    /**
     * 根据名称得到客户端
     * @param name
     * @return
     */
    public AsynchronousSocketChannel getChannelByName(String name){
        if(name == null){
            return null;
        }

        for(AsynchronousSocketChannel channel : channels.keySet()){
            if(name.equals(channels.get(channel))){
                return channel;
            }
        }

        return null;
    }

    /**
     * 添加客户端正在写消息的标志
     * @param channel
     */
    public void addWritingChannel(AsynchronousSocketChannel channel){
        synchronized (core.writingChannel) {
            core.writingChannel.put(channel, 1);
        }
    }

    /**
     * 客户端是否正在写消息
     * @param channel
     * @return
     */
    public boolean writingChnnelExits(AsynchronousSocketChannel channel){
        synchronized (core.writingChannel) {
            return core.writingChannel.containsKey(channel);
        }
    }

    /**
     * 客户端写消息完毕
     * @param channel
     */
    public void removeWritingChannel(AsynchronousSocketChannel channel){
        synchronized (core.writingChannel){
            core.writingChannel.remove(channel);
        }
    }

    /**
     * 发送给单独用户
     * @param channel
     * @param msg
     */
    public void sendPrivate(AsynchronousSocketChannel channel,String msg){
        try {
            PrivateMsgBean privateMsgBean = new PrivateMsgBean();
            privateMsgBean.setMsg(msg);
            privateMsgBean.setChannel(channel);
            core.privateMsgQueue.put(privateMsgBean);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给所有用户
     * @param channel
     * @param msg
     */
    public void sendPublic(AsynchronousSocketChannel channel,String msg){
        String name = core.getChannelName(channel);
        //msg = "【"+name+"】:"+ msg;

        MsgBean msgBean = new MsgBean();
        msgBean.setMsg(msg);
        msgBean.setForm(name);

        ResponseBean responseBean = new ResponseBean();
        responseBean.setData(msgBean);
        responseBean.setCode(ResponseCode.MSG_TYPE.name());
        sendPublic(JSONObject.toJSONString(responseBean));
    }

    /**
     * 发送给所有用户
     * @param msg
     */
    private void sendPublic(String msg){
        try {
            core.publicMsgQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
