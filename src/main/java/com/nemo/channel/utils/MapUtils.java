/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/23 19:34
 */
package com.nemo.channel.utils;

import java.util.Map;

/**
 * Map操作相关工具类
 * Created by Nemo on 2017/11/23.
 */
public class MapUtils {

    /**
     * 判断map是否为空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map map){
        return map == null || map.isEmpty();
    }

    /**
     * 判断map是否不为空
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map map){
        return !isEmpty(map);
    }
}