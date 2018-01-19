package com.nemo.channel.enums;

/**
 * Created by Nemo on 2018/1/19.
 */
public enum ResponseCode {

    METHOD_NOT_FOUND("请求的方法找不到"),
    SUCCESS("操作成功"),
    COMMON_ERROR("操作失败，请重试"),
    ;

    private String remark;

    ResponseCode(String remark){

    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
