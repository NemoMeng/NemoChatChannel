/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 14:18
 */
package com.nemo.channel.exception;

/**
 * Created by Nemo on 2018/1/23.
 */
public class GlobalMsgException extends RuntimeException {

    public GlobalMsgException( String to,String msg) {
        this.to = to;
        this.msg = msg;
    }

    private String to;

    private String msg;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
