/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 10:25
 */
package com.nemo.channel.server;

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.bean.PrivateMsgBean;
import com.nemo.channel.bean.RequestBean;
import com.nemo.channel.bean.ResponseBean;
import com.nemo.channel.bean.RouteBean;
import com.nemo.channel.enums.PropertiesKeys;
import com.nemo.channel.enums.ResponseCode;
import com.nemo.channel.exception.AddChannelException;
import com.nemo.channel.exception.ChannelException;
import com.nemo.channel.exception.GlobalMsgException;
import com.nemo.channel.exception.ShutdownChannelException;
import com.nemo.channel.utils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.CharacterCodingException;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 服务提供者
 * Created by Nemo on 2018/1/19.
 */
public class Server {

    private final AsynchronousServerSocketChannel server;

    private static ServerCore core = ServerCore.core();

    private Server() throws IOException{
        //设置线程数为CPU核数
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
        server = AsynchronousServerSocketChannel.open(channelGroup);
        //重用端口
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        //绑定端口并设置连接请求队列长度
        server.bind(new InetSocketAddress(8080), 80);
    }

    /**
     * 开启服务
     * @throws IOException
     */
    public static void open() throws IOException, InterruptedException {
        Server server = new Server();
        server.dealConnect();
        publishToAll();
        publishToSingle();
    }

    /**
     * 处理每个请求
     */
    private void dealConnect() {
        System.out.println(Thread.currentThread().getName() + ": run in listen method" );
        //开始接受第一个连接请求
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>(){
            @Override
            public void completed(AsynchronousSocketChannel channel,
                                  Object attachment) {
                System.out.println(Thread.currentThread().getName() + ": run in accept completed method" );

                server.accept(attachment, this);
                //处理连接读写
                dealRead(channel,attachment);
            }

            /**
             * 服务器接受连接失败处理
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("server accept failed: " + exc);
            }

        });
    }

    /**
     * 处理读取请求
     * @param channel
     * @param attachment
     */
    private void dealRead(final AsynchronousSocketChannel channel,Object attachment) {
        //每个AsynchronousSocketChannel，分配一个缓冲区
        final ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
        readBuffer.clear();
        channel.read(readBuffer, attachment, new CompletionHandler<Integer, Object>(){

            @Override
            public void completed(Integer count, Object attachment) {

                boolean shutdowned = false;

                if(count > 0){
                    try{
                        readBuffer.flip();
                        CharBuffer charBuffer = CharsetUtils.decode(readBuffer);
                        String question = charBuffer.toString();

                        System.out.println(Thread.currentThread().getName() +"收到消息："+question);

                        ResponseBean responseBean = new ResponseBean();;
                        RequestBean requestBean = JSONObject.parseObject(question,RequestBean.class);
                        if(requestBean.getMethod() == null){    //没有说明任何请求方法，异常连接，强行中断
                            shutdownChannel(channel);
                            core.removeChannel(channel);
                            shutdowned = true;
                        }else {
                            try {
                                RouteBean routeBean = core.getRoute(requestBean.getMethod());
                                if(routeBean == null){
                                    throw new ChannelException(ResponseCode.METHOD_NOT_FOUND.name(),ResponseCode.METHOD_NOT_FOUND.getRemark());
                                }

                                if(requestBean.getParams()==null){
                                    throw new ChannelException(ResponseCode.PARAMETER_ERROR.name(),ResponseCode.PARAMETER_ERROR.getRemark());
                                }

                                String loginPath = NemoFrameworkUrlUtils.getFullRequestUrl(NemoFrameworkPropertiesUtils.getProp(PropertiesKeys.LOGIN_URL.getKey()));
                                //非登录请求，检查客户端是否已经登录,没有登录，则直接终止连接
                                if(!loginPath.equals(routeBean.getPath())){
                                    boolean channelExits = core.isChannelExits(channel);
                                    if(!channelExits){
                                        throw new ShutdownChannelException();
                                    }
                                }

                                ServerContext serverContext = new ServerContext(channel,requestBean.getParams());
                                Object invoke = ReflectUtils.invokeMehod(routeBean.getController(),routeBean.getMethod(), serverContext);

                                //Object invoke = routeBean.getMethod().invoke(routeBean.getController(), requestBean.getParams());
                                if(invoke != null){
                                    responseBean = new ResponseBean();
                                    responseBean.setData(invoke);
                                }
                            } catch (Throwable e) {
                                if(e instanceof InvocationTargetException){
                                    InvocationTargetException exception = (InvocationTargetException)e;
                                    e = exception.getTargetException();
                                }
                                if(e instanceof ChannelException) {
                                    ChannelException channelException = (ChannelException) e;
                                    responseBean.setCode(channelException.getCode());
                                    responseBean.setMsg(channelException.getMsg());
                                    responseBean.setData(channelException.getData());
                                }else if(e instanceof ShutdownChannelException){
                                    shutdownChannel(channel);
                                    shutdowned = true;
                                }else if(e instanceof AddChannelException) {
                                    core.addChannel(channel, ((AddChannelException) e).getName());
                                } else if (e instanceof GlobalMsgException){
                                    core.sendPublic(channel,JSONObject.toJSONString(((GlobalMsgException) e).getMsg()));
                                }else {
                                    responseBean.setCode(ResponseCode.COMMON_ERROR.name());
                                    responseBean.setMsg(ResponseCode.COMMON_ERROR.getRemark());
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(!shutdowned) {
                            core.sendPrivate(channel,JSONObject.toJSONString(responseBean));
                        }
                        readBuffer.clear();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }finally {
                        //继续读取
                        if(!shutdowned) {
                            dealRead(channel, attachment);
                        }
                    }
                }
                else{
                    //如果客户端关闭socket，那么服务器也需要关闭，否则浪费CPU
                    System.out.println(Thread.currentThread().getName() +"客户端请求关闭");
                    shutdownChannel(channel);
                    shutdowned = true;
                }

                if(shutdowned){
                    core.removeChannel(channel);
                }

            }

            /**
             * 服务器读失败处理
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("server read failed: " + exc);
                exc.printStackTrace();
                if(channel != null){
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    /**
     * 关闭连接
     * @param channel
     */
    private void shutdownChannel(AsynchronousSocketChannel channel){
        if(channel!=null){
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从队列中读取发送给某个人的消息
     */
    private static void publishToSingle(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrivateMsgBean privateMsgBean;
                try {
                    while ((privateMsgBean = core.privateMsgQueue.take()) != null) {
                        AsynchronousSocketChannel channel = privateMsgBean.getChannel();
                        if (channel.isOpen()) {
                            notifySingle(channel, privateMsgBean.getMsg());
                        } else {
                            core.removeChannel(channel);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送消息给所有人
     * @throws CharacterCodingException
     * @throws InterruptedException
     */
    private static void publishToAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<AsynchronousSocketChannel, String> channels = core.getChannels();
                String msg;
                try {
                    while ((msg = core.publicMsgQueue.take()) != null) {
                        for (AsynchronousSocketChannel channel : channels.keySet()) {
                            if(channel.isOpen()) {
                                notifySingle(channel, msg);
                            }else{
                                core.removeChannel(channel);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送给单个人
     * @param channel
     * @param msg
     */
    private static synchronized void notifySingle(AsynchronousSocketChannel channel,String msg) {
        try {
            if(msg!=null) {
                //阻塞等待当前channel没有在发送消息
                while (core.writingChnnelExits(channel)) {}
                core.addWritingChannel(channel);
                ByteBuffer byteBuffer = CharsetUtils.encode(msg);
                channel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        if (buffer.hasRemaining()) {
                            channel.write(buffer, buffer, this);
                        }
                        core.removeWritingChannel(channel);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        core.removeWritingChannel(channel);
                        System.out.println("server write failed: " + exc);
                        exc.printStackTrace();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            core.removeWritingChannel(channel);
        }
    }



}
