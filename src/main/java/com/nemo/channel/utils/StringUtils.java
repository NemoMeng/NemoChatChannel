/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/27 14:24
 */
package com.nemo.channel.utils;

/**
 * 字符串相关操作
 * Created by Nemo on 2017/11/27.
 */
public class StringUtils {

    /**
     * 判断字符串是否为空，这里的空只的是null或者是空字符
     * @param str
     * @return
     */
    public static boolean isBlank(String str){
        if(str == null || str.trim().equals("")){
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否不为空，这里的空只的是null或者是空字符
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

}