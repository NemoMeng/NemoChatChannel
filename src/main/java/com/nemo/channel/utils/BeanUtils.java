/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 15:43
 */
package com.nemo.channel.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Bean 操作类
 * Created by nemo on 16-6-23.
 */
public class BeanUtils {

    /**
     * Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
     * @param map
     * @param obj
     * @return
     */
    public static Object transMap2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);  // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            System.out.println("transMap2Bean Error " + e);
        }
        return obj;
    }

}