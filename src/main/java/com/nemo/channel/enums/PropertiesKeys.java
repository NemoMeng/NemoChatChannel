/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/22 16:10
 */
package com.nemo.channel.enums;

/**
 * Created by Nemo on 2018/1/22.
 */
public enum PropertiesKeys {

    LOGIN_URL("nemo.channel.login_url","登录地址")
    ;

    private String key;

    private String remark;

    PropertiesKeys(String key,String remark){
        this.key = key;
        this.remark = remark;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
