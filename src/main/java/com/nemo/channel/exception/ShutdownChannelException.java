/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 10:17
 */
package com.nemo.channel.exception;

import com.nemo.channel.enums.ResponseCode;

/**
 * 关闭客户端异常
 * 业务层抛出此异常，即可终止客户端
 * Created by Nemo on 2018/1/23.
 */
public class ShutdownChannelException extends RuntimeException {

    private String code = ResponseCode.COMMON_ERROR.name();

    private String msg = ResponseCode.COMMON_ERROR.getRemark();

    private Object Data = null;

    public ShutdownChannelException(){
    }

    public ShutdownChannelException(String code) {
        this.code = code;
    }

    public ShutdownChannelException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ShutdownChannelException(String code, String msg, Object data) {
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
