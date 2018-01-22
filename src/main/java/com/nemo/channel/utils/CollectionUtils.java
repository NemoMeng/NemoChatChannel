/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/11/23 16:12
 */
package com.nemo.channel.utils;

import java.util.Collection;

/**
 * 集合工具类
 * Created by Nemo on 2017/11/23.
 */
public class CollectionUtils {

    /**
     * 判断集合是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection collection){
        return !isEmpty(collection);
    }

}