/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/27 12:29
 */
package com.nemo.channel.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by Nemo on 2017/11/27.
 */
public class NemoFrameworkPropertiesUtils {

    /**
     * 初始化加载配置文件信息
     */
    public static void loadProperties(){
        Properties pps = new Properties();
        try {
            ClassLoader classLoader = NemoFrameworkPropertiesUtils.class.getClassLoader();
            //系统配置文件固定为这个
            pps.load( classLoader.getResourceAsStream("nemo.framework.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //得到配置文件的名字
        Enumeration enumeration = pps.propertyNames();
        while(enumeration.hasMoreElements()) {
            String strKey = (String) enumeration.nextElement();
            String strValue = pps.getProperty(strKey);
            //加入缓存中
            NemoFrameworkCacheUtils.addCache(strKey,strValue);
        }
    }

    /**
     * 得到配置信息
     * @param key
     * @return
     */
    public static String getProp(String key) {
        Object prop = NemoFrameworkCacheUtils.getCache(key);
        return prop==null?null:prop.toString();
    }

}