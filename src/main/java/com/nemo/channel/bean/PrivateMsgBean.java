/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/24 12:32
 */
package com.nemo.channel.bean;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by Nemo on 2018/1/24.
 */
public class PrivateMsgBean {

    private String msg;

    private AsynchronousSocketChannel channel;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void setChannel(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
}
