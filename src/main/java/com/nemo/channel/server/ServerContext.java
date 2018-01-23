/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 15:59
 */
package com.nemo.channel.server;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by Nemo on 2018/1/23.
 */
public class ServerContext {

    private AsynchronousSocketChannel channel;

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void setChannel(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
}
