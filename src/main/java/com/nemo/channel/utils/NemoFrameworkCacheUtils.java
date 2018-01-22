/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/27 12:34
 */
package com.nemo.channel.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nemo on 2017/11/27.
 */
public class NemoFrameworkCacheUtils {

    private static Map<String,Object> props = new HashMap<String, Object>();

    private static String DEFAULT_PREFIX = "nemo.framework.cache.";

    /**
     * 得到缓存
     * @return
     */
    public static Object getCache(String key){
        if(key != null){
            return props.get(DEFAULT_PREFIX+key);
        }
        return null;
    }

    /**
     * 删除缓存
     * @param key
     */
    public static void removeCache(String key){
        if(key!=null){
            props.remove(DEFAULT_PREFIX+key);
        }
    }

    /**
     * 添加缓存
     * @param key
     * @param value
     */
    public static void addCache(String key,Object value){
        if(key!=null){
            props.put(DEFAULT_PREFIX+key,value);
        }
    }

}