/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 18:41
 */
package com.nemo.channel.bean;

import com.nemo.channel.enums.ResponseCode;

import java.util.Date;

/**
 * 响应Bean
 * Created by Nemo on 2018/1/19.
 */
public class ResponseBean {

    private String code = ResponseCode.SUCCESS.name();

    private String msg = ResponseCode.SUCCESS.getRemark();

    private Date time = new Date();

    private Object Data = null;

    public ResponseBean() {
    }

    public ResponseBean(String code, Object data) {
        this.code = code;
        Data = data;
    }

    public ResponseBean(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseBean(String code, String msg, Object data) {
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }
}
