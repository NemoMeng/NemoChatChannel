/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:59
 */
package com.nemo.channel.exception;

import com.nemo.channel.enums.ResponseCode;

/**
 * Created by Nemo on 2018/1/19.
 */
public class ChannelException extends RuntimeException {

    private String code = ResponseCode.COMMON_ERROR.name();

    private String msg = ResponseCode.COMMON_ERROR.getRemark();

    private Object Data = null;

    public ChannelException(){
    }

    public ChannelException(String code) {
        this.code = code;
    }

    public ChannelException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ChannelException(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        Data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }
}
