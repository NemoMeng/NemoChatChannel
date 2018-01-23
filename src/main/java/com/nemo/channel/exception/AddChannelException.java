/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 10:37
 */
package com.nemo.channel.exception;

/**
 * Created by Nemo on 2018/1/23.
 */
public class AddChannelException extends RuntimeException {

    public AddChannelException(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
